package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.channel.widgets.ChannelQueryInputBar;
import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

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
	private static final String MEMENTO_QUERY = "ChannelQuery"; //$NON-NLS-1$
	private static final String MEMENTO_ROW_PROPERTY = "RowProperty"; //$NON-NLS-1$
	private static final String MEMENTO_COLUMN_PROPERTY = "ColumnProperty"; //$NON-NLS-1$
	
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
		if (inputBar.getChannelQuery() != null) {
			memento.putString(MEMENTO_QUERY, inputBar.getChannelQuery().getQuery());
		}
		memento.putString(MEMENTO_ROW_PROPERTY, tableWidget.getRowProperty());
		memento.putString(MEMENTO_COLUMN_PROPERTY, tableWidget.getColumnProperty());		
	}
	
	public void setChannelQuery(ChannelQuery channelQuery) {
		inputBar.setChannelQuery(channelQuery);
		changeQuery(channelQuery);
	}
	
	private ChannelQueryInputBar inputBar;
	private PVTableByPropertyWidget tableWidget;
	private Combo columnProperty;
	private Combo rowProperty;
	private Composite parent;
	
	private void changeQuery(ChannelQuery channelQuery) {
		tableWidget.setChannelQuery(channelQuery);
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
		
		inputBar = new ChannelQueryInputBar(parent, SWT.NONE, 
				Activator.getDefault().getDialogSettings(), "pvtablebyproperty.query");
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		inputBar.setLayoutData(fd_combo);
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					setChannelQuery((ChannelQuery) event.getNewValue());
				}
			}
		});
		
		tableWidget = new PVTableByPropertyWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -10);
		fd_waterfallComposite.top = new FormAttachment(inputBar, 6);
		fd_waterfallComposite.left = new FormAttachment(0, 10);
		fd_waterfallComposite.right = new FormAttachment(100, -10);
		tableWidget.setLayoutData(fd_waterfallComposite);
		tableWidget.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("channels".equals(evt.getPropertyName())) {
					if (tableWidget.getChannels() != null) {
						List<String> propertyNames = new ArrayList<String>(ChannelUtil.getPropertyNames(tableWidget.getChannels()));
						Collections.sort(propertyNames);
						
						// Save old selection
						String oldRow = tableWidget.getRowProperty();
						String oldColumn = tableWidget.getColumnProperty();
						
						// Change properties to select
						rowProperty.setItems(propertyNames.toArray(new String[propertyNames.size()]));
						columnProperty.setItems(propertyNames.toArray(new String[propertyNames.size()]));
						
						// Try to keep old selection
						rowProperty.select(propertyNames.indexOf(oldRow));
						columnProperty.select(propertyNames.indexOf(oldColumn));
					}
					PVTableByPropertyView.this.parent.layout();
				}
			}
		});
		
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
				tableWidget.setRowProperty(rowProperty.getItem(rowProperty.getSelectionIndex()));
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
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
				tableWidget.setColumnProperty(columnProperty.getItem(columnProperty.getSelectionIndex()));
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				tableWidget.setColumnProperty(columnProperty.getItem(columnProperty.getSelectionIndex()));
				
			}
		});
		
		if (memento != null) {
			tableWidget.setRowProperty(memento.getString(MEMENTO_ROW_PROPERTY));
			tableWidget.setColumnProperty(memento.getString(MEMENTO_COLUMN_PROPERTY));
			if (memento.getString(MEMENTO_QUERY) != null) {
				setChannelQuery(ChannelQuery.query(memento.getString(MEMENTO_QUERY)).build());
			}
		}
		
		PopupMenuUtil.installPopupForView(tableWidget, getSite(), tableWidget);
		PopupMenuUtil.installPopupForView(inputBar, getSite(), inputBar);
	}
}