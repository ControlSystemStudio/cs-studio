package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PVFormulaInputBarTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Display display = new Display ();
        Shell shell = new Shell (display);
        PVFormulaInputBar bar = new PVFormulaInputBar(shell, SWT.NONE, null, null);
        bar.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
                if ("pvFormula".equals(arg0.getPropertyName())) {
                    System.out.println(arg0.getNewValue());
                }
            }
        });
        shell.setLayout (new RowLayout());
        shell.pack ();
        shell.open ();
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();
    }

}
