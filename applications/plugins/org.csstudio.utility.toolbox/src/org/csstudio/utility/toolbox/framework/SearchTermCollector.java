package org.csstudio.utility.toolbox.framework;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;


public class SearchTermCollector<T extends BindingEntity> {
	
	public List<SearchTerm> collect(WidgetFactory<T> wf) {
		List<SearchTerm> searchTerms = new ArrayList<SearchTerm>();
		for (SearchTerm searchTerm: wf) {
			if (searchTerm.hasValue()) {
				searchTerms.add(searchTerm);
			}
		}
		return searchTerms;
	}

}
