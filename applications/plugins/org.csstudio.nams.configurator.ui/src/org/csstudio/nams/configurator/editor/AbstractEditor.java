package org.csstudio.nams.configurator.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractEditor<ConfigurationType extends AbstractConfigurationBean<ConfigurationType>>
		extends EditorPart implements PropertyChangeListener,
		ConfigurationBeanServiceListener {

	protected static ConfigurationBeanService configurationBeanService;
	protected final int NUM_COLUMNS;
	protected final int MIN_WIDTH = 300;
	protected ConfigurationType bean;
	protected ConfigurationType beanClone;

	protected PropertyChangeListener listener;
	private String superTitle;

	public AbstractEditor() {
		NUM_COLUMNS = getNumColumns();
		configurationBeanService.addConfigurationBeanServiceListener(this);
	}

	protected abstract int getNumColumns();

	@SuppressWarnings("unchecked")
	@Override
	public void doSave(IProgressMonitor monitor) {
		// speicher Änderungen mit dem Service
		ConfigurationType resultBean = configurationBeanService.save(this.beanClone);
		if (this.bean != resultBean) {
			this.bean = resultBean;
			ConfigurationEditorInput cInput = (ConfigurationEditorInput) getEditorInput();
			cInput.setBean(this.bean);

			this.beanClone.removePropertyChangeListener(this);
			
			// create new clone
			this.beanClone = (ConfigurationType) this.bean.getClone();
			this.beanClone.addPropertyChangeListener(this);
			
			initDataBinding();
		}
		
		setPartName(this.bean.getDisplayName() + " - " + superTitle);
		
		firePropertyChange(EditorPart.PROP_DIRTY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		ConfigurationEditorInput cInput = (ConfigurationEditorInput) input;

		this.bean = (ConfigurationType) cInput.getBean();
		
		this.beanClone = (ConfigurationType) this.bean.getClone();
		this.beanClone.addPropertyChangeListener(this);

		superTitle = super.getTitle();
		setPartName(this.bean.getDisplayName() + " - " + superTitle);
		
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
		textWidget.setText("");
		return textWidget;
	}

	protected ComboViewer createComboEntry(Composite parent, String labeltext,
			boolean editable, String[] contents) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		ComboViewer comboWidget = new ComboViewer(parent, SWT.BORDER);
		comboWidget.add(contents);
		// comboWidget.setEditable(editable);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		comboWidget.getCombo().setLayoutData(gridData);
		return comboWidget;
	}

//	class NotifyingListViewer 
	
	protected ListViewer createListEntry(Composite parent, String labeltext,
			boolean editable) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		ListViewer listWidget = new ListViewer(parent, SWT.BORDER){
			
		};
		ArrayContentProvider cp = null;
		
		// listWidget.setEditable(editable);
//		listWidget.setInput(new WritableList());
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		listWidget.getList().setLayoutData(gridData);
		return listWidget;
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

	protected Button createButtonEntry(Composite parent, String labeltext,
			boolean editable) {
		new Composite(parent, SWT.NONE);
		Button buttonWidget = new Button(parent, SWT.PUSH);
		buttonWidget.setText(labeltext);
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

	// private void resetDatabinding() {
	// this.bean.clearPropertyChangeListeners();
	// this.beanClone.clearPropertyChangeListeners();
	// }

	protected abstract void initDataBinding();

	public void setPropertyChangedListener(PropertyChangeListener listener) {
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

	protected static String[] array2StringArray(Object[] objects) {
		String[] result = new String[objects.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = objects[i].toString();
		}
		return result;
	}

	@Override
	public String getTitle() {
		return super.getTitle();
	}
	
	public void onBeanDeleted(IConfigurationBean bean) {
		if (bean.getClass().equals(this.bean.getClass())
				&& bean.getID() == this.bean.getID()) {
			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					getSite().getPage()
							.closeEditor(AbstractEditor.this, true);
				}
			});
		}
	}

	public void onBeanInsert(IConfigurationBean bean) {
	}

	public void onBeanUpdate(IConfigurationBean bean) {
	}
}
