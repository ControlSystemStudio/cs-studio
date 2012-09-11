package org.csstudio.utility.toolbox.framework.action;

import org.csstudio.utility.toolbox.AppLogger;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class AbstractSearchEditorAction <T extends BindingEntity> extends Action {

	@Inject
	private Provider<IWorkbenchPage> pageProvider;

	@Inject
	private Provider<GenericEditorInput<T>> input;

	@Inject
	private AppLogger logger;
	
	private final String editorId;
	
	private final String title;
		
	public AbstractSearchEditorAction(String editorId, String title) {
		this.editorId = editorId;
		this.title = title;
		this.setToolTipText(title);
		this.setText(title);
	}

	public void run() {
		run(new None<Func1Void<IStructuredSelection>>());
	}

	public void goBack(GenericEditorInput<T> editorInput) {
		try {
			pageProvider.get().openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			logger.logError(e);
			throw new IllegalStateException(e);
		}
	}

	public void run(Option<Func1Void<IStructuredSelection>> goBack, IStructuredSelection selection) {
		try {
			GenericEditorInput<T> editorInput = input.get();
			editorInput.init(title, new None<T>(), goBack, new Some<IStructuredSelection>(selection));
			pageProvider.get().openEditor(editorInput, editorId);
		} catch (Exception e) {
			logger.logError(e);
			throw new IllegalStateException(e);	
		}
	}

	public void run(Option<Func1Void<IStructuredSelection>> goBack) {
		try {
			GenericEditorInput<T> editorInput = input.get();
			editorInput.init(title, new None<T>(), goBack, new None<IStructuredSelection>());
			pageProvider.get().openEditor(editorInput, editorId);
		} catch (Exception e) {
			logger.logError(e);
			throw new IllegalStateException(e);	
		}
	}
}
