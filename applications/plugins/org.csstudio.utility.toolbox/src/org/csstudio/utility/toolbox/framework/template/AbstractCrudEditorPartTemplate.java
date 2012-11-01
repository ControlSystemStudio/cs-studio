package org.csstudio.utility.toolbox.framework.template;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.editor.DataChangeSupport;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractCrudEditorPartTemplate<T extends BindingEntity> extends EditorPart implements
			PropertyChangeListener, CrudController<T>, DataChangeSupport {

	// @Inject
	private GenericEditorInput<T> editorInput;

	@Inject
	private Provider<IWorkbenchPage> pageProvider;

	private Set<ConstraintViolation<T>> lastValidationResult;

	private AbstractGuiFormTemplate<T> formTemplate;

	private volatile boolean isDirty = false;

	private Widget focusWidget = null;

	@SuppressWarnings("unchecked")
	public void init(IEditorSite site, IEditorInput input, AbstractGuiFormTemplate<T> formTemplate)
				throws PartInitException {

		Validate.notNull(formTemplate, "formTemplate must not be null");

		this.editorInput = (GenericEditorInput<T>) input;
		this.editorInput.addPropertyChangeListener(this);
		this.formTemplate = formTemplate;

		setSite(site);
		setInput(input);
	}

	// Editor can be closed without saving the data. So make sure that the
	// Object in memory has the same data as in the database.
	@Override
	public void dispose() {
		if (this.editorInput != null) {
			this.editorInput.removePropertyChangeListener(this);
			this.editorInput.refreshData();
		}
		super.dispose();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		isDirty = true;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	protected void setEditorPartName(String propertyName) {
		Validate.notNull(propertyName, "propertyName must not be null");
		String value = editorInput.getDataPropertyValueByName(propertyName);
		if (value != null) {
			if (value.length() > 15) {
				value = value.substring(0, 15);
			}
			this.setPartName(getTitle() + " [" + value + "]");
		} else {
			this.setPartName(getTitle() + " [NEW]");
		}
	}

	public String getTitle() {
		if (this.editorInput == null) {
			return "";
		}
		return editorInput.getTitle();
	}

	public GenericEditorInput<T> getEditorInput() {
		return editorInput;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (isDirty && !isValid()) {
			monitor.setCanceled(true);
			formTemplate.markErrors(lastValidationResult);
			IWorkbenchPage page = pageProvider.get();
			page.bringToTop(this);
			return;
		}
		save();
	}

	public boolean isSaveOnCloseNeeded() {
		return !Environment.isTestMode();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean value) {
		isDirty = value;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);			
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void delete() {
		throw new IllegalStateException("delete must be implemented in subclass.");
	}

	@Override
	public void copy() {
		throw new IllegalStateException("copy must be implemented in subclass");
	}

	public boolean save() {
		if (!isDirty) {
			return true;
		}
		editorInput.saveData();
		isDirty = false;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
		formTemplate.resetErrorMarkers();
		return editorInput.isSaveSuccessful();
	}

	public boolean isValid() {
		lastValidationResult = editorInput.validateData();
		return lastValidationResult.isEmpty();
	}

	public void markErrors() {
		formTemplate.markErrors(lastValidationResult);
	}

	public void setFocusWidget(Widget widget) {
		this.focusWidget = widget;
	}

	@Override
	public void setFocus() {
		WidgetFactory.setFocusedWidgetFactory(formTemplate.getWidgetFactory());
		if ((focusWidget != null) && (focusWidget instanceof Text)) {
			Text text = (Text) focusWidget;
			text.setFocus();
		}
	}

}
