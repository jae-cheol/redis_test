package rdbparser;

import java.util.ArrayList;
import java.util.List;

final class QuickList2 extends LazyList<byte[]> {
  private final List<byte[]> listpacks;

  QuickList2(List<byte[]> listpacks) {
    this.listpacks = listpacks;
  }

  @Override
  protected List<byte[]> realize() {
    List<byte[]> list = new ArrayList<byte[]>();
    for (byte[] listpack : listpacks) {
      list.addAll(new ListpackList(listpack).realize());
    }
    return list;
  }
}
