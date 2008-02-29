package org.csstudio.diag.postanalyser;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that triggers export to file */
public class ExportAction extends Action
{
    final private GUI gui;
    final private Shell shell;
    
    public ExportAction(final GUI gui, final Shell shell)
    {
        super(Messages.GUI_Export);
        this.gui = gui;
        this.shell = shell;
    }

    @Override
    public void run()
    {
        final FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        final String name = dlg.open();
        if (name == null)
            return;
        gui.exportToFile(name);
    }
}
