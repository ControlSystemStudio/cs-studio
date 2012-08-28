package org.csstudio.utility.toolbox.view;

import java.util.List;

import org.csstudio.utility.toolbox.actions.OpenFirmaEditorAction;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.controller.SearchController;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.framework.template.AbstractSearchEditorPartTemplate;
import org.csstudio.utility.toolbox.services.FirmaService;
import org.csstudio.utility.toolbox.view.forms.FirmaGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class FirmaSearchEditorPart extends AbstractSearchEditorPartTemplate<Firma> implements SearchController<Firma> {

	public static final String ID = "org.csstudio.utility.toolbox.view.FirmaSearchEditorPart";
	
	@Inject
	private FirmaGuiForm firmaGuiForm;

	@Inject
	private FirmaService firmaService;

	@Inject
	private OpenFirmaEditorAction openFirmaEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, firmaGuiForm);
		setPartName(getTitle());
	}

	@Override
	public void createPartControl(Composite composite) {
		firmaGuiForm.createSearchPart(composite, getEditorInput(), this);
		setFocusWidget(firmaGuiForm.getFocusWidget());
	}

	@Override
	public void executeSearch(List<SearchTerm> searchTerms) {
		List<Firma> companies = firmaService.find(searchTerms, new OrderBy("name"));		
		setSearchPartName(companies.size());
		firmaGuiForm.createSearchResultTableView(getTableViewProvider(), companies,
					Property.createList("name","nameLang","strasse","postleitzahl","stadt"));
	}

	@Override
	public void create() {
		openFirmaEditorAction.runWith(new Firma());
	}

	public void openRow(Firma firma) {
		openFirmaEditorAction.runWith(firma);
	}

}
