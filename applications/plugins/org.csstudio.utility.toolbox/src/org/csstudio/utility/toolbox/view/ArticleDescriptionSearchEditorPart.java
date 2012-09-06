package org.csstudio.utility.toolbox.view;

import java.util.List;

import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionEditorAction;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.framework.controller.SearchController;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.framework.template.AbstractSearchEditorPartTemplate;
import org.csstudio.utility.toolbox.services.ArticleDescriptionService;
import org.csstudio.utility.toolbox.view.forms.ArticleDescriptionGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class ArticleDescriptionSearchEditorPart extends AbstractSearchEditorPartTemplate<ArticleDescription> implements SearchController<ArticleDescription> {

	public static final String ID = "org.csstudio.utility.toolbox.view.ArticleDescriptionSearchEditorPart";

	@Inject
	private ArticleDescriptionGuiForm articleDescriptionGuiForm;

	@Inject
	private ArticleDescriptionService articleDescriptionService;

	@Inject
	private OpenArticleDescriptionEditorAction openArticleDescriptionEditorAction;
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, articleDescriptionGuiForm);
		setPartName(getTitle());
	}

	@Override
	public void createPartControl(Composite composite) {
		articleDescriptionGuiForm.createSearchPart(composite, getEditorInput(), this);
		setFocusWidget(articleDescriptionGuiForm.getFocusWidget());
	}

	@Override
	public void executeSearch(List<SearchTerm> searchTerms) {
		List<ArticleDescription> descriptions = articleDescriptionService.find(searchTerms, new OrderBy("beschreibung"));
		setSearchPartName(descriptions.size());
		articleDescriptionGuiForm.createSearchResultTableView(getTableViewProvider(), descriptions,
					Property.createList("beschreibung", "produktTyp", "lieferantName"));		
	}

	@Override
	public void create() {
		openArticleDescriptionEditorAction.runWith(new ArticleDescription());
	}

	public void openRow(ArticleDescription articleDescription) {
		openArticleDescriptionEditorAction.runWith(articleDescription);
	}

}
