package org.csstudio.sds.ui.internal.editor;

import org.csstudio.sds.ui.internal.actions.MoveToLayerAction;
import org.csstudio.sds.ui.internal.layers.ILayerManager;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * A {@link CompoundContributionItem}, which contains a {@link MoveToLayerAction} for 
 * every Layer of the {@link DisplayEditor}.
 * @author Kai Meyer
 */
public final class LayerCompoundContributionItem extends CompoundContributionItem {
	
	/**
	 * The {@link ILayerManager}.
	 */
	private ILayerManager _layerManager;
	/**
	 * The {@link DisplayEditor}.
	 */
	private DisplayEditor _displayEditor;

	/**
	 * Constructor.
	 */
	public LayerCompoundContributionItem() {
		super("Layers");
		IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getPartService().getActivePart();
		if (activePart instanceof DisplayEditor) {
			_displayEditor = (DisplayEditor) activePart;
			_layerManager = (ILayerManager) activePart
				.getAdapter(ILayerManager.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		IContributionItem[] items = new IContributionItem[_layerManager.getLayerSupport().getLayers().size()];
		for (int i=0;i<_layerManager.getLayerSupport().getLayers().size();i++) {
			MoveToLayerAction moveToLayerAction = new MoveToLayerAction(_displayEditor, _layerManager.getLayerSupport().getLayers().get(i).getId());
			moveToLayerAction.update();
			items[i] = new ActionContributionItem(moveToLayerAction);
		}
		return items;
	}

}
