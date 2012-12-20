package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.column;
import static org.epics.pvmanager.data.ExpressionLanguage.vStringConstants;
import static org.epics.pvmanager.data.ExpressionLanguage.vTable;
import static org.epics.pvmanager.util.TimeDuration.ms;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
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

import org.csstudio.channel.widgets.util.MementoUtil;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.channel.CSSChannelUtils;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
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
import org.eclipse.ui.IMemento;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VTable;
import org.epics.pvmanager.data.VTableColumn;

public class PVTableByPropertyWidget extends AbstractChannelQueryResultWidget implements ISelectionProvider,
	ConfigurableWidget {
	
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
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		errorBar.setMarginBottom(5);
		
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
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			List<String> properties = Arrays.asList("channels", "rowProperty", "columnProperty", "columnTags");
			
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
	private List<String> columnTags = new ArrayList<String>();
	
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
		columns[0] = column(rowProperty, vStringConstants(rowNames));
		for (int nColumn = 0; nColumn < columnNames.size(); nColumn++) {
			String name = columnNames.get(nColumn);
			List<String> columnPvs = cellPvs.get(nColumn);
			columns[nColumn + 1] = column(name, latestValueOf(channels(columnPvs)));
		}
		// Increasing the notification rate will make the tooltips not work,
		// so it's limited to 500 ms.
		pv = PVManager.read(vTable(columns)).notifyOn(SWTUtil.swtThread()).every(ms(500));
		pv.addPVReaderListener(listener);
		table.setCellLabelProvider(new PVTableByPropertyCellLabelProvider(cellChannels));
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
	
	public List<String> getColumnTags() {
		return columnTags;
	}
	
	public void setColumnTags(List<String> tags) {
		List<String> oldValue = this.columnTags;
		columnTags = Collections.unmodifiableList(new ArrayList<String>(tags));
		changeSupport.firePropertyChange("columnTags", oldValue, columnTags);
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
	
	/**
	 * True if enough properties are set to compute the table channels.
	 * @return true if we can compute the table
	 */
	private boolean propertiesReady() {
		return rowProperty != null && (columnProperty != null ||
				(columnTags != null && !columnTags.isEmpty() ));
	}
	
	private void clearTableChannels() {
		columnNames = null;
		rowNames = null;
		cellPvs = null;
		cellChannels = null;
		reconnect();
	}
	
	private void computeTableChannels() {
		// Not have all the bits to prepare the channel list
		if (!propertiesReady() || channels == null) {
			clearTableChannels();
			return;
		}

		// Filter only the channels that actually have the properties
		// If none, then nothing should be shown
		Collection<Channel> channelsWithRow = ChannelUtil.filterbyProperties(channels, Arrays.asList(rowProperty));
		Collection<String> propertyNames = new ArrayList<String>();
		if (columnProperty != null)
			propertyNames.add(columnProperty);
		List<String> tagNames = this.columnTags;
		if (tagNames == null)
			tagNames = new ArrayList<String>();
		Collection<Channel> channelsInTable = CSSChannelUtils.filterByOneOrMoreElements(channelsWithRow, propertyNames, tagNames);
		if (channelsInTable.isEmpty()) {
			propertyNames = null;
			rowNames = null;
			cellPvs = null;
			cellChannels = null;
			reconnect();
			return;
		}
		
		// Find the rows and columns
		List<String> possibleRows = new ArrayList<String>(ChannelUtil.getPropValues(channelsInTable, rowProperty));
		List<String> columnPropertyValues = new ArrayList<String>(ChannelUtil.getPropValues(channelsInTable, columnProperty));
		//possibleColumns.addAll(tagNames);
		int nRows = possibleRows.size();
		int nColumns = columnPropertyValues.size() + tagNames.size();
		
		// Limit column and cell count
		if (nColumns > MAX_COLUMNS) {
			errorBar.setException(new RuntimeException("Max column count is " + MAX_COLUMNS + " (would generate " + nColumns + ")"));
			clearTableChannels();
			return;
		}
		if (nRows * nColumns > MAX_CELLS) {
			errorBar.setException(new RuntimeException("Max cell count is " + MAX_CELLS + " (would generate " + nRows * nColumns + ")"));
			clearTableChannels();
			return;
		}
		
		Collections.sort(possibleRows);
		Collections.sort(columnPropertyValues);
		
		List<List<String>> cells = new ArrayList<List<String>>();
		List<List<Collection<Channel>>> channels = new ArrayList<List<Collection<Channel>>>();
		for (int nColumn = 0; nColumn < nColumns; nColumn++) {
			List<String> column = new ArrayList<String>();
			List<Collection<Channel>> channelColumn = new ArrayList<Collection<Channel>>();
			for (int nRow = 0; nRow < nRows; nRow++) {
				column.add(null);
				channelColumn.add(new HashSet<Channel>());
			}
			cells.add(column);
			channels.add(channelColumn);
		}
		
		for (Channel channel : channelsInTable) {
			int nColumn = -1;
			// Row is guaranteed to have the property, column may not
			String row = channel.getProperty(rowProperty).getValue();
			if (channel.getProperty(columnProperty) != null) {
				nColumn = columnPropertyValues.indexOf(channel.getProperty(columnProperty).getValue());
			}
			
			int nRow = possibleRows.indexOf(row);
			
			if (nRow != -1 && nColumn != -1) {
				cells.get(nColumn).set(nRow, channel.getName());
				channels.get(nColumn).get(nRow).add(channel);
			}

			int tagCount = 0;
			for (String tagName : tagNames) {
				if (channel.getTag(tagName) != null) {
					nColumn = columnPropertyValues.size() + tagCount;
					cells.get(nColumn).set(nRow, channel.getName());
					channels.get(nColumn).get(nRow).add(channel);
				}
				tagCount++;
			}
		}
		
		List<String> newColumnNames = new ArrayList<String>();
		for (String columnPropertyValue : columnPropertyValues) {
			newColumnNames.add(columnProperty + "=" + columnPropertyValue);
		}
		newColumnNames.addAll(tagNames);
		columnNames = newColumnNames;
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
		errorBar.setException(null);
	}

	@Override
	protected void queryExecuted(Result result) {
		errorBar.setException(result.exception);
		setChannels(result.channels);
	}

	private boolean configurable = true;

	private PVTableByPropertyConfigurationDialog dialog;

	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new PVTableByPropertyConfigurationDialog(this);
		dialog.open();
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		boolean oldConfigurable = configurable;
		this.configurable = configurable;
		changeSupport.firePropertyChange("configurable", oldConfigurable,
				configurable);
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		return dialog != null;
	}

	@Override
	public void configurationDialogClosed() {
		dialog = null;
	}
	
	/** Memento tag */
	private static final String MEMENTO_CHANNEL_QUERY = "channelQuery"; //$NON-NLS-1$
	private static final String MEMENTO_ROW_PROPERTY = "rowProperty"; //$NON-NLS-1$
	private static final String MEMENTO_COLUMN_PROPERTY = "columnProperty"; //$NON-NLS-1$
	private static final String MEMENTO_COLUMN_TAGS = "columnTags"; //$NON-NLS-1$
	
	public void saveState(IMemento memento) {
		if (getChannelQuery() != null) {
			memento.putString(MEMENTO_CHANNEL_QUERY, getChannelQuery().getQuery());
		}
		if (getRowProperty() != null) {
			memento.putString(MEMENTO_ROW_PROPERTY, getRowProperty());
		}
		if (getColumnProperty() != null) {
			memento.putString(MEMENTO_COLUMN_PROPERTY, getColumnProperty());
		}
		if (getColumnTags() != null && !getColumnTags().isEmpty()) {
			memento.putString(MEMENTO_COLUMN_TAGS, MementoUtil.toCommaSeparated(getColumnTags()));
		}
	}
	
	public void loadState(IMemento memento) {
		if (memento != null) {
			if (memento.getString(MEMENTO_ROW_PROPERTY) != null) {
				setRowProperty(memento.getString(MEMENTO_ROW_PROPERTY));
			}
			if (memento.getString(MEMENTO_COLUMN_PROPERTY) != null) {
				setColumnProperty(memento.getString(MEMENTO_COLUMN_PROPERTY));
			}
			if (memento.getString(MEMENTO_COLUMN_TAGS) != null) {
				setColumnTags(MementoUtil.fromCommaSeparated(memento.getString(MEMENTO_COLUMN_TAGS)));
			}
			if (memento.getString(MEMENTO_CHANNEL_QUERY) != null) {
				setChannelQuery(ChannelQuery.query(memento.getString(MEMENTO_CHANNEL_QUERY)).build());
			}
		}
	}
	
}
