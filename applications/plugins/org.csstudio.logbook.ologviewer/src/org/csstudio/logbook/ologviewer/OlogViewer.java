package org.csstudio.logbook.ologviewer;

import org.csstudio.logbook.ologviewer.OlogQuery.Result;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class OlogViewer extends ViewPart {

	private OlogWidget ologWidget;
	private ComboViewer comboViewer;
	private Label lblSearch;

	public OlogViewer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());

		lblSearch = new Label(parent, SWT.NONE);
		lblSearch.setText("Search:");
		FormData fd_lblSearch = new FormData();
		lblSearch.setLayoutData(fd_lblSearch);

		comboViewer = new ComboViewer(parent, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		fd_lblSearch.top = new FormAttachment(combo, 3, SWT.TOP);
		fd_lblSearch.right = new FormAttachment(combo, -6);
		combo.setToolTipText("Space seperated search criterias, patterns may include * and ? wildcards");

		ComboHistoryHelper name_helper = new ComboHistoryHelper(Activator
				.getDefault().getDialogSettings(),
				"org.csstudio.logbook.ologviewer", combo, 20, true) {

			@Override
			public void newSelection(final String queryText) {
//				ologWidget.setLogs(TestLogs.testLogs());
				try {
					ologWidget.setOlogQuery(new OlogQuery(queryText));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -10);
		fd_combo.left = new FormAttachment(0, 63);
		fd_combo.top = new FormAttachment(0, 10);
		combo.setLayoutData(fd_combo);

		ologWidget = new OlogWidget(parent, SWT.None);
		FormData fd_ologWidget = new FormData();
		fd_ologWidget.top = new FormAttachment(lblSearch, 10);
		fd_ologWidget.right = new FormAttachment(100, -10);
		fd_ologWidget.left = new FormAttachment(0, 10);
		fd_ologWidget.bottom = new FormAttachment(100, -10);
		ologWidget.setLayoutData(fd_ologWidget);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
