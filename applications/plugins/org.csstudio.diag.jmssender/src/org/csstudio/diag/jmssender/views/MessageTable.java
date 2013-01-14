/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.diag.jmssender.views;

import org.csstudio.diag.jmssender.internationalization.Messages;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class MessageTable extends TableViewer {

//	private Table table;
	private TableColumn column;

	private PropertyList propertyList = new PropertyList();

	private String[] columnNames = new String[] {
			Messages.MessageTable_ColumnProperty,
			Messages.MessageTable_ColumnValue
			};

	public MessageTable(Table table) {
		super(table);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		for(int i=0;i<columnNames.length;i++){
	        column = new TableColumn(table, SWT.LEFT, i);
	        column.setText(columnNames[i]);
	        column.setWidth(200);
		}

		this.setColumnProperties(columnNames);

		CellEditor[] editors = new CellEditor[columnNames.length];
		TextCellEditor textEditor = new TextCellEditor(table);
		editors[0] = textEditor;
		textEditor = new TextCellEditor(table);
		editors[1] = textEditor;

		this.setCellEditors(editors);
		this.setContentProvider(new MessageContentProvider());
		this.setLabelProvider(new MessageLabelProvider());
		this.setCellModifier(new MessagePropertyModifier());
		this.setInput(propertyList);

	}


		class MessageContentProvider implements IStructuredContentProvider, IMessageViewer {

			public Object[] getElements(Object inputElement) {
				return propertyList.getProperties().toArray();
			}

			public void dispose() {
				propertyList.removeChangeListener(this);
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				if (newInput != null)
					((PropertyList) newInput).addChangeListener(this);
				if (oldInput != null)
					((PropertyList) oldInput).removeChangeListener(this);
			}

			public void addProperty(Property property) {
				MessageTable.this.add(property);
			}

			public void removeProperty(Property property) {
				MessageTable.this.remove(property);
			}

			public void updateProperty(Property property) {
				MessageTable.this.update(property, null);
			}

		}

		class MessageLabelProvider extends LabelProvider implements ITableLabelProvider {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				Property property = (Property) element;
				if (columnIndex == 0) {
					return (property.getProperty());
				} else {
					return (property.getValue());
				}
			}

		}

	class MessagePropertyModifier implements ICellModifier {

			public boolean canModify(Object element, String property) {
				if(property.equals("Property")) { //$NON-NLS-1$
					return false;
				} else {
					return true;
				}
			}

			public Object getValue(Object element, String property) {
				Property p = (Property) element;
				if (property.equals("Property")) { //$NON-NLS-1$
					return p.getProperty();
				} else {
					return p.getValue();
				}
			}

			public void modify(Object element, String property, Object value) {

				TableItem item = (TableItem) element;
				Property p = (Property) item.getData();
				String valueString = ((String) value).trim();;

				if(property.equals("Property")) { //$NON-NLS-1$
					p.setProperty(valueString);
				} else {
					p.setValue(valueString);
				}
				MessageTable.this.propertyList.firePropertyChangedEvent(p);

			}

		}

	public void init() {
		//propertyList.clearList();
		propertyList.initData();
		this.refresh();
	}

	public PropertyList getPropertyList() {
		return propertyList;
	}
}
