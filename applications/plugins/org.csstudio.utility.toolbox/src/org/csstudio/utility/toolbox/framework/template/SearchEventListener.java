package org.csstudio.utility.toolbox.framework.template;

import java.util.List;

import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;

public interface SearchEventListener {

   void beforeExecuteSearch(List<SearchTerm> searchTerms);

   void afterExecuteSearch(List<SearchTerm> searchTerms);

}
