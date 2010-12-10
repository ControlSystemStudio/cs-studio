package org.csstudio.channelfinder.views;

import java.util.Comparator;

public interface IDirectionalComparator<T> extends Comparator<T>{

	public void setDirection(int dir);
}
