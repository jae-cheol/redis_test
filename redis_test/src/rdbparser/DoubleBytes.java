package rdbparser;

import java.nio.charset.Charset;

final class DoubleBytes {

  private static final Charset ASCII = Charset.forName("ASCII");

  static final byte[] POSITIVE_INFINITY = String.valueOf(Double.POSITIVE_INFINITY).getBytes(ASCII);
  static final byte[] NEGATIVE_INFINITY = String.valueOf(Double.NEGATIVE_INFINITY).getBytes(ASCII);
  static final byte[] NaN = String.valueOf(Double.NaN).getBytes(ASCII);

}
