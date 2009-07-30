package org.csstudio.opibuilder.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.commands.ChangeOrderCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**The action which changes the order of widget.
 * @author Xihui Chen
 *
 */
public class ChangeOrderAction extends SelectionAction {
	
	public enum OrderType{		
		TO_FRONT("To Front", "icons/order_tofront.png"), //$NON-NLS-2$
		STEP_FRONT("Step Front", "icons/order_stepfront.png"), //$NON-NLS-2$
		STEP_BACK("Step Back", "icons/order_stepback.png"),//$NON-NLS-2$
		TO_BACK("To Back", "icons/order_toback.png");	//$NON-NLS-2$
		private String label;
		private String iconPath;
		private OrderType(String label, String iconPath) {
			this.label = label;
			this.iconPath = iconPath;
		}
		
		public String getLabel(){
			return label;
		}
		
		public String getActionID(){
			return "org.csstudio.opibuilder.actions." + toString(); //$NON-NLS-1$
		}
		
		public ImageDescriptor getImageDescriptor(){
			return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
					OPIBuilderPlugin.PLUGIN_ID, iconPath);
		}
		
	}
	
	
	class IndexedWidget implements Comparable<IndexedWidget>{
		
		private Integer index;
		
		private AbstractWidgetModel widget;

		public IndexedWidget(int index, AbstractWidgetModel widget) {
			this.index = index;
			this.widget = widget;
		}

		/**
		 * @return the index
		 */
		public final int getIndex() {
			return index;
		}
		
		/**
		 * @return the widget
		 */
		public final AbstractWidgetModel getWidget() {
			return widget;
		}
		
		public int compareTo(IndexedWidget o) {
			
			return index.compareTo(o.getIndex());
		}
		
	}
	
	private OrderType orderType;

	public ChangeOrderAction(IWorkbenchPart part, OrderType orderType) {
		super(part);
		this.orderType = orderType;
		setId(orderType.getActionID());
		setText(orderType.getLabel());
		setImageDescriptor(orderType.getImageDescriptor());
	}

	@Override
	protected boolean calculateEnabled() {		
		if(getSelectedObjects().size() == 0 || getSelectedObjects().size() == 1 && getSelectedObjects().get(0) instanceof DisplayEditpart)
			return false;
		Map<AbstractContainerModel, List<IndexedWidget>> widgetMap = 
			new HashMap<AbstractContainerModel, List<IndexedWidget>>();
		fillWidgetMap(widgetMap);
		
		//create compound command
		for(AbstractContainerModel container : widgetMap.keySet()){
			//sort the list in map by the widget's original order in its container
			List<IndexedWidget> widgetList = widgetMap.get(container);
			Collections.sort(widgetList);		
			
			int newIndex;			
			switch (orderType) {
			case TO_FRONT:
				newIndex = container.getChildren().size() -1;
				break;
			case STEP_FRONT:
				newIndex = widgetList.get(widgetList.size()-1).getIndex() + 1;
				break;
			case STEP_BACK:
				newIndex = widgetList.get(0).getIndex() -1;
				break;
			case TO_BACK:				
			default:
				newIndex = 0;
				break;
			}
			if(newIndex > container.getChildren().size()-1 || newIndex < 0)
				return false;
			for(IndexedWidget indexedWidget : widgetList){
				if(container.getIndexOf(indexedWidget.getWidget()) != newIndex)
					return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		Map<AbstractContainerModel, List<IndexedWidget>> widgetMap = 
			new HashMap<AbstractContainerModel, List<IndexedWidget>>();
		fillWidgetMap(widgetMap);
		
		CompoundCommand compoundCommand = new CompoundCommand(orderType.getLabel());
		
		//create compound command
		for(AbstractContainerModel container : widgetMap.keySet()){
			//sort the list in map by the widget's original order in its container
			List<IndexedWidget> widgetList = widgetMap.get(container);
			Collections.sort(widgetList);		
			
			int newIndex;			
			switch (orderType) {
			case TO_FRONT:
				newIndex = container.getChildren().size() -1;
				break;
			case STEP_FRONT:
				newIndex = widgetList.get(widgetList.size()-1).getIndex() + 1;
				break;
			case STEP_BACK:
				newIndex = widgetList.get(0).getIndex() -1;
				break;
			case TO_BACK:				
			default:
				newIndex = 0;
				break;
			}
			
			//reorder
			switch (orderType) {
			case TO_FRONT:
			case STEP_FRONT:
				for(IndexedWidget indexedWidget : widgetList){
					compoundCommand.add(new ChangeOrderCommand(
							newIndex, container, indexedWidget.getWidget()));
				}
				break;
			case STEP_BACK:
			case TO_BACK:
				for(int i=widgetList.size()-1; i>=0; i--){
					compoundCommand.add(new ChangeOrderCommand(
							newIndex, container, widgetList.get(i).getWidget()));
				}
				break;
			default:
				break;
			}			
		}
		execute(compoundCommand);
	}

	/**
	 * @param widgetMap
	 */
	private void fillWidgetMap(
			Map<AbstractContainerModel, List<IndexedWidget>> widgetMap) {
		
		for(Object selection : getSelectedObjects()){
			if(selection instanceof AbstractBaseEditPart){
				AbstractBaseEditPart widgetEditpart = (AbstractBaseEditPart)selection;
				AbstractWidgetModel widgetModel = widgetEditpart.getCastedModel();
				if(widgetEditpart.getParent() instanceof AbstractContainerEditpart){
					AbstractContainerModel containerModel = 
						((AbstractContainerEditpart)widgetEditpart.getParent()).getCastedModel();
					
					if(!widgetMap.containsKey(containerModel)){
						widgetMap.put(containerModel, new LinkedList<IndexedWidget>());
					}
					widgetMap.get(containerModel).add(
							new IndexedWidget(containerModel.getIndexOf(widgetModel), widgetModel));				
				}
			}
		}
	}
	
	
}
