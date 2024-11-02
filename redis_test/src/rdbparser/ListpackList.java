package rdbparser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ListpackList extends LazyList<byte[]> {
  private static final Charset ASCII = Charset.forName("ASCII");

  private static final int LP_ENCODING_7BIT_UINT = 0;
  private static final int LP_ENCODING_7BIT_UINT_MASK = 0x80;
  private static final int LP_ENCODING_6BIT_STR = 0x80;
  private static final int LP_ENCODING_6BIT_STR_MASK = 0xC0;
  private static final int LP_ENCODING_13BIT_INT = 0xC0;
  private static final int LP_ENCODING_13BIT_INT_MASK = 0xE0;
  private static final int LP_ENCODING_12BIT_STR = 0xE0;
  private static final int LP_ENCODING_12BIT_STR_MASK = 0xF0;

  private static final int LP_ENCODING_16BIT_INT = 0xF1;
  private static final int LP_ENCODING_16BIT_INT_MASK = 0xFF;
  private static final int LP_ENCODING_24BIT_INT = 0xF2;
  private static final int LP_ENCODING_24BIT_INT_MASK = 0xFF;
  private static final int LP_ENCODING_32BIT_INT = 0xF3;
  private static final int LP_ENCODING_32BIT_INT_MASK = 0xFF;
  private static final int LP_ENCODING_64BIT_INT = 0xF4;
  private static final int LP_ENCODING_64BIT_INT_MASK = 0xFF;
  private static final int LP_ENCODING_32BIT_STR = 0xF0;
  private static final int LP_ENCODING_32BIT_STR_MASK = 0xFF;

  private final byte[] envelope;

  ListpackList(byte[] envelope) {
    this.envelope = envelope;
  }

  private class ListpackParser {
    private int pos = 0;
    private List<byte[]> list = new ArrayList<byte[]>();

    private void decodeElement() {
      int b = envelope[pos++] & 0xff;

      int strLen = 0;

      if ((b & LP_ENCODING_6BIT_STR_MASK) == LP_ENCODING_6BIT_STR) {
        strLen = b & ~LP_ENCODING_6BIT_STR_MASK;
      } else if ((b & LP_ENCODING_12BIT_STR_MASK) == LP_ENCODING_12BIT_STR) {
        strLen = ((int)envelope[pos++] & 0xff)
            |     (b & 0xff & ~LP_ENCODING_12BIT_STR_MASK) << 8;
      } else if ((b & LP_ENCODING_32BIT_STR_MASK) == LP_ENCODING_32BIT_STR) {
        strLen = ((int)envelope[pos++] & 0xff) <<  0
            |    ((int)envelope[pos++] & 0xff) <<  8
            |    ((int)envelope[pos++] & 0xff) << 16
            |     (int)envelope[pos++]         << 24;
      }

      if (strLen > 0) {
        pos += strLen;
        list.add(Arrays.copyOfRange(envelope, pos - strLen, pos));
        pos += getLenBytes(strLen);
        return;
      }

      long val, negStart, negMax;

      if ((b & LP_ENCODING_7BIT_UINT_MASK) == LP_ENCODING_7BIT_UINT) {
        list.add(String.valueOf(b & ~LP_ENCODING_7BIT_UINT_MASK).getBytes(ASCII));
        pos++;
        return;
      } else if ((b & LP_ENCODING_13BIT_INT_MASK) == LP_ENCODING_13BIT_INT) {
        val = (b & 0xff & ~LP_ENCODING_13BIT_INT_MASK) << 8 | envelope[pos++] & 0xff;
        negStart = 1 << 12;
        negMax = (1 << 13) - 1;
      } else if ((b & LP_ENCODING_16BIT_INT_MASK) == LP_ENCODING_16BIT_INT) {
        val = ((long) envelope[pos++] & 0xff) | ((long) envelope[pos++] & 0xff) << 8;
        negStart = 1 << 15;
        negMax = (1 << 16) - 1;
      } else if ((b & LP_ENCODING_24BIT_INT_MASK) == LP_ENCODING_24BIT_INT) {
        val = ((long) envelope[pos++] & 0xff) | ((long) envelope[pos++] & 0xff) << 8
            | ((long) envelope[pos++] & 0xff) << 16;
        negStart = 1L << 23;
        negMax = (1L << 24) - 1;
      } else if ((b & LP_ENCODING_32BIT_INT_MASK) == LP_ENCODING_32BIT_INT) {
        val = ((long) envelope[pos++] & 0xff) | ((long) envelope[pos++] & 0xff) << 8
            | ((long) envelope[pos++] & 0xff) << 16
            | ((long) envelope[pos++] & 0xff) << 24;
        negStart = 1L << 31;
        negMax = (1L << 32) - 1;
      } else if ((b & LP_ENCODING_64BIT_INT_MASK) == LP_ENCODING_64BIT_INT) {
        val = ((long) envelope[pos++] & 0xff) | ((long) envelope[pos++] & 0xff) << 8
            | ((long) envelope[pos++] & 0xff) << 16
            | ((long) envelope[pos++] & 0xff) << 24
            | ((long) envelope[pos++] & 0xff) << 32
            | ((long) envelope[pos++] & 0xff) << 40
            | ((long) envelope[pos++] & 0xff) << 48
            | ((long) envelope[pos++] & 0xff) << 56;
        list.add(String.valueOf(val).getBytes(ASCII));
        pos++;
        return;
      } else {
        throw new RuntimeException("Invalid listpack envelope encoding");
      }

      if (val >= negStart) {

        long diff = negMax - val;
        val = diff;
        val = -val - 1;
      }
      pos++;
      list.add(String.valueOf(val).getBytes(ASCII));
    }

    private int getLenBytes(int len) {
      if (len < 128) {
        return 1;
      } else if (len < 16384) {
        return 2;
      } else if (len < 2097152) {
        return 3;
      } else if (len < 268435456) {
        return 4;
      } else {
        return 5;
      }
    }
  }

  @Override
  protected List<byte[]> realize() {

    ListpackParser listpackParser = new ListpackParser();
    listpackParser.pos += 4;
    int numElements = ((int) envelope[listpackParser.pos++] & 0xff) << 0
        | ((int) envelope[listpackParser.pos++] & 0xff) << 8;

    for (int i = 0; i < numElements; i++) {
      listpackParser.decodeElement();
    }
    if ((envelope[listpackParser.pos] & 0xff) != 0xff) {
      throw new IllegalStateException("Listpack did not end with 0xff byte.");
    }
    return listpackParser.list;
  }
}
