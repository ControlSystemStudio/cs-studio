package org.csstudio.ui.util.dialogs;

import org.csstudio.ui.util.DialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/** Information message dialog subclass.
 * Creates new shell, then a dialog with this shell as parent, because on Linux the MessageDialog is
 * opened in the background when running a fullscreen OPI.
 *
 * @author Boris Versic
 */
public class InfoDialog extends MessageDialog {

    /**
     * Create a default info dialog using the INFORMATION style.
     *
     * @param parent the parent shell
     * @param title the title for the dialog
     * @param message the dialog message
     */
    public InfoDialog(Shell parent, String title, String message) {
        this(parent, title, message, MessageDialog.INFORMATION);
    }

    /**
     * Create an info dialog of the given style.
     *
     * @param parent the parent shell
     * @param title the title of the dialog
     * @param message the dialog message
     * @param style the dialog style (one of the MessageDialog styles)
     */
    public InfoDialog(Shell parent, String title, String message, int style) {
        this(parent,title,message, style, style == MessageDialog.QUESTION ?
            new String[] { DialogConstants.YES_LABEL, DialogConstants.NO_LABEL }
            : style == MessageDialog.CONFIRM ?
                new String[] { DialogConstants.OK_LABEL, DialogConstants.CANCEL_LABEL }
            : new String[] { DialogConstants.OK_LABEL });
    }

    private InfoDialog(Shell parent, String title, String message, int style, String[] buttonLabels) {
        super(parent, title, null, message, style, buttonLabels, 0);
        /*
         * Note: Using SWT.ON_TOP for the dialog style forces the dialog to have the NO_TRIM style on Linux (no title
         * bar, no close button) - tested with gtk WM. Ref. on chosen solution (new shell):
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=457115#c18 and answer here:
         * https://dev.eclipse.org/mhonarc/lists/platform-swt-dev/msg07717.html
         */
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setBlockOnOpen(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.MessageDialog#open()
     */
    @Override
    public int open() {
        final Shell shell = new Shell(getParentShell().getDisplay(), SWT.NO_TRIM);
        setParentShell(shell);
        shell.setSize(100, 30);
        Rectangle windowBounds = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
        // center horizontally, but place it a little higher vertically
        Point dialogCenter = new Point(windowBounds.x + windowBounds.width / 2,
            (windowBounds.y + windowBounds.height) / 2);
        Point location = new Point(dialogCenter.x - shell.getBounds().x / 2, dialogCenter.y - shell.getBounds().y / 2);
        shell.setLocation(location);
        int ans = super.open();
        shell.dispose();
        return ans;
    }

    /**
     * Open a new info dialog with information style.
     *
     * @param parent the parent shell
     * @param title dialog title
     * @param message dialog message
     * @return always 0
     * @deprecated use {@link #openInformation(Shell, String, String)}
     */
    @Deprecated
    public static int open(Shell parent, String title, String message) {
        return new InfoDialog(parent, title, message).open();
    }

    /**
     * Open a new info dialog of the given style.
     *
     * @param parent the dialog parent
     * @param title the dialog title
     * @param message the message to display
     * @param type the type of the dialog
     * @return the return code as defined by {@link #open()}
     */
    public static int open(Shell parent, String title, String message, int type) {
        return new InfoDialog(parent, title, message, type).open();
    }

    /**
     * Open a new error style info dialog.
     *
     * @param parent the dialog parent
     * @param title the dialog title
     * @param message the message to display
     * @param type the type of the dialog
     * @return the return code as defined by {@link #open()}
     */
    public static void openError(Shell parent, String title, String message) {
        new InfoDialog(parent, title, message, MessageDialog.ERROR).open();
    }

    /**
     * Open a new warning style info dialog.
     *
     * @param parent the dialog parent
     * @param title the dialog title
     * @param message the message to display
     * @param type the type of the dialog
     */
    public static void openWarning(Shell parent, String title, String message) {
        new InfoDialog(parent, title, message, MessageDialog.WARNING).open();
    }

    /**
     * Open a new info style info dialog.
     *
     * @param parent the dialog parent
     * @param title the dialog title
     * @param message the message to display
     * @param type the type of the dialog
     */
    public static void openInformation(Shell parent, String title, String message) {
        new InfoDialog(parent, title, message, MessageDialog.INFORMATION).open();
    }

    /**
     * Open a new question style info dialog.
     *
     * @param parent the dialog parent
     * @param title the dialog title
     * @param message the message to display
     * @param type the type of the dialog
     * @return true if Yes was pressed or false if No was pressed
     */
    public static boolean openQuestion(Shell parent, String title, String message) {
        return new InfoDialog(parent, title, message, MessageDialog.QUESTION).open() == 0;
    }

    /**
     * Open a new question style info dialog.
     *
     * @param parent the dialog parent
     * @param title the dialog title
     * @param message the message to display
     * @param type the type of the dialog
     * @return true if OK was pressed or false if Cancel was pressed
     */
    public static boolean openConfirm(Shell parent, String title, String message) {
        return new InfoDialog(parent, title, message, MessageDialog.CONFIRM).open() == 0;
    }
}
