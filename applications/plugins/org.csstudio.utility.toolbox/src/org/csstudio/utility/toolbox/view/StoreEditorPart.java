package org.csstudio.utility.toolbox.view;

import org.csstudio.utility.toolbox.actions.OpenStoreEditorAction;
import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.view.forms.StoreGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class StoreEditorPart extends AbstractCrudEditorPartTemplate<Lager> implements CrudController<Lager> {

	public static final String ID = "org.csstudio.utility.toolbox.view.StoreEditorPart";

	@Inject
	private StoreGuiForm storeGuiForm;

	@Inject
	private OpenStoreEditorAction openStoreEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, storeGuiForm);
		setEditorPartName("name");
	}

	@Override
	public void createPartControl(Composite composite) {
		storeGuiForm.createEditPart(composite, getEditorInput(), this);
		setFocusWidget(storeGuiForm.getFocusWidget());
	}

	@Override
	public void create() {
		openStoreEditorAction.runWith(new Lager());
	}

	@Override
	public void copy() {
		if (!getEditorInput().hasData()) {
			throw new IllegalStateException("Data expected");
		}
		getEditorInput().processData(new Func1Void<Lager>() {
			@Override
			public void apply(Lager lager) {
				Lager clone = lager.deepClone();
				openStoreEditorAction.runWith(clone);
			}
		});
	}

}