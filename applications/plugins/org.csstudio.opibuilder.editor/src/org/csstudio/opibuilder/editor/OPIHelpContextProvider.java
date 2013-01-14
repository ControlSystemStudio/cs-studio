package org.csstudio.opibuilder.editor;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IContextProvider;
import org.eclipse.help.IHelpResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**The dynamic help provider for OPI Editor and widgets.
 * @author Xihui Chen
 *
 */
public class OPIHelpContextProvider implements IContextProvider{

	private GraphicalViewer viewer;
	
	public OPIHelpContextProvider(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	public int getContextChangeMask() {
		return IContextProvider.SELECTION;
	}

	public IContext getContext(Object target) {
		ISelection selection = viewer.getSelection();
		if(selection instanceof IStructuredSelection){
			Object obj = ((IStructuredSelection)selection).getFirstElement();
			if(obj instanceof AbstractBaseEditPart && !(obj instanceof DisplayEditpart)){
				return new WidgetSelectionContext(
						((AbstractBaseEditPart)obj).getWidgetModel()); //$NON-NLS-1$
			}
		}
		return HelpSystem.getContext(OPIBuilderPlugin.PLUGIN_ID +  ".opi_editor"); //$NON-NLS-1$
	}

	public String getSearchExpression(Object target) {
		return null;
	}

	final static class WidgetSelectionContext implements IContext2 {

		private AbstractWidgetModel widgetModel;

		public WidgetSelectionContext(AbstractWidgetModel widgetModel) {
			this.widgetModel = widgetModel;
		}

		public IHelpResource[] getRelatedTopics() {
			IHelpResource[] helpResources = new WidgetHelpResource[1];
			helpResources[0] = new WidgetHelpResource(widgetModel);
			return helpResources;
		}

		public String getText() {
			return WidgetsService.getInstance().getWidgetDescriptor(
					widgetModel.getTypeID()).getDescription();
		}

		public String getTitle() {
			return WidgetsService.getInstance().getWidgetDescriptor(
					widgetModel.getTypeID()).getName();
		}

		public String getStyledText() {
			return getText();
		}

		public String getCategory(IHelpResource topic) {
			if(topic instanceof WidgetHelpResource)
				return "See Details";
			return null;
		}
		
	}
	
	final static class WidgetHelpResource implements IHelpResource {

		private AbstractWidgetModel widgetModel;
				
		
		public WidgetHelpResource(AbstractWidgetModel widgetModel) {
			this.widgetModel = widgetModel;
		}

		public String getHref() {
			WidgetDescriptor widgetDescriptor = 
					WidgetsService.getInstance().getWidgetDescriptor(widgetModel.getTypeID());
			String onlineHelpHtml = widgetDescriptor.getOnlineHelpHtml();
			if( onlineHelpHtml != null && !onlineHelpHtml.trim().isEmpty()){
				return widgetDescriptor.getPluginId() + "/" + widgetDescriptor.getOnlineHelpHtml(); //$NON-NLS-1$
			}
			if(widgetDescriptor.getPluginId().trim().equals("org.csstudio.opibuilder.widgets")){ //$NON-NLS-1$
				String modelClassName = widgetModel.getClass().getSimpleName();
				StringBuilder sb = new StringBuilder("/"); //$NON-NLS-1$
				sb.append(OPIBuilderPlugin.PLUGIN_ID);
				sb.append("/html/widgets/"); //$NON-NLS-1$
				sb.append(modelClassName.substring(0, modelClassName.length()-5));
				sb.append(".html");
				return  sb.toString();
			}
			return OPIBuilderPlugin.PLUGIN_ID + "/html/widgets/WidgetHelpNotFound.html"; //$NON-NLS-1$
		}

		public String getLabel() {
			return WidgetsService.getInstance().getWidgetDescriptor(
					widgetModel.getTypeID()).getName();
		}
		
	}
}
