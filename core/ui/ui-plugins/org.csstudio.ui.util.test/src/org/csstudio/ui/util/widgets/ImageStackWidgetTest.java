/**
 *
 */
package org.csstudio.ui.util.widgets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author shroffk
 *
 */
public class ImageStackWidgetTest extends ApplicationWindow {

    public ImageStackWidgetTest() {
    super(null);
    addToolBar(SWT.FLAT | SWT.WRAP);
    addMenuBar();
    addStatusLine();
    }

    /**
     * Create contents of the application window.
     *
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new GridLayout(1, false));
    final ImageStackWidget imageStackWidget = new ImageStackWidget(
        container, SWT.NONE);
    imageStackWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        true, 1, 1));

    Button btnNewButton = new Button(container, SWT.NONE);
    btnNewButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        Map<String, InputStream> imageMap = new HashMap<String, InputStream>();
        File imagefolder = new File("Images");
        for (File file : imagefolder.listFiles()) {
            try {
            imageMap.put(file.getName(), new FileInputStream(file));
            } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            }
        }
        try {
            imageStackWidget.setImageInputStreamsMap(imageMap);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        }
    });
    btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton.setText("Add Test Images");

    Button btnNewButton_1 = new Button(container, SWT.NONE);
    btnNewButton_1.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        final FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String[] { "*.png", "*.jpg" }); //$NON-NLS-1$
        dlg.setFilterNames(new String[] { "PNG Image", "JPEG Image" }); //$NON-NLS-1$
        final String filename = dlg.open();
        if (filename != null) {
            try {
            imageStackWidget.addImage(filename, new FileInputStream(
                filename));
            } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            }
        }
        }
    });
    btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        false, 1, 1));
    btnNewButton_1.setText("Add Image");
    return container;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
    try {
        ImageStackWidgetTest window = new ImageStackWidgetTest();
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    /**
     * Configure the shell.
     *
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Test ImageStackWidgetTest");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
    return new Point(473, 541);
    }
}
