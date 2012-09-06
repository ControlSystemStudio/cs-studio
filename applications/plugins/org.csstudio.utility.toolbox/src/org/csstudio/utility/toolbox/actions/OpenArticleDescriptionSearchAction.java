package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.framework.action.AbstractSearchEditorAction;
import org.csstudio.utility.toolbox.view.ArticleDescriptionSearchEditorPart;

public class OpenArticleDescriptionSearchAction extends AbstractSearchEditorAction<ArticleDescription> {

	public OpenArticleDescriptionSearchAction() {
		super(ArticleDescriptionSearchEditorPart.ID, "Search Article Description");
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}

}
