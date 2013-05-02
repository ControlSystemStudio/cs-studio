package org.csstudio.frib.opiTemplate.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelUtil;
import gov.bnl.channelfinder.api.Property;
import static gov.bnl.channelfinder.api.Property.Builder.property;

import org.csstudio.channel.widgets.AbstractChannelQueryResultWidget;
import org.csstudio.channel.widgets.AbstractSelectionProviderWrapper;
import org.csstudio.channel.widgets.ConfigurableWidget;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.runmode.PatchedScalableFreeformRootEditPart;
import org.csstudio.opibuilder.runmode.PatchedScrollingGraphicalViewer;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class OPITemplateWidget extends AbstractChannelQueryResultWidget
		implements ISelectionProvider, ConfigurableWidget {

	private Collection<Channel> channels = new ArrayList<Channel>();
	private AbstractSelectionProviderWrapper selectionProvider;
	private ErrorBar errorBar;

	private List<String> properties;
	private List<String> propertyValues;
	private List<String> tags;
	private Map<String, HashMap<String,Channel>> cmap;

	private boolean configurable = true;
	private OPITemplateConfigurationDialog dialog;

	final GroupingContainerModel parentGroupingContainer = new GroupingContainerModel();
	final GroupingContainerModel groupingContainer = new GroupingContainerModel();
	final DisplayModel displayModel = new DisplayModel();
	private PatchedScrollingGraphicalViewer viewer;;

	public OPITemplateWidget(Composite parent, int style) {
		super(parent, style);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
		setLayout(gridLayout);
		
		errorBar = new ErrorBar(this, SWT.NONE);
		errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		errorBar.setMarginBottom(5);
		//parentGroupingContainer.setSize(1000, 1000);
		//parentGroupingContainer.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT, true);
		displayModel.addChild(groupingContainer);
		//parentGroupingContainer.addChild(groupingContainer);
		viewer = new PatchedScrollingGraphicalViewer();
		
		ScalableFreeformRootEditPart root = new PatchedScalableFreeformRootEditPart() {
			@Override
			public boolean isSelectable() {
				return false;
			}
		};
		viewer.createControl(this);
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new WidgetEditPartFactory(
				ExecutionMode.RUN_MODE));

		viewer.setContents(displayModel);

		
	
		addPropertyChangeListener(new PropertyChangeListener() {

			List<String> properties = Arrays.asList("channels", "properties",
					"tags");

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					updateWidget();
				}	
			}
		});

	}
	
	protected InputStream getDisplayContents(DisplayModel displayModel) {
		SchemaService.getInstance().applySchema(displayModel);
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}

	private void updateWidget() {
		groupingContainer.removeAllChildren();
		if (cmap != null) {
		for (Map.Entry<String, HashMap<String,Channel>> entry : cmap.entrySet()) {

			IPath path = ResourceUtil
					.getPathFromString("platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/"
							+ entry.getKey() + "_header.opi");
			if (!ResourceUtil.isExsitingFile(path, false)) {
				continue;
			}

			LinkingContainerModel linkingContainerHeader = new LinkingContainerModel();
			linkingContainerHeader.setName(entry.getKey() + "Header");
			linkingContainerHeader.setPropertyValue("opi_file",
					"platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/"
							+ entry.getKey() + "_header.opi");
			linkingContainerHeader.setPropertyValue("auto_size", true);
			linkingContainerHeader.setPropertyValue("zoom_to_fit", false);
			linkingContainerHeader.setPropertyValue("border_style", 0);
			Rectangle range = getChildrenRange(groupingContainer);
			linkingContainerHeader.setY(range.y + range.height);
			groupingContainer.addChild(linkingContainerHeader);

			Iterator itr = entry.getValue().keySet().iterator(); 
			for (Integer i = 0; i < entry.getValue().keySet().size(); i++) {
				Channel chan = entry.getValue().get(itr.next());
				LinkingContainerModel linkingContainer = new LinkingContainerModel();
				linkingContainer.setPropertyValue("opi_file",
						"platform:/plugin/org.csstudio.frib.opiTemplate/opi/device_templates/"
								+ entry.getKey() + ".opi");
				linkingContainer.setPropertyValue("auto_size", true);
				linkingContainer.setPropertyValue("zoom_to_fit", false);
				linkingContainer.setPropertyValue("border_style", 0);
				linkingContainer.addMacro("index", i.toString());
				for(Property prop : chan.getProperties()){
					linkingContainer.addMacro(prop.getName(), prop.getValue());
				}
				range = getChildrenRange(groupingContainer);
				linkingContainer.setY(range.y + range.height);
				groupingContainer.addChild(linkingContainer);
			}
			Rectangle allSize = getChildrenRange(groupingContainer);
			groupingContainer.setLocation(allSize.x, allSize.y);
			groupingContainer.setSize(allSize.width, allSize.height);
		}
		}
	}
	
	private static Rectangle getChildrenRange(AbstractContainerModel container) {
		PointList pointList = new PointList(container.getChildren().size());
		for (Object child : container.getChildren()) {
			AbstractWidgetModel childModel = ((AbstractWidgetModel) child);
			pointList.addPoint(childModel.getLocation());
			pointList.addPoint(childModel.getX() + childModel.getWidth(),
					childModel.getY() + childModel.getHeight());
		}
		return pointList.getBounds();
	}

	public Collection<Channel> getChannels() {
		return channels;
	}

	private void setChannels(Collection<Channel> channels) {
		Collection<Channel> oldChannels = this.channels;
		this.channels = channels;
		if (channels != null) {
			this.properties = new ArrayList<String>(
					ChannelUtil.getPropertyNames(channels));
			this.propertyValues = new ArrayList<String>(
					ChannelUtil.getPropValues(channels, "Device"));
			this.tags = new ArrayList<String>(
					ChannelUtil.getAllTagNames(channels));
			this.cmap =  new TreeMap<String, HashMap<String,Channel>>();
			
			for (String propertyValue: propertyValues){
				HashMap<String,Channel> ids = new HashMap<String,Channel>();
				Collection<Channel> chans = filterByProperty(channels, "Device", propertyValue);
				Set<String> unique = new HashSet<String>(ChannelUtil.getPropValues(chans, "D"));
				for (String id : unique){
					ids.put(id, filterByProperty(chans, "D", id).iterator().next());
				}
				cmap.put(propertyValue, ids);
			}
		} else {
			this.properties = Collections.emptyList();
			this.tags = Collections.emptyList();
			this.propertyValues = Collections.emptyList();
			this.cmap = Collections.emptyMap();
		}
		changeSupport.firePropertyChange("channels", oldChannels, channels);
	}
	
	private Collection<Channel> filterByProperty(Collection<Channel> channels, String propertyName, String propertyValue){
        Collection<Channel> result = new ArrayList<Channel>();
        for (Channel channel : channels) {
            if(channel.getPropertyNames().contains(propertyName)){
            	if(channel.getProperty(propertyName).getValue().equals(propertyValue)){
            		result.add(channel);
            	}
            }
        }
        return result;
    }

	public Collection<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		List<String> oldProperties = this.properties;
		this.properties = properties;
		changeSupport.firePropertyChange("properties", oldProperties,
				properties);
	}

	public Collection<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		List<String> oldTags = this.tags;
		this.tags = tags;
		changeSupport.firePropertyChange("tags", oldTags, tags);
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		boolean oldConfigurable = configurable;
		this.configurable = configurable;
		changeSupport.firePropertyChange("configurable", oldConfigurable,
				configurable);
	}

	@Override
	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new OPITemplateConfigurationDialog(this);
		dialog.open();
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
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
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

	@Override
	protected void queryCleared() {
		this.channels = null;
		this.errorBar.setException(null);
		setChannels(null);
	}

	@Override
	protected void queryExecuted(Result result) {
		Exception e = result.exception;
		errorBar.setException(e);
		if (e == null) {
			setChannels(result.channels);
		}
	}

}
