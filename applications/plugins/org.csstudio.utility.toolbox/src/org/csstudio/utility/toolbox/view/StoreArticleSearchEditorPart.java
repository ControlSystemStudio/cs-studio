package org.csstudio.utility.toolbox.view;

import java.util.List;

import org.csstudio.utility.toolbox.actions.OpenStoreArticleEditorAction;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.controller.SearchController;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.framework.template.AbstractSearchEditorPartTemplate;
import org.csstudio.utility.toolbox.services.LagerArtikelService;
import org.csstudio.utility.toolbox.view.forms.StoreArticleGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class StoreArticleSearchEditorPart extends AbstractSearchEditorPartTemplate<LagerArtikel> implements SearchController<LagerArtikel> {

	public static final String ID = "org.csstudio.utility.toolbox.view.StoreArticleSearchEditorPart";

	@Inject
	private StoreArticleGuiForm storeArticleGuiForm;

	@Inject
	private LagerArtikelService lagerArtikelService;

	@Inject
	private OpenStoreArticleEditorAction openStoreArticleEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, storeArticleGuiForm);
		setPartName(getTitle());
	}

	@Override
	public void createPartControl(Composite composite) {
		storeArticleGuiForm.createSearchPart(composite, getEditorInput(), this);
		setFocusWidget(storeArticleGuiForm.getFocusWidget());
	}

	@Override
	public void executeSearch(List<SearchTerm> searchTerms) {
		List<LagerArtikel> stores = lagerArtikelService.find(searchTerms, new OrderBy("id"));
		setSearchPartName(stores.size());		
		storeArticleGuiForm.createSearchResultTableView(getTableViewProvider(), stores,
					Property.createList("id", "beschreibung", "actualBestand"));	
	}

	@Override
	public void create() {
		openStoreArticleEditorAction.runWith(new LagerArtikel());
	}

	@Override
	public void openRow(LagerArtikel storeArticle) {
		openStoreArticleEditorAction.runWith(storeArticle);		
	}

}