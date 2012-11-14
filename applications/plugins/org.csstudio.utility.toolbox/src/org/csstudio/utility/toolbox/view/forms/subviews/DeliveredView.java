package org.csstudio.utility.toolbox.view.forms.subviews;


import java.util.Date;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleDelivered;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.provider.SimpleDateFormatProvider;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class DeliveredView extends AbstractSubView<ArticleDelivered>{

	@Inject
	private LogUserService logUserService;

	@Inject
	SimpleDateFormatProvider sd;
	
	public Some<Composite> build(CrudController<Article> crudController, ArticleDelivered articleDelivered, TabFolder tabFolder) {
		
		init(crudController, articleDelivered);
				
		Composite composite = createComposite(tabFolder, "ins 10", "[80][250, fill]10[80][80, grow, fill]", "[][][][][]");
		
		wf.label(composite).text("Delivered").add("id").titleStyle().build();

		wf.combo(composite, "eingegangenDurch").label("Received by:").data(logUserService.findAll()).hint("wrap").build();
	
		Text text = wf.date(composite, "eingegangenAm").label("Return Date:").hint("gaptop 5, wrap").build();

		if (articleDelivered.isNew()) {
			if (articleDelivered.getEingegangenAm() == null) {
				text.setText(sd.get().format(new Date()));
			}
		}
		
		return new Some<Composite>(composite);
	}

}
