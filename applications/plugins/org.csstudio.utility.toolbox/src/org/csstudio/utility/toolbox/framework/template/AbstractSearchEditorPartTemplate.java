package org.csstudio.utility.toolbox.framework.template;

import javax.persistence.EntityManager;

import org.csstudio.utility.toolbox.framework.GenericTableViewProvider;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public abstract class AbstractSearchEditorPartTemplate<T extends BindingEntity> extends EditorPart {

	@Inject
	private EntityManager em;

	private GenericEditorInput<T> editorInput;

	@Inject
	private GenericTableViewProvider<T> tableViewProvider;

	private AbstractGuiFormTemplate<T> formTemplate;

	private Widget focusWidget = null;

	@SuppressWarnings("unchecked")
	public void init(IEditorSite site, IEditorInput input, AbstractGuiFormTemplate<T> formTemplate)
				throws PartInitException {
		this.editorInput = (GenericEditorInput<T>) input;
		this.formTemplate = formTemplate;
		setSite(site);
		setInput(input);
	}

	protected void setSearchPartName(int size) {
		this.setPartName(getTitle() + " (Count: " + size + ")");
	}

	public String getTitle() {
		if (this.editorInput == null) {
			return "";
		}
		return editorInput.getTitle();
	}

	@Override
	@Transactional
	public void doSave(IProgressMonitor monitor) {
		throw new IllegalStateException("doSave not supported");
	}

	@Override
	public void doSaveAs() {
		throw new IllegalStateException("doSaveAs not supported");
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
		WidgetFactory.setFocusedWidgetFactory(formTemplate.getWidgetFactory());
		if ((focusWidget != null) && (focusWidget instanceof Text)) {
			Text text = (Text) focusWidget;
			text.setFocus();
		}
	}

	public EntityManager getEm() {
		return em;
	}

	public GenericEditorInput<T> getEditorInput() {
		return editorInput;
	}

	public GenericTableViewProvider<T> getTableViewProvider() {
		return tableViewProvider;
	}

	public void setFocusWidget(Widget widget) {
		this.focusWidget = widget;
	}

}
