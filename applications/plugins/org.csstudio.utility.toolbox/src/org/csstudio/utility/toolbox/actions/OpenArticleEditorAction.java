package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.framework.action.AbstractOpenEditorAction;
import org.csstudio.utility.toolbox.view.ArticleEditorPart;

public class OpenArticleEditorAction extends AbstractOpenEditorAction<Article> {

	public OpenArticleEditorAction() {
		super(ArticleEditorPart.ID, "Article", Article.class);
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}

}
