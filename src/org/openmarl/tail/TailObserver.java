package org.openmarl.tail;

public interface TailObserver {

    public void onLinesRead(final String[] lines);

}
