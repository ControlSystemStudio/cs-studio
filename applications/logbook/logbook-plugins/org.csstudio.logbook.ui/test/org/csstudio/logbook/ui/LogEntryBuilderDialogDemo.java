/**
 *
 */
package org.csstudio.logbook.ui;

import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 *
 */
public class LogEntryBuilderDialogDemo {

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {

        Display display = new Display();
        final Shell shell = new Shell(display);
        LogEntryBuilderDialog dialog = new LogEntryBuilderDialog(
                shell, LogEntryBuilder.withText("this is a test"));
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
            System.out.println("pressed OK");
        }
    }
}
