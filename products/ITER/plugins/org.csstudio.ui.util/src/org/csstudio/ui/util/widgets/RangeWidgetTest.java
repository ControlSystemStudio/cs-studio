package org.csstudio.ui.util.widgets;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class RangeWidgetTest extends ApplicationWindow {

	/**
	 * Create the application window.
	 */
	public RangeWidgetTest() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());
		
		final RangeWidget composite = new RangeWidget(container, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		
		final Spinner spinner = new Spinner(container, SWT.BORDER);
		FormData fd_spinner = new FormData();
		fd_spinner.right = new FormAttachment(0, 148);
		fd_spinner.top = new FormAttachment(0);
		fd_spinner.left = new FormAttachment(0, 70);
		spinner.setLayoutData(fd_spinner);
		spinner.setMaximum(1000000);
		spinner.setDigits(1);
		
		Button button = new Button(container, SWT.NONE);
		fd_composite.bottom = new FormAttachment(button, 0, SWT.BOTTOM);
		FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(100);
		fd_button.right = new FormAttachment(100);
		button.setLayoutData(fd_button);
		button.setText("New Button");
		
		final Button btnInvertDirection = new Button(container, SWT.CHECK);
		FormData fd_btnInvertDirection = new FormData();
		fd_btnInvertDirection.top = new FormAttachment(spinner, 3);
		fd_btnInvertDirection.left = new FormAttachment(composite, 6);
		btnInvertDirection.setLayoutData(fd_btnInvertDirection);
		btnInvertDirection.setText("Invert direction");
		btnInvertDirection.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnInvertDirection.getSelection()) {
					composite.setStartPosition(SWT.DOWN);
				} else {
					composite.setStartPosition(SWT.UP);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				composite.setDistancePerPx(spinner.getSelection() / 10.0);
			}
		});
		
		composite.addRangeListener(new RangeListener() {
			
			@Override
			public void rangeChanged() {
				if (spinner.getSelection() != (int) (composite.getDistancePerPx() * 10)) {
					spinner.setSelection((int) (composite.getDistancePerPx() * 10));
				}
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
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			RangeWidgetTest window = new RangeWidgetTest();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
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
