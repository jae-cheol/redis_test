package rdbparser;

final class Lzf {

  private static int MAX_LITERAL = 32;

  static void expand(byte[] src, byte[] dest) {
    int srcPos = 0;
    int destPos = 0;
    do {
      int ctrl = src[srcPos++] & 0xff;
      if (ctrl < MAX_LITERAL) {
        ctrl++;
        System.arraycopy(src, srcPos, dest, destPos, ctrl);
        destPos += ctrl;
        srcPos += ctrl;
      } else {
        int len = ctrl >> 5;
        if (len == 7) {
          len += src[srcPos++] & 0xff;
        }
        len += 2;

        ctrl = -((ctrl & 0x1f) << 8) - 1;

        ctrl -= src[srcPos++] & 0xff;

        ctrl += destPos;
        for (int i = 0; i < len; i++) {
          dest[destPos++] = dest[ctrl++];
        }
      }
    } while (destPos < dest.length);
  }

}
