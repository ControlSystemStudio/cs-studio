/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import static org.eclipse.core.runtime.Platform.getBundle;
import static org.eclipse.core.runtime.Platform.getPreferencesService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
/**
 *  Default perspective for CS-Studio
 *
 *  The perspective can be configured using preferences.
 *  org.csstudio.utility.product/cs_studio_perspective=PluginId:ViewID:Postition:single/multiple;
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CSStudioPerspective implements IPerspectiveFactory
{
    /** Perspective ID registered in plugin.xml */
    final public static String ID = "org.csstudio.utility.product.CSStudioPerspective";

    /** Suffix for matching View IDs when multiple instances are allowed */
    final private static String MULTIPLE = ":*";

    @Override
    public void createInitialLayout(IPageLayout layout)
    {
        // left | editor
        //      |
        //      |
        //      +-------------
        //      | bottom
        String editor = layout.getEditorArea();
        IFolderLayout left = layout.createFolder("left",
                        IPageLayout.LEFT, 0.25f, editor);
        IFolderLayout bottom = layout.createFolder("bottom",
                        IPageLayout.BOTTOM, 0.66f, editor);

        left.addView("org.eclipse.ui.views.ResourceNavigator");

    for (Entry<String, Integer> entry : findViews().entrySet()) {
        switch (entry.getValue()) {
        case IPageLayout.LEFT:
        left.addPlaceholder(entry.getKey());
        break;
        case IPageLayout.BOTTOM:
        bottom.addPlaceholder(entry.getKey());
        break;
        default:
        break;
        }
    }

        // Populate the "Window/Perspectives..." menu with suggested persp.

        layout.addPerspectiveShortcut(ID);
        for (String perspectiveId : getPerspectiveShortcutIds()) {
            layout.addPerspectiveShortcut(perspectiveId);
    }


        // Populate the "Window/Views..." menu with suggested views
        for (String viewId : getViewShortcutIds()) {
            layout.addShowViewShortcut(viewId);
    }

    }

    /**
     * Searches for views that require placeholders added to the CS-Studio perspective
     * Key is the viewId and the value is the IPageLayout location
     * @return
     */
    private Map<String, Integer> findViews(){
    Map<String, Integer> viewPlaceholderMap = new HashMap<String, Integer>();

    // Defaults
    viewPlaceholderMap.put(IPageLayout.ID_PROGRESS_VIEW, IPageLayout.BOTTOM);

    // Views from preferences
    String csStudioPerspectivePreference = getPreferencesService()
                            .getString("org.csstudio.utility.product",
                                   "cs_studio_perspective",
                                   "",
                                   null);
    for (String viewPlaceholderInfoPref : Arrays.asList(csStudioPerspectivePreference.split(";"))) {
        String[] viewPlaceholderInfo = viewPlaceholderInfoPref.split(":");
        if(viewPlaceholderInfo.length == 4){
        if (isPluginAvailable(viewPlaceholderInfo[0].trim())) {
            int location;
            switch (viewPlaceholderInfo[2].trim()) {
            case "left":
            location = IPageLayout.LEFT;
            break;
            case "bottom":
            location = IPageLayout.BOTTOM;
            break;
            case "right":
            location = IPageLayout.RIGHT;
            break;
            default:
            location = IPageLayout.BOTTOM;
            break;
            }

            if (viewPlaceholderInfo[3].trim().equalsIgnoreCase("multiple")) {
            viewPlaceholderMap.put(viewPlaceholderInfo[0].trim(), location);
            viewPlaceholderMap.put(viewPlaceholderInfo[0].trim() + MULTIPLE, location);
            } else {
            viewPlaceholderMap.put(viewPlaceholderInfo[0].trim(), location);
            }
        }
        }else{
        // syntax error in preference describing view placeholder
        }
    };
    return viewPlaceholderMap;
    }

    /**
     * Get a list of Ids of the perspectives to be added to the open perspective shortcut
     *
     * @return
     */
    private List<String> getPerspectiveShortcutIds() {
    List<String> perspectiveIds = new ArrayList<String>();
    String[] perspectiveShortcut = getPreferencesService()
                        .getString("org.csstudio.utility.product",
                               "perspective_shortcut",
                               "",
                               null).split(";");
    for (String perspectiveInfoPref : Arrays.asList(perspectiveShortcut)) {
        String[] perspectiveInfo =  perspectiveInfoPref.split(":");
        if( perspectiveInfo.length == 2 ){
        if(isPluginAvailable(perspectiveInfo[0].trim())){
            perspectiveIds.add(perspectiveInfo[1].trim());
        }
        }
    }
    return perspectiveIds;
    }

    /**
     * Get a list of Ids of the views to be added to the open view shortcut
     * @return
     */
    private List<String> getViewShortcutIds() {
    List<String> viewIds = new ArrayList<String>();
    // defaults
    viewIds.add(IPageLayout.ID_PROGRESS_VIEW);
    // additional views read from preferences
    String[] viewShortcut = getPreferencesService()
                        .getString("org.csstudio.utility.product",
                               "view_shortcut",
                               "",
                               null).split(";");
    for (String viewInfoPref : Arrays.asList(viewShortcut)) {
        String[] viewInfo =  viewInfoPref.split(":");
        if( viewInfo.length == 2 ){
        if(isPluginAvailable(viewInfo[0].trim())){
            viewIds.add(viewInfo[1].trim());
        }
        }
    }
    return viewIds;
    }

    /** Check if certain plugin is available
     *  @param plugin_id ID of the plugin
     *  @return <code>true</code> if available
     */
    private boolean isPluginAvailable(final String plugin_id)
    {
        return getBundle(plugin_id) != null;
    }
}
