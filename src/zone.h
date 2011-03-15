/* Copyright (c) 2008, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

#ifndef ZONE_H
#define ZONE_H

#include "system.h"
#include "allocator.h"

namespace vm {

class Zone: public Allocator {
 public:
  class Segment {
   public:
    Segment(Segment* next, unsigned size): next(next), size(size) { }

    Segment* next;
    uintptr_t size;
    uint8_t data[0];
  };

  Zone(System* s, Allocator* allocator, unsigned minimumFootprint):
    s(s),
    allocator(allocator),
    segment(0),
    position(0),
    minimumFootprint(minimumFootprint < sizeof(Segment) ? 0 :
                     minimumFootprint - sizeof(Segment))
  { }

  ~Zone() {
    dispose();
  }

  void dispose() {
    for (Segment* seg = segment, *next; seg; seg = next) {
      next = seg->next;
      allocator->free(seg, sizeof(Segment) + seg->size);
    }

    segment = 0;
  }

  bool ensure(unsigned space) {
    if (segment == 0 or position + space > segment->size) {
      unsigned size = max
        (space, max
         (minimumFootprint, segment == 0 ? 0 : segment->size * 2))
        + sizeof(Segment);

      // pad to page size
      size = (size + (LikelyPageSizeInBytes - 1))
        & ~(LikelyPageSizeInBytes - 1);

      void* p = allocator->tryAllocate(size);
      if (p == 0) {
        size = space + sizeof(Segment);
        void* p = allocator->tryAllocate(size);
        if (p == 0) {
          return false;
        }
      }

      segment = new (p) Segment(segment, size - sizeof(Segment));
      position = 0;
    }
    return true;
  }

  virtual void* tryAllocate(unsigned size) {
    size = pad(size);
    if (ensure(size)) {
      void* r = segment->data + position;
      position += size;
      return r;
    } else {
      return 0;
    }
  }

  virtual void* allocate(unsigned size) {
    void* p = tryAllocate(size);
    expect(s, p);
    return p;
  }

  virtual void free(const void*, unsigned) {
    // not supported
    abort(s);
  }
  
  System* s;
  Allocator* allocator;
  void* context;
  Segment* segment;
  unsigned position;
  unsigned minimumFootprint;
};

} // namespace vm

#endif//ZONE_H
