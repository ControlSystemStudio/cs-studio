package org.csstudio.nams.configurator.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceListener;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractEditor<ConfigurationType extends AbstractConfigurationBean<ConfigurationType>>
		extends EditorPart implements PropertyChangeListener,
		ConfigurationBeanServiceListener {

	protected static ConfigurationBeanService configurationBeanService;
	protected final int NUM_COLUMNS;
	protected final int MIN_WIDTH = 300;
	protected ConfigurationType bean;
	protected ConfigurationType beanClone;

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
		try {
			ConfigurationType resultBean = configurationBeanService
					.save(this.beanClone);
			if (this.bean != resultBean) {
				this.bean = resultBean;
				ConfigurationEditorInput cInput = (ConfigurationEditorInput) getEditorInput();
				cInput.setBean(this.bean);
				this.beanClone.setID(this.bean.getID()); // Die Bean darf nicht neu geklont werden!!! Sonst geht das binding der viewer verloren!
			}
		} catch (InconsistentConfigurationException e) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell());
			messageBox.setText(e.getClass().toString());
			messageBox.setMessage(e.getMessage());
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

	protected ComboViewer createRubrikCombo(Composite parent, String labeltext,
			boolean editable, String[] contents) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		int style = SWT.BORDER;
		if (!editable) {
			style |= SWT.READ_ONLY;
		}
		ComboViewer comboWidget = new ComboViewer(parent, style);
		comboWidget.add(contents);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		comboWidget.getCombo().setLayoutData(gridData);
		return comboWidget;
	}
	
	public <T extends Enum<?>> ComboViewer createTitledComboForEnumValues(Composite parent, String labeltext, T[] contents, IConfigurationBean bean, String targetProperty) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		ComboViewer comboWidget;
		try {
			comboWidget = EditorUIUtils.createComboViewerForEnumValues(parent, contents, bean, targetProperty);
		} catch (IntrospectionException e) {
			throw new RuntimeException("failed to create combo viewer", e);
		}
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false,
				NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = MIN_WIDTH;
		gridData.widthHint = MIN_WIDTH;
		comboWidget.getCombo().setLayoutData(gridData);
		return comboWidget;
	}

	protected ListViewer createListEntry(Composite parent, String labeltext,
			boolean editable) {
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		ListViewer listWidget = new ListViewer(parent, SWT.BORDER) {

		};
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
		Text textWidget = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL| SWT.WRAP);
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

	protected abstract void initDataBinding();

	@Override
	public abstract void setFocus();

	public void propertyChange(PropertyChangeEvent evt) {
		this.firePropertyChange(EditorPart.PROP_DIRTY);
	}

	public static void staticInject(ConfigurationBeanService service) {
		configurationBeanService = service;
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
					getSite().getPage().closeEditor(AbstractEditor.this, true);
				}
			});
		}
	}

	public void onBeanInsert(IConfigurationBean bean) {
	}

	public void onBeanUpdate(IConfigurationBean bean) {
	}
}
