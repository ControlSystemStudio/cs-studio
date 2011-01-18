package org.csstudio.config.authorizeid;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides data for second table (eaig, eair).
 * @author Rok Povsic
 */
public class AuthorizeIdLabelProvider extends LabelProvider implements ITableLabelProvider {
    
    /**
     * {@inheritDoc}
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getColumnText(Object element, int columnIndex) {
        
        String result = null;
        
        if (element instanceof AuthorizeIdEntry) {
            AuthorizeIdEntry entry = (AuthorizeIdEntry) element;
            switch (columnIndex) {
                case 0: // Group
                    result = entry.getEaig();
                    break;
                case 1: // Role
                    result = entry.getEair();
                    break;
                case 2: // Users
                    result = getUsers(entry);
                    break;
                default:
                    // ok
            }
        }
        return result;
    }
    
    private String getUsers(AuthorizeIdEntry entry) {
        return "unknown";
    }
    
}
