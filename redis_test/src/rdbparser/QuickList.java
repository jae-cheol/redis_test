package rdbparser;

import java.util.ArrayList;
import java.util.List;

final class QuickList extends LazyList<byte[]> {

  private final List<byte[]> ziplists;

  QuickList(List<byte[]> ziplists) {
    this.ziplists = ziplists;
  }

  @Override
  protected List<byte[]> realize() {
    List<byte[]> list = new ArrayList<byte[]>();
    for (byte[] envelope : ziplists) {
      list.addAll(new ZipList(envelope).realize());
    }
    return list;
  }
}
