/**
 *
 */
package org.csstudio.shift.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShiftSearchDialogTest {

    public static void main(String args[]) {

        Display display = new Display();
        final Shell shell = new Shell(display);
        List<String> shifts = new ArrayList<String>(Arrays.asList("12", "13", "14","15"));
        List<String> types = new ArrayList<String>(Arrays.asList("testing", "clean up"));
        ShiftSearchDialog dialog = new ShiftSearchDialog(shell, shifts, types, Collections.<String, String> emptyMap());
        dialog.setBlockOnOpen(true);
        if (dialog.open() == IDialogConstants.OK_ID) {
            System.out.println("Search string: " + dialog.getSearchString());
        }
    }

}
