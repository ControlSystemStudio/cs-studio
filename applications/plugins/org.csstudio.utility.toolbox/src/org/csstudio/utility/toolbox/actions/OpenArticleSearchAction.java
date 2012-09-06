package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.framework.action.AbstractSearchEditorAction;
import org.csstudio.utility.toolbox.view.ArticleSearchEditorPart;

public class OpenArticleSearchAction extends AbstractSearchEditorAction<Article> {

	public OpenArticleSearchAction() {	
		super(ArticleSearchEditorPart.ID, "Search Article");
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
	
}
