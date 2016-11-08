/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.SWTResourceManager;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class DataSourcesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static String lastPath = DIIRTPreferencesHandler.get().getConfigurationDirectory();

    private Text       directoryText;
    private Text       delimiterText;
    private Image      fileImage   = null;
    private Image      folderImage = null;
    private TreeViewer treeViewer;
    private Image      xmlImage    = null;

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
        directoryText.setText(DIIRTPreferencesHandler.get().getConfigurationDirectory());
        FormData fd_directoryText = new FormData();
        fd_directoryText.left = new FormAttachment(0, 178);
        directoryText.setLayoutData(fd_directoryText);

        Button browseButton = new Button(container, SWT.NONE);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);

                if (lastPath != null) {
                    dialog.setFilterPath(lastPath);
                }

                String choice = dialog.open();

                if (choice != null) {

                    choice = choice.trim();

                    if (choice.length() == 0) {
                        return;
                    }

                    lastPath = choice;

                }

                setErrorMessage(DIIRTPreferencesHandler.verifyDIIRTPath(choice));

                treeViewer.setInput(choice);
                treeViewer.refresh();

            }
        });
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
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick ( DoubleClickEvent event ) {

                TreeSelection selection = (TreeSelection) event.getSelection();
                File fileSelection = (File) selection.getFirstElement();

                try {
                    if ( fileSelection.isFile() ) {
                        Program.launch(fileSelection.getCanonicalPath());
                    }
                } catch ( IOException e ) {
                    setErrorMessage(e.getMessage());
                }

            }

        });
        treeViewer.setLabelProvider(new FileTreeLabelProvider());
        treeViewer.setContentProvider(new FileTreeContentProvider());

        if ( Files.exists(Paths.get(lastPath)) ) {
            treeViewer.setInput(lastPath);
        }

        Group cdsGroup = new Group(container, SWT.NONE);
        fd_treeComposite.bottom = new FormAttachment(cdsGroup, -21);
        cdsGroup.setText(Messages.DSPP_cdsGroup_text);
        cdsGroup.setLayout(new FormLayout());
        FormData fd_cdsGroup = new FormData();
        fd_cdsGroup.top = new FormAttachment(100, -125);
        fd_cdsGroup.bottom = new FormAttachment(100, -30);
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

        return container;

    }

    /**
     * Initialize the preference page.
     */
    @Override
    public void init ( IWorkbench workbench ) {
    }

    @Override
    public boolean performOk ( ) {
        return super.performOk();
    }

    @Override
    protected void performDefaults ( ) {
        super.performDefaults();
    }

    private Image getFileImage ( ) {

        if ( fileImage == null ) {
            fileImage = AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.diirt.util.preferences", "icons/file.png").createImage();
        }

        return fileImage;

    }

    private Image getFolderImage ( ) {

        if ( folderImage == null ) {
            folderImage = AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.diirt.util.preferences", "icons/open.png").createImage();
        }

        return folderImage;

    }

    private Image getXMLImage ( ) {

        if ( xmlImage == null ) {
            xmlImage = AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.diirt.util.preferences", "icons/xml.gif").createImage();
        }

        return xmlImage;

    }

    /**
     * This class provides the content for the tree in FileTree.
     */
    private class FileTreeContentProvider implements ITreeContentProvider {

        File root = new File("root");

        @Override
        public void dispose ( ) {
        }

        /**
         * Gets the children of the specified file object.
         *
         * @param file The parent file object.
         * @return The files and sub-directories in this directory.
         */
        @Override
        public Object[] getChildren ( Object file ) {
            return ( (File) file ).listFiles();
        }

        /**
         * Gets the root element(s) of the tree.
         *
         * @param arg0 The input data (unused).
         * @return The files and sub-directories in the root.
         */
        @Override
        public Object[] getElements ( Object arg0 ) {
            return root.listFiles();
        }

        /**
         * Gets the parent of the specified file.
         *
         * @param file The file object.
         * @return The file's parent.
         */
        @Override
        public Object getParent ( Object file ) {
            return ( (File) file ).getParentFile();
        }

        /**
         * Returns whether the passed file object has children.
         *
         * @param file The parent file object.
         * @return Whether the parent has children.
         */
        @Override
        public boolean hasChildren ( Object file ) {

            Object[] obj = getChildren(file);

            return ( obj == null ) ? false : obj.length > 0;

        }

        /**
         * Called when the input changes.
         *
         * @param viewer The viewer.
         * @param oldValue The old input.
         * @param newValue The new input.
         */
        @Override
        public void inputChanged ( Viewer viewer, Object oldValue, Object newValue ) {
            try {
                root = new File((String) newValue);
            } catch ( Exception e ) {
                root = new File("root");
            }
        }

    }

    /**
     * This class provides the labels for the file tree.
     */
    private class FileTreeLabelProvider implements ILabelProvider {

        @Override
        public void addListener ( ILabelProviderListener listener ) {
        }

        @Override
        public void dispose ( ) {
        }

        @Override
        public Image getImage ( Object element ) {

            if ( element == null || !( element instanceof File ) ) {
                return null;
            } else {

                File file = (File) element;

                if ( file.isFile() ) {
                    if ( file.getName().toLowerCase().endsWith(".xml") ) {
                        return getXMLImage();
                    } else {
                        return getFileImage();
                    }
                } else {
                    return getFolderImage();
                }

            }

        }

        /**
         * Gets the text to display for a node in the tree.
         *
         * @param node The node.
         * @return The string representation of the given node.
         */
        @Override
        public String getText ( Object node ) {

            // Get the name of the file
            String text = ( (File) node ).getName();

            // If name is blank, get the path
            if ( text.length() == 0 ) {
                text = ( (File) node ).getPath();
            }

            return text;

        }

        /**
         * Returns whether changes to the specified property on the specified
         * element would affect the label for the element.
         *
         * @param element The element.
         * @param property The property (name).
         * @return boolean
         */
        @Override
        public boolean isLabelProperty ( Object element, String property ) {
            return false;
        }

        @Override
        public void removeListener ( ILabelProviderListener arg0 ) {
        }

    }

}
