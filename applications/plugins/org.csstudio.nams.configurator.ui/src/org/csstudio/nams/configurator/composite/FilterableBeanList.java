package org.csstudio.nams.configurator.composite;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.nams.configurator.actions.OpenConfigurationEditorAction;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public abstract class FilterableBeanList {

	private static Logger logger;
	
	public static void staticInject(Logger logger) {
		FilterableBeanList.logger = logger;
	}
	
	private String filterkriterium = "";
	private String selectedgruppenname = "ALLE";
	private Set<String> gruppenNamen = new TreeSet<String>();

	private TableViewer table;
	private ComboViewer gruppenCombo;

	public FilterableBeanList(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		Composite main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		TableColumn column = null;

		{
			Composite compDown = new Composite(main, SWT.None);
			compDown.setLayout(new GridLayout(4, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(compDown);

			{
				new Label(compDown, SWT.READ_ONLY).setText("Rubrik");

				gruppenCombo = new ComboViewer(compDown, SWT.BORDER
						| SWT.READ_ONLY);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						gruppenCombo.getControl());
				gruppenCombo.setContentProvider(new ArrayContentProvider());
				gruppenCombo
						.addSelectionChangedListener(new ISelectionChangedListener() {

							public void selectionChanged(
									SelectionChangedEvent event) {
								IStructuredSelection selection = (IStructuredSelection) event
										.getSelection();
								selectedgruppenname = (String) selection
										.getFirstElement();
								table.refresh();
							}
						});
			}

			{
				new Label(compDown, SWT.READ_ONLY).setText("Suche");

				final Text filter = new Text(compDown, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(filter);
				filter.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event event) {
						filterkriterium = filter.getText();
						table.refresh();
					}
				});

			}
		}

		{
			table = new TableViewer(main, SWT.FULL_SELECTION );
			column = new TableColumn(table.getTable(), SWT.LEFT);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					table.getControl());
			table.setContentProvider(new ArrayContentProvider());
			updateView();
			table.setFilters(new ViewerFilter[] { new TableFilter() });
			table.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					openEditor(event);
				}
			});
		}

		main.addControlListener(new TableColumnResizeAdapter(main, table
				.getTable(), column));
	}

	protected void openEditor(DoubleClickEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object source = selection.getFirstElement();
		AbstractConfigurationBean<?> configurationBean = (AbstractConfigurationBean<?>) source;

		new OpenConfigurationEditorAction(configurationBean).run();
	}

	protected abstract IConfigurationBean[] getTableInput();

	private class TableFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			IConfigurationBean bean = (IConfigurationBean) element;

			if (filterkriterium != null && filterkriterium.length() != 0 && (!bean.getDisplayName().toLowerCase().contains(
					filterkriterium.toLowerCase()))) {
				return false;
			}
			if (selectedgruppenname.equals("ALLE")) {
				return true;
			}

			if (selectedgruppenname.equals("Ohne Rubrik")) {
				return (bean.getRubrikName() == null || bean.getRubrikName().length() == 0);
			}

			boolean equals = (bean.getRubrikName() != null && bean
					.getRubrikName().equals(selectedgruppenname));
			return equals;
		}

	}

	public TableViewer getTable() {
		return table;
	}

	public void updateView() {
		IConfigurationBean[] tableInput = this.getTableInput();

		logger.logDebugMessage(this, "new tableInput: " + Arrays.toString(tableInput));
		
		gruppenNamen.clear();
		for (IConfigurationBean bean : tableInput) {
			String groupName = bean.getRubrikName();
			if (groupName != null && !groupName.equals("")) {
				gruppenNamen.add(groupName);
			}
		}

		Object[] newItems = new String[gruppenNamen.size() + 3];
		newItems[0] = "ALLE";
		newItems[1] = "Ohne Rubrik";
		newItems[2] = "-------------";
		System.arraycopy(gruppenNamen.toArray(new Object[gruppenNamen.size()]),
				0, newItems, 3, gruppenNamen.size());
		gruppenCombo.setInput(newItems);

		if (gruppenNamen.contains(selectedgruppenname) || selectedgruppenname.equals("Ohne Rubrik")) {
			gruppenCombo.setSelection(new StructuredSelection(
					selectedgruppenname), true);
		} else {
			gruppenCombo.setSelection(new StructuredSelection(
					"ALLE"), true);
		}
		Arrays.sort(tableInput);
		table.setInput(tableInput);
	}
}
