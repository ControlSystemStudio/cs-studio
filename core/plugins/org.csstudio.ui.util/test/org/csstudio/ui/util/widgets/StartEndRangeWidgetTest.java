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

public class StartEndRangeWidgetTest extends ApplicationWindow {
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;

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
		hStartEndRangeWidget.setBounds(0, 0, 442, 20);
		hStartEndRangeWidget.addRangeListener(new RangeListener() {

			@Override
			public void rangeChanged() {
				text.setText(String.valueOf(hStartEndRangeWidget.getMin()));
				text_1.setText(String.valueOf(hStartEndRangeWidget.getMax()));
				text_2.setText(String.valueOf(hStartEndRangeWidget
						.getSelectedMin()));
				text_3.setText(String.valueOf(hStartEndRangeWidget
						.getSelectedMax()));
			}
		});

		final StartEndRangeWidget vStartEndRangeWidget = new StartEndRangeWidget(
				container, SWT.NONE);
		GridData gd_vStartEndRangeWidget = new GridData(SWT.LEFT, SWT.FILL, false,
				true, 1, 4);
		gd_vStartEndRangeWidget.widthHint = 20;
		vStartEndRangeWidget.setLayoutData(gd_vStartEndRangeWidget);
		vStartEndRangeWidget.setBounds(0, 0, 20, 260);
		vStartEndRangeWidget.setOrientation(ORIENTATION.VERTICAL);
		vStartEndRangeWidget.addRangeListener(new RangeListener() {

			@Override
			public void rangeChanged() {
				text.setText(String.valueOf(vStartEndRangeWidget.getMin()));
				text_1.setText(String.valueOf(vStartEndRangeWidget.getMax()));
				text_2.setText(String.valueOf(vStartEndRangeWidget
						.getSelectedMin()));
				text_3.setText(String.valueOf(vStartEndRangeWidget
						.getSelectedMax()));
			}
		});

		Label lblMin = new Label(container, SWT.NONE);
		lblMin.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		lblMin.setText("min:");

		Label lblMax = new Label(container, SWT.NONE);
		lblMax.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		lblMax.setText("max:");

		text = new Text(container, SWT.BORDER);
		text.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				hStartEndRangeWidget.setMin(Double.valueOf(text.getText()));
				vStartEndRangeWidget.setMin(Double.valueOf(text.getText()));
			}
		});

		text_1 = new Text(container, SWT.BORDER);
		text_1.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				hStartEndRangeWidget.setMax(Double.valueOf(text_1.getText()));
				vStartEndRangeWidget.setMax(Double.valueOf(text_1.getText()));
			}
		});

		Label lblSelectedmin = new Label(container, SWT.NONE);
		lblSelectedmin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblSelectedmin.setText("selectedMin:");

		Label lblSelectedmax = new Label(container, SWT.NONE);
		lblSelectedmax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		lblSelectedmax.setText("selectedMax:");

		text_2 = new Text(container, SWT.BORDER);
		text_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				hStartEndRangeWidget.setSelectedMin(Double.valueOf(text_2
						.getText()));
				vStartEndRangeWidget.setSelectedMin(Double.valueOf(text_2
						.getText()));
			}
		});

		text_3 = new Text(container, SWT.BORDER);
		text_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				hStartEndRangeWidget.setSelectedMax(Double.valueOf(text_3
						.getText()));
				vStartEndRangeWidget.setSelectedMax(Double.valueOf(text_3
						.getText()));
			}
		});

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
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
