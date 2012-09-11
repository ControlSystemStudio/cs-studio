package org.csstudio.utility.toolbox.view;

import org.csstudio.utility.toolbox.actions.OpenStoreArticleEditorAction;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.view.forms.StoreArticleGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class StoreArticleEditorPart  extends AbstractCrudEditorPartTemplate<LagerArtikel> implements CrudController<LagerArtikel> {

	public static final String ID = "org.csstudio.utility.toolbox.view.StoreArticleEditorPart";

	@Inject
	private StoreArticleGuiForm storeArticleGuiForm;

	@Inject
	private OpenStoreArticleEditorAction openStoreArticleEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, storeArticleGuiForm);
		setEditorPartName("id");
	}

	@Override
	public void createPartControl(Composite composite) {
		storeArticleGuiForm.createEditPart(composite, getEditorInput(), this);
		setFocusWidget(storeArticleGuiForm.getFocusWidget());
	}

	@Override
	public void create() {
		openStoreArticleEditorAction.runWith(new LagerArtikel());
	}

}
