package org.csstudio.utility.toolbox.actions;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.framework.action.AbstractOpenEditorAction;
import org.csstudio.utility.toolbox.view.ArticleDescriptionEditorPart;

public class OpenArticleDescriptionEditorAction extends AbstractOpenEditorAction<ArticleDescription> {

	public OpenArticleDescriptionEditorAction() {
		super(ArticleDescriptionEditorPart.ID, "Article Description", ArticleDescription.class);
		this.setImageDescriptor(ToolboxPlugin.getImageDescriptor("icons/view_bottom.png"));
	}
}
