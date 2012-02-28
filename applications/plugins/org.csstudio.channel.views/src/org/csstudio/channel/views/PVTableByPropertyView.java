package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQueryListener;
import gov.bnl.channelfinder.api.ChannelQuery.Result;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.ChannelQueryInputBar;
import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class PVTableByPropertyView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.PVTableByPropertyView";

	/** Memento */
	private IMemento memento = null;
	
	/** Memento tag */
	private static final String MEMENTO_QUERY = "ChannelQuery"; //$NON-NLS-1$
	private static final String MEMENTO_ROW_PROPERTY = "RowProperty"; //$NON-NLS-1$
	private static final String MEMENTO_COLUMN_PROPERTY = "ColumnProperty"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public PVTableByPropertyView() {
	}
	
	private final ChannelQueryListener channelQueryListener = new ChannelQueryListener() {
		
		@Override
		public void queryExecuted(final Result result) {
			SWTUtil.swtThread().execute(new Runnable() {
				
				@Override
				public void run() {
					configureButton.setEnabled(result.channels != null && !result.channels.isEmpty());
				}
			});
		}
	};

	private Button configureButton;

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
		if (inputBar.getChannelQuery() != null) {
			memento.putString(MEMENTO_QUERY, inputBar.getChannelQuery().getQuery());
		}
		memento.putString(MEMENTO_ROW_PROPERTY, tableWidget.getRowProperty());
		memento.putString(MEMENTO_COLUMN_PROPERTY, tableWidget.getColumnProperty());		
	}
	
	public void setChannelQuery(ChannelQuery channelQuery) {
		inputBar.setChannelQuery(channelQuery);
		ChannelQuery oldQuery = tableWidget.getChannelQuery();
		if (oldQuery != null) {
			oldQuery.removeChannelQueryListener(channelQueryListener);
		}
		channelQuery.execute(channelQueryListener);
		tableWidget.setChannelQuery(channelQuery);
	}
	
	private ChannelQueryInputBar inputBar;
	private PVTableByPropertyWidget tableWidget;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 8);
		fd_lblPvName.left = new FormAttachment(0, 5);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("Query:");
		
		inputBar = new ChannelQueryInputBar(parent, SWT.NONE, 
				Activator.getDefault().getDialogSettings(), "pvtablebyproperty.query");
		FormData fd_combo = new FormData();
		fd_combo.left = new FormAttachment(lblPvName, 6);
		fd_combo.top = new FormAttachment(0, 5);
		inputBar.setLayoutData(fd_combo);
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					setChannelQuery((ChannelQuery) event.getNewValue());
				}
			}
		});
		
		tableWidget = new PVTableByPropertyWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -5);
		fd_waterfallComposite.left = new FormAttachment(0, 5);
		fd_waterfallComposite.right = new FormAttachment(100, -5);
		tableWidget.setLayoutData(fd_waterfallComposite);
		
		if (memento != null) {
			tableWidget.setRowProperty(memento.getString(MEMENTO_ROW_PROPERTY));
			tableWidget.setColumnProperty(memento.getString(MEMENTO_COLUMN_PROPERTY));
			if (memento.getString(MEMENTO_QUERY) != null) {
				setChannelQuery(ChannelQuery.query(memento.getString(MEMENTO_QUERY)).build());
			}
		}
		
		PopupMenuUtil.installPopupForView(tableWidget, getSite(), tableWidget);
		PopupMenuUtil.installPopupForView(inputBar, getSite(), inputBar);
		
		configureButton = new Button(parent, SWT.NONE);
		fd_waterfallComposite.top = new FormAttachment(configureButton, 4);
		fd_combo.right = new FormAttachment(configureButton, -6);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(0, 5);
		fd_btnNewButton.right = new FormAttachment(tableWidget, 0, SWT.RIGHT);
		configureButton.setLayoutData(fd_btnNewButton);
		configureButton.setText("Configure");
		configureButton.setEnabled(false);
		configureButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableWidget.openConfigurationDialog();
			}
		});

	}
}