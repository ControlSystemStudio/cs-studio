package org.csstudio.sds.ui.internal.runmode;

import java.io.InputStream;
import java.util.Random;

import org.csstudio.sds.internal.connection.ConnectionService;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * A box that manages a shell, which uses a GEF graphical viewer to display SDS
 * displays.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ViewRunModeBox extends AbstractRunModeBox implements
		IPartListener {
	private DisplayViewPart _viewPart;

	/**
	 * Constructor.
	 * 
	 * @param inputStream
	 * @param title
	 * @param connectionService
	 * @param view
	 *            optional {@link DisplayViewPart} instance
	 */
	public ViewRunModeBox(InputStream inputStream, String title,
			ConnectionService connectionService, DisplayViewPart view) {
		super(inputStream, title, connectionService);
		_viewPart = view;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void handleWindowPositionChange(int x, int y, int width,
			int height) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bringToTop() {
		_viewPart.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */

	protected void doDispose() {
		// close the shell
		if (_viewPart != null) {
			_viewPart.getViewSite().getPage().removePartListener(this);
			// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			// .getActivePage().hideView(_viewPart);
		}
	}

	/**
	 * Note: View parts can be injected (see
	 * {@link #setViewPart(DisplayViewPart)}). In this case the injected view
	 * is used. Otherwise a new view will be created.
	 */
	@Override
	protected GraphicalViewer doOpen(int x, int y, int width, int height,
			String title) {
		if (_viewPart != null) {
			// the view was already provided via constructor (this usually
			// happens during a perspective restore)
			return _viewPart.getGraphicalViewer();
		} else {
			// create and open the view
			String secondaryId = "" + System.currentTimeMillis();

			final IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				page.showView(DisplayViewPart.PRIMARY_ID, secondaryId,
						IWorkbenchPage.VIEW_ACTIVATE);
			} catch (final PartInitException e) {
				_viewPart = null;
			}

			if (page != null) {
				// get a handle to that view
				final IViewReference ref = page.findViewReference(
						DisplayViewPart.PRIMARY_ID, secondaryId);

				final IViewPart tmpView = ref != null ? ref.getView(true)
						: null;

				if ((tmpView != null) && (tmpView instanceof DisplayViewPart)) {
					_viewPart = (DisplayViewPart) tmpView;
				}
			}

			if (_viewPart != null) {
				_viewPart.getViewSite().getPage().addPartListener(this);
				return _viewPart.getGraphicalViewer();
			}
		}
		return null;
	}

	public void partActivated(IWorkbenchPart part) {
	}

	public void partBroughtToTop(IWorkbenchPart part) {
	}

	public void partClosed(IWorkbenchPart part) {
		if (part == _viewPart) {
			dispose();
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
	}

	public void partOpened(IWorkbenchPart part) {
	}

	public DisplayViewPart getView() {
		return _viewPart;
	}
}
