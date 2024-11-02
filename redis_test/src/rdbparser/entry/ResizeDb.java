package rdbparser.entry;

import rdbparser.EntryType;

public final class ResizeDb implements Entry {

  private final long dbHashTableSize;
  private final long expireTimeHashTableSize;

  public ResizeDb(long dbHashTableSize, long expireTimeHashTableSize) {
    this.dbHashTableSize = dbHashTableSize;
    this.expireTimeHashTableSize = expireTimeHashTableSize;
  }

  @Override
  public EntryType getType() {
    return EntryType.RESIZE_DB;
  }

  
  public long getDbHashTableSize() {
    return dbHashTableSize;
  }

  
  public long getExpireTimeHashTableSize() {
    return expireTimeHashTableSize;
  }

  @Override
  public String toString() {
    return String.format("%s (db hash table size: %d, expire time hash table size: %d)",
                         EntryType.RESIZE_DB, dbHashTableSize, expireTimeHashTableSize);
  }
}
