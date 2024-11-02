package rdbparser.entry;

import rdbparser.EntryType;

public final class Eof implements Entry {

  private final byte[] checksum;

  public Eof(byte[] checksum) {
    this.checksum = checksum;
  }

  @Override
  public EntryType getType() {
    return EntryType.EOF;
  }

  public byte[] getChecksum() {
    return checksum;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(EntryType.EOF);
    sb.append(" (");
    for (byte b : checksum) {
      sb.append(String.format("%02x", (int)b & 0xff));
    }
    sb.append(")");
    return sb.toString();
  }
}
