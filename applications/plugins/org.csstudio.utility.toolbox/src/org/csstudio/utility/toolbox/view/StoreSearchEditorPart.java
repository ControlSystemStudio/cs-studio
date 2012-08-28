package org.csstudio.utility.toolbox.view;

import java.util.List;

import org.csstudio.utility.toolbox.actions.OpenStoreEditorAction;
import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.framework.controller.SearchController;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.framework.template.AbstractSearchEditorPartTemplate;
import org.csstudio.utility.toolbox.services.LagerService;
import org.csstudio.utility.toolbox.view.forms.StoreGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class StoreSearchEditorPart extends AbstractSearchEditorPartTemplate<Lager> implements SearchController<Lager> {

	public static final String ID = "org.csstudio.utility.toolbox.view.StoreSearchEditorPart";

	@Inject
	private StoreGuiForm storeGuiForm;

	@Inject
	private LagerService lagerService;

	@Inject
	private OpenStoreEditorAction openStoreEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, storeGuiForm);
		setPartName(getTitle());
	}

	@Override
	public void createPartControl(Composite composite) {
		storeGuiForm.createSearchPart(composite, getEditorInput(), this);
		setFocusWidget(storeGuiForm.getFocusWidget());
	}

	@Override
	public void executeSearch(List<SearchTerm> searchTerms) {
		List<Lager> stores = lagerService.find(searchTerms, new OrderBy("name"));
		setSearchPartName(stores.size());		
		storeGuiForm.createSearchResultTableView(getTableViewProvider(), stores,
					Property.createList("name", "responsiblePerson", "inGebaeude", "inRaum"));	
	}

	@Override
	public void create() {
		openStoreEditorAction.runWith(new Lager());
	}

	@Override
	public void openRow(Lager store) {
		openStoreEditorAction.runWith(store);		
	}

}
