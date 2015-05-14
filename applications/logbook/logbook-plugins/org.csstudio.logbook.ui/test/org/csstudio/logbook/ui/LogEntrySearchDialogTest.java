/**
 *
 */
package org.csstudio.logbook.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 *
 */
public class LogEntrySearchDialogTest {

    public static void main(String args[]) {

    Display display = new Display();
    final Shell shell = new Shell(display);
    List<String> logbooks = new ArrayList<String>(Arrays.asList(
        "Operations", "LOTO", "Mechanical Maintenance",
        " Timing System"));
    List<String> tags = new ArrayList<String>(Arrays.asList("testing",
        "clean up", "shroffk"));
    LogEntrySearchDialog dialog = new LogEntrySearchDialog(shell, logbooks,
        tags, Collections.<String, String> emptyMap());
    dialog.setBlockOnOpen(true);
    if (dialog.open() == IDialogConstants.OK_ID) {
        System.out.println("Search string: " + dialog.getSearchString());
    }
    }

}
