/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.icsiocmonitor.ui.internal;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * A view which displays the state of all IOCs as reported by the
 * Interconnection Servers.
 * 
 * @author Joerg Rathlev
 */
public class IocMonitorView extends ViewPart {
	
	/**
	 * @author Joerg Rathlev
	 *
	 */
	private static class IocMonitorLabelProvider extends BaseLabelProvider
			implements ITableLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IocState) {
				switch (columnIndex) {
				case 0:
					return ((IocState) element).getIocName();
				case 1:
					return ((IocState) element).getSelectedInterconnectionServer();
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * @author Joerg Rathlev
	 *
	 */
	private static class IocMonitorContentProvider implements
			IStructuredContentProvider {

		/**
		 * {@inheritDoc}
		 */
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IocState[]) {
				return (Object[]) inputElement;
			} else {
				return new Object[0];
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void dispose() {
		}

		/**
		 * {@inheritDoc}
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	static final String ID = "org.csstudio.diag.icsiocmonitor.ui.IocMonitorView";

	private TableViewer _tableViewer;

	private Table _table;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		
		_table = createTable(parent);
		_table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		_tableViewer = new TableViewer(_table);
		_tableViewer.setContentProvider(new IocMonitorContentProvider());
		_tableViewer.setLabelProvider(new IocMonitorLabelProvider());
		_tableViewer.setInput(new IocState[] {
				new IocState("a", "1"),
				new IocState("b", "2"),
				new IocState("c", "3"),
			});
	}

	/**
	 * @return
	 */
	private Table createTable(Composite parent) {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableColumn iocCol = new TableColumn(table, SWT.LEFT);
		iocCol.setText("IOC");
		iocCol.setWidth(200);
		TableColumn selectedIcsCol = new TableColumn(table, SWT.LEFT);
		selectedIcsCol.setText("Selected ICS");
		selectedIcsCol.setWidth(200);
		return table;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
	}
}
