package org.csstudio.ui.util.widgets;

import org.csstudio.ui.util.widgets.StartEndRangeWidget.ORIENTATION;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class StartEndRangeWidgetTest extends ApplicationWindow {
    private Text text_min;
    private Text text_max;
    private Text text_selectedMin;
    private Text text_selectedMax;
    private Text textLog;

    public StartEndRangeWidgetTest() {
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
	container.setLayout(new GridLayout(3, false));
	new Label(container, SWT.NONE);

	final StartEndRangeWidget hStartEndRangeWidget = new StartEndRangeWidget(
		container, SWT.NONE);
	GridData gd_hStartEndRangeWidget = new GridData(SWT.FILL, SWT.CENTER,
		true, false, 2, 1);
	gd_hStartEndRangeWidget.heightHint = 20;
	hStartEndRangeWidget.setLayoutData(gd_hStartEndRangeWidget);
	hStartEndRangeWidget.addRangeListener(new RangeListener() {

	    @Override
	    public void rangeChanged() {
		text_min.setText(String.valueOf(hStartEndRangeWidget.getMin()));
		text_max.setText(String.valueOf(hStartEndRangeWidget.getMax()));
		text_selectedMin.setText(String.valueOf(hStartEndRangeWidget.getSelectedMin()));
		text_selectedMax.setText(String.valueOf(hStartEndRangeWidget.getSelectedMax()));
		StringBuffer log = new StringBuffer("[HORIZONTAL] rangeSet:"+hStartEndRangeWidget.isRangeSet() + 
			" range:" + hStartEndRangeWidget.getMin()+","+hStartEndRangeWidget.getMax()+ 
			" select range:" +hStartEndRangeWidget.getSelectedMin()+","+hStartEndRangeWidget.getSelectedMax());
		log.append(System.getProperty("line.separator"));
		log.append(textLog.getText());
		textLog.setText(log.toString());
	    }
	});

	final StartEndRangeWidget vStartEndRangeWidget = new StartEndRangeWidget(
		container, SWT.NONE);
	GridData gd_vStartEndRangeWidget = new GridData(SWT.LEFT, SWT.FILL,
		false, true, 1, 7);
	gd_vStartEndRangeWidget.widthHint = 20;
	vStartEndRangeWidget.setLayoutData(gd_vStartEndRangeWidget);
	vStartEndRangeWidget.setOrientation(ORIENTATION.VERTICAL);
	vStartEndRangeWidget.addRangeListener(new RangeListener() {

	    @Override
	    public void rangeChanged() {				
		text_min.setText(String.valueOf(vStartEndRangeWidget.getMin()));
		text_max.setText(String.valueOf(vStartEndRangeWidget.getMax()));
		text_selectedMin.setText(String.valueOf(vStartEndRangeWidget.getSelectedMin()));
		text_selectedMax.setText(String.valueOf(vStartEndRangeWidget.getSelectedMax()));
		StringBuffer log = new StringBuffer("[VERTICAL] rangeSet:"+vStartEndRangeWidget.isRangeSet() + 
			" range:" + vStartEndRangeWidget.getMin()+","+vStartEndRangeWidget.getMax()+ 
			" select range:" +vStartEndRangeWidget.getSelectedMin()+","+vStartEndRangeWidget.getSelectedMax());
		log.append(System.getProperty("line.separator"));
		log.append(textLog.getText());
		textLog.setText(log.toString());
	    }
	});

	Label lblMin = new Label(container, SWT.NONE);
	lblMin.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
	lblMin.setText("min:");

	Label lblMax = new Label(container, SWT.NONE);
	lblMax.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
	lblMax.setText("max:");

	text_min = new Text(container, SWT.BORDER);
	text_min.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	text_min.addListener(SWT.DefaultSelection, new Listener() {
	    public void handleEvent(Event e) {
//		hStartEndRangeWidget.setMin(Double.valueOf(text.getText()));
//		vStartEndRangeWidget.setMin(Double.valueOf(text.getText()));
		hStartEndRangeWidget.setRange(Double.valueOf(text_min.getText()), Double.valueOf(text_max.getText()));
		vStartEndRangeWidget.setRange(Double.valueOf(text_min.getText()), Double.valueOf(text_max.getText()));
	    }
	});

	text_max = new Text(container, SWT.BORDER);
	text_max.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	text_max.addListener(SWT.DefaultSelection, new Listener() {
	    public void handleEvent(Event e) {
//		hStartEndRangeWidget.setMax(Double.valueOf(text_max.getText()));
//		vStartEndRangeWidget.setMax(Double.valueOf(text_max.getText()));
		hStartEndRangeWidget.setRange(Double.valueOf(text_min.getText()), Double.valueOf(text_max.getText()));
		vStartEndRangeWidget.setRange(Double.valueOf(text_min.getText()), Double.valueOf(text_max.getText()));
	    }
	});

	Label lblSelectedmin = new Label(container, SWT.NONE);
	lblSelectedmin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	lblSelectedmin.setText("selectedMin:");

	Label lblSelectedmax = new Label(container, SWT.NONE);
	lblSelectedmax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	lblSelectedmax.setText("selectedMax:");

	text_selectedMin = new Text(container, SWT.BORDER);
	text_selectedMin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	text_selectedMin.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		hStartEndRangeWidget.setSelectedMin(Double.valueOf(text_selectedMin.getText()));
		vStartEndRangeWidget.setSelectedMin(Double.valueOf(text_selectedMin.getText()));
	    }
	});

	text_selectedMax = new Text(container, SWT.BORDER);
	text_selectedMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	text_selectedMax.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		hStartEndRangeWidget.setSelectedMax(Double.valueOf(text_selectedMax.getText()));
		vStartEndRangeWidget.setSelectedMax(Double.valueOf(text_selectedMax.getText()));
	    }
	});
	
	Button btnNewButton = new Button(container, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
		    hStartEndRangeWidget.resetRange();
		    vStartEndRangeWidget.resetRange();
		}
	});
	btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	btnNewButton.setText("reset");
	
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Event log:");
		new Label(container, SWT.NONE);
	
		textLog = new Text(container, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		textLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

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
     * Create the toolbar manager.
     * 
     * @return the toolbar manager
     */
    @Override
    protected ToolBarManager createToolBarManager(int style) {
	ToolBarManager toolBarManager = new ToolBarManager(style);
	return toolBarManager;
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
	    StartEndRangeWidgetTest window = new StartEndRangeWidgetTest();
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
	return new Point(470, 365);
    }
}
