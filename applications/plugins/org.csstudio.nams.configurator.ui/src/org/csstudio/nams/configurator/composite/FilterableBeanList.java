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

	private class TableFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement,
				final Object element) {
			final IConfigurationBean bean = (IConfigurationBean) element;

			if ((FilterableBeanList.this.filterkriterium != null)
					&& (FilterableBeanList.this.filterkriterium.length() != 0)
					&& (!bean.getDisplayName().toLowerCase().contains(
							FilterableBeanList.this.filterkriterium
									.toLowerCase()))) {
				return false;
			}
			if (FilterableBeanList.this.selectedgruppenname.equals("ALLE")) {
				return true;
			}

			if (FilterableBeanList.this.selectedgruppenname
					.equals("Ohne Rubrik")) {
				return ((bean.getRubrikName() == null) || (bean.getRubrikName()
						.length() == 0));
			}

			final boolean equals = ((bean.getRubrikName() != null) && bean
					.getRubrikName().equals(
							FilterableBeanList.this.selectedgruppenname));
			return equals;
		}

	}

	private static Logger logger;

	public static void staticInject(final Logger logger) {
		FilterableBeanList.logger = logger;
	}

	private String filterkriterium = "";
	private String selectedgruppenname = "ALLE";

	private final Set<String> gruppenNamen = new TreeSet<String>();
	private TableViewer table;

	private ComboViewer gruppenCombo;

	public FilterableBeanList(final Composite parent, final int style) {
		this.createPartControl(parent, style);
	}

	public TableViewer getTable() {
		return this.table;
	}

	public void updateView() {
		final IConfigurationBean[] tableInput = this.getTableInput();

		FilterableBeanList.logger.logDebugMessage(this, "new tableInput: "
				+ Arrays.toString(tableInput));

		this.gruppenNamen.clear();
		for (final IConfigurationBean bean : tableInput) {
			final String groupName = bean.getRubrikName();
			if ((groupName != null) && !groupName.equals("")) {
				this.gruppenNamen.add(groupName);
			}
		}

		final Object[] newItems = new String[this.gruppenNamen.size() + 3];
		newItems[0] = "ALLE";
		newItems[1] = "Ohne Rubrik";
		newItems[2] = "-------------";
		System.arraycopy(this.gruppenNamen.toArray(new Object[this.gruppenNamen
				.size()]), 0, newItems, 3, this.gruppenNamen.size());
		this.gruppenCombo.setInput(newItems);

		if (this.gruppenNamen.contains(this.selectedgruppenname)
				|| this.selectedgruppenname.equals("Ohne Rubrik")) {
			this.gruppenCombo.setSelection(new StructuredSelection(
					this.selectedgruppenname), true);
		} else {
			this.gruppenCombo.setSelection(new StructuredSelection("ALLE"),
					true);
		}
		Arrays.sort(tableInput);
		this.table.setInput(tableInput);
	}

	protected abstract IConfigurationBean[] getTableInput();

	protected void openEditor(final DoubleClickEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		final Object source = selection.getFirstElement();
		final AbstractConfigurationBean<?> configurationBean = (AbstractConfigurationBean<?>) source;

		new OpenConfigurationEditorAction(configurationBean).run();
	}

	private void createPartControl(final Composite parent, final int style) {
		final Composite main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		TableColumn column = null;

		{
			final Composite compDown = new Composite(main, SWT.None);
			compDown.setLayout(new GridLayout(4, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(compDown);

			{
				new Label(compDown, SWT.READ_ONLY).setText("Rubrik");

				this.gruppenCombo = new ComboViewer(compDown, SWT.BORDER
						| SWT.READ_ONLY);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						this.gruppenCombo.getControl());
				this.gruppenCombo
						.setContentProvider(new ArrayContentProvider());
				this.gruppenCombo
						.addSelectionChangedListener(new ISelectionChangedListener() {

							public void selectionChanged(
									final SelectionChangedEvent event) {
								final IStructuredSelection selection = (IStructuredSelection) event
										.getSelection();
								FilterableBeanList.this.selectedgruppenname = (String) selection
										.getFirstElement();
								FilterableBeanList.this.table.refresh();
							}
						});
			}

			{
				new Label(compDown, SWT.READ_ONLY).setText("Suche");

				final Text filter = new Text(compDown, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(filter);
				filter.addListener(SWT.Modify, new Listener() {
					public void handleEvent(final Event event) {
						FilterableBeanList.this.filterkriterium = filter
								.getText();
						FilterableBeanList.this.table.refresh();
					}
				});

			}
		}

		{
			this.table = new TableViewer(main, SWT.FULL_SELECTION);
			column = new TableColumn(this.table.getTable(), SWT.LEFT);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.table.getControl());
			this.table.setContentProvider(new ArrayContentProvider());
			this.updateView();
			this.table.setFilters(new ViewerFilter[] { new TableFilter() });
			this.table.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(final DoubleClickEvent event) {
					FilterableBeanList.this.openEditor(event);
				}
			});
		}

		main.addControlListener(new TableColumnResizeAdapter(main, this.table
				.getTable(), column));
	}
}
