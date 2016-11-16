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
import java.util.Objects;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.SWTResourceManager;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class DataSourcesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static String lastPath = System.getProperty("user.home");

    private Group                cdsGroup;
    private DirectoryFieldEditor directoryEditor;
    private ComboFieldEditor     defaultDataSourceEditor;
    private StringFieldEditor    delimiterEditor;
    private Image                fileImage   = null;
    private Image                folderImage = null;
    private TreeViewer           treeViewer;
    private Image                xmlImage    = null;

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

        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout());

        directoryEditor = new DirectoryFieldEditor(DIIRTPreferencesPlugin.PREF_CONFIGURATION_DIRECTORY, Messages.DSPP_directoryCaption_text, container) {
            @Override
            protected String changePressed ( ) {

                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);

                if ( lastPath != null ) {
                    dialog.setFilterPath(lastPath);
                }

                String choice = dialog.open();

                if ( choice != null ) {

                    try {
                        choice = DIIRTPreferencesPlugin.resolvePlatformPath(choice.trim());
                    } catch ( Exception ex ) {
                        notifyWarning(NLS.bind(Messages.DSPP_resolveMessage, choice));
                        return null;
                    }

                    lastPath = choice;

                }

                verifyAndNotifyWarning(choice);

                treeViewer.setInput(choice);
                treeViewer.refresh();

                return choice;

            }
        };

        directoryEditor.getLabelControl(container).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        directoryEditor.getTextControl(container).setEditable(false);
        directoryEditor.getTextControl(container).setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
        directoryEditor.getTextControl(container).setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
        directoryEditor.setChangeButtonText(Messages.DSPP_browseButton_text);
        directoryEditor.setPage(this);
        directoryEditor.setPreferenceStore(getPreferenceStore());
        directoryEditor.load();

        Composite treeComposite = new Composite(container, SWT.NONE);

        treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        treeComposite.setLayout(new GridLayout());

        treeViewer = new TreeViewer(treeComposite);

        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.setLabelProvider(new FileTreeLabelProvider());
        treeViewer.setContentProvider(new FileTreeContentProvider());
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick ( DoubleClickEvent event ) {

                TreeSelection selection = (TreeSelection) event.getSelection();
                File fileSelection = (File) selection.getFirstElement();

                try {
                    if ( fileSelection.isFile() ) {
                        clearWarning();
                        Program.launch(fileSelection.getCanonicalPath());
                    }
                } catch ( IOException ex ) {
                    notifyWarning(NLS.bind(Messages.DSPP_resolveMessage, ex.getMessage()));
                }

            }

        });

        cdsGroup = new Group(container, SWT.NONE);

        cdsGroup.setText(Messages.DSPP_cdsGroup_text);
        cdsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

        GridLayout cdsGroupLayout = new GridLayout(3, false);

        cdsGroup.setLayout(cdsGroupLayout);

        defaultDataSourceEditor = new ComboFieldEditor(DIIRTPreferencesPlugin.PREF_DS_DEFAULT, Messages.DSPP_defaultDataSourceCaption_text, DIIRTPreferencesPlugin.AVAILABLE_DATA_SOURCES, cdsGroup);

        defaultDataSourceEditor.setPage(this);
        defaultDataSourceEditor.setPreferenceStore(getPreferenceStore());
        defaultDataSourceEditor.load();
        defaultDataSourceEditor.getLabelControl(cdsGroup).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        defaultDataSourceEditor.setPropertyChangeListener(e -> {
            System.out.println("+++ PROPERTY: " + e.getProperty());
        });

        delimiterEditor = new StringFieldEditor(DIIRTPreferencesPlugin.PREF_DS_DELIMITER, Messages.DSPP_delimiterCaption_text, cdsGroup);

        delimiterEditor.getLabelControl(cdsGroup).setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        delimiterEditor.getTextControl(cdsGroup).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        delimiterEditor.getTextControl(cdsGroup).addModifyListener(e -> {
            delimiterEditor.getLabelControl(cdsGroup).setForeground(
                !Objects.equals(getPreferenceStore().getDefaultString(DIIRTPreferencesPlugin.PREF_DS_DELIMITER), delimiterEditor.getStringValue())
                ? SWTResourceManager.getColor(SWT.COLOR_BLUE)
                : SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND)
            );
            System.out.println("*** PROPERTY V: " + getPreferenceStore().getString(DIIRTPreferencesPlugin.PREF_DS_DELIMITER));
            System.out.println("*** PROPERTY E: " + delimiterEditor.getStringValue());
        });
        delimiterEditor.setPropertyChangeListener(e -> {
            System.out.println("*** PROPERTY: " + e.getProperty());
        });
        delimiterEditor.setPage(this);
        delimiterEditor.setPreferenceStore(getPreferenceStore());
        delimiterEditor.load();

        //  This must be the last statement statement for the cdsGroup widget.
        cdsGroupLayout.numColumns = 3;

        initializeValues();

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
    protected IPreferenceStore doGetPreferenceStore ( ) {
        return DIIRTPreferencesPlugin.get().getPreferenceStore();
    }

    @Override
    protected void performDefaults ( ) {
        super.performDefaults();
    }

    private void clearWarning () {
        Display.getDefault().asyncExec(() -> setMessage(null, NONE));
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
     * Initialize widgets with values from the preferences store.
     */
    private void initializeValues ( ) {

        IPreferenceStore store = getPreferenceStore();
        String confDir = store.getString(DIIRTPreferencesPlugin.PREF_CONFIGURATION_DIRECTORY);

        try {
            confDir = DIIRTPreferencesPlugin.resolvePlatformPath(confDir);
        } catch ( IOException ex ) {
            notifyWarning(NLS.bind(Messages.DSPP_resolveMessage, confDir));
        }

        lastPath = confDir;

        directoryEditor.setStringValue(confDir);
        directoryEditor.setFilterPath(new File(confDir).getParentFile());

        verifyAndNotifyWarning(confDir);

        if ( Files.exists(Paths.get(confDir)) ) {
            treeViewer.setInput(confDir);
        }

        updateCaptionColor(defaultDataSourceEditor, cdsGroup, store.getDefaultString(DIIRTPreferencesPlugin.PREF_DS_DEFAULT), store.getString(DIIRTPreferencesPlugin.PREF_DS_DEFAULT));
        updateCaptionColor(delimiterEditor, cdsGroup, store.getDefaultString(DIIRTPreferencesPlugin.PREF_DS_DELIMITER), store.getString(DIIRTPreferencesPlugin.PREF_DS_DELIMITER));

    }

    private void notifyWarning ( final String message ) {
        Display.getDefault().asyncExec(() -> setMessage(message, message != null ? WARNING : NONE));
    }

    private void updateCaptionColor ( FieldEditor editor, Composite parent, Object defaultValue, Object currentValue ) {
        editor.getLabelControl(parent).setForeground(!Objects.equals(defaultValue, currentValue) ? SWTResourceManager.getColor(SWT.COLOR_BLUE) : SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
    }

    private void verifyAndNotifyWarning ( final String path ) {
        notifyWarning(DIIRTPreferencesPlugin.verifyDIIRTPath(path));
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
            return ( (File) file ).listFiles(f -> !f.isHidden());
        }

        /**
         * Gets the root element(s) of the tree.
         *
         * @param arg0 The input data (unused).
         * @return The files and sub-directories in the root.
         */
        @Override
        public Object[] getElements ( Object arg0 ) {
            return root.listFiles(f -> !f.isHidden());
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
                root = new File(System.getProperty("user.home"));
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
