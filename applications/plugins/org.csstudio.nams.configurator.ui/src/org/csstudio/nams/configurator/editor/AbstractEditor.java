
package org.csstudio.nams.configurator.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.service.ConfigurationBeanService;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceListener;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * Abstrakte Oberklasse aller Editoren von Configurations-Elementen des NAMS.
 * 
 * @param <ConfigurationType>
 *            Der Input-Typ, den der konkrete Editor bearbeitet.
 */
public abstract class AbstractEditor<ConfigurationType extends AbstractConfigurationBean<ConfigurationType>>
		extends EditorPart implements PropertyChangeListener,
		ConfigurationBeanServiceListener {

	protected static ConfigurationBeanService configurationBeanService;

	public static void staticInject(final ConfigurationBeanService service) {
		AbstractEditor.configurationBeanService = service;
	}

	/**
	 * The configuration bean service all editors of current configuration
	 * working on. DO NOT chache this instance!
	 * 
	 * @return The bean service, not null.
	 */
	protected static ConfigurationBeanService getConfigurationBeanService() {
		final ConfigurationBeanService result = AbstractEditor.configurationBeanService;

		Contract.ensureResultNotNull(result);
		return result;
	}

	protected final int NUM_COLUMNS;

	protected final int MIN_WIDTH = 300;

	/**
	 * The original bean input wich is referenced in the bean service and change
	 * its stae if configuration is reloaded.
	 */
	private ConfigurationType originalEditorInput;

	/**
	 * The working copy the editor will work on. This instance is a private
	 * instance of the editor and not referenced by the bean service. It is used
	 * for save operations with the bean service and is used to fund the
	 * {@link #originalEditorInput original bean} in bean service which is to be
	 * updated.
	 */
	private ConfigurationType workingCopyOfEditorInput;

	private String superTitle;

	public AbstractEditor() {
		this.NUM_COLUMNS = this.getNumColumns();
		AbstractEditor.configurationBeanService
				.addConfigurationBeanServiceListener(this);
	}

	@Override
	public abstract void createPartControl(Composite parent);

	public <T extends Enum<?>> ComboViewer createTitledComboForEnumValues(
			final Composite parent, final String labeltext, final T[] contents,
			final IConfigurationBean bean, final String targetProperty) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		ComboViewer comboWidget;
		try {
			comboWidget = EditorUIUtils.createComboViewerForEnumValues(parent,
					contents, bean, targetProperty);
		} catch (final IntrospectionException e) {
			throw new RuntimeException("failed to create combo viewer", e); //$NON-NLS-1$
		}

		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, this.NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		comboWidget.getCombo().setLayoutData(gridData);
		return comboWidget;
	}

	@Override
	public void dispose() {
		AbstractEditor.configurationBeanService
				.removeConfigurationBeanServiceListener(this);
		super.dispose();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	public void doSave(final IProgressMonitor monitor) {
		// speicher Änderungen mit dem Service
		try {
			final ConfigurationType resultBean = AbstractEditor.configurationBeanService
					.save(this.workingCopyOfEditorInput);
			if (this.originalEditorInput != resultBean) {
				this.originalEditorInput = resultBean;
				final ConfigurationEditorInput cInput = (ConfigurationEditorInput) this
						.getEditorInput();
				cInput.setBean(this.originalEditorInput);
				this.workingCopyOfEditorInput.setID(this.originalEditorInput
						.getID()); // Die
				// Bean
				// darf
				// nicht
				// neu
				// geklont
				// werden!!!
				// Sonst
				// geht
				// das
				// binding
				// der
				// viewer
				// verloren!
			}
			this.afterSafe();
		} catch (final Throwable e) {
			final MessageBox messageBox = new MessageBox(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell());
			messageBox.setText(Messages.AbstractEditor_saveFailed);
			Throwable cause = e.getCause();
			String message = e.getMessage();
			while (cause != null) {
				message += "\n" + cause.getMessage(); //$NON-NLS-1$
				cause = cause.getCause();
			}
			messageBox.setMessage(message);
			messageBox.open();
		}
		this.setPartName(this.originalEditorInput.getDisplayName() + " - " //$NON-NLS-1$
				+ this.superTitle);

		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
	    // Nothing to do
	}

	@Override
	public String getTitle() {
		return super.getTitle();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		final ConfigurationEditorInput cInput = (ConfigurationEditorInput) input;

		this.originalEditorInput = (ConfigurationType) cInput.getBean();

		this.workingCopyOfEditorInput = this.originalEditorInput.getClone();
		this.workingCopyOfEditorInput.addPropertyChangeListener(this);

		this.superTitle = super.getTitle();
		this.setPartName(this.originalEditorInput.getDisplayName() + " - " //$NON-NLS-1$
				+ this.superTitle);

		this.doInit(site, input);
	}

	@Override
	public boolean isDirty() {
		if ((this.originalEditorInput != null)
				&& (this.workingCopyOfEditorInput != null)) {
			return !this.originalEditorInput
					.equals(this.workingCopyOfEditorInput);
		} else {
			return false;
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
    public void onBeanDeleted(final IConfigurationBean bean) {
		if (bean.getClass().equals(this.originalEditorInput.getClass())
				&& (bean.getID() == this.originalEditorInput.getID())) {
			this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					AbstractEditor.this.getSite().getPage().closeEditor(
							AbstractEditor.this, true);
				}
			});
		}
	}

	@Override
    public void onBeanInsert(final IConfigurationBean bean) {
	    // Nothing to do
	}

	@Override
    public void onBeanUpdate(final IConfigurationBean bean) {
        // Nothing to do
	}

	@Override
    public void onConfigurationReload() {
        // Nothing to do
	}

	@Override
    public void propertyChange(final PropertyChangeEvent evt) {
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public abstract void setFocus();

	protected void addSeparator(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				this.NUM_COLUMNS, 1));
	}

	/**
	 * Zum aufräumen nach dem speichern.
	 */
	protected void afterSafe() {
		// per default nothing to do
	}

	protected Button createButtonEntry(final Composite parent,
			final String labeltext, final boolean editable, final int gridWidth) {
		// Composite composite = new Composite(parent, SWT.NONE);
		final Button buttonWidget = new Button(parent, SWT.PUSH);
		buttonWidget.setText(labeltext);
		buttonWidget.setEnabled(editable);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, gridWidth, 1);
		// composite.setLayoutData(gridData);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		buttonWidget.setLayoutData(gridData);
		return buttonWidget;
	}

	protected Button createCheckBoxEntry(final Composite parent,
			final String labeltext, final boolean editable) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final Button buttonWidget = new Button(parent, SWT.CHECK);
		buttonWidget.setEnabled(editable);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, this.NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		buttonWidget.setLayoutData(gridData);
		return buttonWidget;
	}

	protected ComboViewer createComboEntry(final Composite parent,
			final String labeltext, boolean editable, final String[] contents) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		int style = SWT.BORDER;
		if (!editable) {
			style |= SWT.READ_ONLY;
		}
		final ComboViewer comboWidget = new ComboViewer(parent, style);
		comboWidget.add(contents);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, this.NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		comboWidget.getCombo().setLayoutData(gridData);
		return comboWidget;
	}

	protected Text createDescriptionTextEntry(final Composite parent,
			final String labeltext) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		final Text textWidget = new Text(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, this.NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		gridData.heightHint = 80;
		textWidget.setLayoutData(gridData);
		textWidget.setTextLimit(256);
		return textWidget;
	}

	protected ListViewer createListEntry(final Composite parent,
			final String labeltext, final boolean editable) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final ListViewer listWidget = new ListViewer(parent, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true,
				this.NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		// gridData.minimumHeight = 120;
		listWidget.getList().setLayoutData(gridData);
		return listWidget;
	}

	protected Text createTextEntry(final Composite parent,
			final String labeltext, boolean editable) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setText(labeltext);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final Text textWidget = new Text(parent, SWT.BORDER);
		textWidget.setEditable(editable);
		if (!editable) {
			textWidget.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_INFO_BACKGROUND));
		}
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false,
				false, this.NUM_COLUMNS - 1, 1);
		gridData.minimumWidth = this.MIN_WIDTH;
		gridData.widthHint = this.MIN_WIDTH;
		textWidget.setLayoutData(gridData);
		textWidget.setText(""); //$NON-NLS-1$
		return textWidget;
	}

	protected abstract void doInit(IEditorSite site, IEditorInput input);

	protected abstract int getNumColumns();

	/**
	 * Returns the original editor input. This bean is also referenced by the
	 * editors bean service and will change its state if configuration is
	 * reloaded!
	 * 
	 * @return The editor input bean, not null.
	 */
	protected ConfigurationType getOriginalEditorInput() {
		final ConfigurationType result = this.originalEditorInput;

		Contract.ensureResultNotNull(result);
		return result;
	}

	/**
	 * Returns the current working copy of the editor. This instance is a
	 * private instance of the editor and not referenced by the bean service. It
	 * is used for save operations with the bean service and is used to fund the
	 * {@link #originalEditorInput original bean} in bean service which is to be
	 * updated.
	 * 
	 * @return The editor input working copy, not null.
	 */
	protected ConfigurationType getWorkingCopyOfEditorInput() {
		final ConfigurationType result = this.workingCopyOfEditorInput;

		Contract.ensureResultNotNull(result);
		return result;
	}

	protected abstract void initDataBinding();
}
