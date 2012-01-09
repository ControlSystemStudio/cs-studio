package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.channel.widgets.ChannelQueryInputBar;
import org.csstudio.channel.widgets.PopupMenuUtil;
import org.csstudio.channel.widgets.WaterfallWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class WaterfallView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.WaterfallView";

	/** Memento */
	private IMemento memento = null;
	
	/** Memento tag */
	private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public WaterfallView() {
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
		if (inputBar.getChannelQuery() != null) {
			memento.putString(MEMENTO_PVNAME, inputBar.getChannelQuery().getQuery());
		}
	}
	
	public void setPVName(String name) {
		inputBar.setChannelQuery(ChannelQuery.Builder.query(name).create());
		resolveAndSetPVName(name);
	}
	
	private ChannelQueryInputBar inputBar;
	private WaterfallWidget waterfallComposite;
	
	private void resolveAndSetPVName(String text) {
		waterfallComposite.setInputText(text);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		
		Label lblPvName = new Label(parent, SWT.NONE);
		FormData fd_lblPvName = new FormData();
		fd_lblPvName.top = new FormAttachment(0, 13);
		fd_lblPvName.left = new FormAttachment(0, 10);
		lblPvName.setLayoutData(fd_lblPvName);
		lblPvName.setText("Query:");
		
		inputBar = new ChannelQueryInputBar(parent, SWT.NONE, 
				Activator.getDefault().getDialogSettings(), "waterfall.query");
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(lblPvName, 6);
		fd_combo.right = new FormAttachment(100, -10);
		inputBar.setLayoutData(fd_combo);
		inputBar.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("channelQuery".equals(event.getPropertyName())) {
					setPVName(((ChannelQuery) event.getNewValue()).getQuery());
				}
			}
		});
		
		waterfallComposite = new WaterfallWidget(parent, SWT.NONE);
		FormData fd_waterfallComposite = new FormData();
		fd_waterfallComposite.bottom = new FormAttachment(100, -10);
		fd_waterfallComposite.left = new FormAttachment(0, 10);
		fd_waterfallComposite.top = new FormAttachment(inputBar, 6);
		fd_waterfallComposite.right = new FormAttachment(inputBar, 0, SWT.RIGHT);
		waterfallComposite.setLayoutData(fd_waterfallComposite);
		
		if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
			setPVName(memento.getString(MEMENTO_PVNAME));
		}
		
		PopupMenuUtil.installPopupForView(inputBar, getSite(), inputBar);
	}

}