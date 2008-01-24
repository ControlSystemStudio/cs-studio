package org.csstudio.sds.ui.internal.runmode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A box that manages a shell, which uses a GEF graphical viewer to display SDS
 * displays.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ShellRunModeBox extends AbstractRunModeBox {
	/**
	 * The wideness of the ScrollBars.
	 */
	private static final int SCROLLBAR_WIDTH = 17;

	/**
	 * The shell.
	 */
	private Shell _shell;

	/**
	 * Constructor.
	 * @param inputStream The {@link InputStream} for the display
	 * @param title The title for the shell
	 * @param connectionService The {@link ConnectionService}
	 */
	public ShellRunModeBox(final InputStream inputStream, final String title,
			final ConnectionService connectionService) {
		super(inputStream, title, connectionService);
	}

	/**
	 * {@inheritDoc}
	 */
	protected GraphicalViewer doOpen(final int x, final int y, final int width,final  int height,
			final String title) {
		// create a shell
		_shell = new Shell();

		_shell.setText(title);
		_shell.setLocation(x, y);
		_shell.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0, 0, 0, 0));
		_shell.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/sds.gif"));
		Composite c = new Composite(_shell, SWT.None);
		c.setLayout(new FillLayout());
		GridData gd = new GridData();
		gd.verticalAlignment = 1;
		gd.horizontalAlignment = 1;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.widthHint = width + SCROLLBAR_WIDTH;
		gd.heightHint = height + SCROLLBAR_WIDTH;
		c.setLayoutData(gd);

		// configure a graphical viewer
		final GraphicalViewer graphicalViewer = RunModeService
				.createGraphicalViewer(c);

		ActionRegistry actionRegistry = new ActionRegistry();
		this.createActions(actionRegistry);
		// provide a context menu
		final ContextMenuProvider cmProvider = new RunModeContextMenuProvider(
				graphicalViewer, actionRegistry);

		cmProvider.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				manager.setRemoveAllWhenShown(true);
			}
		});
		graphicalViewer.setContextMenu(cmProvider);
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//				.getActivePart().getSite().registerContextMenu(cmProvider,
//						graphicalViewer);

		// provide a toolbar
		createToolbar(_shell, graphicalViewer);

		_shell.pack();

		// add dispose listener
		_shell.addDisposeListener(new DisposeListener() {
			/**
			 * {@inheritDoc}
			 */
			public void widgetDisposed(final DisposeEvent e) {
				dispose();
			}
		});

		// open the shell
		_shell.open();

		return graphicalViewer;
	}
	
	/**
	 * Creates the actions for the shell.
	 * @param actionRegistry The {@link ActionRegistry} for the created actions
	 */
	private void createActions(final ActionRegistry actionRegistry) {
		Action closeAction = new Action() {
			@Override
			public void run() {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						_shell.close();
					}
				});
			}
		};
		closeAction.setText("Close Shell");
		closeAction.setId(RunModeContextMenuProvider.CLOSE_ACTION_ID);
		actionRegistry.registerAction(closeAction);
	}

	/**
	 * Disposes the shell.
	 */
	protected void doDispose() {
		// close the shell
		if (!_shell.isDisposed()) {
			_shell.close();
			_shell = null;
		}
	}

	/**
	 * Creates a toolbar for the graphical viewer.
	 * 
	 * @param shell
	 *            the shell
	 * @param graphicalViewer
	 *            the graphical viewer
	 */
	@SuppressWarnings("deprecation")
	protected void createToolbar(final Shell shell,
			final GraphicalViewer graphicalViewer) {
		// menu bar
		MenuManager menuManager = new MenuManager();

		// configure zoom actions
		RootEditPart rootEditPart = graphicalViewer.getRootEditPart();

		if (rootEditPart instanceof ScalableFreeformRootEditPart) {
			final ZoomManager zm = ((ScalableFreeformRootEditPart) rootEditPart)
					.getZoomManager();

			final List<String> zoomLevels = new ArrayList<String>(3);
			zoomLevels.add(ZoomManager.FIT_ALL);
			zoomLevels.add(ZoomManager.FIT_WIDTH);
			zoomLevels.add(ZoomManager.FIT_HEIGHT);
			zm.setZoomLevelContributions(zoomLevels);

			if (zm != null) {
				MenuManager zoomManager = new MenuManager("Zoom");
				final IAction zoomIn = new ZoomInAction(zm);
				final IAction zoomOut = new ZoomOutAction(zm);

				zoomManager.add(zoomIn);
				zoomManager.add(zoomOut);
				
				menuManager.add(zoomManager);
			}
			
			MenuManager layerManager = new MenuManager("Layers");
			layerManager.add(new ChangeLayerVisibilityAction(graphicalViewer));
			menuManager.add(layerManager);
		}

		Menu menu = menuManager.createMenuBar(shell);
		shell.setMenuBar(menu);
	}

	/**
	 * Sets the focus on this Shell.
	 */
	public void bringToTop() {
		if (_shell != null && !_shell.isDisposed()) {
			_shell.setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void handleWindowPositionChange(final int x, final int y, final int width,
			final int height) {
		_shell.setBounds(x, y, width, height);
	}

	@Override
	protected void finalize() throws Throwable {
		CentralLogger.getInstance().debug(this, "finalized()");
	}
}
