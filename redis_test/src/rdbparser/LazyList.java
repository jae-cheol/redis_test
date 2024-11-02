package rdbparser;

import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;

abstract class LazyList<T> extends AbstractSequentialList<T> {

  private List<T> list = null;

  protected abstract List<T> realize();

  @Override
  public ListIterator<T> listIterator(int index) {
    if (list == null){
      list = realize();
    }
    return list.listIterator(index);
  }

  @Override
  public int size() {
    if (list == null){
      list = realize();
    }
    return list.size();
  }

}
