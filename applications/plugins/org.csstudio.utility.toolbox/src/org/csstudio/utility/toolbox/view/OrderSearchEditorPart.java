package org.csstudio.utility.toolbox.view;

import java.util.List;

import org.csstudio.utility.toolbox.actions.OpenOrderEditorAction;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.framework.controller.SearchController;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.framework.template.AbstractSearchEditorPartTemplate;
import org.csstudio.utility.toolbox.services.OrderService;
import org.csstudio.utility.toolbox.view.forms.OrderGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class OrderSearchEditorPart extends AbstractSearchEditorPartTemplate<Order> implements SearchController<Order> {

	public static final String ID = "org.csstudio.utility.toolbox.view.OrderSearchEditorPart";

	@Inject
	private OrderGuiForm orderGuiForm;

	@Inject
	private OrderService orderService;

	@Inject
	private OpenOrderEditorAction openOrderEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, orderGuiForm);
		setPartName(getTitle());
	}

	@Override
	public void createPartControl(Composite composite) {
		orderGuiForm.createSearchPart(composite, getEditorInput(), this);
		setFocusWidget(orderGuiForm.getFocusWidget());
	}

	@Override
	public void executeSearch(List<SearchTerm> searchTerms) {
		List<Order> orders = orderService.find(searchTerms, new OrderBy("nummer"));
		setSearchPartName(orders.size());		
		orderGuiForm.createSearchResultTableView(getTableViewProvider(), orders,
					Property.createList("baNummer", "firmaName", "beschreibung"));	
	}

	@Override
	public void create() {
		openOrderEditorAction.runWith(new Order());
	}

	public void openRow(Order order) {
		openOrderEditorAction.runWith(order);
	}

}
