package org.csstudio.utility.toolbox.view.dialogs;

import java.util.List;

import org.csstudio.utility.toolbox.entities.Article;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ContainsDialog extends TitleAreaDialog {

	private static final int FIXED_ID = 9999;
	
	private List<Article> installedArticles;
	private String articleDescription;
	 
	private TableViewer viewer;

	public ContainsDialog(Shell parentShell, String articleDescription, List<Article> installedArticles) {
		super(parentShell);
		this.articleDescription = articleDescription;
		this.installedArticles = installedArticles;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Article Installed in");
		setMessage("The following articles are installed in: " + articleDescription, IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		createViewer(parent);
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, FIXED_ID, "Ok", true);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				okPressed();
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(installedArticles);

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	public TableViewer getViewer() {
		return viewer;
	}

	// This will create the columns for the table
	private void createColumns() {
		String[] titles = { "Internal-ID", "Description"};
		int[] bounds = {100,500};

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Article article = (Article) element;
				return article.getInternId();
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Article article = (Article) element;
				return article.getBeschreibung();
			}
		});


	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

}