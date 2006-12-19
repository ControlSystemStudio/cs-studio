package org.csstudio.trends.databrowser.preferences;

import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

/** A ListEditor for a list of URLs.
 *  @author Kay Kasemir
 */
public class ArchiveListEditor extends ListEditor
{
    /** The last URL, or <code>null</code> if none. */
    private String last_archive;
    private final IInputValidator validator = new IInputValidator()
    {
        /** @return Error message or <code>null</code>. */
        public String isValid(String text)
        {
            IArchiveDataSource archive = Preferences.parseArchiveDataSource(text);
            if (archive != null)
                return null;
            return Messages.ArchiveFormatMessage;
        }
    };

    /** Constructor
     * @param name Name of the preference this field editor works on
     * @param parent Parent of the field editor's control
     */
    public ArchiveListEditor(String name, Composite parent)
    {
        init(name, Messages.Label_Archives);
        createControl(parent);
    }
    
    /** Method declared on ListEditor.
     *  Creates a new URL.
     */
    protected String getNewInputObject()
    {
        InputDialog dialog = new InputDialog(getShell(),
                        Messages.ArchiveInputTitle,
                        Messages.ArchiveInputMessage,
                        last_archive, validator);
        if (dialog.open() != InputDialog.OK)
            return null;
        last_archive = dialog.getValue().trim();
        return last_archive;
    }

    /** (non-Javadoc)
     * Method declared on ListEditor.
     * Creates a single string from the given array.
     */
    protected String createList(String[] items)
    {
        return Preferences.concatListItems(items);
    }

    /** (non-Javadoc)
     * Method declared on ListEditor.
     * Split URLs from string.
     */
    protected String[] parseString(String items)
    {
        return Preferences.splitListItems(items);
    }
}
