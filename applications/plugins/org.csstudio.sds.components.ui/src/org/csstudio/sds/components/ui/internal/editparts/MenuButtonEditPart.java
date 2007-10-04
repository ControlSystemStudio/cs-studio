package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.util.CustomMediaFactory;
import org.csstudio.sds.util.WidgetActionHandlerService;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * 
 * @author Helge Rickens, Kai Meyer
 *
 */
public final class MenuButtonEditPart extends AbstractWidgetEditPart {
	
	/**
	 * The {@link Listener} for the {@link MenuItem}s.
	 */
	private Listener _listener = new MenuActionListener();

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
				if (me.button == 1 && getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
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
	 * @param absolutX The x coordinate of the mouse in the display
	 * @param absolutY The y coordinate of the mouse in the display
	 */
	private void performDirectEdit(final Point point, final int absolutX, final int absolutY) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Menu menu = new Menu(shell, SWT.POP_UP);
		for (WidgetAction action : ((MenuButtonModel)this.getCastedModel()).getActionData().getWidgetActions()) {
			MenuItem item1 = new MenuItem(menu,SWT.PUSH);
			item1.setData(action);
			item1.setText(action.getActionLabel());
			item1.addListener(SWT.Selection, _listener);
			IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(action, IWorkbenchAdapter.class);
			if (adapter!=null) {
				item1.setImage(adapter.getImageDescriptor(action).createImage());
			}
		}
		
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

	/**
	 * {@inheritDoc}
	 */
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
	
	/**
	 * The {@link Listener} for the {@link MenuItem}s.
	 * @author Kai Meyer
	 *
	 */
	private final class MenuActionListener implements Listener {

		/**
		 * {@inheritDoc}
		 */
		public void handleEvent(final Event event) {
			WidgetAction action = (WidgetAction)event.widget.getData();
			WidgetActionHandlerService.getInstance().performAction(getCastedModel().getProperty(MenuButtonModel.PROP_ACTIONDATA), action);
		}
		
	}

}
