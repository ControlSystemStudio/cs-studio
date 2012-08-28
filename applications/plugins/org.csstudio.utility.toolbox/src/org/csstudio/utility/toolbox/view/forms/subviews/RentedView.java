package org.csstudio.utility.toolbox.view.forms.subviews;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleRented;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.LogGroupService;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

import com.google.inject.Inject;

public class RentedView extends AbstractSubView<ArticleRented> {

	@Inject
	private LogUserService logUserService;

	@Inject
	private LogGroupService logGroupService;

	public Some<Composite> build(CrudController<Article> crudController, ArticleRented articleRented,
				TabFolder tabFolder) {

		init(crudController, articleRented);

		Composite composite = createComposite(tabFolder, "ins 10", "[80][250, fill]10[80][80, grow, fill]",
					"[][][][][]");

		wf.label(composite).text("Rent").add("id").titleStyle().build();

		wf.combo(composite, "name").label("User:").data(logUserService.findAll()).hint("wrap").build();

		wf.combo(composite, "groupName").label("Group:").data(logGroupService.findAll()).hint("wrap").build();

		wf.text(composite, "address").multiLine().label("Address:", "wrap").hint("spanx 7, h 90!, growx, wrap").build();

		wf.date(composite, "dateBack").label("Return Date:").hint("gaptop 5, wrap").build();

		wf.combo(composite, "rentedBy").label("Rented By:").data(logUserService.findAll()).hint("wrap").build();

		wf.date(composite, "rentDate").label("Rent Date:").hint("gaptop 5, wrap").build();

		return new Some<Composite>(composite);
	}

}
