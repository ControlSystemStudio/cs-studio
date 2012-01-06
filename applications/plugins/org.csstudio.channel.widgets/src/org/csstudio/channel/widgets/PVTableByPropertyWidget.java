package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.column;
import static org.epics.pvmanager.data.ExpressionLanguage.vStringConstants;
import static org.epics.pvmanager.data.ExpressionLanguage.vTable;
import static org.epics.pvmanager.util.TimeDuration.ms;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.widgets.VTableDisplay;
import org.csstudio.utility.pvmanager.widgets.VTableDisplayCell;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VTable;
import org.epics.pvmanager.data.VTableColumn;

public class PVTableByPropertyWidget extends AbstractChannelQueryResultWidget implements ISelectionProvider {
	
	private static final int MAX_COLUMNS = 200;
	private static final int MAX_CELLS = 50000;
	
	private VTableDisplay table;
	private ErrorBar errorBar;

	public PVTableByPropertyWidget(Composite parent, int style) {
		super(parent, style);
		
		// Close PV on dispose
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (pv != null) {
					pv.close();
					pv = null;
				}
				if (rowSelectionWriter != null) {
					rowSelectionWriter.close();
					rowSelectionWriter = null;
				}
			}
		});
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		table = new VTableDisplay(this);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (rowSelectionWriter != null && ((Table) e.widget).getSelectionCount() > 0) {
					rowSelectionWriter.write(((Table) e.widget).getSelection()[0].getText());
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			List<String> properties = Arrays.asList("channels", "rowProperty", "columnProperty");
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					computeTableChannels();
				}
			}
		});
		
		selectionProvider = new AbstractSelectionProviderWrapper(table, this) {
			@Override
			protected ISelection transform(IStructuredSelection selection) {
				VTableDisplayCell cell = (VTableDisplayCell) selection.getFirstElement();
				if (cell != null)
					return new StructuredSelection(new PVTableByPropertyCell(cell, PVTableByPropertyWidget.this));
				return new StructuredSelection();
			}
		};
	}
	
	private void setLastException(Exception ex) {
		errorBar.setException(ex);
	}
	
	private String rowProperty;
	private String columnProperty;
	
	private List<List<String>> cellPvs;
	private List<List<Collection<Channel>>> cellChannels;
	private List<String> columnNames;
	private List<String> rowNames;
	
	private PVReader<VTable> pv;
	private PVReaderListener listener = new PVReaderListener() {
		
		@Override
		public void pvChanged() {
			if (!table.isDisposed()) {
				setLastException(pv.lastException());
				table.setVTable(pv.getValue());
			}
		}
	};
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
    
	private void reconnect() {
		if (pv != null) {
			pv.close();
			pv = null;
			table.setVTable(null);
		}
		
		if (columnNames == null || rowNames == null || cellPvs == null ||
				cellPvs.size() != columnNames.size()) {
			// Invalid data: don't connect
			return;
		}
		
		VTableColumn[] columns = new VTableColumn[columnNames.size() + 1];
		columns[0] = column(rowProperty + " \\ " + columnProperty, vStringConstants(rowNames));
		for (int nColumn = 0; nColumn < columnNames.size(); nColumn++) {
			String name = columnNames.get(nColumn);
			List<String> columnPvs = cellPvs.get(nColumn);
			columns[nColumn + 1] = column(name, latestValueOf(channels(columnPvs)));
		}
		// Increasing the notification rate will make the tooltips not work,
		// so it's limited to 500 ms.
		pv = PVManager.read(vTable(columns)).notifyOn(SWTUtil.swtThread()).every(ms(500));
		pv.addPVReaderListener(listener);
		table.setCellLabelProvider(new PVTableByPropertyCellLabelProvider(cellPvs));
	}
	
	public String getRowProperty() {
		return rowProperty;
	}
	
	public void setRowProperty(String rowProperty) {
		String oldValue = this.rowProperty;
		this.rowProperty = rowProperty;
		changeSupport.firePropertyChange("rowProperty", oldValue, rowProperty);
	}
	
	public String getColumnProperty() {
		return columnProperty;
	}
	
	public void setColumnProperty(String columnProperty) {
		String oldValue = this.columnProperty;
		this.columnProperty = columnProperty;
		changeSupport.firePropertyChange("columnProperty", oldValue, columnProperty);
	}
	
	public Collection<Channel> getChannels() {
		return channels;
	}
	
	private Collection<Channel> channels;
	
	private void setChannels(Collection<Channel> channels) {
		Collection<Channel> oldChannels = this.channels;
		this.channels = channels;
		changeSupport.firePropertyChange("channels", oldChannels, channels);
	}
	
	private void computeTableChannels() {
		// Not have all the bits to prepare the channel list
		if (channels == null || rowProperty == null || columnProperty == null) {
			columnNames = null;
			rowNames = null;
			cellPvs = null;
			cellChannels = null;
			reconnect();
			return;
		}

		// Filter only the channels that actually have the properties
		// If none, then nothing should be shown
		Collection<Channel> channelsInTable = ChannelUtil.filterbyProperties(channels, Arrays.asList(rowProperty, columnProperty));
		if (channelsInTable.isEmpty()) {
			columnNames = null;
			rowNames = null;
			cellPvs = null;
			cellChannels = null;
			reconnect();
			return;
		}
		
		// Find the rows and columns
		List<String> possibleRows = new ArrayList<String>(ChannelUtil.getPropValues(channelsInTable, rowProperty));
		List<String> possibleColumns = new ArrayList<String>(ChannelUtil.getPropValues(channelsInTable, columnProperty));
		
		// Limit column and cell count
		if (possibleColumns.size() > MAX_COLUMNS) {
			errorBar.setException(new RuntimeException("Max column count is " + MAX_COLUMNS + " (would generate " + possibleColumns.size() + ")"));
			columnNames = null;
			rowNames = null;
			cellPvs = null;
			cellChannels = null;
			reconnect();
			return;
		}
		if (possibleRows.size() * possibleColumns.size() > MAX_CELLS) {
			errorBar.setException(new RuntimeException("Max cell count is " + MAX_CELLS + " (would generate " + possibleRows.size() * possibleColumns.size() + ")"));
			columnNames = null;
			rowNames = null;
			cellPvs = null;
			cellChannels = null;
			reconnect();
			return;
		}
		
		Collections.sort(possibleRows);
		Collections.sort(possibleColumns);
		
		List<List<String>> cells = new ArrayList<List<String>>();
		List<List<Collection<Channel>>> channels = new ArrayList<List<Collection<Channel>>>();
		for (int nColumn = 0; nColumn < possibleColumns.size(); nColumn++) {
			List<String> column = new ArrayList<String>();
			List<Collection<Channel>> channelColumn = new ArrayList<Collection<Channel>>();
			for (int nRow = 0; nRow < possibleRows.size(); nRow++) {
				column.add(null);
				channelColumn.add(new HashSet<Channel>());
			}
			cells.add(column);
			channels.add(channelColumn);
		}
		
		for (Channel channel : channelsInTable) {
			String row = channel.getProperty(rowProperty).getValue();
			String column = channel.getProperty(columnProperty).getValue();
			
			int nRow = possibleRows.indexOf(row);
			int nColumn = possibleColumns.indexOf(column);
			
			if (nRow != -1 && nColumn != -1) {
				cells.get(nColumn).set(nRow, channel.getName());
				channels.get(nColumn).get(nRow).add(channel);
			}
		}
		
		columnNames = possibleColumns;
		rowNames = possibleRows;
		cellPvs = cells;
		cellChannels = channels;
		
		reconnect();
	}
	
	private String rowSelectionPv = null;
	private LocalUtilityPvManagerBridge rowSelectionWriter = null;
	
	public String getSelectionPv() {
		return rowSelectionPv;
	}
	
	public void setRowSelectionPv(String selectionPv) {
		this.rowSelectionPv = selectionPv;
		if (selectionPv == null || selectionPv.trim().isEmpty()) {
			// Close PVManager
			if (rowSelectionWriter != null) {
				rowSelectionWriter.close();
				rowSelectionWriter = null;
			}
			
		} else {
			rowSelectionWriter = new LocalUtilityPvManagerBridge(selectionPv);
		}
	}
	
	private AbstractSelectionProviderWrapper selectionProvider;

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return selectionProvider.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		selectionProvider.setSelection(selection);
	}

	public Collection<Channel> getChannelsAt(int row, int column) {
		if (cellChannels == null)
			return null;
		return cellChannels.get(column).get(row);
	}
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		table.setMenu(menu);
	}

	public Collection<Channel> getChannelsInColumn(int column) {
		Collection<Channel> columnChannels = new HashSet<Channel>();
		for (Collection<Channel> channels : cellChannels.get(column)) {
			columnChannels.addAll(channels);
		}
		return columnChannels;
	}

	public Collection<Channel> getChannelsInRow(int row) {
		Collection<Channel> rowChannels = new HashSet<Channel>();
		for (List<Collection<Channel>> column : cellChannels) {
			rowChannels.addAll(column.get(row));
		}
		return rowChannels;
	}

	public List<String> getColumnPropertyValues() {
		return columnNames;
	}

	public List<String> getRowPropertyValues() {
		return rowNames;
	}

	@Override
	protected void queryCleared() {
		setChannels(null);
	}

	@Override
	protected void queryExecuted(Result result) {
		Exception e = result.exception;
		if (e == null) {
			setChannels(result.channels);
		} else {
			errorBar.setException(e);
		}
	}
	
}
