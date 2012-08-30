package org.csstudio.utility.toolbox.view;

import org.csstudio.utility.toolbox.actions.OpenOrderEditorAction;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.view.forms.OrderGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class OrderEditorPart extends AbstractCrudEditorPartTemplate<Order> implements CrudController<Order>{

	public static final String ID = "org.csstudio.utility.toolbox.view.OrderEditorPart";
	
	@Inject
	private OrderGuiForm orderGuiForm;

	@Inject
	private OpenOrderEditorAction openOrderEditorAction;
		
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, orderGuiForm);
		setEditorPartName("nummer");
	}

	@Override
	public void createPartControl(Composite composite) {
		orderGuiForm.createEditPart(composite, getEditorInput(), this);
		setFocusWidget(orderGuiForm.getFocusWidget());
	}

	@Override
	public void create() {
		openOrderEditorAction.runWith(new Order());
	}

	@Override
	public void copy() {
		if (!getEditorInput().hasData()) {
			throw new IllegalStateException("Data expected");
		}
		getEditorInput().processData(new Func1Void<Some<Order>>() {			
			@Override
			public void apply(Some<Order> order) {
				Order clone = order.get().deepClone();
				openOrderEditorAction.runWith(clone);
			}
		});
	}

}
