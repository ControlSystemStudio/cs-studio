package org.csstudio.nams.configurator.branch.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class FilteredListVarianteA {

	private String filterkriterium = "";
	private String gruppenname = "";

	private TableViewer table;

	public FilteredListVarianteA(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		Composite main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			Composite compDown = new Composite(main, SWT.None);
			compDown.setLayout(new GridLayout(4, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(compDown);

			{
				new Label(compDown, SWT.READ_ONLY).setText("Rubrik");

				final Combo gruppen = new Combo(compDown, SWT.BORDER | SWT.READ_ONLY);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						gruppen);
				gruppen.add("ALLE");
				gruppen.add("Ohne Rubrik");
				gruppen.add("-------------");
				gruppen.add("Kryo OPS");
				gruppen.add("C1-WPS");
				gruppen.select(0);
				
				gruppen.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event event) {
						gruppenname = gruppen.getItem(gruppen.getSelectionIndex());
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
			table = new TableViewer(main, SWT.FULL_SELECTION);
			TableColumn column = new TableColumn(table.getTable(), SWT.LEFT);
			column.setWidth(450);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					table.getControl());
			table.setContentProvider(new ArrayContentProvider());
			// table.setLabelProvider(new S)
			table.setInput(this.getTableInput());
			table.setFilters(new ViewerFilter[] { new TableFilter() });
		}

	}

	protected Object[] getTableInput() {
		List<Object> list = new ArrayList<Object>();
		list.addAll(Arrays.asList(getKryoOps()));
		list.addAll(Arrays.asList(getWPS()));
		return list.toArray();
	}

	protected Object[] getKryoOps() {
		return new String[] { "Max Mayer", "Thomas D", "Max Peter",
				"Hugo Balder", "Nora Jones", "Andre B", "Julius Caesar",
				"Emy Winehouse" };
	}

	protected Object[] getWPS() {
		return new String[] { "Hans Otto", "Thomas Otto", "M C Clausen" };
	}

	private class TableFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if( gruppenname.length() > 0 && !gruppenname.equals("ALLE"))
			{
				if( gruppenname.equals("Kryo OPS")) {
					if( !Arrays.asList(getKryoOps()).contains(element) ) return false;
				}
				
				if( gruppenname.equals("C1-WPS")) {
					if( !Arrays.asList(getWPS()).contains(element) ) return false;
				}
				
				if( gruppenname.equals("Ohne Rubrik")) {
					return false;
				}
			}
			
			return ((String) element).toLowerCase().contains(
					filterkriterium.toLowerCase());
		}

	}

}
