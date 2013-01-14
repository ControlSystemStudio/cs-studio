package org.csstudio.utility.toolbox.view.forms;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionSearchAction;
import org.csstudio.utility.toolbox.actions.OpenStoreArticleEditorAction;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.LagerService;
import org.csstudio.utility.toolbox.services.StoreLookupDataService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class StoreArticleGuiFormActionHandler {

	private GenericEditorInput<LagerArtikel> editorInput;

	private WidgetFactory<LagerArtikel> wf;
	
	private boolean isSearchMode;
	
	@Inject
	private LagerService lagerService;

	@Inject
	private StoreLookupDataService storeLookupDataService;

	@Inject
	private OpenArticleDescriptionSearchAction openArticleDescriptionSearchAction;
	
	@Inject
	private OpenStoreArticleEditorAction openStoreArticleEditorAction;

	private Option<ArticleDescription> selectedArticleDescription = new None<ArticleDescription>();
	
	public void init(boolean isSearchMode, GenericEditorInput<LagerArtikel> editorInput, WidgetFactory<LagerArtikel> wf) {
		Validate.notNull(editorInput, "editorInput must not be null");
		Validate.notNull(wf, "wf must not be null");
		this.isSearchMode = isSearchMode;
		this.editorInput = editorInput;
		this.wf = wf;
	}
	
	public void selectArticle(final Text articleDescriptionText) {
		openArticleDescriptionSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
			@Override
			public void apply(IStructuredSelection selection) {
				if (selection != null) {
					ArticleDescription articleDescription = (ArticleDescription) selection.getFirstElement();
					selectedArticleDescription = new Some<ArticleDescription>(articleDescription);
					articleDescriptionText.setText(articleDescription.getBeschreibung());
					goBackToCallingForm();
				}
			}
		}));
	}
	
	public void lagerNameSelected() {
		String lagerName = wf.getText(P("lagerName"));
		wf.setInput(P("ort"), storeLookupDataService.findAllLocations(lagerName));
		wf.setInput(P("fach"), storeLookupDataService.findAllShelves(lagerName));
		wf.setInput(P("box"), storeLookupDataService.findAllBox(lagerName));
		if (!isSearchMode) {
			Option<Lager> lager = lagerService.findByName(lagerName);
			if ((lager.hasValue() && StringUtils.isEmpty(wf.getText(P("id"))))) {
				wf.setText(P("id"), lager.get().getLagerPrefix());
			}
		}
	}
	
	public Option<ArticleDescription> getSelectedArticleDescription() {
		return selectedArticleDescription;
	}

	private void goBackToCallingForm() {
		openStoreArticleEditorAction.goBack(editorInput);
	}
	
}
