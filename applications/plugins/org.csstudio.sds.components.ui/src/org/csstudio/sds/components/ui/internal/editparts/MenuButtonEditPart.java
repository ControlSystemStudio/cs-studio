package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.components.ui.internal.utils.WidgetActionHandlerService;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Helge Rickens, Kai Meyer
 *
 */
public final class MenuButtonEditPart extends AbstractWidgetEditPart {
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final MenuButtonModel model = (MenuButtonModel) getWidgetModel();

		RefreshableLabelFigure label = new RefreshableLabelFigure();

		label.setTextValue(model.getLabel());
		label.setFont(CustomMediaFactory.getInstance().getFont(
						model.getFont()));
		label.setTextAlignment(model.getTextAlignment());
		label.setTransparent(false);
		label.addMouseListener(new MouseListener() {
			public void mouseDoubleClicked(final MouseEvent me) {
			}

			public void mousePressed(final MouseEvent me) {
				if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
					final org.eclipse.swt.graphics.Point cursorLocation = Display.getCurrent().getCursorLocation();
					new CheckedUiRunnable() {
						protected void doRunInUi() {
							performDirectEdit(me.getLocation(), cursorLocation.x, cursorLocation.y);	
						}
					};	
				}
			}

			public void mouseReleased(final MouseEvent me) {
			}

		});
		return label;
	}
	

	/**
	 * Open the cell editor for direct editing.
	 * @param point the location of the mouse-event
	 */
	private void performDirectEdit(final Point point, int absolutX, int absolutY) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem item1 = new MenuItem(menu,SWT.PUSH);
		item1.setData(((MenuButtonModel)this.getCastedModel()).getActionData1());
		item1.setText(((MenuButtonModel)this.getCastedModel()).getActionData1().getWidgetAction().getActionLabel());
		item1.addListener(SWT.Selection, new Listener(){

			public void handleEvent(final Event event) {
				ActionData data = (ActionData)event.widget.getData();
				WidgetActionHandlerService.getInstance().performAction(getCastedModel().getProperty(MenuButtonModel.PROP_ACTIONDATA1), data.getWidgetAction());
				System.out.println("actionData1 performt");
			}
			
		});
		MenuItem item2 = new MenuItem(menu,SWT.PUSH);
		item2.setText(((MenuButtonModel)this.getCastedModel()).getActionData2().getWidgetAction().getActionLabel());
		item2.addListener(SWT.Selection, new Listener(){

			public void handleEvent(final Event event) {
				System.out.println("actionData2 performt");
			}
			
		});
		MenuItem item3 = new MenuItem(menu,SWT.PUSH);
		item3.setText(((MenuButtonModel)this.getCastedModel()).getActionData3().getWidgetAction().getActionLabel());
		item3.addListener(SWT.Selection, new Listener(){

			public void handleEvent(final Event event) {
				System.out.println("actionData3 performt");
			}
			
		});
		
		int x = absolutX;
		int y = absolutY;
		x = x - point.x + this.getCastedModel().getX();
		y = y - point.y + this.getCastedModel().getY() + this.getCastedModel().getHeight();
		
		menu.setLocation(x,y);
		menu.setVisible(true);
		while(!menu.isDisposed() && menu.isVisible()){
			if(!Display.getCurrent().readAndDispatch()){
				Display.getCurrent().sleep();
			}
		}
		menu.dispose();
	}
	
	/**
	 * Returns the Figure of this EditPart.
	 * @return RefreshableActionButtonFigure
	 * 			The RefreshableActionButtonFigure of this EditPart
	 */
	protected RefreshableLabelFigure getCastedFigure() {
		return (RefreshableLabelFigure) getFigure();
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// label
		IWidgetPropertyChangeHandler labelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableLabelFigure figure = getCastedFigure();
				figure.setTextValue(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_LABEL, labelHandler);
		// font
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableLabelFigure figure = getCastedFigure();
				FontData fontData = (FontData) newValue;
				figure.setFont(CustomMediaFactory.getInstance().getFont(
						fontData.getName(), fontData.getHeight(),
						fontData.getStyle()));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_FONT, fontHandler);
		
		// text alignment
		IWidgetPropertyChangeHandler alignmentHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableLabelFigure figure = getCastedFigure();
				figure.setTextAlignment((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_TEXT_ALIGNMENT, alignmentHandler);

	}

}
