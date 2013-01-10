package org.csstudio.logbook.ologviewer;

import javax.security.auth.Refreshable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;

import edu.msu.nscl.olog.api.Log;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.jface.viewers.TableTreeViewer;

public class OlogDetailWidget extends Composite implements ISelectionProvider {
	private static class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private Label lblDate;
	private Label lblDateValue;
	private Text text;
	private Table logbookTable;

	private Log log;
	private TableViewer logbookTableViewer;
	public PropertyTree propertyTree;
	private Table tagTable;
	private TableViewer tagTableViewer;
	private TableColumn tagTableColumn;
	private TableViewerColumn tagTableViewerColumn;
	
	private ISelectionProvider selectionProvider;

	public OlogDetailWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		lblDate = new Label(this, SWT.NONE);
		FormData fd_lblDate = new FormData();
		fd_lblDate.top = new FormAttachment(0, 2);
		fd_lblDate.left = new FormAttachment(0, 2);
		lblDate.setLayoutData(fd_lblDate);
		lblDate.setText("Date:");

		lblDateValue = new Label(this, SWT.NONE);
		FormData fd_lblDateValue = new FormData();
		fd_lblDateValue.right = new FormAttachment(100, -2);
		fd_lblDateValue.top = new FormAttachment(0, 2);
		fd_lblDateValue.left = new FormAttachment(0, 39);
		lblDateValue.setLayoutData(fd_lblDateValue);

		text = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(100, -302);
		fd_text.top = new FormAttachment(lblDate);
		fd_text.bottom = new FormAttachment(100);
		fd_text.left = new FormAttachment(0, 2);
		text.setLayoutData(fd_text);

		logbookTableViewer = new TableViewer(this, SWT.BORDER
				| SWT.FULL_SELECTION);
		logbookTable = logbookTableViewer.getTable();
		logbookTable.setHeaderVisible(true);
		FormData fd_logbookTable = new FormData();
		fd_logbookTable.left = new FormAttachment(text, 2);
		fd_logbookTable.top = new FormAttachment(0, 22);
		fd_logbookTable.right = new FormAttachment(100, -2);
		logbookTable.setLayoutData(fd_logbookTable);
		logbookTableViewer.setContentProvider(new ContentProvider());

		TableViewerColumn logbookTableViewerColumn = new TableViewerColumn(
				logbookTableViewer, SWT.LEFT);
		logbookTableViewerColumn.getColumn().setText("Logbook:");
		logbookTableViewerColumn.getColumn().setWidth(200);
		logbookTableViewerColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText((String) cell.getElement());
			}
		});
		logbookTableViewer.refresh();

		tagTableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		tagTable = tagTableViewer.getTable();
		tagTable.setHeaderVisible(true);
		FormData fd_tagTable = new FormData();
		fd_tagTable.left = new FormAttachment(text, 2);
		fd_tagTable.top = new FormAttachment(logbookTable, 2);
		fd_tagTable.right = new FormAttachment(100, -2);
		tagTable.setLayoutData(fd_tagTable);

		tagTableViewerColumn = new TableViewerColumn(tagTableViewer, SWT.LEFT);
		tagTableColumn = tagTableViewerColumn.getColumn();
		tagTableColumn.setWidth(200);
		tagTableColumn.setText("Tags:");
		tagTableViewerColumn.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				cell.setText((String) cell.getElement());
			}
		});
		tagTableViewer.setContentProvider(new ContentProvider());
		tagTableViewer.refresh();
	
		propertyTree = new PropertyTree(this, SWT.BORDER);
		FormData fd_propertyTable = new FormData();
		fd_propertyTable.top = new FormAttachment(tagTable, 4);
		fd_propertyTable.bottom = new FormAttachment(text, 0, SWT.BOTTOM);
		fd_propertyTable.left = new FormAttachment(text, 2);
		fd_propertyTable.right = new FormAttachment(100, -2);
		propertyTree.setLayoutData(fd_propertyTable);
		
		selectionProvider = propertyTree;
	}

	public void setLog(Log log) {
		this.log = log;
		logUpdate();
	}

	private void logUpdate() {
		this.lblDateValue.setText(log.getCreatedDate().toString());
		text.setText(log.getDescription());
		logbookTableViewer.setInput(log.getLogbookNames().toArray(
				new String[log.getLogbookNames().size()]));
		tagTableViewer.setInput(log.getTagNames().toArray(
				new String[log.getTagNames().size()]));
		propertyTree.setProperties(log.getProperties());
		update();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
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
}
