package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
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
 * @author Helge Rickens, Kai Meyer, Xihui Chen
 * 
 */
public final class MenuButtonEditPart extends AbstractWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final MenuButtonModel model = (MenuButtonModel) getWidgetModel();

		Label label = new Label();

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
						me.consume();
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
				menuManager.add(new MenuAction(action));
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



	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
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
	 * An Action, which encapsulates a {@link AbstractWidgetActionModel}.
	 * 
	 * @author Kai Meyer
	 * 
	 */
	private final class MenuAction extends Action {
		/**
		 * The {@link AbstractWidgetActionModel}.
		 */
		private AbstractWidgetAction _widgetAction;

		/**
		 * Constructor.
		 * 
		 * @param widgetAction
		 *            The encapsulated {@link AbstractWidgetActionModel}
		 */
		public MenuAction(final AbstractWidgetAction widgetAction) {
			_widgetAction = widgetAction;
			this.setText(_widgetAction.getDescription());
			Object adapter = widgetAction.getAdapter(IWorkbenchAdapter.class);
			if (adapter != null && adapter instanceof IWorkbenchAdapter) {
				this.setImageDescriptor(((IWorkbenchAdapter)adapter)
						.getImageDescriptor(widgetAction));
			}
			setEnabled(widgetAction.isEnabled());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					_widgetAction.run();
				}
			});
		}
	}

	

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		// always one sample
		return 1;
	}



}
