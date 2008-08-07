package org.csstudio.config.kryonamebrowser.ui;

import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class FilterComposite extends Composite {

	private Text text;
	private Combo combo_1;
	private Combo combo;
	private TableViewer viewer;

	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public FilterComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());

		combo_1 = new Combo(this, SWT.NONE);

		text = new Text(this, SWT.BORDER);

		combo = new Combo(this, SWT.NONE);

		final Button searchButton = new Button(this, SWT.NONE);
		searchButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				searchButton.setEnabled(false);
				viewer.setInput(new KryoNameEntry());
				searchButton.setEnabled(true);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		searchButton.setText("search");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
