package org.csstudio.utility.toolbox.view.forms;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.template.AbstractGuiFormTemplate;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class FirmaGuiForm extends AbstractGuiFormTemplate<Firma> {

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

		String[] titles = { "Name", "Full name", "Street", "Zip", "City" };
		int[] bounds = { 20, 30, 20, 10, 20 };

		setSearchResultTableViewer(createTableViewer(composite, SEARCH_RESULT_TABLE_VIEWER, titles,
					bounds));

		final Table table = getSearchResultTableViewer().getTable();

		table.setLayoutData("spanx 7, ay top, growy, growx, height 230:230:1250, width 500:800:2000, wrap");
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

		MigLayout ml = new MigLayout("ins 10, gapy 4, wrap 2", "[90][200, grow, fill]", rowLayout);

		composite.setLayout(ml);

		wf.label(composite).text(getEditorInput().getTitle()).titleStyle().build();

		// Line 1
		Text name = wf.text(composite, "name").label("Name:").enableEditingOnNew().hint("w 200!").build();
		
		setFocusWidget(name);

		// Line 2
		wf.text(composite, "nameLang").label("Full name:").build();

		// Line 3
		wf.text(composite, "strasse").label("Street:").build();

		// Line 4
		wf.text(composite, "postleitzahl").label("Zip:").build();

		// Line 5
		wf.text(composite, "stadt").label("City:").build();

		// Line 6
		wf.text(composite, "land").label("Country:").build();

		// Line 7
		wf.text(composite, "telefon").label("Phone:").build();
	
		// Line 8
		wf.text(composite, "fax").label("Fax:").build();
		
		// Line 9
		wf.text(composite, "email").label("EMail:").build();

		// Line 10
		wf.text(composite, "beschreibung").label("Note:", "ay top").hint("spanx 2, h 60!").build();

	}
}
