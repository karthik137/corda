DROP TABLE IF EXISTS ${schema}.cash_state_participants;
DROP TABLE IF EXISTS ${schema}.cash_states_v2_participants;
DROP TABLE IF EXISTS ${schema}.cp_states_v2_participants;
DROP TABLE IF EXISTS ${schema}.dummy_linear_state_parts;
DROP TABLE IF EXISTS ${schema}.dummy_linear_states_v2_parts;
DROP TABLE IF EXISTS ${schema}.dummy_deal_states_parts;
DROP TABLE IF EXISTS ${schema}.node_attachments_signers;
DROP TABLE IF EXISTS ${schema}.node_attachments_contracts;
DROP TABLE IF EXISTS ${schema}.node_attachments;
DROP TABLE IF EXISTS ${schema}.node_checkpoints;
DROP TABLE IF EXISTS ${schema}.node_transactions;
DROP TABLE IF EXISTS ${schema}.node_message_retry;
DROP TABLE IF EXISTS ${schema}.node_message_ids;
DROP TABLE IF EXISTS ${schema}.vault_states;
DROP TABLE IF EXISTS ${schema}.node_our_key_pairs;
DROP TABLE IF EXISTS ${schema}.node_scheduled_states;
DROP TABLE IF EXISTS ${schema}.node_network_map_nodes;
DROP TABLE IF EXISTS ${schema}.node_network_map_subscribers;
DROP TABLE IF EXISTS ${schema}.node_notary_committed_states;
DROP TABLE IF EXISTS ${schema}.node_notary_committed_transactions;
DROP TABLE IF EXISTS ${schema}.node_notary_request_log;
DROP TABLE IF EXISTS ${schema}.node_transaction_mappings;
DROP TABLE IF EXISTS ${schema}.vault_fungible_states_parts;
DROP TABLE IF EXISTS ${schema}.vault_linear_states_parts;
DROP TABLE IF EXISTS ${schema}.vault_fungible_states;
DROP TABLE IF EXISTS ${schema}.vault_linear_states;
DROP TABLE IF EXISTS ${schema}.node_bft_committed_states;
DROP TABLE IF EXISTS ${schema}.node_raft_committed_states;
DROP TABLE IF EXISTS ${schema}.vault_transaction_notes;
DROP TABLE IF EXISTS ${schema}.link_nodeinfo_party;
DROP TABLE IF EXISTS ${schema}.node_link_nodeinfo_party;
DROP TABLE IF EXISTS ${schema}.node_info_party_cert;
DROP TABLE IF EXISTS ${schema}.node_info_hosts;
DROP TABLE IF EXISTS ${schema}.node_infos;
DROP TABLE IF EXISTS ${schema}.cp_states;
DROP TABLE IF EXISTS ${schema}.node_contract_upgrades;
DROP TABLE IF EXISTS ${schema}.node_identities;
DROP TABLE IF EXISTS ${schema}.node_named_identities;
DROP TABLE IF EXISTS ${schema}.node_properties;
DROP TABLE IF EXISTS ${schema}.children;
DROP TABLE IF EXISTS ${schema}.parents;
DROP TABLE IF EXISTS ${schema}.contract_cash_states;
DROP TABLE IF EXISTS ${schema}.contract_cash_states_v1;
DROP TABLE IF EXISTS ${schema}.messages;
DROP TABLE IF EXISTS ${schema}.state_participants;
DROP TABLE IF EXISTS ${schema}.cash_states_v2;
DROP TABLE IF EXISTS ${schema}.cash_states_v3;
DROP TABLE IF EXISTS ${schema}.cp_states_v1;
DROP TABLE IF EXISTS ${schema}.cp_states_v2;
DROP TABLE IF EXISTS ${schema}.dummy_deal_states;
DROP TABLE IF EXISTS ${schema}.dummy_linear_states;
DROP TABLE IF EXISTS ${schema}.dummy_linear_states_v2;
DROP TABLE IF EXISTS ${schema}.dummy_test_states_parts;
DROP TABLE IF EXISTS ${schema}.dummy_test_states;
DROP TABLE IF EXISTS ${schema}.node_mutual_exclusion;
DROP TABLE IF EXISTS ${schema}.DATABASECHANGELOG;
DROP TABLE IF EXISTS ${schema}.DATABASECHANGELOGLOCK;
DROP TABLE IF EXISTS ${schema}.cert_revocation_request_AUD;
DROP TABLE IF EXISTS ${schema}.cert_signing_request_AUD;
DROP TABLE IF EXISTS ${schema}.network_map_AUD;
DROP TABLE IF EXISTS ${schema}.REVINFO;
DROP TABLE IF EXISTS ${schema}.cert_revocation_request;
DROP TABLE IF EXISTS ${schema}.cert_data;
DROP TABLE IF EXISTS ${schema}.cert_revocation_list;
DROP TABLE IF EXISTS ${schema}.node_info;
DROP TABLE IF EXISTS ${schema}.cert_signing_request;
DROP TABLE IF EXISTS ${schema}.network_map;
DROP TABLE IF EXISTS ${schema}.parameters_update;
DROP TABLE IF EXISTS ${schema}.network_parameters;
DROP TABLE IF EXISTS ${schema}.private_network;
DROP TABLE IF EXISTS ${schema}.pk_hash_to_ext_id_map;
DROP TABLE IF EXISTS ${schema}.state_party;
DROP TABLE IF EXISTS ${schema}.foos;
DROP VIEW IF EXISTS ${schema}.v_pkey_hash_ex_id_map;
DROP SEQUENCE IF EXISTS ${schema}.hibernate_sequence;
DROP USER IF EXISTS ${schema};
DROP SCHEMA IF EXISTS ${schema};