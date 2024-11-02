package rdbparser.entry;

import rdbparser.EntryType;
import rdbparser.util.StringUtils;

public final class AuxField implements Entry {

  private final byte[] key;
  private final byte[] value;

  public AuxField(byte[] key, byte[] value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public EntryType getType() {
    return EntryType.AUX_FIELD;
  }

  public byte[] getKey() {
    return key;
  }

  public byte[] getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("%s (k: %s, v: %s)",
                         EntryType.AUX_FIELD,
                         StringUtils.getPrintableString(key),
                         StringUtils.getPrintableString(value));
  }
}
