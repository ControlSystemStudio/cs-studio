package org.csstudio.display.multichannelviewer.views;

import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.display.multichannelviewer.Activator;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class MultiChannelView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.display.multichannelviewer.views.MultiChannelView";
	private static final String QUERY_LIST_TAG = "query_list";
	private Combo combo;
	private MultiChannelGraph graph;
	
	private ComboViewer comboViewer;
	@SuppressWarnings("unused")
	private ComboHistoryHelper comboHistoryHelper;

	/**
	 * The constructor.
	 */
	public MultiChannelView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		Label lblChannels = new Label(parent, SWT.NONE);
		lblChannels.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblChannels.setText("Channels: ");

		comboViewer = new ComboViewer(parent, SWT.SINGLE | SWT.BORDER);
		comboViewer
				.getCombo()
				.setToolTipText(
						"space seperated search criterias, patterns may include * and ? wildcards\r\nchannelNamePatter\r\npropertyName=propertyValuePattern1,propertyValuePattern2\r\nTags=tagNamePattern\r\nEach criteria is logically ANDed and , or || seperated values are logically ORed\r\n");
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		comboViewer.getCombo().setLayoutData(gd);

		comboHistoryHelper = new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), QUERY_LIST_TAG, comboViewer.getCombo(),
				10, true) {

			@Override
			public void newSelection(String entry) {
				graph.setQueryString(comboViewer.getCombo().getText());
			}

		};

		Label lblSortBy = new Label(parent, SWT.NONE);
		lblSortBy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSortBy.setText("Sorted By: ");

		combo = new Combo(parent, SWT.NONE);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				System.out.println("sort by : "+combo.getText());
				graph.setSortProperty(combo.getText());
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		graph = new MultiChannelGraph(parent, SWT.NONE);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		graph.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
//				System.out.println(evt.getPropertyName());
				if (evt.getPropertyName().equals("channels")) {
					PlatformUI.getWorkbench().getDisplay()
							.asyncExec(new Runnable() {

								@Override
								public void run() {
									update();
								}
							});
				}
			}
		});

	}

	@Override
	public void setFocus() {

	}

	public void update() {
		List<String> propertyNames = new ArrayList<String>(
				ChannelUtil.getPropertyNames(graph.getChannels()));
		propertyNames.add("channel-name");
		Collections.sort(propertyNames);
		combo.setItems(propertyNames.toArray(new String[propertyNames.size()]));
		combo.setText(graph.getSortProperty());
	}
}