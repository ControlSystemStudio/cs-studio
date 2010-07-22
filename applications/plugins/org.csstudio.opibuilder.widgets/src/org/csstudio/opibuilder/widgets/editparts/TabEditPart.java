package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.TabModel;
import org.csstudio.opibuilder.widgets.model.TabModel.TabProperty;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.widgets.figures.TabFigure;
import org.csstudio.swt.widgets.figures.TabFigure.ITabListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

/**The editpart of tab widget.
 * @author Xihui Chen
 *
 */
public class TabEditPart extends AbstractContainerEditpart {

	
	
	@Override
	protected IFigure doCreateFigure() {
		TabFigure tabFigure = new TabFigure();
		tabFigure.addTabListener(new ITabListener(){
			public void activeTabIndexChanged(int oldIndex, int newIndex) {
				for(AbstractWidgetModel child : getWidgetModel().getChildren())
					child.setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, false);				
				getWidgetModel().getChildren().get(newIndex).
					setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, true);
			}
			
		});
		
		return tabFigure;
	}
	
	@Override
	protected void createEditPolicies() {	
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, null);				
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
		
	}
	
	public void addTab(int index, GroupingContainerModel groupingContainerModel, Label label){
		if(groupingContainerModel == null)
			groupingContainerModel = createGroupingContainer();
		getWidgetModel().addChild(index, groupingContainerModel);
		
		getTabFigure().addTab((String) getWidgetModel().getPropertyValue(
						TabModel.makeTabPropID(
						TabProperty.TITLE.propIDPre, index)), index);	
		//init label
		initTabLabel(index);	
		
	
		//right shift tab's properties
		for(int j = getWidgetModel().getChildren().size()-1; j > index; j--){
			for(TabProperty tabProperty : TabProperty.values()){				
				String propID1 = TabModel.makeTabPropID(
						tabProperty.propIDPre, j-1);
				String propID2 = TabModel.makeTabPropID(
						tabProperty.propIDPre, j);
				getWidgetModel().setPropertyValue(propID2, getWidgetModel().getPropertyValue(propID1));
			}	
		}

			//apply default tab properties to the new tab		
			for(TabProperty tabProperty : TabProperty.values()){				
				String propID = TabModel.makeTabPropID(
						tabProperty.propIDPre, index);
				getWidgetModel().setPropertyValue(propID, label == null ? 
						getWidgetModel().getProperty(propID).getDefaultValue() : 
							getLabelProperty(tabProperty, label)); 
			}

		
		if(label == null){
			//set the name of new tab as "new tab"
			String prop = TabModel.makeTabPropID(TabProperty.TITLE.propIDPre, index);
			getWidgetModel().setPropertyValue(prop, "new tab"); 
		}
		
		
		
		//update property sheet		
		getWidgetModel().setPropertyValue(TabModel.PROP_TAB_COUNT, getWidgetModel().getChildren().size());
		
		for(TabProperty tabProperty : TabProperty.values()){				
				String propID = TabModel.makeTabPropID(
						tabProperty.propIDPre, getWidgetModel().getChildren().size()-1);
				getWidgetModel().setPropertyVisible(propID, true); 
		}			
		
		
	
		//update active tab index to the new added tab
		updateTabAreaSize();
		//if(getActiveTabIndex() >= index){
		//	setActiveTabIndex(getActiveTabIndex() + 1);
		//}
		setActiveTabIndex(index);
	}
	
	private Object getLabelProperty(TabProperty tabProperty, Label label){
		switch (tabProperty) {
		case TITLE:
			return label.getText();
		case FONT:
			return label.getFont().getFontData();
		case BACKCOLOR:
			return label.getBackgroundColor().getRGB();
		case FORECOLOR:
			return label.getForegroundColor().getRGB();			
		default:
			return null;
		}
	}
	
	public void addTab(){
		GroupingContainerModel groupingContainerModel = createGroupingContainer();
		getWidgetModel().addChild(groupingContainerModel);
		
		int tabIndex = getWidgetModel().getChildren().size()-1;
		getTabFigure().addTab((String) getWidgetModel().getPropertyValue(
						TabModel.makeTabPropID(
						TabProperty.TITLE.propIDPre, tabIndex)));	
		//init label
		initTabLabel(tabIndex);	
		updateTabAreaSize();
	}

	/**
	 * @param tabIndex
	 */
	private void initTabLabel(int tabIndex) {
		Label label = getTabFigure().getTabLabel(tabIndex);
		label.setFont(CustomMediaFactory.getInstance().getFont(
				((OPIFont) getWidgetModel().getPropertyValue(TabModel.makeTabPropID(
						TabProperty.FONT.propIDPre, tabIndex))).getFontData()));
		label.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				((OPIColor) getWidgetModel().getPropertyValue(TabModel.makeTabPropID(
						TabProperty.FORECOLOR.propIDPre, tabIndex))).getRGBValue()));
		getTabFigure().setTabColor(tabIndex, CustomMediaFactory.getInstance().getColor(
				((OPIColor) getWidgetModel().getPropertyValue(TabModel.makeTabPropID(
						TabProperty.BACKCOLOR.propIDPre, tabIndex))).getRGBValue()));
	}

	/**
	 * @return
	 */
	private GroupingContainerModel createGroupingContainer() {
		GroupingContainerModel groupingContainerModel =  new GroupingContainerModel();
		groupingContainerModel.setLocation(1,1);
		groupingContainerModel.setBorderStyle(BorderStyle.NONE);
		groupingContainerModel.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT, true);
		groupingContainerModel.setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, false);
		return groupingContainerModel;
	}
	
	public void removeTab(){
		getWidgetModel().removeChild(
				getWidgetModel().getChildren().get(getWidgetModel().getChildren().size()-1));
		getTabFigure().removeTab();
		updateTabAreaSize();
	}
	
	public void removeTab(int index){
	//	setActiveTabIndex(index > 0 ? index-1 : getWidgetModel().getChildren().size()-1);
		getWidgetModel().removeChild(
				getWidgetModel().getChildren().get(index));
		getTabFigure().removeTab(index);
		
		
		//left shift tab's properties
		for(int j = index; j < getWidgetModel().getChildren().size(); j++){
			for(TabProperty tabProperty : TabProperty.values()){				
				String propID1 = TabModel.makeTabPropID(
						tabProperty.propIDPre, j);
				String propID2 = TabModel.makeTabPropID(
						tabProperty.propIDPre, j+1);
				getWidgetModel().setPropertyValue(propID1, getWidgetModel().getPropertyValue(propID2));
			}	
		}
		
		//update property sheet		
		getWidgetModel().setPropertyValue(TabModel.PROP_TAB_COUNT, getWidgetModel().getChildren().size());
		
		for(TabProperty tabProperty : TabProperty.values()){				
				String propID = TabModel.makeTabPropID(
						tabProperty.propIDPre, getWidgetModel().getChildren().size());
				getWidgetModel().setPropertyVisible(propID, false); 
		}			
		
		
	
		//update active tab index to the new added tab		
		updateTabAreaSize();
		
		setActiveTabIndex(index >= getWidgetModel().getChildren().size() ? index -1 : index);
	}
	
	public void setActiveTabIndex(int index){
		getTabFigure().setActiveTabIndex(index);
	}
	
	public int getActiveTabIndex(){
		return getTabFigure().getActiveTabIndex(); 
	}
	
	public Label getTabLabel(int index){
		return getTabFigure().getTabLabel(index);
	}
	
	public GroupingContainerModel getGroupingContainer(int index){
		return (GroupingContainerModel) getWidgetModel().getChildren().get(index);
	}
	
	@Override
	public TabModel getWidgetModel() {
		return (TabModel)getModel();
	}
	
	private TabFigure getTabFigure(){
		return (TabFigure)getFigure();
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		//init tabs
		int i=0;
		for(AbstractWidgetModel child : getWidgetModel().getChildren()){
			if(child instanceof GroupingContainerModel){
				child.setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, true);
				child.setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, false);
				getTabFigure().addTab((String) getWidgetModel().getPropertyValue(
								TabModel.makeTabPropID(TabProperty.TITLE.propIDPre, i)));
				for(TabProperty tabProperty : TabProperty.values())
					setTabProperty(i, tabProperty, getWidgetModel().getPropertyValue(
							TabModel.makeTabPropID(tabProperty.propIDPre, i)));

				i++;
			}
		}
		IWidgetPropertyChangeHandler relocContainerHandler = new IWidgetPropertyChangeHandler(){
	
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					updateTabAreaSize();
					refreshVisuals();					
					return false;
				}

					
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, relocContainerHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, relocContainerHandler);
	
		
		
		registerTabPropertyChangeHandlers();
		registerTabsAmountChangeHandler();
		
	}
	
	/**
	 * 
	 */
	private void updateTabAreaSize() {
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				for(AbstractWidgetModel child : getWidgetModel().getChildren()){
					child.setSize(getTabAreaSize());
				}
			}
		});
	}		
	
	private Dimension getTabAreaSize(){
		return new Dimension(getWidgetModel().getWidth() - 2 - 
								getTabFigure().getInsets().left - getTabFigure().getInsets().right,
							 getWidgetModel().getHeight() - 2 -
							 	getTabFigure().getTabLabelHeight() - getTabFigure().getInsets().bottom);	
	}
	
	@Override
	public void activate() {
		super.activate();

		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				//add initial tab
				int j = getTabFigure().getTabAmount();
				while( j < getWidgetModel().getTabsAmount()){			
					addTab();
					j++;
				}				
			}
		});
		
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
		
			public void run() {			
				getTabFigure().setActiveTabIndex(0);
				getWidgetModel().getChildren().get(0).setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, true);		
			}
		});
		
	}
	
	@Override
	public IFigure getContentPane() {
		return getTabFigure().getContentPane();
	}
	
	private void registerTabsAmountChangeHandler(){
		final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure refreshableFigure) {				
				TabModel model = getWidgetModel();
				TabFigure figure = (TabFigure)refreshableFigure;
				int currentTabAmount = figure.getTabAmount();
				//add tabs
				if((Integer)newValue > currentTabAmount){
					for(int i=0; i<(Integer)newValue - currentTabAmount; i++){	
						for(TabProperty tabProperty : TabProperty.values()){				
							String propID = TabModel.makeTabPropID(
								tabProperty.propIDPre, i + currentTabAmount);
							model.setPropertyVisible(propID, true); 
						}							
						addTab();
					}						
				}else if((Integer)newValue < currentTabAmount){ //remove tabs
					for(int i=currentTabAmount-1; i>=(Integer)newValue; i--){
						for(TabProperty tabProperty : TabProperty.values()){				
							String propID = TabModel.makeTabPropID(
								tabProperty.propIDPre, i);
							model.setPropertyVisible(propID, false); 
						}							
						removeTab();
					}				
					setActiveTabIndex(0);
				}
				return true;
			}			
		};
		getWidgetModel().getProperty(TabModel.PROP_TAB_COUNT).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
			});
	}

	private void registerTabPropertyChangeHandlers(){
		//set prop handlers and init all the potential tabs
		for(int i=0; i<TabModel.MAX_TABS_AMOUNT; i++){			
			
			for(TabProperty tabProperty : TabProperty.values()){
				
				String propID = TabModel.makeTabPropID(
					tabProperty.propIDPre, i);
				IWidgetPropertyChangeHandler handler = new TabPropertyChangeHandler(i, tabProperty);
				setPropertyChangeHandler(propID, handler);				
			}			
		}
		
		for(int i=TabModel.MAX_TABS_AMOUNT -1; i>= getWidgetModel().getTabsAmount(); i--){
			for(TabProperty tabProperty : TabProperty.values()){		
				String propID = TabModel.makeTabPropID(
					tabProperty.propIDPre, i);
				getWidgetModel().setPropertyVisible(propID, false);
			}
		}
	}	
	
	
	private void setTabProperty(int index, TabProperty tabProperty, Object newValue){
		Label label = getTabFigure().getTabLabel(index);
		switch (tabProperty) {
		case TITLE:
			label.setText((String)newValue);			
			break;
		case FONT:
			label.setFont(CustomMediaFactory.getInstance().getFont(((OPIFont)newValue).getFontData()));
			updateTabAreaSize();
			break;
		case BACKCOLOR:
			getTabFigure().setTabColor(index, CustomMediaFactory.getInstance().getColor(
					((OPIColor)newValue).getRGBValue()));
			break;
		case FORECOLOR:
			label.setForegroundColor(CustomMediaFactory.getInstance().getColor(
						((OPIColor)newValue).getRGBValue()));
				break;
		default:
			break;
		}		
}
	class TabPropertyChangeHandler implements IWidgetPropertyChangeHandler {
		private int tabIndex;
		private TabProperty tabProperty;
		public TabPropertyChangeHandler(int tabIndex, TabProperty tabProperty) {
			this.tabIndex = tabIndex;
			this.tabProperty = tabProperty;
		}
		public boolean handleChange(Object oldValue, Object newValue,
				IFigure refreshableFigure) {
			setTabProperty(tabIndex, tabProperty, newValue);			
			return true;
		}
	}
	
	
	/**
	 * {@inheritDoc} Overidden, to set the selection behaviour of child
	 * EditParts.
	 */
	@Override
	protected final EditPart createChild(final Object model) {
		EditPart result = super.createChild(model);

		// setup selection behavior for the new child
		if (result instanceof AbstractBaseEditPart) {
			((AbstractBaseEditPart) result).setSelectable(false);
		}

		return result;
	}
}
