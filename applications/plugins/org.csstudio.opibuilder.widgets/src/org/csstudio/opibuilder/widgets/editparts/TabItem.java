package org.csstudio.opibuilder.widgets.editparts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.TabModel;
import org.csstudio.opibuilder.widgets.model.TabModel.TabProperty;

/**The tab item, which host all the properties data for a tab item.
 * @author Xihui Chen
 *
 */
public class TabItem {
	
	private GroupingContainerModel groupingContainerModel;

	private Map<TabProperty, Object> propertyMap;

	/**The tab item will be initialized with the corresponding tab properties value in tab model. 
	 * @param tabModel
	 * @param index
	 * @param groupingContainerModel
	 */
	public TabItem(TabModel tabModel, int index, GroupingContainerModel groupingContainerModel) {
		super();
		this.groupingContainerModel = groupingContainerModel;
		propertyMap = new HashMap<TabProperty, Object>();
		injectPropertiesValue(tabModel, index);
	}
	
	public TabItem(int index){
		this.groupingContainerModel = TabEditPart.createGroupingContainer();
		propertyMap = new HashMap<TabProperty, Object>();
		injectDefaultPropertiesValue(index);
	}
	
	private TabItem(GroupingContainerModel groupingContainerModel, 
			Map<TabProperty, Object> propertyMap){
		this.groupingContainerModel = groupingContainerModel;
		this.propertyMap = propertyMap;
		
	}

	
	public void setPropertyValue(TabProperty tabProperty, Object value){
		propertyMap.put(tabProperty, value);
	}
	
	/**Copy the properties from TabModel to this tab item.
	 * @param tabModel
	 * @param index
	 */
	public void injectPropertiesValue(TabModel tabModel, int index){
		for(TabProperty tabProperty : TabProperty.values()){
			propertyMap.put(tabProperty, 
					tabModel.getTabPropertyValue(index, tabProperty));
		}
	}
	
	/**Copy the default properties value from TabModel to this tab item.
	 * @param index the index of the tab item.
	 */
	public void injectDefaultPropertiesValue(int index){
		TabModel tempModel = new TabModel();
		for(TabProperty tabProperty : TabProperty.values()){
			String propID = TabModel.makeTabPropID(
					tabProperty.propIDPre, index);			
			propertyMap.put(tabProperty, tempModel.getProperty(propID).getDefaultValue()); 
		}
	}
	
	public GroupingContainerModel getGroupingContainerModel() {
		return groupingContainerModel;
	}

	public void setGroupingContainerModel(
			GroupingContainerModel groupingContainerModel) {
		this.groupingContainerModel = groupingContainerModel;
	}
	
	/**
	 * @return A copy of this tab.
	 * @throws Exception
	 */
	public TabItem getCopy() throws Exception{
		String xmlString = XMLUtil.widgetToXMLString(groupingContainerModel, false);
		
		GroupingContainerModel newGroupingContainerModel = 
			(GroupingContainerModel) XMLUtil.XMLStringToWidget(xmlString);
		
		Map<TabProperty, Object> newPropertyMap = new HashMap<TabProperty, Object>();
		
		for(Entry<TabProperty, Object> entry : propertyMap.entrySet()){
			newPropertyMap.put(entry.getKey(), entry.getValue());
		}
		
		return new TabItem(newGroupingContainerModel, newPropertyMap);		
	}

	public Object getPropertyValue(TabProperty tabProperty) {
		return propertyMap.get(tabProperty);
	}
	
}
