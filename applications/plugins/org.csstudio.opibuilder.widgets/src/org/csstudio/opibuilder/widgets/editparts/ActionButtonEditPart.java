package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.widgets.figures.ActionButtonFigure;
import org.csstudio.swt.widgets.figures.ActionButtonFigure.ButtonActionListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.InputEvent;
import org.eclipse.swt.graphics.FontData;

/**
 * EditPart controller for the ActioButton widget. The controller mediates
 * between {@link ActionButtonModel} and {@link ActionButtonFigure2}.
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public final class ActionButtonEditPart extends AbstractPVWidgetEditPart {
	  
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ActionButtonModel model = getWidgetModel();

		final ActionButtonFigure buttonFigure = new ActionButtonFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
		buttonFigure.setText(model.getText());
		buttonFigure.setFont(CustomMediaFactory.getInstance().getFont(
				model.getFont().getFontData()));
		buttonFigure.setToggleStyle(model.isToggleButton());
		buttonFigure.setImagePath(model.getImagePath());
		updatePropSheet(model.isToggleButton());	
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		return buttonFigure;
	}
	
	@Override
	protected void hookMouseClickAction() {

		((ActionButtonFigure)getFigure()).addActionListener(new ButtonActionListener(){
			public void actionPerformed(int mouseEventState) {					
				AbstractWidgetAction action = getHookedAction();
				if(action!= null){
					if(action instanceof OpenDisplayAction){
						((OpenDisplayAction) action).setCtrlPressed(false);
						((OpenDisplayAction) action).setShiftPressed(false);
						if(mouseEventState == InputEvent.CONTROL){
							((OpenDisplayAction) action).setCtrlPressed(true);
						}else if (mouseEventState == InputEvent.SHIFT){
							((OpenDisplayAction) action).setShiftPressed(true);
						}	
					}
					action.run();
				}
							
			}
		});
	}
	
	@Override
	public AbstractWidgetAction getHookedAction() {
		int actionIndex;
		
		if(getWidgetModel().isToggleButton()){
			if(((ActionButtonFigure)getFigure()).isSelected()){
				actionIndex = getWidgetModel().getActionIndex();
			}else
				actionIndex = getWidgetModel().getReleasedActionIndex();
		}else
			actionIndex = getWidgetModel().getActionIndex();
		
		if(actionIndex >= 0 && getWidgetModel().getActionsInput().
				getActionsList().size() > actionIndex){
			return getWidgetModel().getActionsInput().
						getActionsList().get(actionIndex);			
		}
		
		return null;
			
	}

	@Override
	public ActionButtonModel getWidgetModel() {
		return (ActionButtonModel)getModel();
	}
	
	@Override
	public void deactivate() {		
		((ActionButtonFigure)getFigure()).dispose();
		super.deactivate();
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// the button should be disabled in edit mode to make select work.
//		((ActionButtonFigure)getFigure()).setEnabled(
//				getExecutionMode() == ExecutionMode.RUN_MODE);
//		
//		removeAllPropertyChangeHandlers(ActionButtonModel.PROP_ENABLED);
//		
//		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler(){
//			public boolean handleChange(Object oldValue, Object newValue,
//					IFigure figure) {
//				//enablement only takes effect in run mode
//				//if(getExecutionMode() == ExecutionMode.RUN_MODE)
//					figure.setEnabled((Boolean)newValue);
//				return false;
//			}
//		};		
//		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, enableHandler);
//		

		// text
		IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
				figure.setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_TEXT, textHandler);
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
				FontData fontData = ((OPIFont) newValue).getFontData();
				figure.setFont(CustomMediaFactory.getInstance().getFont(fontData));
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_FONT, fontHandler);

		// font
		IWidgetPropertyChangeHandler imageHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;				
				IPath absolutePath = (IPath)newValue;
				if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
					absolutePath = ResourceUtil.buildAbsolutePath(
							getWidgetModel(), absolutePath);
				figure.setImagePath(absolutePath);
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_IMAGE, imageHandler);

		
		/*// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
				figure.setTextAlignment((Integer) newValue);
				return true;
			}
		};
		//setPropertyChangeHandler(ActionButtonModel.PROP_TEXT_ALIGNMENT,
		//		alignmentHandler);
		*/
		// button style
		final IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
				figure.setToggleStyle((Boolean) newValue);				
				updatePropSheet((Boolean) newValue);
				return true;
			}

			
		};
		getWidgetModel().getProperty(ActionButtonModel.PROP_TOGGLE_BUTTON).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					buttonStyleHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
			});
		//cannot use handler because it will delay the propsheet update.
		//setPropertyChangeHandler(ActionButtonModel.PROP_TOGGLE_BUTTON,
		//		buttonStyleHandler);
	}
	
	/**
		* @param newValue
		*/
	private void updatePropSheet(final boolean newValue) {
		getWidgetModel().setPropertyVisible(
					ActionButtonModel.PROP_RELEASED_ACTION_INDEX, newValue);
		getWidgetModel().setPropertyDescription(ActionButtonModel.PROP_ACTION_INDEX, 
					newValue ? "Push Action Index" : "Click Action Index" );
	}
	
	

	@Override
	public void setValue(Object value) {		
	}
	
	@Override
	public Object getValue() {
		return getPVValue(AbstractPVWidgetModel.PROP_PVNAME);
	}

}
