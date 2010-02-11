package org.csstudio.trends.databrowser.search;

import org.csstudio.trends.databrowser.model.ArchiveDataSource;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit Plug-in demo of the AddArchiveDialog
 *  @author Kay Kasemir
 */
public class AddArchiveDialogTest
{
    @Test
    public void testArchiveGUI() throws Exception
    {
        final Shell shell = new Shell();
        
        final AddArchiveDialog dlg = new AddArchiveDialog(shell);
        if (dlg.open() == AddArchiveDialog.OK)
            for (ArchiveDataSource arch : dlg.getArchives())
                System.out.println(arch);
    }
}
