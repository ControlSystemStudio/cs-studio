package org.csstudio.nams.configurator.editor.stackparts;

import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.configurator.modelmapping.IConfigurationModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@Deprecated
public abstract class AbstractStackPart<ConfigurationType extends IConfigurationBean> {

	protected final int NUM_COLUMNS;
	protected final int MIN_WIDTH = 300;
	private Class<ConfigurationType> _associatedBean;
	private DirtyFlagProvider _dirtyFlagProvider;
	protected ConfigurationType bean;
	protected ConfigurationType beanClone;
	protected IConfigurationModel model;
	
	protected PropertyChangeListener listener;
	protected Composite main;

	
	public AbstractStackPart(DirtyFlagProvider flagProvider,
			Class<ConfigurationType> associatedBean, int numColumns) {
		_dirtyFlagProvider = flagProvider;
		_associatedBean = associatedBean;
		NUM_COLUMNS = numColumns;
	}

	protected DirtyFlagProvider getDirtyFlagProvider() {
		return _dirtyFlagProvider;
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

	public Control getMainControl()
	{
		return main;
	}

	public Class<ConfigurationType> getAssociatedBean() {
		return _associatedBean;
	}

	public boolean isDirty() {
		if (this.bean != null && this.beanClone != null) {
			return !this.bean.equals(this.beanClone);
		} else
			return false;
	};

	@SuppressWarnings("unchecked")
	public void save() { 
		// welche gruppe hat user gewählt?
		// TODO may bla
		// String group = this._groupComboEntry.getItem(this._groupComboEntry
		// .getSelectionIndex());

		// speicher Änderungen im lokalen Model
		IConfigurationBean updatedBean = this.model.save(this.beanClone);

		// copy clone state to original bean
		this.bean = (ConfigurationType) updatedBean;

		// create new clone
		this.beanClone = (ConfigurationType) this.bean.getClone();
	}

	@SuppressWarnings("unchecked")
	public void setInput(IConfigurationBean input, IConfigurationModel model) {
		this.model = model;
		this.bean = (ConfigurationType) input;
		this.beanClone = (ConfigurationType) ((ConfigurationType) input)
				.getClone();
		initDataBinding();
	}

	protected abstract void initDataBinding();

	public void setPropertyChangedListener(
			PropertyChangeListener listener){
		this.listener = listener;
		beanClone.addPropertyChangeListener(listener);;
	}

	public abstract void setFocus();

}
