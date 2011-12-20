package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;

import java.util.Arrays;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * View that allows to create a tree view out of the results of a channel query.
 */
public class ChannelTreeByPropertyView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.ChannelTreeByPropertyView";

	/** Memento */
	private IMemento memento = null;
	
	/** Memento tags */
	private static final String MEMENTO_QUERY = "ChannelQuery"; //$NON-NLS-1$
	private static final String MEMENTO_PROPERTIES = "Property"; //$NON-NLS-1$
	
	private final ChannelQueryListener channelQueryListener = new ChannelQueryListener() {
		
		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {
				
				@Override
				public void run() {
					btnProperties.setEnabled(result.channels != null && !result.channels.isEmpty());
				}
			});
		}
	};
	
	/**
	 * The constructor.
	 */
	public ChannelTreeByPropertyView() {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException {
		super.init(site, memento);
		// Save the memento
		this.memento = memento;
	}

	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		// Save the currently selected variable
		if (combo.getText() != null) {
			memento.putString(MEMENTO_QUERY, combo.getText());
			if (!treeWidget.getProperties().isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String property : treeWidget.getProperties()) {
					sb.append(property).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				memento.putString(MEMENTO_PROPERTIES, sb.toString());
			}
		}
	}
	
	private void setQueryText(String text) {
		if (text == null)
			text = "";
		combo.setText(text);
		changeQuery(text);
	}
	
	private Combo combo;
	private ChannelTreeByPropertyWidget treeWidget;
	private Composite parent;
	private Button btnProperties;
	
	private void changeQuery(String text) {
		ChannelQuery oldQuery = treeWidget.getChannelQuery();
		if (text == null)
			text = "";
		text = text.trim();
		
		// Query is the same, do nothing
		if (oldQuery != null && oldQuery.getQuery().equals(text)) {
			return;
		}
		
		ChannelQuery newQuery = ChannelQuery.Builder.query(text).create();
		setChannelQuery(newQuery);
	}
	
	public void setChannelQuery(ChannelQuery query) {
		combo.setText(query.getQuery());
		ChannelQuery oldQuery = treeWidget.getChannelQuery();
		if (oldQuery != null) {
			oldQuery.removeChannelQueryListener(channelQueryListener);
		}
		query.execute(channelQueryListener);
		treeWidget.setChannelQuery(query);
	}
	
	public void configure() {
		treeWidget.openConfigurationDialog();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.left = new FormAttachment(0, 10);
		fd_lblPvName.top = new FormAttachment(0, 18);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("Query:");
		
		ComboViewer comboViewer = new ComboViewer(parent, SWT.NONE);
		combo = comboViewer.getCombo();
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 15);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		combo.setLayoutData(fd_combo);
		
		treeWidget = new ChannelTreeByPropertyWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.top = new FormAttachment(combo, 6);
		fd_waterfallComposite.bottom = new FormAttachment(100, -10);
		fd_waterfallComposite.left = new FormAttachment(0, 10);
		fd_waterfallComposite.right = new FormAttachment(100, -10);
		treeWidget.setLayoutData(fd_waterfallComposite);
		
		ComboHistoryHelper name_helper =
			new ComboHistoryHelper(Activator.getDefault()
				.getDialogSettings(), "WaterfallPVs", combo, 20, true) {
			@Override
			public void newSelection(final String pv_name) {
				changeQuery(pv_name);
			}
		};
		
		btnProperties = new Button(parent, SWT.NONE);
		fd_combo.right = new FormAttachment(btnProperties, -6);
		FormData fd_btnProperties = new FormData();
		fd_btnProperties.top = new FormAttachment(0, 13);
		fd_btnProperties.right = new FormAttachment(100, -10);
		btnProperties.setLayoutData(fd_btnProperties);
		btnProperties.setText("Properties");
		btnProperties.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configure();
			}
		});
		name_helper.loadSettings();
		
		if (memento != null) {
			setQueryText(memento.getString(MEMENTO_QUERY));
			if (memento.getString(MEMENTO_PROPERTIES) != null) {
				treeWidget.setProperties(Arrays.asList(memento.getString(MEMENTO_PROPERTIES).split(",")));
			}
		}
		
		PopupMenuUtil.installPopupForView(treeWidget, getSite(), treeWidget.getTreeSelectionProvider());
	}
}