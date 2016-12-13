/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import static org.csstudio.diirt.util.preferences.pojo.ChannelAccess.CA_DIR;
import static org.csstudio.diirt.util.preferences.pojo.ChannelAccess.CA_FILE;
import static org.csstudio.diirt.util.preferences.pojo.DataSources.DATASOURCES_DIR;
import static org.csstudio.diirt.util.preferences.pojo.DataSources.DATASOURCES_FILE;
import static org.csstudio.diirt.util.preferences.pojo.DataSources.PREF_DEFAULT;
import static org.csstudio.diirt.util.preferences.pojo.DataSources.PREF_DELIMITER;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.csstudio.diirt.util.preferences.pojo.DataSources;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.SWTResourceManager;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class DataSourcesPreferencePage extends BasePreferencePage {

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

        IPreferenceStore store = getPreferenceStore();
        Composite container = new Composite(parent, SWT.NULL);

        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout());

        directoryEditor = new ConfigurationDirectoryFieldEditor(DIIRTPreferencesPlugin.PREF_CONFIGURATION_DIRECTORY, Messages.DSPP_directoryCaption_text, container, store);

        directoryEditor.setChangeButtonText(Messages.DSPP_browseButton_text);
        directoryEditor.getTextControl(container).setEditable(false);
        directoryEditor.getTextControl(container).setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
        directoryEditor.getTextControl(container).setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));

        addField(directoryEditor, container, false, () -> store.getString(DIIRTPreferencesPlugin.PREF_CONFIGURATION_DIRECTORY));

        Composite treeComposite = new Composite(container, SWT.NONE);
        GridData gd_treeComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);

        gd_treeComposite.heightHint = -14;
        treeComposite.setLayoutData(gd_treeComposite);
        treeComposite.setLayout(new GridLayout());

        treeViewer = new TreeViewer(treeComposite);

        GridData gridData = new GridData(GridData.FILL_BOTH);

        gridData.heightHint = -16;
        treeViewer.getTree().setLayoutData(gridData);
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

        GridLayout cdsGroupLayout = new GridLayout();

        cdsGroup.setLayout(cdsGroupLayout);

        defaultDataSourceEditor = new ComboFieldEditor(PREF_DEFAULT, Messages.DSPP_defaultDataSourceCaption_text, DIIRTPreferencesPlugin.AVAILABLE_DATA_SOURCES, cdsGroup);

        addField(defaultDataSourceEditor, cdsGroup, true, () -> store.getDefaultString(PREF_DEFAULT), () -> store.getString(PREF_DEFAULT));

        delimiterEditor = new StringFieldEditor(PREF_DELIMITER, Messages.DSPP_delimiterCaption_text, cdsGroup);

        delimiterEditor.getTextControl(cdsGroup).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        addField(delimiterEditor, cdsGroup, true, () -> store.getDefaultString(PREF_DELIMITER), () -> store.getString(PREF_DELIMITER));

        initializeValues(store);

        return container;

    }

    @Override
    protected void contributeButtons ( Composite parent ) {

        ((GridLayout) parent.getLayout()).numColumns++;

        Button button = new Button(parent, SWT.PUSH);

        button.setText(Messages.DSPP_exportButton_text);
        button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected ( SelectionEvent e ) {
                exportConfiguration();
            }

            @Override
            public void widgetDefaultSelected ( SelectionEvent e ) {
                exportConfiguration();
            }

        });

    }

    @Override
    protected void initializeCancelStore ( IPreferenceStore store, IPreferenceStore cancelStore ) {
        DataSources.copy(store, cancelStore);
    }

    @Override
    protected String initializeValues ( IPreferenceStore store ) {

        String confDir = super.initializeValues(store);

        lastPath = confDir;

        directoryEditor.setStringValue(confDir);
        directoryEditor.setFilterPath(new File(confDir).getParentFile());

        if ( Files.exists(Paths.get(confDir)) ) {
            treeViewer.setInput(confDir);
        }

        return confDir;

    }

    @Override
    protected void performCancel ( IPreferenceStore store, IPreferenceStore cancelStore ) {
        DataSources.copy(cancelStore, store);
    }

    /**
     * Ask the user for a folder where to save the DIIRT configuration files.
     */
    private void exportConfiguration ( ) {

        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);

        dialog.setText(Messages.DSPP_exportDialog_text);
        dialog.setMessage(Messages.DSPP_exportDialog_message);

        if ( lastPath != null ) {
            dialog.setFilterPath(lastPath);
        }

        String choice = dialog.open();

        if ( choice != null ) {

            try {
                choice = DIIRTPreferencesPlugin.resolvePlatformPath(choice.trim());
            } catch ( Exception ex ) {
                notifyWarning(NLS.bind(Messages.DSPP_resolveMessage, choice));
                return;
            }

            lastPath = choice;

            Path parentPath = Paths.get(choice);
            Path dsPath = Paths.get(parentPath.toString(), DATASOURCES_DIR);
            Path dsFile = Paths.get(dsPath.toString(), DATASOURCES_FILE);
            Path caPath = Paths.get(dsPath.toString(), CA_DIR);
            Path caFile = Paths.get(caPath.toString(), CA_FILE);
            Path[] content = new Path[] { dsPath, dsFile, caPath, caFile };

            if ( Arrays.asList(content).stream().anyMatch(p -> Files.exists(p, LinkOption.NOFOLLOW_LINKS))  ) {

                boolean overwrite = MessageDialog.openConfirm(
                    getShell(),
                    Messages.DSPP_exportFilesExist_title,
                    NLS.bind(Messages.DSPP_exportFilesExist_message, choice)
                );

                if ( !overwrite ) {
                    return;
                }

            }

            try {
                DIIRTPreferencesPlugin.get().exportConfiguration(parentPath.toFile());
                notifyInformation(NLS.bind(Messages.DSPP_exportSuccessful_message, choice));
            } catch ( JAXBException | IOException | XMLStreamException ex ) {
                notifyWarning(NLS.bind(Messages.DSPP_exportFailed_message, choice, ex.getMessage()));
            }

        }

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
     * Editor for the configuration directory.
     */
    private class ConfigurationDirectoryFieldEditor extends DirectoryFieldEditor {

        private final IPreferenceStore store;

        protected ConfigurationDirectoryFieldEditor ( String name, String labelText, Composite parent, IPreferenceStore store ) {

            super(name, labelText, parent);

            this.store = store;

        }

        @Override
        protected String changePressed ( ) {

            DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);

            dialog.setText(Messages.DSPP_browseDialog_text);
            dialog.setMessage(Messages.DSPP_browseDialog_message);

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

                if ( verifyAndNotifyWarning(choice) ) {
                    DIIRTPreferencesPlugin.get().updateDefaultsAndValues(choice, store);
                    reloadEditorsForAllPages();
                }

                treeViewer.setInput(choice);
                treeViewer.refresh();

            }

            return choice;

        }

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

    }   //  class FileTreeContentProvider

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

    }   //  class FileTreeLabelProvider

}
