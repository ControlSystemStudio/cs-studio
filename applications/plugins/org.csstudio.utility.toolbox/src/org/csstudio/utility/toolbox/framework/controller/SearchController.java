package org.csstudio.utility.toolbox.framework.controller;

import java.util.List;

import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.eclipse.swt.widgets.Widget;

public interface SearchController<T> {

	void executeSearch(List<SearchTerm> searchTerms);

	void create();
	
	void openRow(T object);
	
	void setFocusWidget(Widget widget);

}
