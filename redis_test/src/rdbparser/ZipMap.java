package rdbparser;

import java.util.ArrayList;
import java.util.List;

final class ZipMap extends LazyList<byte[]> {

  private final byte[] envelope;

  ZipMap(byte[] envelope) {
    this.envelope = envelope;
  }

  @Override
  protected List<byte[]> realize() {
    int pos = 0;
    int zmlen = (int)envelope[pos++] & 0xff;
    List<byte[]> list = zmlen < 254 ? new ArrayList<byte[]>(2*zmlen) : new ArrayList<byte[]>();
    while (true) {
      int b = (int)envelope[pos++] & 0xff;
      if (b == 255) { // reached end of zipmap
        break;
      }
      int len = 0;
      if (b < 253) {
        len = b;
      } else {
        len = ((int)envelope[pos++] & 0xff) <<  24
            | ((int)envelope[pos++] & 0xff) <<  16
            | ((int)envelope[pos++] & 0xff) << 8
            | ((int)envelope[pos++] & 0xff) << 0;
      }
      byte[] buf = new byte[len];
      System.arraycopy(envelope, pos, buf, 0, len);
      pos += len;
      list.add(buf);

      b = (int)envelope[pos++] & 0xff;
      len = 0;
      if (b < 253) {
        len = b;
      } else {
        len = ((int)envelope[pos++] & 0xff) <<  24
            | ((int)envelope[pos++] & 0xff) <<  16
            | ((int)envelope[pos++] & 0xff) << 8
            | ((int)envelope[pos++] & 0xff) << 0;
      }
      int free = (int)envelope[pos++] & 0xff;
      buf = new byte[len];
      System.arraycopy(envelope, pos, buf, 0, len);
      pos += len + free;
      list.add(buf);
    }
    return list;
  }
}
