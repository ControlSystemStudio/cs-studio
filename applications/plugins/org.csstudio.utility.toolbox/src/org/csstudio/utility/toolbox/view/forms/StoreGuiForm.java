package org.csstudio.utility.toolbox.view.forms;

import java.util.ArrayList;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.entities.Raum;
import org.csstudio.utility.toolbox.framework.template.AbstractGuiFormTemplate;
import org.csstudio.utility.toolbox.services.GebaeudeService;
import org.csstudio.utility.toolbox.services.LogGroupService;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.csstudio.utility.toolbox.view.forms.listener.BuildingModifyListener;
import org.csstudio.utility.toolbox.view.forms.listener.RoomFocusListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class StoreGuiForm extends AbstractGuiFormTemplate<Lager> {

	@Inject
	private GebaeudeService gebaeudeService;

	@Inject 
	private BuildingModifyListener buildingModifyListener;
	
	@Inject
	private RoomFocusListener roomFocusListener;
	
	@Inject
	private LogGroupService logGroupService;
	
	@Inject
	private LogUserService logUserService;
	
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

		String[] titles = { "Name", "Responsible Person", "In Building", "In Room" };
		int[] bounds = { 20, 30, 20, 10, 20 };

		setSearchResultTableViewer(createTableViewer(composite, SEARCH_RESULT_TABLE_VIEWER, titles, bounds));

		final Table table = getSearchResultTableViewer().getTable();

		table.setLayoutData("spanx 7, ay top, growy, growx, height 430:430:1250, width 500:800:2000, wrap");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		return getSearchResultTableViewer();

	}

	private void createPart(Composite composite) {

		String rowLayout;

		if (isSearchMode()) {
			rowLayout = "[][][][][][][]12[][][]12[][][][grow, fill]";
		} else {
			rowLayout = "[][][][][][][]12[][][]12[fill][]";
		}

		MigLayout ml = new MigLayout("ins 10, gapy 4, wrap 2", "[90][300, fill][grow]", rowLayout);

		composite.setLayout(ml);

		wf.label(composite).text(getEditorInput().getTitle()).titleStyle().build();

		// Line 1
		Text name = wf.text(composite, "name").label("Name:").enableEditingOnNew().hint("w 200!").build();

		setFocusWidget(name);

		wf.combo(composite, "responsiblePerson").label("User:").data(logUserService.findAll()).hint("wrap").build();

		wf.combo(composite, "groupOwner").label("Group:").data(logGroupService.findAll()).hint("wrap").build();

		
		Text building = wf.text(composite, "inGebaeude").label("Building:").data(gebaeudeService.findAll()).build();

		Text room = wf.text(composite, "inRaum").label("Room:").hint("wrap").data(new ArrayList<Raum>())
					.message("Please provide value for building first...").notEditable().build();

		buildingModifyListener.init(building, room);
		roomFocusListener.init(building, room);
		
		building.addModifyListener(buildingModifyListener);
		room.addFocusListener(roomFocusListener);
		
		// --s
		wf.text(composite, "lagerPrefix").label("Store prefix:").build();

	}
}
