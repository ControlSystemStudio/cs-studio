/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.ui;

import org.csstudio.opibuilder.validation.Activator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * <code>ResultsDialog</code> shows the summary of the validation - how many files, widgets, and properties were
 * analyzed and how many of them failed validation.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ResultsDialog extends TitleAreaDialog {

    private static final String MESSAGE = "OPI Validation completed successfully.\n"
            + "Below is the summary of the validation. Details can be found in the Problems View.";
    private static final String TITLE = "OPI Validation Results";

    private final int noValidatedFiles;
    private final int noFilesWithFailures;
    private final int noValidatedWidgets;
    private final int noWidgetsWithFailures;
    private final int noValidatedROProperties;
    private final int noROPropertiesWithCriticalFailures;
    private final int noROPropertiesWithMajorFailures;
    private final int noValidatedWRITEProperties;
    private final int noWRITEPropertiesWithFailures;
    private final int noValidatedRWProperties;
    private final int noRWPropertiesWithFailures;
    private final int noDeprecatedProperties;

    private final int noWidgetsWithRules;
    private final int noAllRules;
    private final int noWidgetsWithScripts;
    private final int noWidgetsWithPythonEmbedded;
    private final int noWidgetsWithJavascriptEmbedded;
    private final int noWidgetsWithPythonStandalone;
    private final int noWidgetsWithJavascriptStandalone;

    private Font font;
    private Font font2;

    /**
     * Constructs a new dialog.
     *
     * @param parentShell the parent shell
     * @param noValidatedFiles number of validated files
     * @param noFilesWithFailures number of files that did not pass the validation
     * @param noValidatedWidgets number of validated widgets
     * @param noWidgetsWithFailures number of widgets that did not pass the validation
     * @param noValidatedROProperties number of validated read-only properties
     * @param noROPropertiesWithCriticalFailures number of read-only properties that produced a critical validation failure
     * @param noROPropertiesWithMajorFailures number of read-only properties that produced a major validation failure
     * @param noValidatedWRITEProperties number of validated write properties
     * @param noWRITEPropertiesWithFailures number of write properties that did not pass validation
     * @param noDeprecatedProperties number of times deprecated properties usage was detected
     * @param noWidgetsWithRules number of widgets that have rules attached
     * @param noAllRules number of all rules
     * @param noWidgetsWithScripts number of widgets that have scripts attached
     * @param noWidgetsWithPythonEmbedded number of widgets with embedded python scripts attached
     * @param noWidgetsWithJavascriptEmbedded number of widgets with embedded javascripts attached
     * @param noWidgetsWithPythonStandalone number of widgets with standalone python scripts attached
     * @param noWidgetsWithJavascriptStandalone number of widgets with standalone javascripts attached
     */
    public ResultsDialog(Shell parentShell, int noValidatedFiles, int noFilesWithFailures,
            int noValidatedWidgets, int noWidgetsWithFailures, int noValidatedROProperties,
            int noROPropertiesWithCriticalFailures, int noROPropertiesWithMajorFailures,
            int noValidatedWRITEProperties, int noWRITEPropertiesWithFailures,
            int noValidatedRWProperties, int noRWPropertiesWithFailures, int noDeprecatedProperties,
            int noWidgetsWithRules, int noAllRules, int noWidgetsWithScripts, int noWidgetsWithPythonEmbedded,
            int noWidgetsWithJavascriptEmbedded, int noWidgetsWithPythonStandalone,
            int noWidgetsWithJavascriptStandalone) {
        super(parentShell);
        this.noValidatedFiles = noValidatedFiles;
        this.noFilesWithFailures = noFilesWithFailures;
        this.noValidatedWidgets = noValidatedWidgets;
        this.noWidgetsWithFailures = noWidgetsWithFailures;
        this.noValidatedROProperties = noValidatedROProperties;
        this.noROPropertiesWithCriticalFailures = noROPropertiesWithCriticalFailures;
        this.noROPropertiesWithMajorFailures = noROPropertiesWithMajorFailures;
        this.noValidatedWRITEProperties = noValidatedWRITEProperties;
        this.noWRITEPropertiesWithFailures = noWRITEPropertiesWithFailures;
        this.noValidatedRWProperties = noValidatedRWProperties;
        this.noRWPropertiesWithFailures = noRWPropertiesWithFailures;
        this.noDeprecatedProperties = noDeprecatedProperties;
        this.noWidgetsWithRules = noWidgetsWithRules;
        this.noAllRules = noAllRules;
        this.noWidgetsWithScripts = noWidgetsWithScripts;
        this.noWidgetsWithPythonEmbedded = noWidgetsWithPythonEmbedded;
        this.noWidgetsWithJavascriptEmbedded = noWidgetsWithJavascriptEmbedded;
        this.noWidgetsWithPythonStandalone = noWidgetsWithPythonStandalone;
        this.noWidgetsWithJavascriptStandalone = noWidgetsWithJavascriptStandalone;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(TITLE);
        newShell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (font != null) {
                    font.dispose();
                }
                if (font2 != null) {
                    font2.dispose();
                }
            }
        });

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite c = (Composite)super.createDialogArea(parent);
        setTitle(TITLE);
        setMessage(MESSAGE);
        GridLayout layout = (GridLayout)c.getLayout();
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 5;
        layout.marginLeft = 5;
        layout.marginRight = 5;

        Composite report = new Composite(parent, SWT.NONE);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true,true);
        report.setLayoutData(data);

        GridLayout reportLayout = new GridLayout();
        reportLayout.verticalSpacing = 3;
        reportLayout.horizontalSpacing = 3;
        reportLayout.marginLeft = 15;
        report.setLayout(reportLayout);

        createReportContents(report);
        return c;
    }

    private void createReportContents(Composite parent) {
        Label l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Validated files: " + noValidatedFiles);
        l.setLayoutData(createGridData(false));
        if (font == null) {
            FontData fd = l.getFont().getFontData()[0];
            fd.setStyle(SWT.ITALIC);
            font = new Font(parent.getDisplay(),fd);
        }

        l = new Label(parent, SWT.HORIZONTAL);
        int ratio = (int)((noFilesWithFailures/(double)noValidatedFiles)*100);
        l.setText("Files with errors: " + noFilesWithFailures + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Validated widgets: " + noValidatedWidgets);
        l.setLayoutData(createGridData(false));

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWidgetsWithFailures/(double)noValidatedWidgets)*100);
        l.setText("Widgets with errors: " + noWidgetsWithFailures + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Validated RO properties: " + noValidatedROProperties);
        l.setLayoutData(createGridData(false));

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noROPropertiesWithCriticalFailures/(double)noValidatedROProperties)*100);
        int ratio1 = (int)((noROPropertiesWithMajorFailures/(double)noValidatedROProperties)*100);
        l.setText("RO properties with major errors: " + noROPropertiesWithMajorFailures + " ("
                    + ratio1 + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);
        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("RO properties with critical errors: " + noROPropertiesWithCriticalFailures + " ("
                + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Validated WRITE properties: " + noValidatedWRITEProperties);
        l.setLayoutData(createGridData(false));
        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWRITEPropertiesWithFailures/(double)noValidatedWRITEProperties)*100);
        l.setText("WRITE properties with errors: " + noWRITEPropertiesWithFailures + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Validated RW properties: " + noValidatedRWProperties);
        l.setLayoutData(createGridData(false));
        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noRWPropertiesWithFailures/(double)noValidatedRWProperties)*100);
        l.setText("RW properties with errors: " + noRWPropertiesWithFailures + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Number of properties changed by rules: " + noAllRules);
        l.setLayoutData(createGridData(false));

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWidgetsWithRules/(double)noValidatedWidgets)*100);
        l.setText("Widgets with predefined rules: " + noWidgetsWithRules + " (" + ratio + " %)");
        l.setLayoutData(createGridData(false));

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Number of widgets using scripts: " + noWidgetsWithScripts);
        l.setLayoutData(createGridData(false));

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWidgetsWithPythonEmbedded/(double)noValidatedWidgets)*100);
        l.setText("Widgets using embedded python scripts: " + noWidgetsWithPythonEmbedded + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWidgetsWithPythonStandalone/(double)noValidatedWidgets)*100);
        l.setText("Widgets using standalone python scripts: " + noWidgetsWithPythonStandalone + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWidgetsWithJavascriptEmbedded/(double)noValidatedWidgets)*100);
        l.setText("Widgets using embedded javascripts: " + noWidgetsWithJavascriptEmbedded + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        ratio = (int)((noWidgetsWithJavascriptStandalone/(double)noValidatedWidgets)*100);
        l.setText("Widgets using standalone javascripts: " + noWidgetsWithJavascriptStandalone + " (" + ratio + " %)");
        l.setLayoutData(createGridData(true));
        l.setFont(font);

        l = new Label(parent, SWT.HORIZONTAL);
        l.setText("Deprecated properties: " + noDeprecatedProperties);
        l.setLayoutData(createGridData(false));

        if (Activator.getInstance().isWarnAboutJythonScripts()) {
            if (noWidgetsWithPythonEmbedded + noWidgetsWithPythonStandalone > 0) {
                l = new Label(parent, SWT.HORIZONTAL);
                l.setText("Jython sciprts are used!");
                l.setLayoutData(createGridData(true));
                l.setFont(font);
            }
        }
    }

    private GridData createGridData(boolean indent) {
        GridData d = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true,false);
        if (indent) {
            d.horizontalIndent = 5;
        } else {
            d.verticalIndent = 5;
        }
        return d;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
    }
}
