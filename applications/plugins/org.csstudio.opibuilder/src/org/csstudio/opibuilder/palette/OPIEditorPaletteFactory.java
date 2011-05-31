/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.palette;

import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.feedback.IGraphicalFeedbackFactory;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.jface.resource.ImageDescriptor;

/**The factory help to create the palette.
 * @author Xihui Chen
 *
 */
public class OPIEditorPaletteFactory {

	public static PaletteRoot createPalette(){
		PaletteRoot palette = new PaletteRoot();
		createPaletteContents(palette);
		return palette;
	}
	
	private static void createPaletteContents(PaletteRoot palette){
		Map<String, List<String>> categoriesMap = 
			WidgetsService.getInstance().getAllCategoriesMap();
		for(final Map.Entry<String, List<String>> entry: categoriesMap.entrySet()){
			PaletteDrawer categoryDrawer = new PaletteDrawer(entry.getKey());
			for(String typeId : entry.getValue()){
				WidgetDescriptor widgetDescriptor = 
					WidgetsService.getInstance().getWidgetDescriptor(typeId);
				ImageDescriptor icon = CustomMediaFactory.getInstance().
					getImageDescriptorFromPlugin(
							widgetDescriptor.getPluginId(), widgetDescriptor.getIconPath());
				CombinedTemplateCreationEntry widgetEntry = new CombinedTemplateCreationEntry(
					widgetDescriptor.getName(), 
					widgetDescriptor.getDescription(),
					new WidgetCreationFactory(widgetDescriptor), icon, icon);
				
				IGraphicalFeedbackFactory feedbackFactory = 
					WidgetsService.getInstance().getWidgetFeedbackFactory(
							widgetDescriptor.getTypeID());
				if( feedbackFactory != null && feedbackFactory.getCreationTool() != null){
					widgetEntry.setToolClass(feedbackFactory.getCreationTool());
				}				
				categoryDrawer.add(widgetEntry);
			}
			palette.add(categoryDrawer);			
		}		
	}
	
}
