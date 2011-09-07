package org.csstudio.display.multichannelviewer.views;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.utility.channelfinder.ChannelQuery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MultiChannelView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.display.multichannelviewer.views.MultiChannelView";
	private Text textChannelQuery;
	private Combo combo;
	private MultiChannelGraph graph;

	private volatile Collection<Channel> channels;

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

		textChannelQuery = new Text(parent, SWT.BORDER);
		textChannelQuery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					graph.setQueryString(textChannelQuery.getText());
				}
			}
		});
		textChannelQuery.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Label lblSortBy = new Label(parent, SWT.NONE);
		lblSortBy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSortBy.setText("Sorted By: ");

		combo = new Combo(parent, SWT.NONE);
		combo.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("sort by : "+combo.getText());
				graph.setSortProperty(combo.getText());
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		graph = new MultiChannelGraph(parent, SWT.NONE);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		graph.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getPropertyName());
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
	}
}