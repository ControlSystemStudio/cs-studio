/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.script.Expression;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <code>Utilities</code> provides some static methods used during the validation or quick fixing the failures.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class Utilities {

    static final String TAG_NAME = "name";
    static final String PROP_ACTION_HOOK_ALL = "hook_all";
    static final String PROP_ACTION_HOOK = "hook";
    
    public static String ruleMatchValueToMessage(int value) {
        switch(value) {
            case 1: return "PV name or trigger value does not match";
            case 2: return "Expressions do not match";
            case 3: return "Property ID does not match";
            default: return null;
        }
    }
    
    public static String actionMatchValueToMessage(int value) {
        if (value == 1) {
            return "Action of the same type was found, but the properties values were different";
        }
        return null;
    }
    
    public static String scriptMatchValueToMessage(int value) {
        switch(value) {
            case 1: return "PV name or trigger value does not match";
            case 2: return "Stop execution value does not match";
            case 3: return "Skip execution on first connection does not match";
            case 4: return "Execute even if PVs are disconnected does not match";
            case 5: return "Script text does not match";
            default: return null;    
        }
    }
    
    /**
     * Check if the rule definitions are identical. Rules are identical if they have the same name, property id,
     * PV tuples and expressions.
     * 
     * @param original the original rule
     * @param model the validated rule
     * @return 0 if the rules are identical, 1 if tuples are different, 2 if expressions are different, 3 if
     *          properties are different or 4 if names are different
     */
    public static int areRulesIdentical(RuleData original, RuleData model) {
        if (!Objects.equals(original.getName(), model.getName())) return 4;
        if (!Objects.equals(original.getPropId(),model.getPropId())) return 3;
                
        List<Expression> orgex = original.getExpressionList();
        List<Expression> modex = original.getExpressionList();
        if (orgex.size() != modex.size()) {
            return 2;
        }
        //order of expressions is irrelevant
        for (Expression oex : orgex) {
            checkExpression: {
                for (Expression mex : modex) {
                    if (mex.getBooleanExpression().equals(oex.getBooleanExpression()) 
                            && Objects.equals(oex.getValue(), mex.getValue())) {
                        break checkExpression;
                    }
                }
                return 2;
            }
        }
        
        List<PVTuple> orgpvs = original.getPVList();
        List<PVTuple> mpvs = model.getPVList();
        if (orgpvs.size() != mpvs.size()) {
            return 1;
        }
        //order of pvs is important
        for (int i = 0; i < orgpvs.size(); i++) {
            PVTuple opt = orgpvs.get(i);
            PVTuple mpt = mpvs.get(i);
            if (!mpt.pvName.equals(opt.pvName) || mpt.trigger != opt.trigger) {
                return 1; 
            }
        }
        
        return 0;
    }
    
    /**
     * Compare the widget actions and return 0 if the actions are identical, 1 if their
     * properties are different or 2 if they are of different types.
     * 
     * @param original the original action
     * @param model the validated action
     * @return 0 if actions are identical, 1 if properties are different or 2 if action types are different
     */
    public static int areActionsIdentical(AbstractWidgetAction original, AbstractWidgetAction model) {
        if (original.getActionType() != model.getActionType()) return 2;
        for (String id : original.getAllPropertyIDs()) {
            if (!Objects.equals(original.getPropertyValue(id),model.getPropertyValue(id))) {
                return 1;
            }
        }
        return 0;
    }
    
    /**
     * Checks if two scripts are identical, which means that all their fields are identical. If one of the properties
     * from the original is different from the one in the mode a value greater than 0 is returned. The value depends
     * on which property is different. 
     * 
     * @param original the original script
     * @param model the validated script
     * @return 0 if identical, or more than 0 if they are different
     */
    public static int areScriptsIdentical(ScriptData original, ScriptData model) {
        if (original.getScriptType() != model.getScriptType()) return 9;
        if (original.isEmbedded() != model.isEmbedded()) return 8;
        if (!Objects.equals(original.getPath(),model.getPath())) return 7;
        if (!Objects.equals(original.getScriptName(), model.getScriptName())) return 6;
        if (!Objects.equals(original.getScriptText(), model.getScriptText())) return 5;
        if (original.isCheckConnectivity() != model.isCheckConnectivity()) return 4;
        if (original.isSkipPVsFirstConnection() != model.isSkipPVsFirstConnection()) return 3;
        if (original.isStopExecuteOnError() != model.isStopExecuteOnError()) return 2;
        
        List<PVTuple> orgpvs = original.getPVList();
        List<PVTuple> mpvs = model.getPVList();
        if (orgpvs.size() != mpvs.size()) {
            return 1;
        }
        //order of pvs is important
        for (int i = 0; i < orgpvs.size(); i++) {
            PVTuple opt = orgpvs.get(i);
            PVTuple mpt = mpvs.get(i);
            if (!mpt.pvName.equals(opt.pvName) || mpt.trigger != opt.trigger) {
                return 1; 
            }
        }
        
        return 0;
    }
    
    /**
     * Loads the schema from the given path and stores data into a map, where the keys are the widget IDs and
     * the values are the widget models.
     * 
     * @param path the path to the schema
     * @return a map containing all elements defined in the schema
     * @throws IOException if there was an error reading the schema
     */
    public static Map<String,AbstractWidgetModel> loadSchema(IPath path) throws IOException {
        try (InputStream inputStream = ResourceUtil.pathToInputStream(path, false)) {
            DisplayModel displayModel = new DisplayModel(path);
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
    
            Map<String, AbstractWidgetModel> map = new HashMap<>();
            map.put(displayModel.getTypeID(), displayModel);
            loadModelFromContainer(displayModel,map);
            if (!displayModel.getConnectionList().isEmpty()) {
                map.put(ConnectionModel.ID, displayModel.getConnectionList().get(0));
            }
            return map;
        } catch (Exception e) {
            throw new IOException("Unable to load the OPI from " + path.toOSString() + ".",e);
        }
    }

    private static void loadModelFromContainer(AbstractContainerModel containerModel,
            Map<String, AbstractWidgetModel> map) {
        for (AbstractWidgetModel model : containerModel.getChildren()) {
            if (!map.containsKey(model.getTypeID())) {
                map.put(model.getTypeID(), model);
            }
            if (model instanceof AbstractContainerModel) {
                loadModelFromContainer((AbstractContainerModel) model, map);
            }
        }
    }
}
