package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.actions.WidgetActionMenuAction;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgetActions.WritePVAction;
import org.csstudio.opibuilder.widgets.figures.ComboFigure;
import org.csstudio.opibuilder.widgets.figures.ImageBoolButtonFigure;
import org.csstudio.opibuilder.widgets.model.ComboModel;
import org.csstudio.opibuilder.widgets.model.ImageBoolButtonModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.sun.corba.se.impl.ior.WireObjectKeyTemplate;

/**
 * 
 * @author Xihui Chen
 * 
 */
public final class ComboEditPart3 extends AbstractPVWidgetEditPart {

	private PVListener loadItemsFromPVListener;

	private IEnumeratedMetaData meta = null;
	
	private Combo combo;
	private SelectionListener comboSelectionListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final ComboModel model = getWidgetModel();
		//updatePropSheet(model.isActionsFromPV());
		
		ComboFigure comboFigure = new ComboFigure((Composite) getViewer().getControl());
		comboFigure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
		comboFigure.setFont(CustomMediaFactory.getInstance().getFont(
						model.getFont().getFontData()));
		combo = comboFigure.getCombo();
		List<String> items = getWidgetModel().getItems();
		
		updateCombo(items);
		autoSizeWidget(comboFigure);
		return comboFigure;
	}

	/**
	 * @param items
	 */
	private void updateCombo(List<String> items) {
		if(items !=null && getExecutionMode() == ExecutionMode.RUN_MODE){
			combo.removeAll();
			int i=0;
			final List<WritePVAction> widgetActionList = new ArrayList<WritePVAction>();
			for(String item : items){
				final WritePVAction action = new WritePVAction();
				action.setWidgetModel(getWidgetModel());
				action.setPropertyValue(WritePVAction.PROP_PVNAME, getWidgetModel().getPVName());				
				action.setPropertyValue(WritePVAction.PROP_VALUE,  item);									
				i++;
				combo.add(item);
				widgetActionList.add(action);
				
			}
			if(comboSelectionListener !=null)				
				combo.removeSelectionListener(comboSelectionListener);
			comboSelectionListener = new SelectionAdapter(){
					@Override
					public void widgetSelected(SelectionEvent e) {
						widgetActionList.get(combo.getSelectionIndex()).run();
					}
			};
			combo.addSelectionListener(comboSelectionListener);			
		}
	}

	@Override
	public ComboModel getWidgetModel() {
		return (ComboModel)getModel();
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
			
			Menu menu = new Menu(shell);
			combo = new Combo((Composite) getViewer().getControl(), SWT.DROP_DOWN | SWT.READ_ONLY);
			
			int i=0;
			final List<WritePVAction> widgetActionList = new ArrayList<WritePVAction>();
			for(String item : getWidgetModel().getItems()){
				final WritePVAction action = new WritePVAction();
				action.setWidgetModel(getWidgetModel());
				action.setPropertyValue(WritePVAction.PROP_PVNAME, getWidgetModel().getPVName());				
				action.setPropertyValue(WritePVAction.PROP_VALUE, item);
				MenuItem menuItem = new MenuItem(menu, SWT.NONE);
				menuItem.setText(item);
				menuItem.addSelectionListener(new SelectionAdapter(){
					@Override
					public void widgetSelected(SelectionEvent e) {
						action.run();
					}
				});						
				i++;
				combo.add(item);
				widgetActionList.add(action);
				
			}
			combo.addSelectionListener(new SelectionAdapter(){
					@Override
					public void widgetSelected(SelectionEvent e) {
						widgetActionList.get(combo.getSelectionIndex()).run();
					}
			});

			int x = absolutX;
			int y = absolutY;
			x = x - point.x + getWidgetModel().getLocation().x;
			y = y - point.y + getWidgetModel().getLocation().y
					+ getWidgetModel().getSize().height;

			//menu.setLocation(x, y);
			//menu.setVisible(true);
			Rectangle rect = getFigure().getClientArea();
			getFigure().translateToAbsolute(rect);
			org.eclipse.swt.graphics.Rectangle trim = combo.computeTrim(0, 0, 0, 0);
			rect.translate(trim.x, trim.y);
			rect.width += trim.width;
			rect.height += trim.height;
			combo.setBounds(rect.x, rect.y, rect.width, rect.height);
			//combo.setLocation(x,y);
			combo.setVisible(true);
			
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
			if(getWidgetModel().isItemsFromPV()){
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){	
					loadItemsFromPVListener = new PVListener() {					
						public void pvValueUpdate(PV pv) {
							IValue value = pv.getValue();
							if (value != null && value.getMetaData() instanceof IEnumeratedMetaData){
								IEnumeratedMetaData new_meta = (IEnumeratedMetaData)value.getMetaData();
								if(meta  == null || !meta.equals(new_meta)){
									meta = new_meta;
									List<String> itemsFromPV = new ArrayList<String>();
									for(String writeValue : meta.getStates()){										
										itemsFromPV.add(writeValue);
									}
									getWidgetModel().setPropertyValue(
											ComboModel.PROP_ITEMS, itemsFromPV);
								}
							}
						}					
						public void pvDisconnected(PV pv) {}
					};
					pv.addListener(loadItemsFromPVListener);				
				}
			}
		}
	}
	
	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isItemsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null){	
				pv.removeListener(loadItemsFromPVListener);
			}
		}
		((ComboFigure)getFigure()).dispose();
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
				if(newValue != null && newValue instanceof IValue){
					Double doubleValue = ValueUtil.getDouble(((IValue)newValue));
					String stringValue = ValueUtil.getString((IValue)newValue);
					//if(doubleValue == Double.NaN || doubleValue == Double.NEGATIVE_INFINITY ||
					//		getWidgetModel().getItems().indexOf(stringValue) != -1)
						combo.setText(stringValue);
					//else
					//	combo.select(doubleValue.intValue());
				}
					
				return true;
			}
		};
		setPropertyChangeHandler(ComboModel.PROP_PVVALUE, pvhandler);
		
		// Items
		IWidgetPropertyChangeHandler itemsHandler = new IWidgetPropertyChangeHandler() {
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(newValue != null && newValue instanceof List){
					updateCombo((List<String>)newValue);
					if(getWidgetModel().isItemsFromPV())
						combo.setText(ValueUtil.getString(getPVValue(AbstractPVWidgetModel.PROP_PVNAME)));
				}
				return true;
			}
		};
		setPropertyChangeHandler(ComboModel.PROP_ITEMS, itemsHandler);
		
		
		
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
		setPropertyChangeHandler(ComboModel.PROP_FONT, fontHandler);
		
		final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {				
				updatePropSheet((Boolean) newValue);
				return false;
			}			
		};
		getWidgetModel().getProperty(ComboModel.PROP_ITEMS_FROM_PV).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
		});

		
		//size change handlers--don't a
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {				
				autoSizeWidget((ComboFigure)figure);
				return true;
			}
		};
		setPropertyChangeHandler(ImageModel.PROP_HEIGHT, handle);		

	}
	
		/**
		* @param actionsFromPV
		*/
	private void updatePropSheet(final boolean itemsFromPV) {
		getWidgetModel().setPropertyVisible(
				ComboModel.PROP_ITEMS, !itemsFromPV);	
	}

	private void autoSizeWidget(ComboFigure comboFigure) {		
		Dimension d = comboFigure.getAutoSizeDimension();
		getWidgetModel().setSize(getWidgetModel().getWidth(), d.height);
	}

}
