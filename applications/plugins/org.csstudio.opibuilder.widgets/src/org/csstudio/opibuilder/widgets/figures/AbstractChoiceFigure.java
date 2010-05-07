package org.csstudio.opibuilder.widgets.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ButtonGroup;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Toggle;
import org.eclipse.draw2d.ToggleModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**The abstract figure for widget which can perform choice action.
 * @author Xihui Chen
 *
 */
public abstract class AbstractChoiceFigure extends Figure {

	private ButtonGroup buttonGroup;
	
	private List<String> states;
	private List<Toggle> toggles;
	private List<ToggleModel> models;
	private List<IChoiceButtonListener> listeners;
	
	private boolean fromSetState = false;

	protected Color selectedColor = ColorConstants.black;

	
	public AbstractChoiceFigure() {
		buttonGroup = new ButtonGroup();
		states = new ArrayList<String>();
		toggles = new ArrayList<Toggle>();
		models = new ArrayList<ToggleModel>();
		listeners = new ArrayList<IChoiceButtonListener>();
	}
	
	@Override
	protected void layout() {
		super.layout();
		if(states.size() >0){
			Rectangle clientArea = getClientArea();
			int avgHeight = clientArea.height/states.size();
			int startY = clientArea.y;		
			for(Object child : getChildren()){
				((Figure)child).setBounds(new Rectangle(
						clientArea.x, startY, clientArea.width, avgHeight));
				startY += avgHeight;
			}
		}
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(states.size() <=0){
			graphics.fillRectangle(getClientArea());
		}
	}
	
	public void setSelectedColor(RGB checkedColor) {		
		this.selectedColor = 
			CustomMediaFactory.getInstance().getColor(checkedColor);
		repaint();
	}
	
	protected abstract Toggle createToggle(String text);
		
	/**Set all the state string values.
	 * @param states the states
	 */
	public void setStates(List<String> states) {
		this.states = states;
		removeAll();
		for(Object model : buttonGroup.getElements().toArray()){
			buttonGroup.remove((ToggleModel) model);
		}
		toggles.clear();
		models.clear();
		int i=0;
		for(final String state : states){
			final int index = i++;
			ToggleModel toggleModel = new ToggleModel();
			final Toggle toggle = createToggle(state);
			
			toggleModel.addChangeListener(new ChangeListener() {
				
				public void handleStateChanged(ChangeEvent event) {
					if(event.getPropertyName().equals(ToggleModel.SELECTED_PROPERTY) && 
							toggle.isSelected()){
						if(fromSetState)
							fromSetState = false;
						else
							fireButtonPressed(index, state);
					}
				}
			});
			
			buttonGroup.add(toggleModel);
			toggle.setModel(toggleModel);	
			
			toggles.add(toggle);
			models.add(toggleModel);
			add(toggle);			
		}
	}
	
	public synchronized void setState(String state){
		if(states.contains(state)){
			fromSetState = true;
			buttonGroup.setSelected(models.get(
				states.indexOf(state)));
			fromSetState = false;
		}
	}
	
	public synchronized void setState(int stateIndex){
		if(stateIndex < states.size()){
			fromSetState = true;
			buttonGroup.setSelected(models.get(stateIndex));
			fromSetState = false;
		}
			
	}
	
	
	public String getState(){
		return states.get(models.indexOf(buttonGroup.getSelected()));
	}
	
	/**Get all states.
	 * @return all states.
	 */
	public List<String> getStates() {
		return states;
	}
	
	public void addChoiceButtonListener(IChoiceButtonListener listener){
		listeners.add(listener);
	}
	
	private void fireButtonPressed(int index, String value){
		for(IChoiceButtonListener listener : listeners){
			listener.buttonPressed(index, value);
		}
	}
	
	public interface IChoiceButtonListener {		
		/**Executed when one of the choice button pressed.
		 * @param index the index of the button.
		 * @param value the string value of the state.
		 */
		public void buttonPressed(int index, String value);		
	}
	
}
