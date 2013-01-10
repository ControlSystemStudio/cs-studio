package org.csstudio.utility.toolbox.view;

import org.csstudio.utility.toolbox.actions.OpenFirmaEditorAction;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.view.forms.FirmaGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class FirmaEditorPart extends AbstractCrudEditorPartTemplate<Firma> implements CrudController<Firma> {

	public static final String ID = "org.csstudio.utility.toolbox.view.FirmaEditorPart";
	
	@Inject
	private FirmaGuiForm firmaGuiForm;

	@Inject
	private OpenFirmaEditorAction openFirmaEditorAction;
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, firmaGuiForm);
		setEditorPartName("name");
	}

	@Override
	public void createPartControl(Composite composite) {
		firmaGuiForm.createEditPart(composite, getEditorInput(), this);
		setFocusWidget(firmaGuiForm.getFocusWidget());
	}

	@Override
	public void create() {
		openFirmaEditorAction.runWith(new Firma());
	}

	@Override
	public void copy() {
		if (!getEditorInput().hasData()) {
			throw new IllegalStateException("Data expected");
		}
		getEditorInput().processData(new Func1Void<Firma>() {			
			@Override
			public void apply(Firma firma) {
				Firma clone = firma.deepClone();
				openFirmaEditorAction.runWith(clone);
			}
		});
	}

}
