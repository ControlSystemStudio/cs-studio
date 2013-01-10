package org.csstudio.channel.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractConfigurationDialog<Widget extends Composite & ConfigurableWidget,
ConfigurationComposite extends AbstractConfigurationComposite> extends Dialog {

	protected Shell dialogShell;
	private Widget widget;
	private ConfigurationComposite configurationComposite;
	
	protected AbstractConfigurationDialog(Widget control, int style, String title) {
		super(control.getShell(), style);
		setText(title);
		this.widget = control;
	}
	
	public void open(SelectionEvent evt) {
		Point point = evt.display.map((Control) evt.getSource(), null, new Point(evt.x, evt.y));
		open(point.x, point.y);
	}
	
	public void open() {
		createContents();
		dialogShell.open();
		dialogShell.layout();
		Rectangle widgetBounds = widget.getBounds();
		Point origin = widget.toDisplay(0, 0);
		Rectangle dialogBounds = dialogShell.getBounds();
		moveTo(origin.x + ( widgetBounds.width - dialogBounds.width) / 2,
				origin.y + ( widgetBounds.height - dialogBounds.height) / 2);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public void open(int x, int y) {
		createContents();
		dialogShell.open();
		dialogShell.layout();
		moveTo(x, y);
	}
	
	private void moveTo(int x, int y) {
		dialogShell.setBounds(Math.min(x, dialogShell.getDisplay().getClientArea().width - dialogShell.getBounds().width),
				Math.min(y, dialogShell.getDisplay().getClientArea().height - dialogShell.getBounds().height),
				dialogShell.getBounds().width, dialogShell.getBounds().height);
	}
	
	private List<String> initialProperties = new ArrayList<String>();
	private Map<String, Object> initialValues = new HashMap<String, Object>();
	
	/**
	 * Use this to save the value for the properties that need to be
	 * reset if cancel is pressed.
	 * 
	 * @param name name of the property
	 * @param value value of the property
	 */
	protected final void addInitialValues(String name, Object value) {
		initialProperties.add(name);
		initialValues.put(name, value);
	}
	
	public Map<String, Object> getInitialValues() {
		return initialValues;
	}
	
	public List<String> getInitialProperties() {
		return initialProperties;
	}
	
	protected abstract void onPropertyChange(PropertyChangeEvent evt);
	
	/**
	 * Populate the configurationComposite based on the initial values.
	 * <p>
	 * TODO: should be automated by introspection
	 */
	protected abstract void populateInitialValues();
	
	protected abstract ConfigurationComposite createConfigurationComposite(Shell shell);

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		dialogShell = new Shell(getParent(), getStyle());
		dialogShell.setSize(550, 450);
		dialogShell.setText(getText());
		dialogShell.setLayout(new FormLayout());
		
		configurationComposite = createConfigurationComposite(dialogShell);
		FormData fd_propertyListSelectionWidget = new FormData();
		fd_propertyListSelectionWidget.left = new FormAttachment(0);
		fd_propertyListSelectionWidget.right = new FormAttachment(0, 544);
		fd_propertyListSelectionWidget.top = new FormAttachment(0);
		configurationComposite.setLayoutData(fd_propertyListSelectionWidget);
		
		Button btnCancel = new Button(dialogShell, SWT.NONE);
		fd_propertyListSelectionWidget.bottom = new FormAttachment(btnCancel, -6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateInitialValues();
				getWidget().configurationDialogClosed();
				dialogShell.close();
			}
		});
		
		Button btnApply = new Button(dialogShell, SWT.NONE);
		FormData fd_btnApply = new FormData();
		fd_btnApply.bottom = new FormAttachment(btnCancel, 0, SWT.BOTTOM);
		fd_btnApply.right = new FormAttachment(btnCancel, -6);
		btnApply.setLayoutData(fd_btnApply);
		btnApply.setText("Apply");
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWidget().configurationDialogClosed();
				dialogShell.close();
			}
		});
		
		populateInitialValues();
		configurationComposite.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onPropertyChange(evt);
			}
		});
	}
	
	public Widget getWidget() {
		return widget;
	}
	
	public ConfigurationComposite getConfigurationComposite() {
		return configurationComposite;
	}
	
}
