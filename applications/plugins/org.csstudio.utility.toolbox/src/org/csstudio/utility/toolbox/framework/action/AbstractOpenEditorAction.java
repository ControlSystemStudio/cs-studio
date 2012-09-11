package org.csstudio.utility.toolbox.framework.action;

import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.AppLogger;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Some;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractOpenEditorAction<T extends BindingEntity> extends Action {

	@Inject
	private Provider<IWorkbenchPage> pageProvider;

	@Inject
	private EditorInputProvider<T> input;

	@Inject
	private AppLogger logger;

	private final String editorId;

	private final String title;

	private final Class<T> clazz;
	
	public AbstractOpenEditorAction(String editorId, String title, Class<T> clazz) {
		this.editorId = editorId;
		this.title = title;
		this.clazz = clazz;
		this.setToolTipText("Create " + title);
		this.setText("Create " + title);
	}

	public void goBack(GenericEditorInput<T> editorInput) {
		try {
			pageProvider.get().openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			logger.logError(e);
			throw new IllegalStateException(e);
		}
	}

	public void run() {
		try {  
			runWith(clazz.newInstance());
		} catch (Exception e) {
			logger.logError(e);
			throw new IllegalStateException(e);
		}
	}
	
	public void runWith(T object) {
		
		Validate.notNull(object, "Object must not be null");

		IWorkbenchPage page = pageProvider.get();

		GenericEditorInput<T> editorInput = input.get();
		editorInput.init(title, new Some<T>(object), new None<Func1Void<IStructuredSelection>>(), new None<IStructuredSelection>());

		try {
			page.openEditor(editorInput, editorId);
		} catch (Exception e) {
			logger.logError(e);
			throw new IllegalStateException(e);
		}

	}
	
}
