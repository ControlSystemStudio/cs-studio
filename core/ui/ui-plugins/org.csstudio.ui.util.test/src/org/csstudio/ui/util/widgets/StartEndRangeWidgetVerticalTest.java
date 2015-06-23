package org.csstudio.ui.util.widgets;

import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class StartEndRangeWidgetVerticalTest extends ApplicationWindow {
    private Text text_selectedMin;
    private Text text_selectedMax;
    private Text textLog;

    public StartEndRangeWidgetVerticalTest() {
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
    container.setLayout(new GridLayout(6, true));

    final StartEndRangeWidget vStartEndRangeWidget = new StartEndRangeWidget(container, SWT.NONE);
    vStartEndRangeWidget.setOrientation(ORIENTATION.VERTICAL);
    GridData gd_hStartEndRangeWidget = new GridData(SWT.LEFT, SWT.FILL,
        false, true, 1, 4);
    gd_hStartEndRangeWidget.heightHint = 20;
    vStartEndRangeWidget.setLayoutData(gd_hStartEndRangeWidget);
    vStartEndRangeWidget.addRangeListener(new RangeListener() {

        @Override
        public void rangeChanged() {
        text_selectedMin.setText(String.valueOf(vStartEndRangeWidget.getSelectedMin()));
        text_selectedMax.setText(String.valueOf(vStartEndRangeWidget.getSelectedMax()));
        StringBuffer log = new StringBuffer("[VERTICAL] rangeSet:"
            + vStartEndRangeWidget.isRangeSet() + " range:"
            + vStartEndRangeWidget.getMin() + ","
            + vStartEndRangeWidget.getMax() + " select range:"
            + vStartEndRangeWidget.getSelectedMin() + ","
            + vStartEndRangeWidget.getSelectedMax());
        log.append(System.getProperty("line.separator"));
        log.append(textLog.getText());
        textLog.setText(log.toString());
        }
    });

    Button btnRange1 = new Button(container, SWT.NONE);
    btnRange1.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        vStartEndRangeWidget.setRange(0, 1);
        }
    });
    btnRange1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
        false, 1, 1));
    btnRange1.setText("[0-1]");

    Button btnRange2 = new Button(container, SWT.NONE);
    btnRange2.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        vStartEndRangeWidget.setRange(0, 100);
        }
    });
    btnRange2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
        false, 1, 1));
    btnRange2.setText("[0-100]");

    Button btnRange3 = new Button(container, SWT.NONE);
    btnRange3.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        vStartEndRangeWidget.setRange(0, 1000);
        }
    });
    btnRange3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
        false, 1, 1));
    btnRange3.setText("[0-1000]");

    Button btnRange4 = new Button(container, SWT.NONE);
    btnRange4.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        vStartEndRangeWidget.setRange(-1, 1);
        }
    });
    btnRange4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
        false, 1, 1));
    btnRange4.setText("[-1 - 1]");

    Button btnRange5 = new Button(container, SWT.NONE);
    btnRange5.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        vStartEndRangeWidget.setRange(-10, 10);
        }
    });
    btnRange5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
        false, 1, 1));
    btnRange5.setText("[-10 - 10]");

        Label lblSelectedmin = new Label(container, SWT.NONE);
        lblSelectedmin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
            false, 1, 1));
        lblSelectedmin.setText("selectedMin:");

        text_selectedMin = new Text(container, SWT.BORDER);
        text_selectedMin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
            false, false, 1, 1));
        text_selectedMin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            vStartEndRangeWidget.setSelectedMin(Double
                .valueOf(text_selectedMin.getText()));
            }
        });

        Label lblSelectedmax = new Label(container, SWT.NONE);
        lblSelectedmax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
            false, 1, 1));
        lblSelectedmax.setText("selectedMax:");

        text_selectedMax = new Text(container, SWT.BORDER);
        text_selectedMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
            false, false, 1, 1));
        text_selectedMax.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            vStartEndRangeWidget.setSelectedMax(Double
                .valueOf(text_selectedMax.getText()));
            }
        });
        new Label(container, SWT.NONE);

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText("Event log:");
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

        textLog = new Text(container, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL
            | SWT.V_SCROLL | SWT.CANCEL);
        textLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));

    return container;
    }

    /**
     * Create the menu manager.
     *
     * @return the menu manager
     */
    @Override
    protected MenuManager createMenuManager() {
    MenuManager menuManager = new MenuManager("menu");
    return menuManager;
    }

    /**
     * Create the status line manager.
     *
     * @return the status line manager
     */
    @Override
    protected StatusLineManager createStatusLineManager() {
    StatusLineManager statusLineManager = new StatusLineManager();
    return statusLineManager;
    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(String args[]) {
    try {
        StartEndRangeWidgetVerticalTest window = new StartEndRangeWidgetVerticalTest();
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
    newShell.setText("New Application");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
    return new Point(616, 681);
    }
}
