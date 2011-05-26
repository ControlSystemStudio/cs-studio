/**
 * 
 */
package org.csstudio.utility.dal.epics.infobar;

import java.util.Properties;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.epics.css.dal.epics.EPICSPlug;

/**
 * @author jhatje
 * 
 */
public class EpicsInformationBar extends WorkbenchWindowControlContribution {

	private static final String FORMAT = "%-50s";
	private static final String TRENNER = "\t: ";
	private static final String USE_JNI = "EPICSPlug.use_jni";

	/**
	 * Creates a new toolbar.
	 */
	public EpicsInformationBar() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new toolbar with an id.
	 * 
	 * @param id
	 *            the id.
	 */
	public EpicsInformationBar(final String id) {
		super(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createControl(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Button button = new Button(composite, SWT.NONE);
		Font font = button.getFont();
		font.getFontData()[0].setHeight(6);
        button.setCursor(new Cursor(null, SWT.CURSOR_HELP));
//		button.setFont(font)
//		GridData layoutData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).indent(0,-5).create();
		GridData layoutData = GridDataFactory.swtDefaults().hint(SWT.DEFAULT,22).align(SWT.FILL, SWT.TOP).indent(0,-2).create();
        button.setLayoutData(layoutData);
		if (EpicsPlugin.getDefault().usePureJava()) {
			button.setText("Pure Java");
		} else {
			button.setText("JNI");
		}
		
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showInfoDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				showInfoDialog();
			}
		});

		return composite;
	}

	private void showInfoDialog() {
		StringBuilder sb = new StringBuilder();
		EPICSPlug instance = null;
		readEpicsPref(sb);
		MessageDialog.openInformation(null, "Epics Info", sb.toString());
	}

	private void readEpicsPref(StringBuilder sb) {
		sb.append("Epics libs preferences:\n");
		if (EpicsPlugin.getDefault().usePureJava()) {
			sb.append("Channel Access libs: PURE JAVA");
		} else {
			sb.append("Channel Access libs: JNI");
		}
	}

	private void readEpicsPlugContex(StringBuilder sb, EPICSPlug instance) {
		try {
			instance = (EPICSPlug) EPICSPlug.getInstance(new Properties());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sb.append("Epics plug context\n");
			appendIntProperty(EPICSPlug.DEFAULT_MONITOR_MASK, sb, instance);
			appendDoubleProperty(EPICSPlug.DEFAULT_PENDIO_TIMEOUT, sb,
					instance);
			appendStringProperty(EPICSPlug.DEFAULT_PROPERTY_IMPL_CLASS, sb,
					instance);
			appendBoolProperty(
					EPICSPlug.INITIALIZE_CHARACTERISTICS_ON_CONNECT, sb,
					instance);
			appendLongProperty(EPICSPlug.JNI_FLUSH_TIMER_DELAY, sb,
					instance);
			// appendIntProperty(EPICSPlug.PARAMETER_MONITOR_MASK,sb,
			// instance);
			appendIntProperty(EPICSPlug.PROPERTY_CORE_THREADS, sb, instance);
			appendIntProperty(EPICSPlug.PROPERTY_MAX_THREADS, sb, instance);
			appendBoolProperty(EPICSPlug.PROPERTY_USE_COMMON_EXECUTOR, sb,
					instance);
			appendBoolProperty(EPICSPlug.USE_JNI, sb, instance);
		} catch (Exception e) {
			sb.append(e.getMessage());
		}
	}

	private void appendIntProperty(String propKey, StringBuilder sb,
			EPICSPlug instance) {
		sb.append(String.format(FORMAT, propKey)).append(TRENNER);
		Integer integerProperty = new Integer(System.getProperty(propKey, null));
		if (integerProperty == null) {
			sb.append("NULL");
		} else {
			sb.append(integerProperty);
		}
		sb.append("\n");
	}

	private void appendLongProperty(String propKey, StringBuilder sb,
			EPICSPlug instance) {
		sb.append(String.format(FORMAT, propKey)).append(TRENNER);
		Long property = new Long(System.getProperty(propKey, null));
		if (property == null) {
			sb.append("NULL");
		} else {
			sb.append(property);
		}
		sb.append("\n");
	}

	private void appendDoubleProperty(String propKey, StringBuilder sb,
			EPICSPlug instance) {
		sb.append(String.format(FORMAT, propKey)).append(TRENNER);
		Double property = new Double(System.getProperty(propKey, null));
		if (property == null) {
			sb.append("NULL");
		} else {
			sb.append(property);
		}
		sb.append("\n");
	}

	private void appendBoolProperty(String propKey, StringBuilder sb,
			EPICSPlug instance) {
		sb.append(String.format(FORMAT, propKey)).append(TRENNER);
		Boolean property = new Boolean(System.getProperty(propKey, null));
		if (property == null) {
			sb.append("NULL");
		} else {
			sb.append(property);
		}
		sb.append("\n");
	}

	private void appendStringProperty(String propKey, StringBuilder sb,
			EPICSPlug instance) {
		sb.append(String.format(FORMAT, propKey)).append(TRENNER);
		String property = new String(System.getProperty(propKey, null));
		if (property == null) {
			sb.append("NULL");
		} else {
			sb.append(property);
		}
		sb.append("\n");
	}

	/**
	 * Returns <code>true</code>.
	 * 
	 * @return <code>true</code>.
	 */
	@Override
	public boolean isDynamic() {
		// The login information toolbar must be marked as dynamic so that its
		// contribution manager will recreate the control (by calling the
		// createControl method) whenever it updates.
		return true;
	}
}
