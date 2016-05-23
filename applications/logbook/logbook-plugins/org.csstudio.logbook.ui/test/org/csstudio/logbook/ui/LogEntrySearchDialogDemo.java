/**
 *
 */
package org.csstudio.logbook.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 *
 */
public class LogEntrySearchDialogDemo {

    public static void main(String args[]) {

    Display display = new Display();
    final Shell shell = new Shell(display);
    List<String> logbooks = new ArrayList<String>(Arrays.asList(
        "Operations", "LOTO", "Mechanical Maintenance",
        " Timing System"));
    List<String> tags = new ArrayList<String>(Arrays.asList("testing",
        "clean up", "shroffk", "test"));
    LogEntrySearchDialog dialog = new LogEntrySearchDialog(shell, logbooks,
        tags, Collections.<String, String> emptyMap());
    dialog.setBlockOnOpen(true);
    if (dialog.open() == IDialogConstants.OK_ID) {
        System.out.println("Search string: " + dialog.getSearchString());
    }
    }

}
