package org.csstudio.channel.widgets;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;

/**
 * A tree constructed by a query to channel finder and a set of properties.
 * 
 * @author carcassi
 * 
 */
public class ChannelTreeByPropertyWidget extends AbstractChannelQueryResultWidget
implements ConfigurableWidget, ISelectionProvider {
	
	private Tree tree;
	private ErrorBar errorBar;
	private ISelectionProvider selectionProvider;
	private List<String> properties = new ArrayList<String>();
	private ChannelTreeByPropertyModel model;
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		tree.setMenu(menu);
	}
	
	public ChannelTreeByPropertyWidget(Composite parent, int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		errorBar.setMarginBottom(5);
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			List<String> properties = Arrays.asList("properties");
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					computeTree();
				}
			}
		});
		
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
		selectionProvider = SelectionProviders.treeItemDataSelectionProvider(tree);
	}
	
	private boolean showChannelNames = true;
	
	public boolean isShowChannelNames() {
		return showChannelNames;
	}
	
	public void setShowChannelNames(boolean showChannelNames) {
		boolean oldShowChannelNames = showChannelNames;
		this.showChannelNames = showChannelNames;
		computeTree();
		changeSupport.firePropertyChange("showChannelNames", oldShowChannelNames, showChannelNames);
	}
	
	/**
	 * The properties, in the correct order, used to create the tree.
	 * 
	 * @return a list of property names
	 */
	public List<String> getProperties() {
		return properties;
	}
	
	/**
	 * Changes the properties that are used to create the tree.
	 * 
	 * @param properties a list of property names
	 */
	public void setProperties(List<String> properties) {
		List<String> oldProperties = this.properties;
		this.properties = properties;
		tree.setItemCount(0);
		tree.clearAll(true);
		changeSupport.firePropertyChange("properties", oldProperties, properties);
	}
	
	@Override
	protected void queryCleared() {
		tree.setItemCount(0);
		tree.clearAll(true);
		errorBar.setException(null);
	}
	
	@Override
	protected void queryExecuted(Result result) {
		errorBar.setException(result.exception);
		if (result.exception == null) {
			List<String> newProperties = new ArrayList<String>(getProperties());
			newProperties.retainAll(ChannelUtil.getPropertyNames(result.channels));
			if (newProperties.size() != getProperties().size()) {
				setProperties(newProperties);
			}
			computeTree();
		}
	}
	
	private void computeTree() {
		tree.setItemCount(0);
		tree.clearAll(true);
		if (getChannelQuery() == null) {
			model = new ChannelTreeByPropertyModel(null, null, getProperties(), this, showChannelNames);
		} else if (getChannelQuery().getResult() == null) {
			model = new ChannelTreeByPropertyModel(getChannelQuery().getQuery(), null, getProperties(), this, showChannelNames);
		} else {
			model = new ChannelTreeByPropertyModel(getChannelQuery().getQuery(), getChannelQuery().getResult().channels, getProperties(), this, showChannelNames);
		}
		if (model.getRoot().getChildrenNames() != null) {
			tree.setItemCount(model.getRoot().getChildrenNames().size());
		}
	}
	
	private boolean configurable = true;
	
	private ChannelTreeByPropertyConfigurationDialog dialog;
	
	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new ChannelTreeByPropertyConfigurationDialog(this);
		dialog.open();
	}
	
	private final String MEMENTO_CHANNEL_QUERY = "channelQuery";
	private final String MEMENTO_PROPERTIES = "properties";
	private final String MEMENTO_SHOW_CHANNEL_NAMES = "showChanelNames";
	
	public void saveState(IMemento memento) {
		if (getChannelQuery() != null) {
			memento.putString(MEMENTO_CHANNEL_QUERY, getChannelQuery().getQuery());
		}
		if (!getProperties().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String property : getProperties()) {
				sb.append(property).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			memento.putString(MEMENTO_PROPERTIES, sb.toString());
		}
		memento.putBoolean(MEMENTO_SHOW_CHANNEL_NAMES, isShowChannelNames());
	}
	
	public void loadState(IMemento memento) {
		if (memento != null) {
			if (memento.getString(MEMENTO_CHANNEL_QUERY) != null) {
				setChannelQuery(ChannelQuery.query(memento.getString(MEMENTO_CHANNEL_QUERY)).build());
			}
			if (memento.getString(MEMENTO_PROPERTIES) != null) {
				setProperties(Arrays.asList(memento.getString(MEMENTO_PROPERTIES).split(",")));
			}
			if (memento.getBoolean(MEMENTO_SHOW_CHANNEL_NAMES) != null) {
				setShowChannelNames(memento.getBoolean(MEMENTO_SHOW_CHANNEL_NAMES));
			}
		}
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		boolean oldConfigurable = configurable;
		this.configurable = configurable;
		changeSupport.firePropertyChange("configurable", oldConfigurable, configurable);
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		return dialog != null;
	}

	@Override
	public void configurationDialogClosed() {
		dialog = null;
	}

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
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
