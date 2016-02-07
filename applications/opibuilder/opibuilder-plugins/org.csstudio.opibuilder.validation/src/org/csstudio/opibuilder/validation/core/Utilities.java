/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
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

import org.csstudio.opibuilder.editor.OPIEditor;
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
import org.csstudio.opibuilder.validation.Activator;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 *
 * <code>Utilities</code> provides some static methods used during the validation or quick fixing the failures.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class Utilities {

    static final String PROP_ACTION_HOOK_ALL = "hook_all";
    static final String PROP_ACTION_HOOK = "hook";

    private Utilities() {
    }

    /**
     * Get message for the rule match failure.
     *
     * @see #areRulesIdentical(RuleData, RuleData)
     *
     * @param value the rule comparison value
     * @return message corresponding to the given value
     */
    public static String ruleMatchValueToMessage(int value) {
        switch (value) {
            case 1:
                return "PV name or trigger value does not match";
            case 2:
                return "Expressions do not match";
            case 3:
                return "Property ID does not match";
            default:
                return null;
        }
    }

    /**
     * Get message for the action match failure.
     *
     * @see #areActionsIdentical(AbstractWidgetAction, AbstractWidgetAction)
     *
     * @param value the action comparison value
     * @return message corresponding to the given value
     */
    public static String actionMatchValueToMessage(int value) {
        if (value == 1) {
            return "Action of the same type was found, but the properties values were different";
        }
        return null;
    }

    /**
     * Get message for the script match failure.
     *
     * @see #areScriptsIdentical(ScriptData, ScriptData)
     *
     * @param value the script comparison failure
     * @return message corresponding to the given value
     */
    public static String scriptMatchValueToMessage(int value) {
        switch (value) {
            case 1:
                return "PV name or trigger value does not match";
            case 2:
                return "Stop execution value does not match";
            case 3:
                return "Skip execution on first connection does not match";
            case 4:
                return "Execute even if PVs are disconnected does not match";
            case 5:
                return "Script text does not match";
            default:
                return null;
        }
    }

    /**
     * Check if the rule definitions are identical. Rules are identical if they have the same name, property id, PV
     * tuples and expressions.
     *
     * @param original the original rule
     * @param model the validated rule
     * @return 0 if the rules are identical, 1 if tuples are different, 2 if expressions are different, 3 if properties
     *         are different or 4 if names are different
     */
    public static int areRulesIdentical(RuleData original, RuleData model) {
        if (!Objects.equals(original.getName(), model.getName())) {
            return 4;
        }
        if (!Objects.equals(original.getPropId(), model.getPropId())) {
            return 3;
        }

        List<Expression> orgex = original.getExpressionList();
        List<Expression> modex = original.getExpressionList();
        if (orgex.size() != modex.size()) {
            return 2;
        }
        // order of expressions is irrelevant
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
        // order of pvs is important
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
     * Compare the widget actions and return 0 if the actions are identical, 1 if their properties are different or 2 if
     * they are of different types.
     *
     * @param original the original action
     * @param model the validated action
     * @return 0 if actions are identical, 1 if properties are different or 2 if action types are different
     */
    public static int areActionsIdentical(AbstractWidgetAction original, AbstractWidgetAction model) {
        if (original.getActionType() != model.getActionType()) {
            return 2;
        }
        for (String id : original.getAllPropertyIDs()) {
            if (!Objects.equals(original.getPropertyValue(id), model.getPropertyValue(id))) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Checks if two scripts are identical, which means that all their fields are identical. If one of the properties
     * from the original is different from the one in the mode a value greater than 0 is returned. The value depends on
     * which property is different.
     *
     * @param original the original script
     * @param model the validated script
     * @return 0 if identical, or more than 0 if they are different
     */
    public static int areScriptsIdentical(ScriptData original, ScriptData model) {
        if (original.getScriptType() != model.getScriptType()) {
            return 9;
        }
        if (original.isEmbedded() != model.isEmbedded()) {
            return 8;
        }
        if (!Objects.equals(original.getPath(), model.getPath())) {
            return 7;
        }
        if (!Objects.equals(original.getScriptName(), model.getScriptName())) {
            return 6;
        }
        if (!Objects.equals(original.getScriptText(), model.getScriptText())) {
            return 5;
        }
        if (original.isCheckConnectivity() != model.isCheckConnectivity()) {
            return 4;
        }
        if (original.isSkipPVsFirstConnection() != model.isSkipPVsFirstConnection()) {
            return 3;
        }
        if (original.isStopExecuteOnError() != model.isStopExecuteOnError()) {
            return 2;
        }

        List<PVTuple> orgpvs = original.getPVList();
        List<PVTuple> mpvs = model.getPVList();
        if (orgpvs.size() != mpvs.size()) {
            return 1;
        }
        // order of pvs is important
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
     * Loads the schema from the given path and stores data into a map, where the keys are the widget IDs and the values
     * are the widget models.
     *
     * @param path the path to the schema
     * @return a map containing all elements defined in the schema
     * @throws IOException if there was an error reading the schema
     */
    public static Map<String, AbstractWidgetModel> loadSchema(IPath path) throws IOException {
        try (InputStream inputStream = ResourceUtil.pathToInputStream(path, false)) {
            DisplayModel displayModel = new DisplayModel(path);
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());

            Map<String, AbstractWidgetModel> map = new HashMap<>();
            map.put(displayModel.getTypeID(), displayModel);
            loadModelFromContainer(displayModel, map);
            if (!displayModel.getConnectionList().isEmpty()) {
                map.put(ConnectionModel.ID, displayModel.getConnectionList().get(0));
            }
            return map;
        } catch (Exception e) {
            throw new IOException("Unable to load the OPI from " + path.toOSString() + ".", e);
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

    /**
     * Check if any of the resources are currently open in any of the editors. If yes and the file is not saved, a popup
     * message will inform the user about that and ask to continue or cancel. If the user cancels the popup at any time,
     * the method returns false. If there are no dirty files or if the user confirms all popups, method returns true.
     *
     * @param process the name of the process that is being done (validation, quick fix)
     * @param resources the resources to check
     * @return true if all messages have been confirmed or no resource is dirty, false if at least one popup has been
     *         cancelled
     * @throws PartInitException if initialisation of the editor failed
     */
    public static boolean shouldContinueIfFileOpen(String process, IResource... resources) throws PartInitException {
        final boolean save = Activator.getInstance().isSaveBeforeValidation();
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow w : windows) {
            for (IEditorReference r : w.getActivePage().getEditorReferences()) {
                if (OPIEditor.ID.equals(r.getId()) && r.isDirty()) {
                    IEditorInput input = r.getEditorInput();
                    if (input instanceof NoResourceEditorInput) {
                        input = ((NoResourceEditorInput) input).getOriginEditorInput();
                    }
                    if (input instanceof FileEditorInput) {
                        final IFile file = ((FileEditorInput) input).getFile();
                        for (IResource m : resources) {
                            if (m.equals(file)) {
                                if (save) {
                                    Display.getDefault()
                                        .syncExec(() -> r.getEditor(false).doSave(new NullProgressMonitor()));
                                } else {
                                    final Shell shell = w.getShell();
                                    boolean[] ret = new boolean[1];
                                    Display.getDefault()
                                        .syncExec(() -> ret[0] = MessageDialog.openConfirm(shell,
                                            "File Is Being Edited",
                                            "The OPI file " + file.getFullPath() + " is being edited and has unsaved "
                                                + "changes.\nClick OK to continue " + process
                                                + " or Cancel to abort."));
                                    if (!ret[0]) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
