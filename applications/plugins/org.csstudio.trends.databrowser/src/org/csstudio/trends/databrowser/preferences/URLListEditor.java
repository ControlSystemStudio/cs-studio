package org.csstudio.trends.databrowser.preferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

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
                        "Archive Server URLs",
                        "Enter Archive Server URL",
                        last_url, null);
        if (dialog.open() != InputDialog.OK)
            return null;
        last_url = dialog.getValue().trim();
        return last_url;
    }

    /** (non-Javadoc)
     * Method declared on ListEditor.
     * Creates a single string from the given URL array.
     */
    protected String createList(String[] urls)
    {
        return Preferences.concatURLs(urls);
    }

    /** (non-Javadoc)
     * Method declared on ListEditor.
     * Split URLs from string.
     */
    protected String[] parseString(String urls)
    {
        return Preferences.splitURLs(urls);
    }
}
