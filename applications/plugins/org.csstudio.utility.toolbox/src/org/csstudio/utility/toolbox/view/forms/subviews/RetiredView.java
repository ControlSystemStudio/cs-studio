package org.csstudio.utility.toolbox.view.forms.subviews;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleRetired;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

import com.google.inject.Inject;

public class RetiredView extends AbstractSubView<ArticleRetired>{

	@Inject
	private LogUserService logUserService;

	public Some<Composite> build(CrudController<Article> crudController, ArticleRetired articleRetired, TabFolder tabFolder) {
		
		init(crudController, articleRetired);
				
		Composite composite = createComposite(tabFolder, "ins 10", "[80][250, fill]10[80][80, grow, fill]", "[][][][][]");
		
		wf.label(composite).text("Article retired").add("id").titleStyle().build();

		wf.combo(composite, "ausgemustertDurch").label("User:").data(logUserService.findAll()).hint("wrap").build();
	
		wf.date(composite, "ausgemustertAm").label("Date:").hint("gaptop 5, wrap").build();

		wf.text(composite, "begruendung").multiLine().label("Reason:", "wrap").hint("spanx 7, h 90!, growx, wrap")
			.build();

		return new Some<Composite>(composite);
	}

}
