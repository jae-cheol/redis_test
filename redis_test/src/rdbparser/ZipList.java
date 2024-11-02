package rdbparser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class ZipList extends LazyList<byte[]> {

  private static final Charset ASCII = Charset.forName("ASCII");
  private final byte[] envelope;

  ZipList(byte[] envelope) {
    this.envelope = envelope;
  }

  @Override
  protected List<byte[]> realize() {
    int pos = 8;
    int num = ((int)envelope[pos++] & 0xff) << 0
            | ((int)envelope[pos++] & 0xff) << 8;
    List<byte[]> list = new ArrayList<byte[]>(num);
    int idx = 0;
    while (idx < num) {
      int prevLen = (int)envelope[pos++] & 0xff;
      if (prevLen > 0xfd) {
        pos += 4;
      }
      int special = (int)envelope[pos++] & 0xff;
      int top2bits = special >> 6;
      int len;
      byte[] buf;
      switch (top2bits) {
        case 0:
          len = special & 0x3f;
          buf = new byte[len];
          System.arraycopy(envelope, pos, buf, 0, len);
          pos += len;
          list.add(buf);
          break;
        case 1:
          len = ((special & 0x3f) << 8) | ((int)envelope[pos++] & 0xff);
          buf = new byte[len];
          System.arraycopy(envelope, pos, buf, 0, len);
          pos += len;
          list.add(buf);
          break;
        case 2:
          len = ((int)envelope[pos++] & 0xff) << 24
              | ((int)envelope[pos++] & 0xff) << 16
              | ((int)envelope[pos++] & 0xff) <<  8
              | ((int)envelope[pos++] & 0xff) <<  0;
          buf = new byte[len];
          System.arraycopy(envelope, pos, buf, 0, len);
          pos += len;
          list.add(buf);
          break;
        case 3:
          int flag = (special & 0x30) >> 4;
          long val;
          switch (flag) {
            case 0:
              val = (long)envelope[pos++] & 0xff
                  | (long)envelope[pos++] << 8;
              list.add(String.valueOf(val).getBytes(ASCII));
              break;
            case 1:
              val = ((long)envelope[pos++] & 0xff) <<  0
                  | ((long)envelope[pos++] & 0xff) <<  8
                  | ((long)envelope[pos++] & 0xff) << 16
                  |  (long)envelope[pos++]         << 24;
              list.add(String.valueOf(val).getBytes(ASCII));
              break;
            case 2:
              val = ((long)envelope[pos++] & 0xff) <<  0
                  | ((long)envelope[pos++] & 0xff) <<  8
                  | ((long)envelope[pos++] & 0xff) << 16
                  | ((long)envelope[pos++] & 0xff) << 24
                  | ((long)envelope[pos++] & 0xff) << 32
                  | ((long)envelope[pos++] & 0xff) << 40
                  | ((long)envelope[pos++] & 0xff) << 48
                  |  (long)envelope[pos++]         << 56;
              list.add(String.valueOf(val).getBytes(ASCII));
              break;
            case 3:
              int loBits = special & 0x0f;
              switch (loBits) {
                case 0:
                  val = ((long)envelope[pos++] & 0xff) <<  0
                      | ((long)envelope[pos++] & 0xff) <<  8
                      |  (long)envelope[pos++]         << 16;
                  list.add(String.valueOf(val).getBytes(ASCII));
                  break;
                case 0x0e:
                  val = (long)envelope[pos++];
                  list.add(String.valueOf(val).getBytes(ASCII));
                  break;
                default:
                  list.add(String.valueOf(loBits - 1).getBytes(ASCII));
                  break;
              }
              break;
            default:
          }
          break;
        default:
      }
      idx += 1;
    }
    return list;
  }
}
