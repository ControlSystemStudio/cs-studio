package org.csstudio.nams.configurator.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.beans.AbstractObservableBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractEditor<ConfigurationType extends AbstractObservableBean> extends EditorPart implements PropertyChangeListener{

	protected static ConfigurationBeanService configurationBeanService;
	protected final int NUM_COLUMNS;
	protected final int MIN_WIDTH = 300;
	protected ConfigurationType bean;
	protected ConfigurationType beanClone;
	
	protected PropertyChangeListener listener;
	
	public AbstractEditor() {
		NUM_COLUMNS = getNumColumns();
	}
	
	protected abstract int getNumColumns();

	@SuppressWarnings("unchecked")
	@Override
	public void doSave(IProgressMonitor monitor) {
		// welche gruppe hat user gewählt?
		// TODO das muss in das bean nicht hier her
		// String group = this._groupComboEntry.getItem(this._groupComboEntry
		// .getSelectionIndex());

		// speicher Änderungen mit dem Service
		ConfigurationType updatedBean = configurationBeanService.save(this.beanClone);

		this.bean.removePropertyChangeListener(this);
		this.beanClone.removePropertyChangeListener(this);

		// copy clone state to original bean
		this.bean = updatedBean;
		this.bean.addPropertyChangeListener(this);
		
		// create new clone
		this.beanClone = (ConfigurationType) this.bean.getClone();
		this.beanClone.addPropertyChangeListener(this);
		
		initDataBinding();
		firePropertyChange(EditorPart.PROP_DIRTY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		ConfigurationEditorInput cInput = (ConfigurationEditorInput) input;
		
		this.bean = (ConfigurationType) cInput.getBean();;
		this.beanClone = (ConfigurationType) this.bean.getClone();
		this.beanClone.addPropertyChangeListener(this);
		
		doInit(site, input);
	}

	protected abstract void doInit(IEditorSite site, IEditorInput input);

	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public abstract void createPartControl(Composite parent);

	@Override
	public void doSaveAs() {
		
	}

	public void editConfiguration(ConfigurationType bean) {
		this.bean = bean;
	}

	protected Text createTextEntry(Composite parent, String labeltext,
			boolean editable) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		Text textWidget = new Text(parent, SWT.BORDER);
		textWidget.setEditable(editable);
		if (!editable) {
			textWidget.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_INFO_BACKGROUND));
		}
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		textWidget.setLayoutData(gridData);
		return textWidget;
	}

	protected Combo createComboEntry(Composite parent, String labeltext,
			boolean editable) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		Combo comboWidget = new Combo(parent, SWT.BORDER);
		// comboWidget.setEditable(editable);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		comboWidget.setLayoutData(gridData);
		return comboWidget;
	}

	protected Button createCheckBoxEntry(Composite parent, String labeltext,
			boolean editable) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		Button buttonWidget = new Button(parent, SWT.CHECK);
		buttonWidget.setEnabled(editable);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		buttonWidget.setLayoutData(gridData);
		return buttonWidget;
	}

	protected void addSeparator(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				NUM_COLUMNS, 1));
	}
	
	protected Text createDescriptionTextEntry(Composite parent, String labeltext) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		Text textWidget = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		gridData.heightHint = 80;
		textWidget.setLayoutData(gridData);
		textWidget.setTextLimit(256);
		return textWidget;
	}

	@Override
	public boolean isDirty() {
		if (this.bean != null && this.beanClone != null) {
			return !this.bean.equals(this.beanClone);
		} else
			return false;
	};

	private void resetDatabinding() {
		this.bean.clearPropertyChangeListeners();
		this.beanClone.clearPropertyChangeListeners();
	}

	protected abstract void initDataBinding();

	public void setPropertyChangedListener(
			PropertyChangeListener listener){
		this.listener = listener;
		beanClone.addPropertyChangeListener(listener);
	}

	@Override
	public abstract void setFocus();

	public void propertyChange(PropertyChangeEvent evt) {
		this.firePropertyChange(EditorPart.PROP_DIRTY);
	}
	
	public static void staticInject(ConfigurationBeanService service) {
		configurationBeanService = service;
	}
}
