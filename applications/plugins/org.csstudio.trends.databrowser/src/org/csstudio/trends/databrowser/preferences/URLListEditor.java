package org.csstudio.trends.databrowser.preferences;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

/** A ListEditor for a list of URLs.
 *  @author Kay Kasemir
 */
public class URLListEditor extends ListEditor
{
    /** The last URL, or <code>null</code> if none. */
    private String last_url;

    /** Constructor
     * @param name Name of the preference this field editor works on
     * @param parent Parent of the field editor's control
     */
    public URLListEditor(String name, Composite parent)
    {
        init(name, Messages.URLS_Label);
        createControl(parent);
    }
    
    /** Method declared on ListEditor.
     *  Creates a new URL.
     */
    protected String getNewInputObject()
    {
        InputDialog dialog = new InputDialog(getShell(),
                        Messages.URLInput_Title,
                        Messages.URLInput_Message,
                        last_url, null);
        if (dialog.open() != InputDialog.OK)
            return null;
        last_url = dialog.getValue().trim();
        return last_url;
    }

    /** (non-Javadoc)
     * Method declared on ListEditor.
     * Creates a single string from the given item array.
     */
    protected String createList(String[] items)
    {
        return Preferences.concatListItems(items);
    }

    /** (non-Javadoc)
     * Method declared on ListEditor.
     * Split item list from string.
     */
    protected String[] parseString(String items)
    {
        return Preferences.splitListItems(items);
    }
}
