/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.actions.WidgetActionMenuAction;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WritePVAction;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
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
	
	class MenuButtonFigure extends Label implements ITextFigure{
		
	}

	private PVListener loadActionsFromPVListener;

	private IEnumeratedMetaData meta = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final MenuButtonModel model = (MenuButtonModel) getWidgetModel();
		updatePropSheet(model.isActionsFromPV());
		final MenuButtonFigure label = new MenuButtonFigure();
		label.setOpaque(!model.isTransparent());
		label.setText(model.getLabel());
		if (getExecutionMode() == ExecutionMode.RUN_MODE)
			label.addMouseListener(new MouseListener() {
				public void mouseDoubleClicked(final MouseEvent me) {
				}

				public void mousePressed(final MouseEvent me) {
					if (me.button == 1) {
						me.consume();
					}
				}

				public void mouseReleased(final MouseEvent me) {
					if (me.button == 1
							&& getExecutionMode()
									.equals(ExecutionMode.RUN_MODE)) {
						final org.eclipse.swt.graphics.Point cursorLocation = Display
								.getCurrent().getCursorLocation();
						showMenu(me.getLocation(), cursorLocation.x,
								cursorLocation.y);
					}
				}

			});
		label.addMouseMotionListener(new MouseMotionListener.Stub() {
			@Override
			public void mouseEntered(MouseEvent me) {
				if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
					Color backColor = label.getBackgroundColor();
					RGB darkColor = GraphicsUtil.mixColors(backColor.getRGB(),
							new RGB(0, 0, 0), 0.9);
					label.setBackgroundColor(CustomMediaFactory.getInstance()
							.getColor(darkColor));
				}

			}

			@Override
			public void mouseExited(MouseEvent me) {
				if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
					label.setBackgroundColor(CustomMediaFactory.getInstance()
							.getColor(getWidgetModel().getBackgroundColor()));
				}
			}
		});

		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		return label;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		if(getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextDirectEditPolicy());
	}	
	

	@Override
	public void performRequest(Request request){
		if (getExecutionMode() == ExecutionMode.EDIT_MODE &&(
				request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
				request.getType() == RequestConstants.REQ_OPEN))
			new TextEditManager(this, 
					new LabelCellEditorLocator(getFigure()), false).show();
	}
	
	@Override
	public MenuButtonModel getWidgetModel() {
		return (MenuButtonModel) getModel();
	}

	/**
	 * Show Menu
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
			for (AbstractWidgetAction action : getWidgetModel()
					.getActionsInput().getActionsList()) {
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

		}
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		registerLoadActionsListener();
	}

	/**
	 *
	 */
	private void registerLoadActionsListener() {
		if (getExecutionMode() == ExecutionMode.RUN_MODE) {
			if (getWidgetModel().isActionsFromPV()) {
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if (pv != null) {
					if (loadActionsFromPVListener == null)
						loadActionsFromPVListener = new PVListener() {
							public void pvValueUpdate(PV pv) {
								IValue value = pv.getValue();
								if (value != null
										&& value.getMetaData() instanceof IEnumeratedMetaData) {
									IEnumeratedMetaData new_meta = (IEnumeratedMetaData) value
											.getMetaData();
									if (meta == null || !meta.equals(new_meta)) {
										meta = new_meta;
										ActionsInput actionsInput = new ActionsInput();
										for (String writeValue : meta
												.getStates()) {
											WritePVAction action = new WritePVAction();
											action.setPropertyValue(
													WritePVAction.PROP_PVNAME,
													getWidgetModel()
															.getPVName());
											action.setPropertyValue(
													WritePVAction.PROP_VALUE,
													writeValue);
											action.setPropertyValue(
													WritePVAction.PROP_DESCRIPTION,
													writeValue);
											actionsInput.getActionsList().add(
													action);
										}
										getWidgetModel()
												.setPropertyValue(
														AbstractWidgetModel.PROP_ACTIONS,
														actionsInput);

									}
								}
							}

							public void pvDisconnected(PV pv) {
							}
						};
					pv.addListener(loadActionsFromPVListener);
				}
			}
		}
	}

	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if (getWidgetModel().isActionsFromPV()) {
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if (pv != null && loadActionsFromPVListener != null) {
				pv.removeListener(loadActionsFromPVListener);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				registerLoadActionsListener();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME,
				pvNameHandler);

		// PV_Value
		IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (newValue != null && newValue instanceof IValue)
					((Label) refreshableFigure).setText(ValueUtil
							.getString((IValue) newValue));
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_PVVALUE, pvhandler);

		// label
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				((Label) refreshableFigure).setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_LABEL, labelHandler);

		// Transparent
		IWidgetPropertyChangeHandler transparentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				((Label) refreshableFigure).setOpaque(!(Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MenuButtonModel.PROP_TRANSPARENT,
				transparentHandler);

		final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				updatePropSheet((Boolean) newValue);
				return false;
			}
		};
		getWidgetModel().getProperty(MenuButtonModel.PROP_ACTIONS_FROM_PV)
				.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						handler.handleChange(evt.getOldValue(),
								evt.getNewValue(), getFigure());
					}
				});

		/*
		 * // text alignment IWidgetPropertyChangeHandler alignmentHandler = new
		 * IWidgetPropertyChangeHandler() { public boolean handleChange(final
		 * Object oldValue, final Object newValue, final IFigure
		 * refreshableFigure) {
		 * ((Label)refreshableFigure).setTextAlignment((Integer) newValue);
		 * return true; } };
		 * setPropertyChangeHandler(MenuButtonModel.PROP_TEXT_ALIGNMENT,
		 * alignmentHandler);
		 */
	}

	/**
	 * @param actionsFromPV
	 */
	private void updatePropSheet(final boolean actionsFromPV) {
		getWidgetModel().setPropertyVisible(MenuButtonModel.PROP_ACTIONS,
				!actionsFromPV);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		// always one sample
		return 1;
	}

	@Override
	public String getValue() {
		return ((Label) getFigure()).getText();
	}

	@Override
	public void setValue(Object value) {
		((Label) getFigure()).setText(value.toString());
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if(key == ITextFigure.class)
			return getFigure();

		return super.getAdapter(key);
	}

}
