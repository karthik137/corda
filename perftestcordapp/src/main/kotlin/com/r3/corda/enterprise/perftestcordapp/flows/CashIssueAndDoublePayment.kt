package com.r3.corda.enterprise.perftestcordapp.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.enterprise.perftestcordapp.contracts.asset.Cash
import com.r3.corda.enterprise.perftestcordapp.contracts.asset.OnLedgerAsset
import com.r3.corda.enterprise.perftestcordapp.contracts.asset.PartyAndAmount
import com.r3.corda.enterprise.perftestcordapp.flows.AbstractCashFlow.Companion.FINALISING_TX
import com.r3.corda.enterprise.perftestcordapp.flows.AbstractCashFlow.Companion.GENERATING_TX
import com.r3.corda.enterprise.perftestcordapp.flows.AbstractCashFlow.Companion.SIGNING_TX
import net.corda.core.contracts.*
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.internal.uncheckedCast
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.OpaqueBytes
import net.corda.core.utilities.ProgressTracker
import java.util.*

abstract class AbstractCashIssueAndDoublePaymentFlow(
        val amount: Amount<Currency>,
        val issueRef: OpaqueBytes,
        val notary: Party,
        anonymous: Boolean,
        recipient: Party,
        progressTracker: ProgressTracker
) : AbstractConfidentialAwareCashFlow<StateAndRef<Cash.State>>(anonymous, recipient, progressTracker) {

    protected constructor(creator: AbstractCashIssueAndDoublePaymentFlow) : this(creator.amount, creator.issueRef, creator.notary, creator.anonymous, creator.recipient, creator.progressTracker)

    override fun makeAnonymousFlow(): AbstractConfidentialAwareCashFlow<StateAndRef<Cash.State>> {
        return CashIssueAndDoublePaymentAnonymous(this)
    }

    @Suspendable
    override fun mainCall(maybeAnonymousRecipient: AbstractParty, recipientSession: FlowSession): AbstractCashFlow.Result {
        fun deriveState(txState: TransactionState<Cash.State>, amt: Amount<Issued<Currency>>, owner: AbstractParty) = txState.copy(data = txState.data.copy(amount = amt, owner = owner))

        val issueResult = subFlow(CashIssueFlow(amount, issueRef, notary))
        val cashStateAndRef: StateAndRef<Cash.State> = uncheckedCast(serviceHub.loadStates(setOf(StateRef(issueResult.id, 0))).single())

        val changeIdentity = serviceHub.keyManagementService.freshKeyAndCert(ourIdentityAndCert, false)

        progressTracker.currentStep = GENERATING_TX
        val builder1 = TransactionBuilder(notary)
        val (spendTx1, keysForSigning1) = OnLedgerAsset.generateSpend(builder1, listOf(PartyAndAmount(maybeAnonymousRecipient, amount)), listOf(cashStateAndRef),
                changeIdentity.party.anonymise(),
                { state, quantity, owner -> deriveState(state, quantity, owner) },
                { Cash().generateMoveCommand() })

        val builder2 = TransactionBuilder(notary)
        val (spendTx2, keysForSigning2) = OnLedgerAsset.generateSpend(builder2, listOf(PartyAndAmount(maybeAnonymousRecipient, amount)), listOf(cashStateAndRef),
                changeIdentity.party.anonymise(),
                { state, quantity, owner -> deriveState(state, quantity, owner) },
                { Cash().generateMoveCommand() })

        progressTracker.currentStep = SIGNING_TX
        val tx1 = serviceHub.signInitialTransaction(spendTx1, keysForSigning1)
        val tx2 = serviceHub.signInitialTransaction(spendTx2, keysForSigning2)

        progressTracker.currentStep = FINALISING_TX
        val sessionsForFinality = if (serviceHub.myInfo.isLegalIdentity(recipient)) emptyList() else listOf(recipientSession)

        val notarised1 = finaliseTx(tx1, sessionsForFinality, "Unable to notarise spend first time")
        try {
            finaliseTx(tx2, sessionsForFinality, "Unable to notarise spend second time")
        } catch (expected: CashException) {
            val cause = expected.cause
            if (cause is NotaryException) {
                if (cause.error is NotaryError.Conflict) {
                    return Result(notarised1.id, recipient)
                }
                throw expected // Wasn't actually expected!
            }
        }
        throw FlowException("Managed to do double spend.  Should have thrown NotaryError.Conflict.")
    }
}

abstract class AbstractCashIssueAndDoublePaymentResponderFlow(anonymous: Boolean, otherSide: FlowSession) : AbstractConfidentialAwareCashResponderFlow<Unit>(anonymous, otherSide) {
    @Suspendable
    override fun respond() {
        // Not ideal that we have to do this check, but we must as FinalityFlow does not send locally
        if (!serviceHub.myInfo.isLegalIdentity(otherSide.counterparty)) {
            subFlow(ReceiveFinalityFlow(otherSide))
        }
    }
}

/**
 * Initiates a flow that self-issues cash.  We then try and send it to another party twice.  The flow only succeeds if
 * the second payment is rejected by the notary as a double spend.
 *
 * @param amount the amount of currency to issue.
 * @param issueRef a reference to put on the issued currency.
 * @param recipient payee Party
 * @param anonymous whether to anonymise before the transaction
 * @param notary the notary to set on the output states.
 */
@StartableByRPC
@InitiatingFlow
class CashIssueAndDoublePayment(amount: Amount<Currency>,
                                issueRef: OpaqueBytes,
                                recipient: Party,
                                anonymous: Boolean,
                                notary: Party,
                                progressTracker: ProgressTracker) : AbstractCashIssueAndDoublePaymentFlow(amount, issueRef, notary, anonymous, recipient, progressTracker) {
    constructor(request: CashIssueAndPaymentFlow.IssueAndPaymentRequest) : this(request.amount, request.issueRef, request.recipient, request.anonymous, request.notary, tracker())
    constructor(amount: Amount<Currency>, issueRef: OpaqueBytes, payTo: Party, anonymous: Boolean, notary: Party) : this(amount, issueRef, payTo, anonymous, notary, tracker())
}

@InitiatedBy(CashIssueAndDoublePayment::class)
class CashIssueAndDoublePaymentResponderFlow(otherSide: FlowSession) : AbstractCashIssueAndDoublePaymentResponderFlow(false, otherSide)

@InitiatingFlow
class CashIssueAndDoublePaymentAnonymous(creator: AbstractCashIssueAndDoublePaymentFlow) : AbstractCashIssueAndDoublePaymentFlow(creator)

@InitiatedBy(CashIssueAndDoublePaymentAnonymous::class)
class CashIssueAndDoublePaymentAnonymousResponderFlow(otherSide: FlowSession) : AbstractCashIssueAndDoublePaymentResponderFlow(true, otherSide)
