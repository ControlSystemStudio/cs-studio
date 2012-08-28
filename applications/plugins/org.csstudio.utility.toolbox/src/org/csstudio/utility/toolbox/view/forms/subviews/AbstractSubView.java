package org.csstudio.utility.toolbox.view.forms.subviews;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.builder.Binder;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.view.forms.GenericEditorInputProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Inject;

public class AbstractSubView<T extends BindingEntity>  implements PropertyChangeListener{
	
	@Inject
	private GenericEditorInputProvider<T> genericEditorInputProvider;
	
	@Inject
	protected WidgetFactory<T> wf;
	
	@Inject
	private Binder<T> binder;
	
	private boolean initialized = false;
	
	private CrudController<Article> crudController;
	
	private Option<GenericEditorInput<T>> editorInput = new None<GenericEditorInput<T>>();

	private Option<GenericEditorInput<T>> lastEditorInput = new None<GenericEditorInput<T>>();

	public void init(CrudController<Article> crudController, T data) {
		this.crudController = crudController;
		if (lastEditorInput.hasValue()) {
			lastEditorInput.get().removePropertyChangeListener(this);
		}
		editorInput = new Some<GenericEditorInput<T>>(genericEditorInputProvider.get()); 					
		editorInput.get().init("", new Some<T>(data), new None<Func1Void<IStructuredSelection>>() , new None<IStructuredSelection>());
		editorInput.get().addPropertyChangeListener(this);
		lastEditorInput = new Some<GenericEditorInput<T>>(editorInput);
		CrudControllerImpostor<Article, T> impostor = new CrudControllerImpostor<Article, T>(crudController);	
		Some<CrudController<T>> crudImpostor = new Some<CrudController<T>>(impostor);
		binder.init(editorInput.get(), crudImpostor, false);
		wf.init(editorInput.get(), new Some<CrudController<T>>(impostor), false, binder);
		initialized = true;
	}
	
	protected Composite createComposite(Composite parent, String layoutCons, String columnCons, String rowCons) {
		Composite composite = new Composite(parent, SWT.BORDER);
		MigLayout layout = new MigLayout(layoutCons, columnCons, rowCons);
		composite.setLayout(layout);
		return composite;
	}
	
	public void updateModels() {
		if (initialized) {
			binder.updateModels();
		}
	}

	public void dispose() {
		if (editorInput.hasValue()) {
			editorInput.get().removePropertyChangeListener(this);
		}
	}
	
	public void markErrors() {
		//
	}
	
	public Option<GenericEditorInput<T>> getEditorInput() {
		return editorInput;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		crudController.setDirty(true);		
	}

}
