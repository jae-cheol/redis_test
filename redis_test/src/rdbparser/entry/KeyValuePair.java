package rdbparser.entry;

import rdbparser.EntryType;
import rdbparser.RdbParser;
import rdbparser.ValueType;
import rdbparser.util.StringUtils;

import java.util.List;

public final class KeyValuePair implements Entry {

  byte[] key;
  ValueType valueType;
  List<byte[]> values;
  byte[] expireTime;
  Long idle;
  Integer freq;

  public byte[] getKey() {
    return key;
  }

  public ValueType getValueType() {
    return valueType;
  }

  @Override
  public EntryType getType() {
    return EntryType.KEY_VALUE_PAIR;
  }

  public void setValueType(ValueType valueType) {
    this.valueType = valueType;
  }

  public void setValues(List<byte[]> values) {
    this.values = values;
  }

  public List<byte[]> getValues() {
    return values;
  }


  public void setKey(byte[] key) {
    this.key = key;
  }

  public void setExpireTime(byte[] expireTime) {
    this.expireTime = expireTime;
  }

  public Long getExpireTime() {
    if (expireTime == null) {
      return null;
    }
    switch (expireTime.length) {
      case 4:
        return parseExpireTime4Bytes();
      case 8:
        return parseExpireTime8Bytes();
      default:
        throw new IllegalStateException("Invalid number of expire time bytes");
    }
  }

  private long parseExpireTime4Bytes() {
    return 1000L * ( ((long)expireTime[3] & 0xff) << 24
                   | ((long)expireTime[2] & 0xff) << 16
                   | ((long)expireTime[1] & 0xff) <<  8
                   | ((long)expireTime[0] & 0xff) <<  0);
  }

  private long parseExpireTime8Bytes() {
    return ((long)expireTime[7] & 0xff) << 56
         | ((long)expireTime[6] & 0xff) << 48
         | ((long)expireTime[5] & 0xff) << 40
         | ((long)expireTime[4] & 0xff) << 32
         | ((long)expireTime[3] & 0xff) << 24
         | ((long)expireTime[2] & 0xff) << 16
         | ((long)expireTime[1] & 0xff) <<  8
         | ((long)expireTime[0] & 0xff) <<  0;
  }



  
  public Integer getFreq() {
    return freq;
  }

  public void setFreq(Integer freq) {
    this.freq = freq;
  }

  
  public Long getIdle() {
    return idle;
  }

  public void setIdle(Long idle) {
    this.idle = idle;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(EntryType.KEY_VALUE_PAIR);
    sb.append(" (key: ");
    sb.append(StringUtils.getPrintableString(key));
    if (expireTime != null) {
      sb.append(", expire time: ");
      sb.append(getExpireTime());
    }
    sb.append(", ");
    int len = getValues().size();
    sb.append(len);
    if (len == 1) {
      sb.append(" value)");
    } else {
      sb.append(" values)");
    }
    return sb.toString();
  }

}
