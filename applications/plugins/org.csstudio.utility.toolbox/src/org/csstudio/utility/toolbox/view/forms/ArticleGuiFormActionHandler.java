package org.csstudio.utility.toolbox.view.forms;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.actions.OpenArticleEditorAction;
import org.csstudio.utility.toolbox.actions.OpenArticleSearchAction;
import org.csstudio.utility.toolbox.actions.OpenFirmaSearchAction;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.ArticleService;
import org.csstudio.utility.toolbox.view.forms.subviews.InstalledView;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;

import com.google.inject.Inject;

public class ArticleGuiFormActionHandler {

	@Inject
	private Environment env;

	@Inject
	private OpenFirmaSearchAction openFirmaSearchAction;

	@Inject
	private OpenArticleEditorAction openArticleEditorAction;

	@Inject
	private OpenArticleSearchAction openArticleSearchAction;

	@Inject
	private ArticleService articleService;
	
	private boolean isSearchMode;

	private GenericEditorInput<Article> editorInput;

	private WidgetFactory<Article> wf;

	private Option<CrudController<Article>> crudController;

	private IStructuredSelection selectedArticleSelection;
		
	public void init(boolean isSearchMode, GenericEditorInput<Article> editorInput, WidgetFactory<Article> wf,
				Option<CrudController<Article>> crudController, DataBindingContext ctx) {

		Validate.notNull(editorInput, "editorInput must not be null");
		Validate.notNull(wf, "wf must not be null");

		this.isSearchMode = isSearchMode;
		this.editorInput = editorInput;
		this.wf = wf;
		this.crudController = crudController;
	}

	public void selectFirma(final Combo company) {
		openFirmaSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
			@Override
			public void apply(IStructuredSelection selection) {
				if (selection != null) {
					Firma firma = (Firma) selection.getFirstElement();
					company.setText(firma.getName());
					goBackToCallingForm();
				}
			}
		}));
	}

	public void lookupArticle(final InstalledView installedView) {
		openArticleSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
			@Override
			public void apply(IStructuredSelection selection) {
				if (selection != null) {
					installedView.updateAfterArticleSelection(selection);
					goBackToCallingForm();
				}
			}
		}), selectedArticleSelection);
	}
	
	static class SelectLastItem implements Func1Void<Widget> {
		public void apply(Widget widget) {
			if (widget instanceof Combo) {
				Combo combo = (Combo)widget;
				combo.select(combo.getItemCount()-1);
			}
		}		
	}

	public Article addNewArticle(WritableList articlesInGroup) {
		
		Article firstArticle = (Article)articlesInGroup.get(0);
		
		Article article = firstArticle.createPrototype(firstArticle.getGruppeArtikel());
						
		article.setId(articleService.createId());
		
		article.setIndex(articlesInGroup.size() + 1);
		articlesInGroup.add(article);
		
		wf.doCommand(ArticleGuiForm.ITEM_COMBO, new SelectLastItem());		
		wf.replaceBindings(article);
				
		crudController.get().setDirty(true);
		
		return article;
	}

	public Article addRemainingArticles(WritableList articlesInGroup, int count) {		
		for (int i=0; i < count; i++) {
			addNewArticle(articlesInGroup);
		}
		return (Article)articlesInGroup.get(articlesInGroup.size() - 1);
	}
	
	public void assignId() {
		wf.setText(P("internId"), env.getActiveLogGroup() + articleService.getNextInternId().toString());
	}

	public void bulkAssignId(WritableList articlesInGroup) {
		for (Object object: articlesInGroup) {
			Article article = (Article)object;
			if (StringUtils.isEmpty(article.getInternId())) {
				article.setInternId(env.getActiveLogGroup() + articleService.getNextInternId().toString());
			}
		}
	}

	public boolean changeArticleSelectionAndUpdateBinding(IStructuredSelection selectedItem) {
		selectedArticleSelection = selectedItem;
		wf.replaceBindings((Article) selectedItem.getFirstElement());
		return true;
	}
	
	public void setSelectedArticleSelection(IStructuredSelection selectedArticleSelection) {
		this.selectedArticleSelection = selectedArticleSelection;
	}
	
	private void goBackToCallingForm() {
		if (isSearchMode) {
			openArticleSearchAction.goBack(editorInput);
		} else {
			openArticleEditorAction.goBack(editorInput);
		}
	}

}
