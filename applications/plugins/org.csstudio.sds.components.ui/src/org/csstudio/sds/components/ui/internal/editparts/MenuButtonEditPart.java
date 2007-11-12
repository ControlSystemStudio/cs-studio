package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.components.model.LabelModel;
import org.csstudio.sds.components.model.MenuButtonModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.widgetactionhandler.WidgetActionHandlerService;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
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
		label.setEnabled(model.getEnabled() && getExecutionMode().equals(ExecutionMode.RUN_MODE));
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
		if (this.getCastedModel().getEnabled() && getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
			final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MenuManager menuManager = new MenuManager();
			for (WidgetAction action : ((MenuButtonModel)this.getCastedModel()).getActionData().getWidgetActions()) {
				menuManager.add(new MenuAction(action));
			}
			Menu menu = menuManager.createContextMenu(shell);
			
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
			//shell.setFocus();
		}
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
	 * An Action, which encapsulates a {@link WidgetAction}.
	 * @author Kai Meyer
	 *
	 */
	private final class MenuAction extends Action {
		/**
		 * The {@link WidgetAction}.
		 */
		private WidgetAction _widgetAction;
		
		/**
		 * Constructor.
		 * @param widgetAction The encapsulated {@link WidgetAction}
		 */
		public MenuAction(final WidgetAction widgetAction) {
			_widgetAction = widgetAction;
			this.setText(_widgetAction.getActionLabel());
			IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(widgetAction, IWorkbenchAdapter.class);
			if (adapter!=null) {
				this.setImageDescriptor(adapter.getImageDescriptor(widgetAction));
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					WidgetActionHandlerService.getInstance().performAction(getCastedModel().getProperty(AbstractWidgetModel.PROP_ACTIONDATA), _widgetAction);
				}
			});
		}
	}

}
