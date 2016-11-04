/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.SWTResourceManager;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class DataSourcesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Text directoryText;
    private Text delimiterText;
    private TreeViewer treeViewer;

    /**
     * Create the preference page.
     */
    public DataSourcesPreferencePage ( ) {
        setDescription(Messages.DSPP_description);
        setTitle(Messages.DSPP_title);
    }

    /**
     * Create contents of the preference page.
     *
     * @param parent
     */
    @Override
    public Control createContents ( Composite parent ) {

        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new FormLayout());

        Label directoryCaption = new Label(container, SWT.NONE);
        FormData fd_directoryCaption = new FormData();
        directoryCaption.setLayoutData(fd_directoryCaption);
        directoryCaption.setText(Messages.DSPP_directoryCaption_text);

        directoryText = new Text(container, SWT.BORDER);
        directoryText.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        directoryText.setEditable(false);
        fd_directoryCaption.top = new FormAttachment(directoryText, 3, SWT.TOP);
        fd_directoryCaption.right = new FormAttachment(directoryText, -6);
        directoryText.setText("");
        FormData fd_directoryText = new FormData();
        fd_directoryText.left = new FormAttachment(0, 178);
        directoryText.setLayoutData(fd_directoryText);

        Button browseButton = new Button(container, SWT.NONE);
        fd_directoryText.right = new FormAttachment(browseButton, -6);
        fd_directoryText.top = new FormAttachment(browseButton, 4, SWT.TOP);
        FormData fd_browseButton = new FormData();
        fd_browseButton.top = new FormAttachment(0, 10);
        fd_browseButton.right = new FormAttachment(100, -10);
        browseButton.setLayoutData(fd_browseButton);
        browseButton.setText(Messages.DSPP_browseButton_text);

        Composite treeComposite = new Composite(container, SWT.NONE);
        treeComposite.setLayout(new TreeColumnLayout());
        FormData fd_treeComposite = new FormData();
        fd_treeComposite.top = new FormAttachment(browseButton, 6);
        fd_treeComposite.left = new FormAttachment(directoryCaption, 0, SWT.LEFT);
        fd_treeComposite.right = new FormAttachment(browseButton, 0, SWT.RIGHT);
        treeComposite.setLayoutData(fd_treeComposite);

        treeViewer = new TreeViewer(treeComposite, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

                Group cdsGroup = new Group(container, SWT.NONE);
                cdsGroup.setText(Messages.DSPP_cdsGroup_text);
                cdsGroup.setLayout(new FormLayout());
                FormData fd_cdsGroup = new FormData();
                fd_cdsGroup.bottom = new FormAttachment(100, -21);
                fd_cdsGroup.right = new FormAttachment(browseButton, 0, SWT.RIGHT);
                fd_cdsGroup.left = new FormAttachment(directoryCaption, 0, SWT.LEFT);
                cdsGroup.setLayoutData(fd_cdsGroup);

                        Label defaultDataSourceCaption = new Label(cdsGroup, SWT.NONE);
                        defaultDataSourceCaption.setAlignment(SWT.RIGHT);
                        FormData fd_defaultDataSourceCaption = new FormData();
                        fd_defaultDataSourceCaption.top = new FormAttachment(0, 14);
                        defaultDataSourceCaption.setLayoutData(fd_defaultDataSourceCaption);
                        defaultDataSourceCaption.setText(Messages.DSPP_defaultDataSourceCaption_text);

                                Combo defaultDataSourceCombo = new Combo(cdsGroup, SWT.NONE);
                                fd_defaultDataSourceCaption.right = new FormAttachment(defaultDataSourceCombo, -6);
                                FormData fd_defaultDataSourceCombo = new FormData();
                                fd_defaultDataSourceCombo.right = new FormAttachment(0, 291);
                                fd_defaultDataSourceCombo.left = new FormAttachment(0, 129);
                                fd_defaultDataSourceCombo.top = new FormAttachment(0, 10);
                                defaultDataSourceCombo.setLayoutData(fd_defaultDataSourceCombo);

                                        Label delimiterCaption = new Label(cdsGroup, SWT.NONE);
                                        delimiterCaption.setAlignment(SWT.RIGHT);
                                        FormData fd_delimiterCaption = new FormData();
                                        fd_delimiterCaption.top = new FormAttachment(defaultDataSourceCaption, 9);
                                        fd_delimiterCaption.right = new FormAttachment(defaultDataSourceCaption, 0, SWT.RIGHT);
                                        fd_delimiterCaption.left = new FormAttachment(defaultDataSourceCaption, 0, SWT.LEFT);
                                        delimiterCaption.setLayoutData(fd_delimiterCaption);
                                        delimiterCaption.setText(Messages.DSPP_delimiterCaption_text);

                                                delimiterText = new Text(cdsGroup, SWT.BORDER);
                                                delimiterText.setText(Messages.DSPP_delimiterText_text);
                                                FormData fd_delimiterText = new FormData();
                                                fd_delimiterText.right = new FormAttachment(defaultDataSourceCombo, 0, SWT.RIGHT);
                                                fd_delimiterText.left = new FormAttachment(delimiterCaption, 6);
                                                fd_delimiterText.top = new FormAttachment(defaultDataSourceCaption, 6);
                                                delimiterText.setLayoutData(fd_delimiterText);

        Button overrideCheckBox = new Button(container, SWT.CHECK);
        fd_treeComposite.bottom = new FormAttachment(overrideCheckBox, -18);
        fd_cdsGroup.top = new FormAttachment(overrideCheckBox, 6);
        FormData fd_overrideCheckBox = new FormData();
        fd_overrideCheckBox.left = new FormAttachment(0, 10);
        fd_overrideCheckBox.bottom = new FormAttachment(100, -116);
        overrideCheckBox.setLayoutData(fd_overrideCheckBox);
        overrideCheckBox.setText(Messages.DSPP_overrideCheckBox_text);

        return container;

    }

    /**
     * Initialize the preference page.
     */
    @Override
    public void init ( IWorkbench workbench ) {
        // Initialize the preference page
    }
}
