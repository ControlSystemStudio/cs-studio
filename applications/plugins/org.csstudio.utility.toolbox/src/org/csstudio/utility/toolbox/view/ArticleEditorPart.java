package org.csstudio.utility.toolbox.view;

import org.csstudio.utility.toolbox.actions.OpenArticleEditorAction;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.view.forms.ArticleGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class ArticleEditorPart extends AbstractCrudEditorPartTemplate<Article> implements CrudController<Article> {

	public static final String ID = "org.csstudio.utility.toolbox.view.ArticleEditorPart";

	@Inject
	private ArticleGuiForm articleGuiForm;

	@Inject
	private OpenArticleEditorAction openArticleEditorAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input, articleGuiForm);
		getEditorInput().addPropertyChangeListener(articleGuiForm);
		setEditorPartName("beschreibung");
	}

	@Override
	public void createPartControl(Composite composite) {
		articleGuiForm.createEditPart(composite, getEditorInput(), this);
		setFocusWidget(articleGuiForm.getFocusWidget());
	}

	@Override
	public void create() {
		openArticleEditorAction.runWith(new Article());
	}

	@Override
	public void dispose() {
		articleGuiForm.dispose();
		getEditorInput().removePropertyChangeListener(articleGuiForm);
		super.dispose();
	}

	public void setFocus() {
		super.setFocus();
	}

}
