package org.csstudio.config.ioconfig.view.serachview;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 
 * Abstract Viewer Filter for String columns.
 * 
 * @author hrickens
 * @author $Author: $
 * @since 23.09.2010
 */
abstract class AbstractStringViewerFilter extends ViewerFilter {
    
    private boolean _caseSensetive;
    private String _searchText = "";
    
    public AbstractStringViewerFilter() {
        // Default Constructor
    }
    
    protected boolean checkSearchText() {
        return _searchText != null && _searchText.trim().length() > 0;
    }
    
    protected boolean compareStrings(@Nonnull final String text) {
        String string1;
        String string2 = getSearchText();
        if (isCaseSensetive()) {
            string1 = text;
        } else {
            string1 = text.toLowerCase();
            if(string2!=null) {
                string2 = string2.toLowerCase();
            }
        }
        return string2==null?false:string1.contains(string2);
    }
    
    @CheckForNull
    public String getSearchText() {
        return _searchText;
    }
    
    public boolean isCaseSensetive() {
        return _caseSensetive;
    }
    
    public void setCaseSensetive(final boolean caseSensetive) {
        _caseSensetive = caseSensetive;
    }
    
    public void setText(@Nullable final String searchText) {
        _searchText = searchText;
    }
    
}
