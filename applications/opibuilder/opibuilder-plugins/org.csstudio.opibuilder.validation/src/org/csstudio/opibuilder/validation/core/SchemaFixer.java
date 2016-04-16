/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;

/**
 *
 * <code>SchemaFixer</code> provides utility methods for applying quick fixes on the validation markers.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class SchemaFixer {

    private SchemaFixer() {
    }

    /**
     * Fix the given validation failures. All failures are expected to belong to the same OPI file. The fix replaces the
     * actual value of the validated property with the expected value.
     *
     * @param failureToFix the validation failures to fix
     * @throws IOException if there was an exception in reading the OPI
     * @throws IllegalArgumentException if the failures do not belong to the same OPI
     */
    public static void fixOPIFailure(ValidationFailure[] failureToFix) throws IOException, IllegalArgumentException {
        if (failureToFix.length == 0) {
            return;
        }
        IPath path = failureToFix[0].getPath();
        for (ValidationFailure f : failureToFix) {
            if (!f.getPath().equals(path)) {
                throw new IllegalArgumentException("All validation failures must belong to the same path.");
            } else if (!f.isFixable()) {
                throw new IllegalArgumentException("Validation failure '" + f + "' is not fixable.");
            }
        }
        DisplayModel displayModel = null;
        try (InputStream inputStream = ResourceUtil.pathToInputStream(path, false)) {
            displayModel = new DisplayModel(failureToFix[0].getPath());
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
        } catch (Exception e) {
            throw new IOException("Could not read the opi " + path.toOSString() + ".", e);
        }

        for (ValidationFailure f : failureToFix) {
            fixFailure: if (f instanceof SubValidationFailure) {
                // this is a sub failure in action, script or rule
                if (((SubValidationFailure) f).isFixed()) {
                    continue;
                }
                // if the parent is being fixed, do not fix this one, it will be fixed by the parent
                ValidationFailure parent = ((SubValidationFailure) f).getParent();
                for (ValidationFailure ff : failureToFix) {
                    if (ff == parent) {
                        break fixFailure;
                    }
                }

                // fix the sub validation failure
                AbstractWidgetModel model = findWidget(displayModel, parent);
                if (model == null) {
                    continue;
                }
                fixSubValidation((SubValidationFailure) f, model);
            } else {
                AbstractWidgetModel model = findWidget(displayModel, f);
                if (model == null) {
                    continue;
                }

                if (f.getRule() == ValidationRule.RO) {
                    model.setPropertyValue(f.getProperty(), f.getExpectedValue());
                } else if (f.getRule() == ValidationRule.WRITE) {
                    // the only writable and fixable properties are actions, scripts and rules, fonts and colors
                    // in this case add the missing ones to the model
                    if (f.isUsingUndefinedValue()) {
                        model.setPropertyValue(f.getProperty(), f.getExpectedValue());
                    } else {
                        Object value = model.getPropertyValue(f.getProperty());
                        addToValue(value, f.getExpectedValue());
                    }
                } else if (f.getRule() == ValidationRule.RW) {
                    // if this is color
                    if (f.isUsingUndefinedValue()) {
                        model.setPropertyValue(f.getProperty(), f.getExpectedValue());
                    }
                } else if (f.getRule() == ValidationRule.DEPRECATED) {
                    // should be self fixing
                }
                if (f.hasSubFailures()) {
                    SubValidationFailure[] subs = f.getSubFailures();
                    for (SubValidationFailure s : subs) {
                        if (s.isToBeRemoved()) {
                            fixSubValidation(s, model);
                        }
                        s.setFixed(true);
                    }
                }
            }
        }

        IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path, false);
        if (r instanceof IFile) {
            IFile file = (IFile) r;
            if (file.exists()) {
                try (FileOutputStream output = new FileOutputStream(file.getLocation().toFile())) {
                    XMLUtil.widgetToOutputStream(displayModel, output, true);
                }
            }
        }
    }

    /**
     * For actions, rules and scripts we need to add the missing elements.
     *
     * @param destination the destination
     * @param expected the expected value
     */
    private static void addToValue(Object destination, Object expected) {
        if (destination instanceof RulesInput) {
            List<RuleData> existing = ((RulesInput) destination).getRuleDataList();
            List<RuleData> needToBe = ((RulesInput) expected).getRuleDataList();
            addMissing(existing, needToBe, (r1, r2) -> Utilities.areRulesIdentical(r1, r2));
        } else if (destination instanceof ActionsInput) {
            List<AbstractWidgetAction> existing = ((ActionsInput) destination).getActionsList();
            List<AbstractWidgetAction> needToBe = ((ActionsInput) expected).getActionsList();
            addMissing(existing, needToBe, (a1, a2) -> Utilities.areActionsIdentical(a1, a2));
        } else if (destination instanceof ScriptsInput) {
            List<ScriptData> existing = ((ScriptsInput) destination).getScriptList();
            List<ScriptData> needToBe = ((ScriptsInput) expected).getScriptList();
            addMissing(existing, needToBe, (s1, s2) -> Utilities.areScriptsIdentical(s1, s2));
        }
    }

    /**
     * Add elements from needToBe to destination if they are not already present there.
     *
     * @param destination the list that should contain the element
     * @param needToBe the list of elements that should be present in the destination
     * @param comparator the comparator to compare elements
     */
    private static <T> void addMissing(List<T> destination, List<T> needToBe, Comparator<T> comparator) {
        for (T stuff : needToBe) {
            findStuff: {
                for (T d : destination) {
                    if (comparator.compare(stuff, d) == 0) {
                        break findStuff;
                    }
                }
                destination.add(stuff);
            }
        }
    }

    /**
     * Finds the widget model that matches the validation failure.
     *
     * @param parent the parent to look for the widget model in
     * @param failure the failure to match
     * @return the widget model if found, or null if match was not found
     */
    private static AbstractWidgetModel findWidget(AbstractContainerModel parent, ValidationFailure failure) {
        AbstractWidgetModel model = findWidgetInternal(parent, failure, true);
        if (model == null) {
            model = findWidgetInternal(parent, failure, false);
        }
        return model;
    }

    private static AbstractWidgetModel findWidgetInternal(AbstractContainerModel parent, ValidationFailure failure,
        boolean useWuid) {
        AbstractWidgetModel m = doesWidgetMatch(parent, failure, useWuid);
        if (m != null) {
            return m;
        }
        for (AbstractWidgetModel model : parent.getChildren()) {
            if (model instanceof AbstractContainerModel) {
                m = findWidgetInternal((AbstractContainerModel) model, failure, useWuid);
                if (m != null) {
                    return m;
                }
            }
            m = doesWidgetMatch(model, failure, useWuid);
            if (m != null) {
                return m;
            }
        }
        if (failure.getWidgetType().equals(ConnectionModel.ID) && parent instanceof DisplayModel) {
            for (ConnectionModel model : ((DisplayModel) parent).getConnectionList()) {
                m = doesWidgetMatch(model, failure, useWuid);
                if (m != null) {
                    return m;
                }
            }
        }
        return null;
    }

    private static AbstractWidgetModel doesWidgetMatch(AbstractWidgetModel model, ValidationFailure failure,
        boolean useWuid) {
        String widgetType = model.getTypeID();
        String widgetName = model.getName();
        boolean skipCheck = false;
        if (useWuid && !failure.getWUID().equals(model.getWUID())) {
            skipCheck = true;
        }

        if (!skipCheck && widgetType.equals(failure.getWidgetType()) && widgetName.equals(failure.getWidgetName())) {
            Object obj = model.getPropertyValue(failure.getProperty());
            if (equals(obj, failure.getActualValue())) {
                return model;
            }
        }
        return null;
    }

    /**
     * Check if the objects are identical. In most cases this is simple equals call, except in the case of actions,
     * scripts and rules. Ideally those would be compared with equals as well, but they have properties that we do not
     * want to compare here (e.g. widgetModel).
     *
     * @param o1
     * @param o2
     * @return true if the objects are identical or false otherwise
     */
    private static boolean equals(Object o1, Object o2) {
        if (o1 instanceof ActionsInput && o2 instanceof ActionsInput) {
            ActionsInput oi = (ActionsInput) o1;
            ActionsInput mi = (ActionsInput) o2;
            if (oi.isFirstActionHookedUpToWidget() != mi.isFirstActionHookedUpToWidget()) {
                return false;
            }
            if (oi.isHookUpAllActionsToWidget() != mi.isHookUpAllActionsToWidget()) {
                return false;
            }
            LinkedList<AbstractWidgetAction> olist = oi.getActionsList();
            LinkedList<AbstractWidgetAction> mlist = mi.getActionsList();
            for (AbstractWidgetAction a : olist) {
                findAction: {
                    for (AbstractWidgetAction b : mlist) {
                        if (Utilities.areActionsIdentical(a, b) == 0) {
                            break findAction;
                        }
                    }
                    return false;
                }
            }
            return true;
        } else if (o1 instanceof ScriptsInput && o2 instanceof ScriptsInput) {
            ScriptsInput oi = (ScriptsInput) o1;
            ScriptsInput mi = (ScriptsInput) o2;
            List<ScriptData> olist = oi.getScriptList();
            List<ScriptData> mlist = mi.getScriptList();
            for (ScriptData a : olist) {
                findScript: {
                    for (ScriptData b : mlist) {
                        if (Utilities.areScriptsIdentical(a, b) == 0) {
                            break findScript;
                        }
                    }
                    return false;
                }
            }
            return true;
        } else if (o1 instanceof RulesInput && o2 instanceof RulesInput) {
            RulesInput oi = (RulesInput) o1;
            RulesInput mi = (RulesInput) o2;
            List<RuleData> olist = oi.getRuleDataList();
            List<RuleData> mlist = mi.getRuleDataList();
            for (RuleData a : olist) {
                findRule: {
                    for (RuleData b : mlist) {
                        if (Utilities.areRulesIdentical(a, b) == 0) {
                            break findRule;
                        }
                    }
                    return false;
                }
            }
            return true;
        } else {
            return Objects.equals(o1, o2);
        }
    }

    private static void fixSubValidation(SubValidationFailure failure, AbstractWidgetModel model) {
        ValidationFailure parent = failure.getParent();
        Object propValue = model.getPropertyValue(parent.getProperty());
        ValidationRule rule = failure.getRule();
        if (failure.isToBeRemoved()) {
            // describes a sub item that needs to be removed
            Object toRemove = failure.getActualValue();
            if (toRemove instanceof RuleData) {
                List<RuleData> list = ((RulesInput) propValue).getRuleDataList();
                for (int i = 0; i < list.size(); i++) {
                    if (Utilities.areRulesIdentical(list.get(i), (RuleData) toRemove) == 0) {
                        list.remove(i);
                        break;
                    }
                }
            } else if (toRemove instanceof ScriptData) {
                List<ScriptData> list = ((ScriptsInput) propValue).getScriptList();
                for (int i = 0; i < list.size(); i++) {
                    if (Utilities.areScriptsIdentical(list.get(i), (ScriptData) toRemove) == 0) {
                        list.remove(i);
                        break;
                    }
                }
            } else if (toRemove instanceof AbstractWidgetAction) {
                List<AbstractWidgetAction> list = ((ActionsInput) propValue).getActionsList();
                for (int i = 0; i < list.size(); i++) {
                    if (Utilities.areActionsIdentical(list.get(i), (AbstractWidgetAction) toRemove) == 0) {
                        list.remove(i);
                        break;
                    }
                }
            }
        } else if (rule == ValidationRule.WRITE) {
            // expected sub value is missing
            Object toAdd = failure.getExpectedValue();
            if (toAdd instanceof RuleData) {
                ((RulesInput) propValue).getRuleDataList().add((RuleData) toAdd);
            } else if (toAdd instanceof ScriptData) {
                ((ScriptsInput) propValue).getScriptList().add((ScriptData) toAdd);
            } else if (toAdd instanceof AbstractWidgetAction) {
                ((ActionsInput) propValue).getActionsList().add((AbstractWidgetAction) toAdd);
            }
        } else if (rule == ValidationRule.RO) {
            if (failure.getActualValue() == null) {
                // missing actual value, so add it
                Object toAdd = failure.getExpectedValue();
                if (toAdd instanceof RuleData) {
                    ((RulesInput) propValue).getRuleDataList().add((RuleData) toAdd);
                } else if (toAdd instanceof ScriptData) {
                    ((ScriptsInput) propValue).getScriptList().add((ScriptData) toAdd);
                } else if (toAdd instanceof AbstractWidgetAction) {
                    ((ActionsInput) propValue).getActionsList().add((AbstractWidgetAction) toAdd);
                }
            } else if (failure.getExpectedValue() == null) {
                // there is an actual value, which should not be there
                Object toRemove = failure.getActualValue();
                if (toRemove instanceof RuleData) {
                    RuleData r = (RuleData) toRemove;
                    List<RuleData> list = ((RulesInput) propValue).getRuleDataList();
                    for (int i = 0; i < list.size(); i++) {
                        if (Utilities.areRulesIdentical(list.get(i), r) == 0) {
                            list.remove(i);
                            return;
                        }
                    }
                } else if (toRemove instanceof ScriptData) {
                    ScriptData r = (ScriptData) toRemove;
                    List<ScriptData> list = ((ScriptsInput) propValue).getScriptList();
                    for (int i = 0; i < list.size(); i++) {
                        if (Utilities.areScriptsIdentical(list.get(i), r) == 0) {
                            list.remove(i);
                            return;
                        }
                    }
                } else if (toRemove instanceof AbstractWidgetAction) {
                    AbstractWidgetAction r = (AbstractWidgetAction) toRemove;
                    List<AbstractWidgetAction> list = ((ActionsInput) propValue).getActionsList();
                    for (int i = 0; i < list.size(); i++) {
                        if (Utilities.areActionsIdentical(list.get(i), r) == 0) {
                            list.remove(i);
                            return;
                        }
                    }
                }
            } else {
                // can only happen in the action case
                ActionsInput ai = (ActionsInput) propValue;
                if (Utilities.PROP_ACTION_HOOK.equals(failure.getSubPropertyTag())) {
                    ai.setHookUpFirstActionToWidget((Boolean) failure.getExpectedValue());
                } else if (Utilities.PROP_ACTION_HOOK_ALL.equals(failure.getSubPropertyTag())) {
                    ai.setHookUpAllActionsToWidget((Boolean) failure.getExpectedValue());
                }
            }
        }
    }
}
