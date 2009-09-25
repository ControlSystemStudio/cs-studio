package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.actions.WidgetActionMenuAction;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WritePVAction;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Helge Rickens, Kai Meyer, Xihui Chen
 * 
 */
public final class MenuButtonEditPart extends AbstractPVWidgetEditPart {

	private PVListener loadActionsFromPVListener;

	private IEnumeratedMetaData meta = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final MenuButtonModel model = (MenuButtonModel) getWidgetModel();
		updatePropSheet(model.isActionsFromPV());
		Label label = new Label();
		label.setOpaque(true);
		label.setText(model.getLabel());
		label.setFont(CustomMediaFactory.getInstance().getFont(
						model.getFont().getFontData()));
		//label.setTextAlignment(model.getTextAlignment());
		
		if(getExecutionMode() == ExecutionMode.RUN_MODE)
			label.addMouseListener(new MouseListener() {
				public void mouseDoubleClicked(final MouseEvent me) {
				}
	
				public void mousePressed(final MouseEvent me) {
					if (me.button == 1
							&& getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
						final org.eclipse.swt.graphics.Point cursorLocation = Display
								.getCurrent().getCursorLocation();
						showMenu(me.getLocation(), cursorLocation.x,
								cursorLocation.y);
						//me.consume();
					}
				}
	
				public void mouseReleased(final MouseEvent me) {
				}
	
			});

		return label;
	}

	@Override
	public MenuButtonModel getWidgetModel() {
		return (MenuButtonModel)getModel();
	}
	
	/**
	 *Show Menu
	 * 
	 * @param point
	 *            the location of the mouse-event
	 * @param absolutX
	 *            The x coordinate of the mouse in the display
	 * @param absolutY
	 *            The y coordinate of the mouse in the display
	 */
	private void showMenu(final Point point, final int absolutX,
			final int absolutY) {
		if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
			final Shell shell = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell();
			MenuManager menuManager = new MenuManager();
			for (AbstractWidgetAction action : 
				getWidgetModel().getActionsInput().getActionsList()){
				menuManager.add(new WidgetActionMenuAction(action));
			}
			Menu menu = menuManager.createContextMenu(shell);		

			int x = absolutX;
			int y = absolutY;
			x = x - point.x + getWidgetModel().getLocation().x;
			y = y - point.y + getWidgetModel().getLocation().y
					+ getWidgetModel().getSize().height;

			menu.setLocation(x, y);
			menu.setVisible(true);
			
			
			// 2008-10-14: swende: The following lines prevent the menu actions from being executed consequently. 
			// What was the sense for that? A problem on Mac systems?
//			while (!menu.isDisposed() && menu.isVisible()) {
//				if (!Display.getCurrent().readAndDispatch()) {
//					Display.getCurrent().sleep();
//				}
//			}
//			menu.dispose();
			// shell.setFocus();
			
		}
	}


	
	@Override
	protected void doActivate() {
		super.doActivate();
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			if(getWidgetModel().isActionsFromPV()){
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){	
					loadActionsFromPVListener = new PVListener() {					
						public void pvValueUpdate(PV pv) {
							IValue value = pv.getValue();
							if (value != null && value.getMetaData() instanceof IEnumeratedMetaData){
								IEnumeratedMetaData new_meta = (IEnumeratedMetaData)value.getMetaData();
								if(meta  == null || !meta.equals(new_meta)){
									meta = new_meta;
									ActionsInput actionsInput = new ActionsInput();
									for(String writeValue : meta.getStates()){
										WritePVAction action = new WritePVAction();
										action.setPropertyValue(WritePVAction.PROP_PVNAME, getWidgetModel().getPVName());
										action.setPropertyValue(WritePVAction.PROP_VALUE, writeValue);
										actionsInput.getActionsList().add(action);
									}
									getWidgetModel().setPropertyValue(
											AbstractWidgetModel.PROP_ACTIONS, actionsInput);
									
								}
							}
						}					
						public void pvDisconnected(PV pv) {}
					};
					pv.addListener(loadActionsFromPVListener);				
				}
			}
		}
	}
	
	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isActionsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null){	
				pv.removeListener(loadActionsFromPVListener);
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		
		// PV_Value
		IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(getWidgetModel().isActionsFromPV() && 
						newValue != null && newValue instanceof IValue)
					((Label)refreshableFigure).setText(ValueUtil.getString((IValue)newValue));
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_PVVALUE, pvhandler);
		
		// label
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				((Label)refreshableFigure).setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_LABEL, labelHandler);
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				FontData fontData = ((OPIFont)newValue).getFontData();
				refreshableFigure.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_FONT, fontHandler);
		
		final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {				
				updatePropSheet((Boolean) newValue);
				return false;
			}			
		};
		getWidgetModel().getProperty(MenuButtonModel.PROP_ACTIONS_FROM_PV).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
		});
		
		
		
/*
		// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {				
				((Label)refreshableFigure).setTextAlignment((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_TEXT_ALIGNMENT,
			alignmentHandler);
*/	
	}
	
		/**
		* @param actionsFromPV
		*/
	private void updatePropSheet(final boolean actionsFromPV) {
		getWidgetModel().setPropertyVisible(
					MenuButtonModel.PROP_ACTIONS, !actionsFromPV);
		getWidgetModel().setPropertyVisible(
					MenuButtonModel.PROP_PVNAME, actionsFromPV);
	
	}

	

	

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		// always one sample
		return 1;
	}



}
