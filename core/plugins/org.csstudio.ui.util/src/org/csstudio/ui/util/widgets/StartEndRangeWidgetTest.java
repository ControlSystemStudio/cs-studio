package org.csstudio.ui.util.widgets;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

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
		container.setLayout(new GridLayout(2, false));

		final StartEndRangeWidget composite = new StartEndRangeWidget(
				container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				2, 1));
		composite.addRangeListener(new RangeListener() {

			@Override
			public void rangeChanged() {
				text.setText(String.valueOf(composite.getMin()));
				text_1.setText(String.valueOf(composite.getMax()));
				text_2.setText(String.valueOf(composite.getSelectedMin()));
				text_3.setText(String.valueOf(composite.getSelectedMax()));
			}
		});

		Label lblMin = new Label(container, SWT.NONE);
		lblMin.setText("min:");

		Label lblMax = new Label(container, SWT.NONE);
		lblMax.setText("max:");

		text = new Text(container, SWT.BORDER);
		text.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				composite.setMin(Double.valueOf(text.getText()));
			}
		});

		text_1 = new Text(container, SWT.BORDER);
		text_1.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				composite.setMax(Double.valueOf(text_1.getText()));
			}
		});

		Label lblSelectedmin = new Label(container, SWT.NONE);
		lblSelectedmin.setText("selectedMin:");

		Label lblSelectedmax = new Label(container, SWT.NONE);
		lblSelectedmax.setText("selectedMax:");

		text_2 = new Text(container, SWT.BORDER);
		text_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				composite.setSelectedMin(Double.valueOf(text_2.getText()));
			}
		});

		text_3 = new Text(container, SWT.BORDER);
		text_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				composite.setSelectedMax(Double.valueOf(text_3.getText()));
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
