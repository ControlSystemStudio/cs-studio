package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelUtil;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class PVTableByPropertyView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.PVTableByPropertyView";

	/** Memento */
	private IMemento memento = null;
	
	/** Memento tag */
	private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public PVTableByPropertyView() {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException {
		super.init(site, memento);
		// Save the memento
		this.memento = memento;
	}

	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		// Save the currently selected variable
		if (combo.getText() != null) {
			memento.putString(MEMENTO_PVNAME, combo.getText());
		}
	}
	
	public void setPVName(String name) {
		combo.setText(name);
		changeQuery(name);
	}
	
	private Combo combo;
	private PVTableByPropertyWidget tableWidget;
	private Combo columnProperty;
	private Combo rowProperty;
	private Composite parent;
	
	private void changeQuery(String text) {
		tableWidget.setChannelQuery(text);
		if (tableWidget.getChannels() != null) {
			Collection<String> propertyNames = ChannelUtil.getPropertyNames(tableWidget.getChannels());
			System.out.println(tableWidget.getChannels());
			System.out.println(propertyNames);
			rowProperty.setItems(propertyNames.toArray(new String[propertyNames.size()]));
			columnProperty.setItems(propertyNames.toArray(new String[propertyNames.size()]));
			parent.layout();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 13);
		fd_lblPvName.left = new FormAttachment(0, 10);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("Query:");
		
		ComboViewer comboViewer = new ComboViewer(parent, SWT.NONE);
		combo = comboViewer.getCombo();
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		combo.setLayoutData(fd_combo);
		
		tableWidget = new PVTableByPropertyWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -10);
		fd_waterfallComposite.top = new FormAttachment(combo, 6);
		fd_waterfallComposite.left = new FormAttachment(0, 10);
		fd_waterfallComposite.right = new FormAttachment(100, -10);
		tableWidget.setLayoutData(fd_waterfallComposite);
		
		ComboHistoryHelper name_helper =
			new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), "WaterfallPVs", combo, 20, true) {
			@Override
			public void newSelection(final String pv_name) {
				changeQuery(pv_name);
			}
		};
		
		Label lblRow = new Label(parent, SWT.NONE);
		fd_combo.right = new FormAttachment(lblRow, -6);
		FormData fd_lblRow = new FormData();
		fd_lblRow.top = new FormAttachment(lblPvName, 0, SWT.TOP);
		lblRow.setLayoutData(fd_lblRow);
		lblRow.setText("Row:");
		
		rowProperty = new Combo(parent, SWT.NONE);
		fd_lblRow.right = new FormAttachment(rowProperty, -6);
		FormData fd_rowProperty = new FormData();
		fd_rowProperty.top = new FormAttachment(lblPvName, -3, SWT.TOP);
		rowProperty.setLayoutData(fd_rowProperty);
		rowProperty.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println (e.widget + " - Default Selection");
				tableWidget.setRowProperty(rowProperty.getItem(rowProperty.getSelectionIndex()));
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println (e.widget + " - Default Selection");
				tableWidget.setRowProperty(rowProperty.getItem(rowProperty.getSelectionIndex()));
				
			}
		});
		
		Label lblColumn = new Label(parent, SWT.NONE);
		fd_rowProperty.right = new FormAttachment(lblColumn, -6);
		FormData fd_lblColumn = new FormData();
		fd_lblColumn.top = new FormAttachment(lblPvName, 0, SWT.TOP);
		lblColumn.setLayoutData(fd_lblColumn);
		lblColumn.setText("Column:");
		
		columnProperty = new Combo(parent, SWT.NONE);
		fd_lblColumn.right = new FormAttachment(columnProperty, -6);
		FormData fd_columnProperty = new FormData();
		fd_columnProperty.bottom = new FormAttachment(tableWidget, -6);
		fd_columnProperty.right = new FormAttachment(100, -10);
		columnProperty.setLayoutData(fd_columnProperty);
		columnProperty.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println (e.widget + " - Default Selection");
				tableWidget.setColumnProperty(columnProperty.getItem(columnProperty.getSelectionIndex()));
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println (e.widget + " - Default Selection");
				tableWidget.setColumnProperty(columnProperty.getItem(columnProperty.getSelectionIndex()));
				
			}
		});
		name_helper.loadSettings();
		
		if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
			setPVName(memento.getString(MEMENTO_PVNAME));
		}
	}
}