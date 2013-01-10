/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.swt.widgets.natives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**A table allow displaying and editing 2D text array as in spreadsheet.
 * The internal data operated by this table is a nested string list.
 * @author Xihui Chen
 *
 */
/**
 * @author Xihui Chen
 * 
 */
public class SpreadSheetTable extends Composite {
	
	private static final String TEXT_EDITING_SUPPORT_KEY = "text_editing_support"; //$NON-NLS-1$

	/**
	 * Listener on table cell editing events.
	 *
	 */
	public interface ITableCellEditingListener {
		/**Called whenever the value in a cell has been edited.
		 * @param row index of the row
		 * @param col index of the column
		 * @param oldValue old value in the cell
		 * @param newValue new value in the cell.
		 */
		public void cellValueChanged(int row, int col, String oldValue, String newValue);
	}
	
	/**
	 * Listener on table contents modified events.
	 *
	 */
	public interface ITableModifiedListener {
		
		/**Called whenever the table content is modified. 
		 * @param content of the table.
		 */
		public void modified(String[][] content);
	}
	
	/**
	 *Listener on table selection changed events.
	 *
	 */
	public interface ITableSelectionChangedListener {
		
		/**Called when selection on the table changed. 
		 * @param selection a 2D string array which represents the selected rows.
		 */
		public void selectionChanged(String[][] selection);
	}

	private class TextEditingSupport extends EditingSupport {

		private TextCellEditor textCellEditor;
		
		private String oldValue;
		private ViewerCell viewerCell;
		private boolean columnEditable = true;

		public TextEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(Object element) {
			if(!editable)
				return false;			
			return columnEditable;
		}
		
		private int findColumnIndex() {
			return viewerCell.getColumnIndex();
		}

		private int findRowIndex(){
			Table table = tableViewer.getTable();
			TableItem cellItem = (TableItem)viewerCell.getItem();
			return table.indexOf(cellItem);
		}
		
		@Override
		protected CellEditor getCellEditor(Object element) {
			if (textCellEditor == null)
				textCellEditor = new TextCellEditor(tableViewer.getTable());
			return textCellEditor;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getValue(Object element) {
			oldValue = ((List<String>) element).get(findColumnIndex());
			return oldValue;
		}

		@Override
		protected void initializeCellEditorValue(CellEditor cellEditor,
				ViewerCell cell) {
			this.viewerCell = cell;
			super.initializeCellEditorValue(cellEditor, cell);
			
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void setValue(Object element, Object value) {
			int col = findColumnIndex();
			String oldValue = ((List<String>) element).get(col);
			((List<String>) element).set(col, value.toString());
			if(!value.equals(oldValue)){
				if(tableEditingListeners != null && !tableEditingListeners.isEmpty()){
					int row = findRowIndex();
					for(Object listener: tableEditingListeners.getListeners()){
						((ITableCellEditingListener)listener).cellValueChanged(row, col, oldValue, value.toString());
					}
				}
				fireTableModified();
			}
			((TableItem)viewerCell.getItem()).setText(col, value.toString());
		}

		public boolean isColumnEditable() {
			return columnEditable;
		}

		public void setColumnEditable(boolean columnEditable) {
			this.columnEditable = columnEditable;
		}

	}

	private static class TextTableLableProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			return ((List<String>) element).get(columnIndex);
		}

	}

	private static final int DEFAULT_COLUMN_WIDTH = 60;

	private TableViewer tableViewer;

	private boolean editable = true;

	private List<List<String>> input;
	
	private ListenerList tableEditingListeners;
	private ListenerList selectionChangedListeners;
	private ListenerList tableModifiedListeners;
	
	/**Create a spreadsheet table.
	 * @param parent parent composite.
	 * @param style the style of widget to construct
	 */
	public SpreadSheetTable(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		tableViewer = new TableViewer(this, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		setInput(new ArrayList<List<String>>());
	}

	/**Add a table cell editing listener. Called whenever the value in a cell has been edited.
	 * @param listener the listener
	 */
	public void addCellEditingListener(ITableCellEditingListener listener){
		if(tableEditingListeners == null)
			tableEditingListeners = new ListenerList();
		tableEditingListeners.add(listener);
	}
	
	/**Add a table modified listener. Called whenever content of the table has been modified from the table.
	 * @param listener the listener
	 */
	public void addModifiedListener(ITableModifiedListener listener){
		if(tableModifiedListeners == null)
			tableModifiedListeners = new ListenerList();
		tableModifiedListeners.add(listener);
	}
	
	/**Add a selection changed listener. Call whenever selection of the table changes.
	 * @param listener the listener
	 */
	public void addSelectionChangedListener(ITableSelectionChangedListener listener){
		if(selectionChangedListeners == null){
			selectionChangedListeners = new ListenerList();
			tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				public void selectionChanged(SelectionChangedEvent event) {
					String[][] selection = getSelection();
					for(Object listener : selectionChangedListeners.getListeners()){
						((ITableSelectionChangedListener)listener).selectionChanged(selection);
					}
				}
			});
		}
		selectionChangedListeners.add(listener);
	}
	
	
	/**
	 * Auto size all columns.
	 */
	public void autoSizeColumns() {
		for (TableColumn column : tableViewer.getTable().getColumns()) {
			column.pack();
		}
	}

	/**
	 * @param col
	 */
	private void checkColumnIndex(int col) {
		if(col >= getColumnCount() || col <0){
			throw new IllegalArgumentException(
					NLS.bind("column index {0} out of range [0, {1}].", col, getColumnCount()));
		}
	}

	/**
	 * @param row
	 */
	private void checkRowIndex(int row) {
		if(row <0 || row >= input.size()){
			throw new IllegalArgumentException(
					NLS.bind("row index {0} out of range [0, {1}].", row, input.size()));		
		}
	}

	/**
	 * Delete a column.
	 * 
	 * @param index
	 *            index of the column.
	 */
	public void deleteColumn(int index) {
		tableViewer.getTable().getColumn(index).dispose();
		if(getColumnCount() == 0){
			input.clear();
			refresh();
		}else {
			for (int i = 0; i < getRowCount(); i++) {
				input.get(i).remove(index);
			}
		}
		
		fireTableModified();
	}

	/**
	 * Delete a row.
	 * 
	 * @param index
	 *            index of the row.
	 */
	public void deleteRow(int index) {
		input.remove(index);
		tableViewer.refresh();
		fireTableModified();
	}
	
	protected void fireTableModified(){
		if(tableModifiedListeners != null){
			String[][] content = getContent();
			for(Object o : tableModifiedListeners.getListeners()){
				((ITableModifiedListener)o).modified(content);
			}
		}
	}

	/**
	 * Get text of a cell.
	 * 
	 * @param row
	 *            row index of the cell.
	 * @param col
	 *            column index of the cell.
	 * @return the cell text.
	 */
	public String getCellText(int row, int col) {
		return input.get(row).get(col);
	}

	/**
	 * Get number of columns.
	 * 
	 * @see {@link Table#getColumnCount()}
	 */
	public int getColumnCount() {
		return tableViewer.getTable().getColumnCount();
	}

	/**
	 * Get content of the table in a 2D string array.
	 * 
	 * @return content of the table.
	 */
	public String[][] getContent() {
		String[][] result = new String[input.size()][getColumnCount()];
		for (int i = 0; i < input.size(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				result[i][j] = input.get(i).get(j);
			}
		}
		return result;
	}

	/**
	 * Get input of the table by which the table is backed. To keep the table's
	 * content synchronized with the table, client should call
	 * {@link #refresh()} if the returned list has been modified outside.
	 * 
	 * @return the input of the table.
	 */
	public List<List<String>> getInput() {
		return input;
	}

	/**Get row and column index under given point.
	 * @param point the widget-relative coordinates
	 * @return int[0] is row index, int[1] is column index. 
	 * null if no cell was found under given point.
	 */
	public int[] getRowColumnIndex(Point point) {
		Table table = tableViewer.getTable();
		ViewerCell cell = tableViewer.getCell(point);
		if(cell == null)
			return null;
		int col = cell.getColumnIndex();
//		int row = table.indexOf((TableItem) cell.getItem());
//		return new int[]{row, col};
		int row = -1;
		// get row index
		Rectangle clientArea = table.getClientArea();

		int index = table.getTopIndex();
		while (index < table.getItemCount()) {
			boolean visible = false;
			TableItem item = table.getItem(index);
			Rectangle rect = item.getBounds(col);
			if (rect.contains(point)) {
				row = index;
				return new int[] { row, col };
			}
			if (!visible && rect.intersects(clientArea)) {
				visible = true;
			}

			if (!visible)
				return new int[] { row, col };
			index++;
		}
		return new int[] { row, col };
	}

	public int getRowCount() {
		return input.size();
	}

	/**
	 * Get selected part.
	 * 
	 * @return the 2D string array under selection.
	 */
	@SuppressWarnings("unchecked")
	public String[][] getSelection() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer
				.getSelection();
		String[][] result = new String[selection.size()][getColumnCount()];
		int i = 0;
		for (Object o : selection.toArray()) {
			for (int j = 0; j < getColumnCount(); j++) {
				result[i][j] = ((List<String>) o).get(j);
			}
			i++;
		}
		return result;
	}

	/**
	 * @return the {@link TableViewer} wrapped by this widget.
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * Insert a column. Default values for the new column are empty strings.
	 * 
	 * @param index
	 *            index of the column.
	 */
	public void insertColumn(int index) {
		for (List<String> row : input) {
			row.add(index, ""); //$NON-NLS-1$
		}
		final TableViewerColumn viewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE, index);
		viewerColumn.getColumn().setMoveable(false);
		viewerColumn.getColumn().setWidth(DEFAULT_COLUMN_WIDTH);
		TextEditingSupport textEditingSupport = new TextEditingSupport(tableViewer);
		viewerColumn.setEditingSupport(textEditingSupport);
		viewerColumn.getColumn().setData(TEXT_EDITING_SUPPORT_KEY, textEditingSupport);
		tableViewer.setLabelProvider(new TextTableLableProvider());
		fireTableModified();
	}

	/**
	 * Insert a row. Shifts the element currently at that position (if any) and any subsequent elements to the below
	 *  (adds one to their indices).Default values for the new row are empty strings.
	 * 
	 * @param index
	 *            index of the row.
	 */
	public void insertRow(int index) {
		String[] array = new String[getColumnCount()];
		Arrays.fill(array, ""); //$NON-NLS-1$
		input.add(index, new ArrayList<String>(Arrays.asList(array)));
		tableViewer.refresh(false);
		fireTableModified();
	}

	/**
	 * @return true if table is editable.
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * @return true if table content is empty.
	 */
	public boolean isEmpty(){
		return input.isEmpty();
	}
	
	/**
	 * @param columnIndex index of the column.
	 * @return true if the column is editable.
	 */
	public boolean isColumnEditable(int columnIndex){
		checkColumnIndex(columnIndex);
		return ((TextEditingSupport)(tableViewer.getTable().getColumn(columnIndex).
				getData(TEXT_EDITING_SUPPORT_KEY))).isColumnEditable();
	}

	@Override
	public void pack() {
		for (int i = 0; i < getColumnCount(); i++)
			tableViewer.getTable().getColumn(i).pack();
		super.pack();
	}

	/**
	 * Refresh the table to reflect its content.
	 */
	public void refresh() {
		getTableViewer().refresh();
	}
	
	/**Set background color of the cell.
	 * @param row row index of the cell.
	 * @param col column index of the cell.
	 * @param rgbColor color in RGB.
	 */
	public void setCellBackground(int row, int col, RGB rgbColor){
		checkRowIndex(row);	
		checkColumnIndex(col);
		tableViewer.getTable().getItem(row).setBackground(
				col, CustomMediaFactory.getInstance().getColor(rgbColor));
	}
	
	/**Set forground color of the cell.
	 * @param row row index of the cell.
	 * @param col column index of the cell.
	 * @param rgbColor color in RGB.
	 */
	public void setCellForeground(int row, int col, RGB rgbColor){
		checkRowIndex(row);	
		checkColumnIndex(col);
		tableViewer.getTable().getItem(row).setForeground(
				col, CustomMediaFactory.getInstance().getColor(rgbColor));
	}
	
	/**
	 * Set the text of a cell. If the row index is larger than current content,
	 *  it will extend the current content to have that row.
	 *  
	 * 
	 * @param row
	 *            row index of the cell. Start from 0.
	 * @param col
	 *            column index of the cell. Start from 0.
	 * @param text
	 *            text to be set.
	 */
	public void setCellText(int row, int col, String text) {
		checkColumnIndex(col);
		if(row >= input.size()){
			for(int i=input.size(); i<=row; i++){
				String[] array = new String[getColumnCount()];
				Arrays.fill(array, ""); //$NON-NLS-1$
				input.add(new ArrayList<String>(Arrays.asList(array)));
			}
			tableViewer.refresh();
		}
		input.get(row).set(col, text);
		tableViewer.getTable().getItem(row).setText(col, text);
		fireTableModified();
	}
	
	/**Set if a column is editable. 
	 * @param columnIndex index of the column.
	 * @param editable editable if true.
	 */
	public void setColumnEditable(int columnIndex, boolean editable){
		checkColumnIndex(columnIndex);
		((TextEditingSupport)(tableViewer.getTable().getColumn(columnIndex).
				getData(TEXT_EDITING_SUPPORT_KEY))).setColumnEditable(editable);
	}
	
	
	
	/**
	 * Set the header of a column.
	 * 
	 * @param columnIndex
	 *            index of the column.
	 * @param header
	 *            header text.
	 */
	public void setColumnHeader(int columnIndex, String header) {
		checkColumnIndex(columnIndex);
		tableViewer.getTable().getColumn(columnIndex).setText(header);
	}
	

	/**
	 * Set column headers. If the size of the headers array is larger than the
	 * existing columns count. It will increase the columns count automatically.
	 * 
	 * @param headers
	 *            headers text.
	 */
	public void setColumnHeaders(String[] headers) {
		if (headers.length > getColumnCount()) {
			setColumnsCount(headers.length);
		}
		for (int i = 0; i < headers.length; i++) {
			tableViewer.getTable().getColumn(i).setText(headers[i]);
		}
	}
	
	

	/**
	 * Show/hide table column headers.
	 * 
	 * @param show
	 *            the new visibility state
	 */
	public void setColumnHeaderVisible(boolean show) {
		tableViewer.getTable().setHeaderVisible(show);
	}

	/**
	 * Set number of columns. If the new count is less than old count, columns
	 * from right will be deleted. If the new count is more than old count, new
	 * columns will be appended to the right.
	 * 
	 * @param count
	 *            number of columns.
	 */
	public void setColumnsCount(int count) {
		TableColumn[] columns = tableViewer.getTable().getColumns();
		int oldCount = getColumnCount();
		if (count == oldCount)
			return;
		if (count < oldCount) {
			for (int i = count; i < oldCount; i++) {
				columns[i].dispose();
			}
			for (List<String> row : input) {
				for (int i = oldCount - 1; i > count - 1; i--) {
					row.remove(i);
				}
			}
			return;
		}

		// if count > old count
		for (List<String> row : input) {
			for (int i = 0; i < count - oldCount; i++) {
				row.add("");
			}
		}

		for (int i = oldCount; i < count; i++) {
			final TableViewerColumn viewerColumn = new TableViewerColumn(
					tableViewer, SWT.NONE);
			viewerColumn.getColumn().setMoveable(false);
			viewerColumn.getColumn().setWidth(DEFAULT_COLUMN_WIDTH);
			TextEditingSupport textEditingSupport = new TextEditingSupport(tableViewer);
			viewerColumn.setEditingSupport(textEditingSupport);
			viewerColumn.getColumn().setData(TEXT_EDITING_SUPPORT_KEY, textEditingSupport);
		}
		tableViewer.setLabelProvider(new TextTableLableProvider());
		fireTableModified();

	}
	
	public void setColumnWidth(int col, int width) {
		checkColumnIndex(col);
		tableViewer.getTable().getColumn(col).setWidth(width);
	}

	/**
	 * Set width of each column. If length of the sizes array is larger than the
	 * existing columns count. It will increase the columns count automatically.
	 * 
	 * @param widthes
	 *            column size in pixels.
	 */
	public void setColumnWidths(int[] widthes){
		if (widthes.length > tableViewer.getTable().getColumnCount()) {
			setColumnsCount(widthes.length);
		}
		for (int i = 0; i < widthes.length; i++) {
			tableViewer.getTable().getColumn(i).setWidth(widthes[i]);
		}
	}

	/**
	 * Set content of the table.Old content in table will be replaced by the new
	 * content.
	 * 
	 * @param content
	 *            the new content.
	 */
	public void setContent(String[][] content) {
		Assert.isNotNull(content);
		input.clear();
		if (content.length <= 0) {
			tableViewer.refresh();
			return;
		}
		setColumnsCount(content[0].length);
		for (int i = 0; i < content.length; i++) {
			List<String> row = new ArrayList<String>(content[0].length);
			for (int j = 0; j < content[0].length; j++) {
				row.add(content[i][j]);
			}
			input.add(row);
		}		
		tableViewer.refresh();
		fireTableModified();
	}

	/**
	 * Set if the table is editable.
	 * 
	 * @param editable
	 *            true if table is editable.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		tableViewer.getTable().setFont(font);
	}
	
	/**
	 * Set input of the table. The input is the back of the table, so content of
	 * the input is always synchronized with content of the table.
	 * 
	 * @param input
	 *            input of the table.
	 */
	public void setInput(List<List<String>> input) {
		tableViewer.setInput(input);
		this.input = input;
		fireTableModified();
	}

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		tableViewer.getTable().setMenu(menu);
	}

	/**Set background color of the row.
	 * @param row row index of the cell.
	 * @param rgbColor color in RGB.
	 */
	public void setRowBackground(int row, RGB rgbColor){
		checkRowIndex(row);	
		tableViewer.getTable().getItem(row).setBackground(
				CustomMediaFactory.getInstance().getColor(rgbColor));
	}
	
	/**Set foreground color of the row.
	 * @param row row index of the cell.
	 * @param rgbColor color in RGB.
	 */
	public void setRowForeground(int row, RGB rgbColor){
		checkRowIndex(row);	
		tableViewer.getTable().getItem(row).setForeground(
				CustomMediaFactory.getInstance().getColor(rgbColor));
	}

}
