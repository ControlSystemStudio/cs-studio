package org.csstudio.nams.configurator.composite;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.csstudio.nams.configurator.actions.OpenConfigurationEditorAction;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public abstract class FilteredListVarianteA {

	private String filterkriterium = "";
	private String selectedgruppenname = "ALLE";
	private Set<String> gruppenNamen = new TreeSet<String>();

	private TableViewer table;
	private Combo gruppenCombo;

	public FilteredListVarianteA(Composite parent, int style) {
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

				gruppenCombo = new Combo(compDown, SWT.BORDER
						| SWT.READ_ONLY);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						gruppenCombo);
				gruppenCombo.add("ALLE");
				gruppenCombo.add("Ohne Rubrik");
				gruppenCombo.add("-------------");
//				gruppenCombo.add("Kryo OPS");
//				gruppenCombo.add("C1-WPS");
				gruppenCombo.select(0);
				//FIX ME DONE! remove hardcoded group names


				gruppenCombo.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event event) {
						selectedgruppenname = gruppenCombo.getItem(gruppenCombo
								.getSelectionIndex());
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
			table = new TableViewer(main, SWT.FULL_SELECTION | SWT.MULTI);
			column = new TableColumn(table.getTable(), SWT.LEFT);
			// column.setWidth(450);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					table.getControl());
			table.setContentProvider(new ArrayContentProvider());
//			table.setLabelProvider(new 
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
		AbstractConfigurationBean<?> configurationBean= (AbstractConfigurationBean<?>) source;

//		IConfigurationModel model = new ConfigurationModel() {
//			@Override
//			public Collection<String> getSortgroupNames() {
//				Collection<String> groupNames = new ArrayList<String>();
//				groupNames.add("Kryo OPS");
//				groupNames.add("C1-WPS");
//				//FIXME remove hardcoded group names
//				return groupNames;
//			}
//		};
//		configurationBean.addPropertyChangeListener(new PropertyChangeListener(){
//
//			public void propertyChange(PropertyChangeEvent evt) {
//				updateView();
//				
//			}
//			
//		});
		new OpenConfigurationEditorAction(configurationBean).run();
	}

	/**
	 * Setzt die Spaltenbreite einer Tabelle nach dem Style SWT.FILL
	 * 
	 * @author eugrei
	 * 
	 */
	private class TableColumnResizeAdapter extends ControlAdapter {
		private Table table;
		private final Composite parent;
		private final TableColumn column;

		public TableColumnResizeAdapter(Composite parent, Table table,
				TableColumn column) {
			this.parent = parent;
			this.table = table;
			this.column = column;
		}

		@Override
		public void controlResized(ControlEvent e) {
			Rectangle area = parent.getClientArea();
			Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			ScrollBar vBar = table.getVerticalBar();
			int width = area.width - table.computeTrim(0, 0, 0, 0).width
					- vBar.getSize().x;
			if (size.y > area.height + table.getHeaderHeight()) {
				// Subtract the scrollbar width from the total column width
				// if a vertical scrollbar will be required
				Point vBarSize = vBar.getSize();
				width -= vBarSize.x;
			}
			Point oldSize = table.getSize();
			if (oldSize.x > area.width) {
				// table is getting smaller so make the columns
				// smaller first and then resize the table to
				// match the client area width
				column.setWidth(width);
				table.setSize(area.width, area.height);
			} else {
				// table is getting bigger so make the table
				// bigger first and then make the columns wider
				// to match the client area width
				table.setSize(area.width, area.height);
				column.setWidth(width);
			}
		}
	}

	protected abstract IConfigurationBean[] getTableInput();

	private class TableFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			IConfigurationBean bean = (IConfigurationBean) element;
			
			if (!bean.getDisplayName().toLowerCase().contains(
					filterkriterium.toLowerCase())) {
				return false;
			}
			if (selectedgruppenname.equals("ALLE")) {
				return true;
			}
			
			if (selectedgruppenname.equals("Ohne Rubrik")) {
				return (bean.getRubrikName() == null || bean.getRubrikName().equals(""));
			}
			
			boolean equals = (bean.getRubrikName() != null && bean.getRubrikName().equals(selectedgruppenname));
			return equals;			
		}

	}

	public TableViewer getTable() {
		return table;
	}
	
	public void updateView(){
		IConfigurationBean[] tableInput = this.getTableInput();
		
		if (gruppenCombo.getItemCount() > 3) {
			//TODO just removed this is probably test code?
//			gruppenCombo.remove(3, gruppenCombo.getItemCount());
		}
		gruppenNamen.clear();
		for (IConfigurationBean bean : tableInput) {
			String groupName = bean.getRubrikName();
			if (groupName != null && !groupName.equals("")) {
				gruppenNamen.add(groupName);
			}
		}
		for (String gruppenName : gruppenNamen) {
			gruppenCombo.add(gruppenName);
		}
		
		Arrays.sort(tableInput);
		table.setInput(tableInput);
	}
}
