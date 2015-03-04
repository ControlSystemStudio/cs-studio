/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.validation.Activator;
import org.csstudio.opibuilder.validation.ui.ResultsDialog;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
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
public class Validator extends AbstractValidator {

    private static final Logger LOGGER = Logger.getLogger(Validator.class.getName());
    
    /** Marker name for OPI validation failure */ 
    public static final String MARKER_PROBLEM = Activator.ID + ".opiValidationProblem";
    /** Marker name for OPI loading error */
    public static final String MARKER_ERROR = Activator.ID + ".opiLoadingError";
    /** The ID of the editor to open when the marker is double clicked */
    private static final String DEFAULT_TEXT_EDITOR = "org.eclipse.ui.DefaultTextEditor";
    /** The name of the marker attribute that contains the validation failure */
    public static final String ATTR_VALIDATION_FAILURE = "validationFailure";
    
    private final SchemaVerifier verifier;

    /**
     * Construct the validator.
     */
    public Validator() {
        IPath rulesFile = Activator.getInstance().getRulesFile();
        Map<String, ValidationRule> rules = new HashMap<String, ValidationRule>();
        if (rulesFile != null) {
            Properties p = new Properties();
            try (FileInputStream stream = new FileInputStream(rulesFile.toFile())) {
                p.load(stream);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Cannot read the rules definition file: " + rulesFile.toOSString(), e);
            }
            
            for (Entry<Object,Object> e : p.entrySet()) {
                rules.put((String)e.getKey(), ValidationRule.valueOf((String)e.getValue()));
            }
        }
        verifier = new SchemaVerifier(rules);
    }

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
        
        ValidationResult result = new ValidationResult();
        try {
            ValidationFailure[] failures = verifier.validate(resource.getLocation());
            for (ValidationFailure vf : failures) {
                ValidatorMessage message = ValidatorMessage.create(vf.getMessage(), resource);
                message.setType(MARKER_PROBLEM);
                if (vf.getRule() == ValidationRule.RO) {
                    if (vf.isCritical()) {
                        message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                    } else {
                        message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                    }
                } else if (vf.getRule() == ValidationRule.WRITE) {
                    message.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                }
                message.setAttribute(IMarker.LOCATION, vf.getLocation());
                message.setAttribute(IMarker.LINE_NUMBER, vf.getLineNumber());
                message.setAttribute(ATTR_VALIDATION_FAILURE, vf);
                message.setAttribute(IDE.EDITOR_ID_ATTR, DEFAULT_TEXT_EDITOR);
                
                result.add(message);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.AbstractValidator#validationStarting(org.eclipse.core.resources.IProject,
     * org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
        verifier.clean();
        if (project == null) {
            showView("org.eclipse.ui.views.ProgressView");
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
            showView("org.eclipse.ui.views.ProblemView");
            if (Activator.getInstance().isShowSummaryDialog()) {
                Display.getDefault().asyncExec(() -> {
                    IWorkbenchPage page = getPage();
                    ResultsDialog dialog = new ResultsDialog(page.getWorkbenchWindow().getShell(),
                            verifier.getNumberOfAnalyzedFiles(),
                            verifier.getNumberOfFilesFailures(),
                            verifier.getNumberOfAnalyzedWidgets(),
                            verifier.getNumberOfWidgetsFailures(),
                            verifier.getNumberOfROProperties(),
                            verifier.getNumberOfCriticalROFailures(),
                            verifier.getNumberOfMajorROFailures(),
                            verifier.getNumberOfWRITEProperties(),
                            verifier.getNumberOfWRITEFailures()
                            );
                    dialog.open();
                });
            }
        }
    }
    
    private void showView(String view) {
        Display.getDefault().asyncExec(() -> {
            try {
                IWorkbenchPage page = getPage();
                if (page != null) {
                    page.showView(view);
                }
            } catch (PartInitException e) {
            }
        });
    }
    
    private IWorkbenchPage getPage() {
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

}
