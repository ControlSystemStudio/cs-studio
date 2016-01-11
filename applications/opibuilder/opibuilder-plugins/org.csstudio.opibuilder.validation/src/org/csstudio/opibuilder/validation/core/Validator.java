/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.validation.Activator;
import org.csstudio.opibuilder.validation.core.ui.ContentProvider;
import org.csstudio.opibuilder.validation.core.ui.TreeViewerListener;
import org.csstudio.opibuilder.validation.ui.ResultsDialog;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.internal.SWTEventListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.internal.views.markers.ProblemsView;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

/**
 *
 * <code>Validator</code> implements the wst validation api for validating OPI files.
 * OPIs are validated by comparing their values to the ones defined in the OPI schema.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
@SuppressWarnings("restriction")
public class Validator extends AbstractValidator {

    private static class NonNullHashMap<K,T> extends HashMap<K,T> {
        private static final long serialVersionUID = 7385574868370529896L;
        @Override
        public T put(K key, T value) {
            if (value == null) return null;
            return super.put(key, value);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(Validator.class.getName());

    /** Marker name for OPI validation failure */
    public static final String MARKER_PROBLEM = Activator.ID + ".opiValidationProblem";
    /** Marker name for OPI loading error */
    public static final String MARKER_ERROR = Activator.ID + ".opiLoadingError";
    /** The ID of the editor to open when the marker is double clicked */
    private static final String DEFAULT_TEXT_EDITOR = "org.eclipse.ui.DefaultTextEditor";
    /** The name of the marker attribute that contains the validation failure */
    public static final String ATTR_VALIDATION_FAILURE = "validationFailure";

    private SchemaVerifier verifier;

    //if a rule matches this pattern it defines a property, if it doesn't match, the rule is a regex
    private static final Pattern TRUE_PROPERTY_PATTERN = Pattern.compile("[0-9a-z_\\.]*");

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.validation.AbstractValidator#validate(org.eclipse.core.resources.IResource, int, org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
        if (resource.getType() != IResource.FILE) {
            return null;
        }

        if (monitor.isCanceled()) {
            return null;
        }

        try {
            if(!Utilities.shouldContinueIfFileOpen("validation",resource)) {
                monitor.setCanceled(true);
                return null;
            }
        } catch (PartInitException e) {
            LOGGER.log(Level.SEVERE, "Could not obtain editor inputs.", e);
            monitor.setCanceled(true);
            return null;
        }

        boolean useDefaultEditor = Activator.getInstance().isShowMarkersInDefaultEditor();
        ValidationResult result = new ValidationResult();
        try {
            ValidationFailure[] failures = verifier.validate(resource.getFullPath());
            ValidatorMessage message;
            for (ValidationFailure vf : failures) {
                message = createMessage(vf, resource, useDefaultEditor);
                result.add(message);
                if (vf.hasSubFailures()) {
                    for (SubValidationFailure f : vf.getSubFailures()) {
                        message = createMessage(f, resource, useDefaultEditor);
                        result.add(message);
                    }
                }
            }
        } catch (IOException e) {
            ValidatorMessage message = ValidatorMessage.create(e.getMessage(), resource);
            message.setType(MARKER_ERROR);
            message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            message.setAttribute(IMarker.LOCATION, resource.getFullPath().toFile().getAbsolutePath());
            result.add(message);
        }

        return result;
    }

    private ValidatorMessage createMessage(ValidationFailure vf, IResource resource, boolean useDefaultEditor) {
        if (vf instanceof SubValidationFailure) {
            IResource res = ((SubValidationFailure)vf).getResource();
            if (res != null) {
                resource = res;
            }
        }
        ValidatorMessage message = ValidatorMessage.create(vf.getMessage(), resource);
        message.setType(MARKER_PROBLEM);
        if (vf.getRule() == ValidationRule.RO) {
            if (vf.isCritical()) {
                message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            } else {
                message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            }
        } else if (vf.getRule() == ValidationRule.WRITE) {
            if (vf.isCritical()) {
                message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            } else {
                message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
            }
        } else if (vf.getRule() == ValidationRule.RW) {
            //Can happen in the font and colour case
            message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        }
        message.setAttribute(IMarker.LOCATION, vf.getLocation());
        message.setAttribute(IMarker.LINE_NUMBER, vf.getLineNumber());
        message.setAttribute(ATTR_VALIDATION_FAILURE, vf);
        message.setAttribute(AbstractWidgetModel.PROP_WIDGET_UID, vf.getWUID());
        if (!useDefaultEditor) {
            message.setAttribute(IDE.EDITOR_ID_ATTR, DEFAULT_TEXT_EDITOR);
        }
        return message;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.wst.validation.AbstractValidator#validationStarting(org.eclipse.core.resources.IProject,
     * org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
        if (project == null) {
            if (Activator.getInstance().isClearMarkers()) {
                try {
                    //cleanup all opi validation markers
                    ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(MARKER_PROBLEM, true, IProject.DEPTH_INFINITE);
                    ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(MARKER_ERROR, true, IProject.DEPTH_INFINITE);
                } catch (CoreException e) {
                    LOGGER.log(Level.WARNING, "Could not delete opi validation markers.",e);
                }
            }
            //bring the progress view to the top
            showView("org.eclipse.ui.views.ProgressView",false);
            //reload the rules, just in case they have been changed
            IPath rulesFile = Activator.getInstance().getRulesFile();
            verifier = createVerifier(PreferencesHelper.getSchemaOPIPath(),rulesFile,monitor);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.wst.validation.AbstractValidator#validationFinishing(org.eclipse.core.resources.IProject,
     * org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
        if (project == null) {
            //at the end bring the problems view to the top and if requested show the summary dialog
            showView("org.eclipse.ui.views.ProblemView", true);
            if (Activator.getInstance().isShowSummaryDialog()) {
                final SchemaVerifier sv = verifier;
                Display.getDefault().asyncExec(() -> {
                    IWorkbenchPage page = getPage();
                    new ResultsDialog(page.getWorkbenchWindow().getShell(),
                            sv.getNumberOfAnalyzedFiles(),
                            sv.getNumberOfFilesFailures(),
                            sv.getNumberOfAnalyzedWidgets(),
                            sv.getNumberOfWidgetsFailures(),
                            sv.getNumberOfROProperties(),
                            sv.getNumberOfCriticalROFailures(),
                            sv.getNumberOfMajorROFailures(),
                            sv.getNumberOfWRITEProperties(),
                            sv.getNumberOfWRITEFailures(),
                            sv.getNumberOfRWProperties(),
                            sv.getNumberOfRWFailures(),
                            sv.getNumberOfDeprecatedFailures(),
                            sv.getNumberOfWidgetsWithRules(),
                            sv.getNumberOfAllRules(),
                            sv.getNumberOfWidgetsWithScripts(),
                            sv.getNumberOfWidgetsWithPythonEmbedded(),
                            sv.getNumberOfWidgetsWithJavascriptEmbedded(),
                            sv.getNumberOfWidgetsWithPythonStandalone(),
                            sv.getNumberOfWidgetsWithJavascriptStandalone())
                        .open();
                });
            }
        }
    }

    private static void showView(final String view, final boolean update) {
        Display.getDefault().asyncExec(() -> {
            try {
                IWorkbenchPage page = getPage();
                if (page != null) {
                    page.showView(view);
                    if (update) {
                        updateProblemsView(page);
                    }
                }
            } catch (PartInitException e) {
                LOGGER.log(Level.WARNING, "Could not open the view '" + view + "'.",e);
            }
        });
    }

    private static void updateProblemsView(IWorkbenchPage page) {
        try {
            ProblemsView v = (ProblemsView)page.showView("org.eclipse.ui.views.ProblemView");
            Field f = ExtendedMarkersView.class.getDeclaredField("viewer");
            f.setAccessible(true);
            TreeViewer viewer = (TreeViewer)f.get(v);
            ITreeContentProvider provider = (ITreeContentProvider) viewer.getContentProvider();
            if (!(provider instanceof ContentProvider)) {
                viewer.setContentProvider(new ContentProvider(provider));
                Listener[] exp = viewer.getTree().getListeners(SWT.Expand);
                Listener[] col = viewer.getTree().getListeners(SWT.Collapse);
                for (int i = 0; i < exp.length; i++) {
                    if (exp[i] instanceof TypedListener) {
                        SWTEventListener l = ((TypedListener)exp[i]).getEventListener();
                        if (l.getClass().getName().contains("ExtendedMarkersView")) {
                            viewer.getTree().removeListener(SWT.Expand, exp[i]);
                            viewer.getTree().addListener(SWT.Expand,
                                    new TypedListener(new TreeViewerListener((TreeListener)l)));
                        }
                    }
                }
                for (int i = 0; i < col.length; i++) {
                    if (col[i] instanceof TypedListener) {
                        SWTEventListener l = ((TypedListener)col[i]).getEventListener();
                        if (l.getClass().getName().contains("ExtendedMarkersView")) {
                            viewer.getTree().removeListener(SWT.Collapse, col[i]);
                            viewer.getTree().addListener(SWT.Collapse,
                                    new TypedListener(new TreeViewerListener((TreeListener)l)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ignore
            //cannot update view, we will use the original one
        }
    }

    private static IWorkbenchPage getPage() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null && PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0) {
            window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
        }
        if (window == null) {
            return null;
        }
        IWorkbenchPage page = window.getActivePage();
        if (page == null && window.getPages().length > 0) {
            page = window.getPages()[0];
        }
        return page;
    }

    /**
     * Creates a verifier based on the schema and rules.
     *
     * @param schema the path to the OPI schema against which the files will be verified
     * @param rulesFile the file containing the validation rules
     * @param monitor a monitor which is cancelled in case of failure (can be null)
     * @return schema verifier
     */
    public static SchemaVerifier createVerifier(IPath schema, IPath rulesFile, IProgressMonitor monitor) {
        Map<String, ValidationRule> rules = new HashMap<>();
        Map<Pattern, ValidationRule> rulePatterns = new HashMap<>();
        Map<String, String[]> acceptableValues = new NonNullHashMap<>();
        Map<Pattern, String[]> acceptableValuesPatterns = new NonNullHashMap<>();
        Map<String, String[]> removedValues = new NonNullHashMap<>();
        Map<Pattern, String[]> removedValuesPatterns = new NonNullHashMap<>();
        if (rulesFile != null) {
            Properties p = new Properties();
            try (FileInputStream stream = new FileInputStream(rulesFile.toFile())) {
                p.load(stream);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Cannot read the rules definition file: " + rulesFile.toOSString(), e);
                if (monitor != null) {
                    monitor.setCanceled(true);
                }
            }
            String ruleStr;
            for (Entry<Object,Object> e : p.entrySet()) {
                String key = ((String)e.getKey()).toLowerCase();
                String value = (String)e.getValue();
                int idx = value.indexOf('[');
                int idxRem = value.indexOf('{');
                String[] acceptables = null;
                String[] removies = null;
                if (idx > 0 || idxRem > 0) {
                    if (idx > 0 && idxRem > 0) {
                        ruleStr = value.substring(0,Math.min(idx,idxRem)).trim();
                    } else if (idx > 0) {
                        ruleStr = value.substring(0,idx).trim();
                    } else {
                        ruleStr = value.substring(0,idxRem).trim();
                    }
                    try {
                        if (idx > 0) {
                            acceptables = value.substring(idx+1, value.indexOf(']')).split("\\;");
                            for (int i = 0; i < acceptables.length; i++) {
                                acceptables[i] = acceptables[i].trim();
                            }
                        }
                        if (idxRem > 0) {
                            removies = value.substring(idxRem+1, value.indexOf('}')).split("\\;");
                            for (int i = 0; i < removies.length; i++) {
                                removies[i] = removies[i].trim();
                            }
                        }
                    } catch (Exception ex) {
                        //in case that acceptables cannot be parsed, just ignore them
                        LOGGER.log(Level.WARNING, "The rule for property '" + key + "' is incorrectly defined."
                                + " Check the alternative acceptable and removed values definition.");
                    }
                } else {
                    ruleStr = value;
                }

                try {
                    ValidationRule rule = ValidationRule.valueOf(ruleStr.toUpperCase());
                    if (TRUE_PROPERTY_PATTERN.matcher(key).matches()) {
                        rules.put(key, rule);
                        acceptableValues.put(key, acceptables);
                        removedValues.put(key, removies);
                    } else {
                        Pattern ptrn = Pattern.compile(key);
                        rulePatterns.put(ptrn, rule);
                        acceptableValuesPatterns.put(ptrn, acceptables);
                        removedValuesPatterns.put(ptrn, removies);
                    }
                } catch(IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, e.getKey() + " is not defined correctly.");
                }
            }
        }
        return new SchemaVerifier(schema,rules, rulePatterns, acceptableValues, acceptableValuesPatterns,
                removedValues, removedValuesPatterns);
    }
}
