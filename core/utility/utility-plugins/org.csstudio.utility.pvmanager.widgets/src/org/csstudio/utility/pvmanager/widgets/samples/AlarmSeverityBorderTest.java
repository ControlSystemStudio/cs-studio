package org.csstudio.utility.pvmanager.widgets.samples;

import java.util.Random;

import org.csstudio.utility.pvmanager.widgets.AlarmSeverityBorder;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.epics.vtype.AlarmSeverity;

public class AlarmSeverityBorderTest extends ApplicationWindow {
	
	private Text txtNone;
	private Text txtMinor;
	private Text txtMajor;
	private Text txtInvalid;
	private Text txtDisconnected;	
	private Text dynamicText;
	private AlarmSeverityBorder dynamicAlarmSeverityBorder;

	/**
	 * Create the application window.
	 */
	public AlarmSeverityBorderTest() {
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
	protected Control createContents(final Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);
		container.setLayout(new GridLayout(1, false));
		
		dynamicAlarmSeverityBorder = new AlarmSeverityBorder(container, SWT.NONE);
		dynamicAlarmSeverityBorder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		dynamicText = new Text(dynamicAlarmSeverityBorder, SWT.BORDER);
		
		AlarmSeverityBorder alarmSeverityBorder_none = new AlarmSeverityBorder(container, SWT.NONE);
		alarmSeverityBorder_none.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtNone = new Text(alarmSeverityBorder_none, SWT.BORDER);
		txtNone.setText("None");
		txtNone.setBounds(10, 10, 78, 26);
		
		AlarmSeverityBorder alarmSeverityBorder_minor = new AlarmSeverityBorder(container, SWT.NONE);
		alarmSeverityBorder_minor.setAlarmSeverity(AlarmSeverity.MINOR);
		alarmSeverityBorder_minor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtMinor = new Text(alarmSeverityBorder_minor, SWT.BORDER);
		txtMinor.setText("Minor");
		
		AlarmSeverityBorder alarmSeverityBorder_major = new AlarmSeverityBorder(container, SWT.NONE);
		alarmSeverityBorder_major.setAlarmSeverity(AlarmSeverity.MAJOR);
		alarmSeverityBorder_major.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		txtMajor = new Text(alarmSeverityBorder_major, SWT.BORDER);
		txtMajor.setText("Major");
		
		AlarmSeverityBorder alarmSeverityBorder_invalid = new AlarmSeverityBorder(container, SWT.NONE);
		alarmSeverityBorder_invalid.setAlarmSeverity(AlarmSeverity.INVALID);
		alarmSeverityBorder_invalid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		txtInvalid = new Text(alarmSeverityBorder_invalid, SWT.BORDER);
		txtInvalid.setText("Invalid");
		
		AlarmSeverityBorder alarmSeverityBorder_disconnected = new AlarmSeverityBorder(container, SWT.NONE);
		alarmSeverityBorder_disconnected.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		alarmSeverityBorder_disconnected.setAlarmSeverity(AlarmSeverity.UNDEFINED);
		
		txtDisconnected = new Text(alarmSeverityBorder_disconnected, SWT.BORDER);
		txtDisconnected.setText("Disconnected");
		
		new Thread() {
			@Override
			public void run() {
				final Random rand = new Random();
				boolean done = false;
				while (!done) {
					parent.getDisplay().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							dynamicAlarmSeverityBorder.setAlarmSeverity(AlarmSeverity.values()[rand.nextInt(AlarmSeverity.values().length)]);
							dynamicText.setText(Double.toString(rand.nextDouble()));
							
						}
					});
					try {
						sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						done = true;
					}
				}
			}
		}.start();

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			AlarmSeverityBorderTest window = new AlarmSeverityBorderTest();
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
		return new Point(450, 300);
	}
}
