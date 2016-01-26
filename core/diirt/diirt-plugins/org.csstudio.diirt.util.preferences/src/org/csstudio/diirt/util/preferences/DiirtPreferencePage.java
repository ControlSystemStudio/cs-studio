package org.csstudio.diirt.util.preferences;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * Preference page for configuring diirt preferences primarily the Location of
 * the diirt configuration folder.
 *
 * In addition it has a directory view which shows all the individual
 * configuration files. Double click on any file will result in opening that
 * file with the configured default editor
 *
 * @author Kunal Shroff
 *
 */
public class DiirtPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private ScopedPreferenceStore store;

    private TreeViewer tv;
    private StringButtonFieldEditor diirtPathEditor;
    private Composite top;

    private static final String PLATFORM_URI_PREFIX = "platform:";

    public DiirtPreferencePage() {
    }

    @Override
    protected Control createContents(Composite parent) {
        top = new Composite(parent, SWT.LEFT);
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        top.setLayout(new GridLayout());

        diirtPathEditor = new StringButtonFieldEditor("diirt.home", "&Diirt configuration directory:", top) {
            private String lastPath = store.getString("diirt.home");

            @Override
            protected String changePressed() {
                DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);
                if (lastPath != null) {
                    try {
                        lastPath = getSubstitutedPath(lastPath);
                        if (new File(lastPath).exists()) {
                            File lastDir = new File(lastPath);
                            dialog.setFilterPath(lastDir.getCanonicalPath());
                        }
                    } catch (IOException e) {
                        dialog.setFilterPath(lastPath);
                    }
                }
                String dir = dialog.open();
                if (dir != null) {
                    dir = dir.trim();
                    if (dir.length() == 0) {
                        return null;
                    }
                    lastPath = dir;
                }
                tv.setInput(dir);
                tv.refresh();
                return dir;
            }
        };
        diirtPathEditor.setChangeButtonText("Browse");
        diirtPathEditor.setPage(this);
        diirtPathEditor.setPreferenceStore(getPreferenceStore());
        diirtPathEditor.load();

        // Detailed view of all the configuration files
        Composite treeComposite = new Composite(parent, SWT.NONE);
        treeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        treeComposite.setLayout(new GridLayout(1, false));

        // Create the tree viewer to display the file tree
        tv = new TreeViewer(treeComposite);
        tv.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        tv.setContentProvider(new FileTreeContentProvider());
        tv.setLabelProvider(new FileTreeLabelProvider());
        try {
            final String configPath = getSubstitutedPath(store.getString("diirt.home"));
            if (Files.exists(Paths.get(configPath))) {
                tv.setInput(configPath);
            }
        } catch (IOException e1) {
            setErrorMessage(e1.getMessage());
        }
        tv.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                TreeSelection selection = (TreeSelection) event.getSelection();
                File sel = (File) selection.getFirstElement();
                try {
                    if (sel.isFile()) {
                        Program.launch(sel.getCanonicalPath());
                    }
                } catch (IOException e) {
                    setErrorMessage(e.getMessage());
                }
            }
        });

        return parent;
    }

    @Override
    public void init(IWorkbench workbench) {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.csstudio.diirt.util.preferences");
        store.addPropertyChangeListener((PropertyChangeEvent event) -> {
            if (event.getProperty() == "diirt.home") {
                if (!getControl().isDisposed()) {
                    try {
                        String fullPath = getSubstitutedPath(store.getString("diirt.home"));
                        if (verifyDiirtPath(fullPath)) {
                            tv.setInput(fullPath);
                            setMessage("Restart is needed", ERROR);
                        }
                    } catch (IOException e) {
                        setMessage("Diirt home not understood", ERROR);
                    }
                }
            }
        });
        setPreferenceStore(store);
        setDescription("Diirt preference page");
    }

    /**
     * Verify the selected path is a valid location for DIIRT config:
     * i) path exists
     * ii) path contains the datasources.xml file
     *
     * @param configPath
     * @return
     */
    private boolean verifyDiirtPath(String configPath) {
        boolean isValid = true;

        if (!Files.exists(Paths.get(configPath))) {
            setErrorMessage("Diirt home not found");
            isValid = false;
        } else {
            final Path datasourcePath = Paths.get(configPath, "datasources/datasources.xml");
            isValid = Files.exists(datasourcePath);

            if (!isValid) {
                setErrorMessage("No Diirt configuration found");
            }
        }
        return isValid;
    }

    @Override
    public boolean performOk() {
        try {
            final String lastDir = getSubstitutedPath(diirtPathEditor.getStringValue());
            if (verifyDiirtPath(lastDir)) {
               diirtPathEditor.store();
            }
        } catch (Exception e1) {
            setErrorMessage("Invalid config location : " + diirtPathEditor.getStringValue());
        }
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        diirtPathEditor.loadDefault();
        super.performDefaults();
    };

    /**
     * This class provides the content for the tree in FileTree
     */

    class FileTreeContentProvider implements ITreeContentProvider {
        File root = new File("root");

        /**
         * Gets the children of the specified object
         *
         * @param arg0 the parent object
         * @return Object[]
         */
        public Object[] getChildren(Object arg0) {
            // Return the files and subdirectories in this directory
            return ((File) arg0).listFiles();
        }

        /**
         * Gets the parent of the specified object
         *
         * @param arg0
         *            the object
         * @return Object
         */
        public Object getParent(Object arg0) {
            // Return this file's parent file
            return ((File) arg0).getParentFile();
        }

        /**
         * Returns whether the passed object has children
         *
         * @param arg0 the parent object
         * @return boolean
         */
        public boolean hasChildren(Object arg0) {
            // Get the children
            Object[] obj = getChildren(arg0);
            // Return whether the parent has children
            return obj == null ? false : obj.length > 0;
        }

        /**
         * Gets the root element(s) of the tree
         *
         * @param arg0 the input data
         * @return Object[]
         */
        public Object[] getElements(Object arg0) {
            return root.listFiles();
        }

        /**
         * Disposes any created resources
         */
        public void dispose() {
            // Nothing to dispose
        }

        /**
         * Called when the input changes
         *
         * @param arg0 the viewer
         * @param arg1 the old input
         * @param arg2 the new input
         */
        public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            try {
                root = new File((String)arg2);
            } catch (Exception e) {
                root = new File("root");
            }
        }
    }

    /**
     * This class provides the labels for the file tree
     */

    class FileTreeLabelProvider implements ILabelProvider {

        /**
         * Constructs a FileTreeLabelProvider
         */
        public FileTreeLabelProvider() {
        }

        /**
         * Gets the text to display for a node in the tree
         *
         * @param arg0
         *            the node
         * @return String
         */
        public String getText(Object arg0) {
            // Get the name of the file
            String text = ((File) arg0).getName();

            // If name is blank, get the path
            if (text.length() == 0) {
                text = ((File) arg0).getPath();
            }
            return text;
        }


        /**
         * Called when this LabelProvider is being disposed
         */
        public void dispose() {

        }

        /**
         * Returns whether changes to the specified property on the specified
         * element would affect the label for the element
         *
         * @param arg0
         *            the element
         * @param arg1
         *            the property
         * @return boolean
         */
        public boolean isLabelProperty(Object arg0, String arg1) {
            return false;
        }

        /**
         * Removes the listener
         *
         * @param arg0 the listener to remove
         */
        public void removeListener(ILabelProviderListener arg0) {

        }

        @Override
        public Image getImage(Object element) {
            return null;
        }

        @Override
        public void addListener(ILabelProviderListener listener) {

        }
    }

    /**
     * handles the platform urls
     *
     * @param path
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    static String getSubstitutedPath(String path) throws MalformedURLException, IOException {
        if(path != null && !path.isEmpty()) {
            if(path.startsWith(PLATFORM_URI_PREFIX)) {
                return FileLocator.resolve(new URL(path)).getPath().toString();
            } else {
                return path;
            }
        } else {
            return "root";
        }
    }
}
