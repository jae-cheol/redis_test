package rdbparser.util;

public final class StringUtils {

  public static String getPrintableString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      if (b > 31 && b < 127) { // printable ascii characters
        sb.append((char)b);
      } else {
        sb.append(String.format("\\x%02x", (int)b & 0xff));
      }
    }
    return sb.toString();
  }

}
