package org.csstudio.graphene.opiwidgets.properties;

import org.csstudio.graphene.AxisRangeEditorComposite;
import org.csstudio.opibuilder.visualparts.HelpTrayDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.epics.graphene.AxisRange;

public class AxisRangePropertyDialog extends HelpTrayDialog {

    protected AxisRange axisRange;
    private String dialogTitle;
    private AxisRangeEditorComposite editor;
    protected Shell shell;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public AxisRangePropertyDialog(Shell parent, AxisRange axisRange, String dialogTitle) {
        super(parent);
        this.axisRange = axisRange;
        this.dialogTitle = dialogTitle;
    }

    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        if (dialogTitle != null) {
            shell.setText(dialogTitle);
        }
    }

    @Override
    protected String getHelpResourcePath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        editor = new AxisRangeEditorComposite(parent, getShellStyle());
        if (axisRange != null) {
            editor.setAxisRange(axisRange);
        }
        return editor;
    }

    public AxisRange getAxisRange() {
        return editor.getAxisRange();
    }

}
