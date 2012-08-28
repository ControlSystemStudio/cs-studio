package org.csstudio.utility.toolbox.view.forms;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionEditorAction;
import org.csstudio.utility.toolbox.actions.OpenFirmaSearchAction;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.framework.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.template.AbstractGuiFormTemplate;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.FirmaService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class ArticleDescriptionGuiForm extends AbstractGuiFormTemplate<ArticleDescription> {

	@Inject
	private FirmaService firmaService;

	@Inject
	private OpenFirmaSearchAction openFirmaSearchAction;

	@Inject
	private OpenArticleDescriptionEditorAction openArticleDescriptionEditorAction;

	@Override
	protected void createEditComposite(Composite composite) {
		createPart(composite);
	}

	@Override
	protected void createSearchComposite(Composite composite) {
		createPart(composite);
	}

	@Override
	protected TableViewer createSearchResultComposite(Composite composite) {

		String[] titles = { "Note", "Type", "Company" };
		int[] bounds = { 33, 33, 33 };

		setSearchResultTableViewer(createTableViewer(composite, SEARCH_RESULT_TABLE_VIEWER, titles, bounds));

		final Table table = getSearchResultTableViewer().getTable();

		table.setLayoutData("spanx 7, ay top, growy, growx, height 310:310:1250, width 500:800:2000, wrap");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return getSearchResultTableViewer();

	}

	private void createPart(Composite composite) {

		String rowLayout;

		if (isSearchMode()) {
			rowLayout = "[][][]12[][][][]12[][][]12[][][][grow, fill]";
		} else {
			rowLayout = "[][][]12[][][][]12[][][]12[fill][]";
		}

		MigLayout ml = new MigLayout("ins 15, gapy 4", "[80][250]80[80][250][60][10, grow, fill]", rowLayout);

		composite.setLayout(ml);

		wf.label(composite).text(getEditorInput().getTitle()).titleStyle().build();

		Text note = wf.text(composite, "beschreibung").multiLine().label("Note:", "wrap")
					.hint("spanx 7, h 90!, growx, wrap").build();

		setFocusWidget(note);

		// pre initialize the article note with the given selection
		Option<IStructuredSelection> selection = getEditorInput().getStructuredSelection();
		if (selection.hasValue()) {
			OrderPos orderPos = (OrderPos) selection.get().getFirstElement();
			Article article = orderPos.getArticle();
			if (article != null) {
				ArticleDescription ad = article.getArticleDescription();
				if (ad != null) {
					String text = ad.getBeschreibung();
					if (text != null) {
						note.setText(text);
					}
				}
			}
		}

		wf.text(composite, "produktTyp").label("Product Type:").hint("growx").build();

		// Line 2
		final Combo company = wf.combo(composite, "lieferantName").label("Company:").hint("growx")
					.data(firmaService.findAll()).build();

		wf.button(composite, "lookupCompany").withSearchImage().hint("wrap").listener(new SimpleSelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFirmaSearchAction.run(Some.some(new Func1Void<IStructuredSelection>() {
					@Override
					public void apply(IStructuredSelection selection) {
						if (selection != null) {
							Firma firma = (Firma) selection.getFirstElement();
							company.setText(firma.getName());
							openArticleDescriptionEditorAction.goBack(getEditorInput());
						}
					}
				}));
			}
		}).build();

		wf.text(composite, "dfgSchluessel").label("DFG:").hint("growx").build();

		wf.text(composite, "lieferantBestNr").label("Order Nr:").hint("growx, wrap").build();

		wf.text(composite, "lieferantStueckpreis").label("Price:").hint("growx, split 2").build();

		wf.label(composite).text("EUR").hint("wrap").build();

		wf.text(composite, "htmlLink").label("HTML-Link:").hint("spanx 7, growx,  wrap").build();

	}

}
