package org.csstudio.sds.ui.internal.runmode;

import org.csstudio.sds.model.layers.LayerSupport;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editparts.DisplayEditPart;
import org.csstudio.sds.ui.internal.layers.LayerTreeViewer;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * An action to manage the visibility of the layer contained in the current display.
 * @author Kai Meyer
 *
 */
public final class ChangeLayerVisibilityAction extends Action {
	
	private GraphicalViewer _viewer;

	/**
	 * Constructor.
	 */
	public ChangeLayerVisibilityAction(GraphicalViewer viewer) {
		this.setText("Layer Visibility...");
		this.setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "/icons/layer_view.gif"));
		this.setToolTipText("Opens a dialog to change the visibility of the defined layers");
		_viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		EditPart contents = _viewer.getRootEditPart().getContents();
		if (contents instanceof DisplayEditPart) {
			DisplayEditPart displayEditPart = (DisplayEditPart) contents;
			LayerSupport layerSupport = displayEditPart.getContainerModel().getLayerSupport();
			Dialog dialog = new LayerDialog(_viewer.getControl().getShell(), layerSupport);
			dialog.open();
		}
	}
	
	private final class LayerDialog extends Dialog {
		
		private LayerTreeViewer _layerTreeViewer;
		private LayerSupport _layerSupport;

		/**
	     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
	     * will have no visual representation (no widgets) until it is told to open.
	     * <p>
	     * Note that the <code>open</code> method blocks for input dialogs.
	     * </p>
	     * 
	     * @param parentShell
	     *            the parent shell, or <code>null</code> to create a top-level
	     *            shell
	     * @param layerSupport
	     *            the {@link LayerSupport}
	     */
		public LayerDialog(final Shell parentShell, final LayerSupport layerSupport) {
			super(parentShell);
			_layerSupport = layerSupport;
			this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
					| SWT.BORDER | SWT.RESIZE);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configureShell(final Shell shell) {
	        super.configureShell(shell);
			shell.setText("Layers");
	    }
		
		/**
	     * {@inheritDoc}
	     */
		@Override
	    protected Control createDialogArea(final Composite parent) {
	        Composite composite = (Composite) super.createDialogArea(parent);
	        composite.setLayout(new GridLayout(1,false));
	        _layerTreeViewer = new LayerTreeViewer(null, composite, "LAYER_DIALOG");
	        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true,true);
	        gridData.heightHint = 150;
	        gridData.widthHint = 200;
			_layerTreeViewer.setLayoutData(gridData);
	        _layerTreeViewer.setLayerSupport(_layerSupport);
	        return composite;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean close() {
			_layerTreeViewer.dispose();
			return super.close();
		}
		
	}

}
