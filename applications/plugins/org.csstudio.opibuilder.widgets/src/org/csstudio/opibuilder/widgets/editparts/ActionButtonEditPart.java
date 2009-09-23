package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.figures.ActionButtonFigure;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;

/**
 * EditPart controller for the ActioButton widget. The controller mediates
 * between {@link ActionButtonModel} and {@link ActionButtonFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class ActionButtonEditPart extends AbstractWidgetEditPart {
  
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ActionButtonModel model = getWidgetModel();

		final ActionButtonFigure buttonFigure = new ActionButtonFigure(getExecutionMode());
		buttonFigure.setText(model.getText());
		buttonFigure.setFont(CustomMediaFactory.getInstance().getFont(
				model.getFont().getFontData()));
		buttonFigure.setStyle(model.isToggleButton());
		updatePropSheet(model.isToggleButton());
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			buttonFigure.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event) {
					
					int actionIndex;
					
					if(getWidgetModel().isToggleButton()){
						if(buttonFigure.isSelected()){
							actionIndex = getWidgetModel().getActionIndex();
						}else
							actionIndex = getWidgetModel().getReleasedActionIndex();
					}else
						actionIndex = getWidgetModel().getActionIndex();
					
					if(actionIndex >= 0 && getWidgetModel().getActionsInput().getActionsList().size() > 
						actionIndex)
						getWidgetModel().getActionsInput().getActionsList().get(
							actionIndex).run();				
				}
			});
		}
		
		return buttonFigure;
	}

	@Override
	public ActionButtonModel getWidgetModel() {
		return (ActionButtonModel)getModel();
	}
	
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// the button should be disabled in edit mode to make select work.
		((ActionButtonFigure)getFigure()).setEnabled(
				getExecutionMode() == ExecutionMode.RUN_MODE);
		
		removeAllPropertyChangeHandlers(ActionButtonModel.PROP_ENABLED);
		
		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				//enablement only takes effect in run mode
				if(getExecutionMode() == ExecutionMode.RUN_MODE)
					figure.setEnabled((Boolean)newValue);
				return true;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, enableHandler);
		

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
				figure.setStyle((Boolean) newValue);
				
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
}
