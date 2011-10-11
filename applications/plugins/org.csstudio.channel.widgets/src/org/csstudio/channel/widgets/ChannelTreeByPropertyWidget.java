package org.csstudio.channel.widgets;

import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.data.ExpressionLanguage.column;
import static org.epics.pvmanager.data.ExpressionLanguage.vStringConstants;
import static org.epics.pvmanager.data.ExpressionLanguage.vTable;
import static org.epics.pvmanager.util.TimeDuration.ms;
import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.channel.widgets.ChannelTreeByPropertyModel.Node;
import org.csstudio.utility.channelfinder.CFClientManager;
import org.csstudio.utility.channelfinder.ChannelQuery;
import org.csstudio.utility.channelfinder.ChannelQueryListener;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.widgets.VTableDisplay;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VTable;
import org.epics.pvmanager.data.VTableColumn;

public class ChannelTreeByPropertyWidget extends Composite {
	
	private String channelQuery;
	
	private Tree tree;
	private ErrorBar errorBar;
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	private ChannelTreeByPropertyModel model;

	public Tree getTree() {
		return tree;
	}
	
	public ChannelTreeByPropertyWidget(Composite parent, int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		tree = new Tree(this, SWT.VIRTUAL | SWT.BORDER);
		tree.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				if (model != null) {
					TreeItem item = (TreeItem)event.item;
					TreeItem parentItem = item.getParentItem();
					ChannelTreeByPropertyNode parentNode;
					ChannelTreeByPropertyNode node;
					int index;
					if (parentItem == null) {
						parentNode = model.getRoot();
						index = tree.indexOf(item);
					} else {
						parentNode = (ChannelTreeByPropertyNode) parentItem.getData();
						index = parentItem.indexOf(item);
					}
					node = parentNode.getChild(index);
					item.setData(node);
					item.setText(node.getDisplayName());
					if (node.getChildrenNames() == null) {
						item.setItemCount(0);
					} else {
						item.setItemCount(node.getChildrenNames().size());
					}
				}
			}
		});
		tree.setItemCount(0);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			List<String> properties = Arrays.asList("channels", "rowProperty", "columnProperty");
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					computeTree();
				}
			}
		});
		
		changeSupport.addPropertyChangeListener("channelQuery", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				queryChannels();
			}
		});
	}
	
	private void setLastException(Exception ex) {
		errorBar.setException(ex);
	}
	
    public void addPropertyChangeListener( PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener ) {
    	changeSupport.removePropertyChangeListener( listener );
    }
	
	public String getChannelQuery() {
		return channelQuery;
	}
	
	public void setChannelQuery(String channelQuery) {
		String oldValue = this.channelQuery;
		this.channelQuery = channelQuery;
		changeSupport.firePropertyChange("channelQuery", oldValue, channelQuery);
	}
	
	public Collection<Channel> getChannels() {
		return channels;
	}
	
	private Collection<Channel> channels = new ArrayList<Channel>();
	private List<String> properties = new ArrayList<String>();
	
	private void setChannels(Collection<Channel> channels) {
		Collection<Channel> oldChannels = this.channels;
		this.channels = channels;
		changeSupport.firePropertyChange("channels", oldChannels, channels);
	}
	
	public List<String> getProperties() {
		return properties;
	}
	
	public void setProperties(List<String> properties) {
		List<String> oldProperties = this.properties;
		this.properties = properties;
		computeTree();
		changeSupport.firePropertyChange("properties", oldProperties, properties);
	}
	
	private void queryChannels() {
		setChannels(new ArrayList<Channel>());
		tree.setItemCount(0);
		tree.clearAll(true);
		final ChannelQuery query = ChannelQuery.Builder.query(channelQuery).create();
		query.addChannelQueryListener(new ChannelQueryListener() {
			
			@Override
			public void getQueryResult() {
				SWTUtil.swtThread().execute(new Runnable() {
					
					@Override
					public void run() {
						Exception e = query.getLastException();
						if (e == null) {
							setChannels(query.getResult());
							List<String> newProperties = new ArrayList<String>(getProperties());
							newProperties.retainAll(ChannelUtil.getPropertyNames(channels));
							if (newProperties.size() != getProperties().size()) {
								setProperties(newProperties);
							}
							computeTree();
						} else {
							errorBar.setException(e);
						}
					}
				});
				
			}
		});
		query.execute();
	}
	
	private void computeTree() {
		tree.setItemCount(0);
		tree.clearAll(true);
		model = new ChannelTreeByPropertyModel(getChannels(), getProperties());
		tree.setItemCount(model.getRoot().getChildrenNames().size());
	}
}
