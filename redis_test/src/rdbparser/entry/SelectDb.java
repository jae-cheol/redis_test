package rdbparser.entry;

import rdbparser.EntryType;


public final class SelectDb implements Entry {

  private final long id;

  public SelectDb(long id) {
    this.id = id;
  }

  @Override
  public EntryType getType() {
    return EntryType.SELECT_DB;
  }

  
  public long getId() {
    return id;
  }

  @Override
  public String toString() {
    return EntryType.SELECT_DB + " (" + id + ")";
  }
}
