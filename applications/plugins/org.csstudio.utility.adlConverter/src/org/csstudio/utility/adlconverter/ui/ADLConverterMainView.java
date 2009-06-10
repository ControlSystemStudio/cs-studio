/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.dialogs.ResourceSelectionDialog;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.ui.preferences.ADLConverterPreferenceConstants;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 16.08.2007
 */
public class ADLConverterMainView extends ViewPart {

    /**
     * Default Constructor.
     */
    public ADLConverterMainView() {
    }

    /**
     * The target path selected by the user.
     */
    private IPath _targetPath;

    /**
     * A marker for the path Part.
     */
    private int _pathPos = 0;

    /**
     * The parent Shell.
     */
    private Shell _shell;

    /**
     * A CheckButton to switch on/off use relative Path.
     */
    private Button _isRelativePath;

    /**
     * A Text field with the relative Path.
     */
    private Text _relativePathText;

    /**
     * The Viewer with the list of the files to Convert.
     */
    private ListViewer _avaibleFiles;

    /**
     * The ADL Converter Preferences. Contain the different default path.
     */
    private Preferences _preferences;

    /**
     * A Label to show a path example.
     */
    private Label _examplePathLabel;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void createPartControl(final Composite parent) {
        _shell = parent.getShell();
        _preferences = Activator.getDefault().getPluginPreferences();
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".adl_converter");
        parent.setLayout(new GridLayout(1, true));

        // Source and Destination Groups
        Group sourceGroup = new Group(parent, SWT.NONE);
        sourceGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sourceGroup.setLayout(new GridLayout(4, true));
        sourceGroup.setText(Messages.ADLConverterMainView_SourceGroup);

        Group destinationGroup = new Group(parent, SWT.NONE);
        destinationGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        destinationGroup.setLayout(new GridLayout(2, false));
        destinationGroup.setText(Messages.ADLConverterMainView_DestinationGroup);

        IResource initial = ResourcesPlugin.getWorkspace().getRoot();
        generateSourceBlock(sourceGroup, initial);
        generateDestinationBlock(destinationGroup);
    }

    /**
     * @param sourceGroup
     *            the Parent composite.
     * @param initial
     *            The Workspace resource.
     */
    private void generateSourceBlock(final Group sourceGroup, final IResource initial) {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        gridData.minimumWidth = 40;

        _avaibleFiles = new ListViewer(sourceGroup);
        _avaibleFiles.getList().setLayoutData(gridData);
        _avaibleFiles.setContentProvider(new ArrayContentProvider());

        Button openSourceButton = new Button(sourceGroup, SWT.PUSH);
        openSourceButton.setLayoutData(new GridData(80, 25));
        openSourceButton.setText(Messages.ADLConverterMainView_ADLSourceFileDialogButton);

        Button subFolderButton = new Button(sourceGroup, SWT.PUSH);
        subFolderButton.setLayoutData(new GridData(80, 25));
        subFolderButton.setText(Messages.ADLConverterMainView_ADLSourceFolderDialogButton);
        
        Button clearSourceButton = new Button(sourceGroup, SWT.PUSH);
        clearSourceButton.setText(Messages.ADLConverterMainView_ClearButtonText);
        gridData = new GridData(80, 25);
        gridData.horizontalAlignment = SWT.CENTER;
        clearSourceButton.setLayoutData(gridData);

        Button convertButton = new Button(sourceGroup, SWT.PUSH);
        convertButton.setText(Messages.ADLConverterMainView_ConvcertButtonText);
        gridData = new GridData(80, 25);
        gridData.horizontalAlignment = SWT.RIGHT;
        convertButton.setLayoutData(gridData);

        // Listener

        openSourceButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            public void widgetSelected(final SelectionEvent e) {
                FileDialog dialog = new FileDialog(_shell, SWT.MULTI);
                dialog.setFilterNames(new String[] { Messages.ADLConverterMainView_BothFileSourceDialogFileDes,
                        Messages.ADLConverterMainView_ADLFileSourceDialogFileDes, Messages.ADLConverterMainView_MDPFileSourceDialogFileDes,
                        Messages.ADLConverterMainView_AllFileSourceDialogFileDes });
                dialog.setFilterExtensions(new String[] { "*.adl;*.mfp", "*.adl", "*.mfp", "*.*" }); // Windows
                // wild cards //$NON-NLS-1$ //$NON-NLS-2$
                String path = _preferences
                        .getString(ADLConverterPreferenceConstants.P_STRING_Path_Source);
                // path = initial.getProjectRelativePath().toOSString()
                dialog.setFilterPath(path);
                dialog.open();
                path = dialog.getFilterPath();
                String[] files = dialog.getFileNames();
                for (String name : files) {
                    _avaibleFiles.add(new File(path, name));
                }
                _avaibleFiles.getList().selectAll();
                refreshexamplePathLabel();
                checkRelativPath();
            }

        });

        subFolderButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(_shell, SWT.MULTI);
                String path = _preferences
                        .getString(ADLConverterPreferenceConstants.P_STRING_Path_Source);
//                path = initial.getProjectRelativePath().toOSString();
                System.out.println("Path_: "+path);
                dialog.setFilterPath(path);
                String open = dialog.open();
                if(open==null) {
                    return;
                }
                path = dialog.getFilterPath();
                File file = new File(path);
                fillFiles(file);
                _avaibleFiles.getList().selectAll();
                refreshexamplePathLabel();
                checkRelativPath();               
                _preferences.setValue(ADLConverterPreferenceConstants.P_STRING_Path_Source,path);
            }

            private void fillFiles(File file) {
                String[] list = file.list(new FilenameFilter() {

                    public boolean accept(File dir, String name) {
                        
                        boolean adl = false;
                        adl |= dir.isDirectory();
                        adl |= name.endsWith(".adl");
                        adl |= name.endsWith(".mfp");
                        return adl;
                    }
                    
                });
                for (String name : list) {
                    File element = new File(file, name);
                    if(element.isFile()) {
                        _avaibleFiles.add(element);
                    }else {
                        fillFiles(element);
                    }
                }
            }
            
        });
        
        clearSourceButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            public void widgetSelected(final SelectionEvent e) {
                _avaibleFiles.getList().removeAll();
                _pathPos = 0;
                // _relativePathText.setText("");
            }

        });

        convertButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            @SuppressWarnings("unchecked")//$NON-NLS-1$
            public void widgetSelected(final SelectionEvent e) {
                StructuredSelection sel = (StructuredSelection) _avaibleFiles.getSelection();
                ArrayList<Object> list = new ArrayList<Object>(sel.toList());
                while (list.size() > 0) {
                    ADLDisplayImporter di = new ADLDisplayImporter();
                    File file = (File) list.remove(0);
                    IPath targetProject;
                    if (_isRelativePath.getSelection()) {
                        targetProject = getRelativPath(file);
                        // remove File Name
                        targetProject = targetProject.removeLastSegments(1);
                    } else {
                        targetProject = initial.getProjectRelativePath().append(_targetPath);
                    }
                    try {
                        if (file.getName().endsWith(".adl")) {//$NON-NLS-1$ 
                            if (!di.importDisplay(file.getAbsolutePath(), targetProject, file
                                    .getName().replace(".adl", ".css-sds"))) { //$NON-NLS-1$ //$NON-NLS-2$
                                if (di.getStatus() == 2) {
                                    // Job is canceled.
                                    break;
                                }
                            }
                        } else if (file.getName().endsWith(".mfp")) {//$NON-NLS-1$ 
                            if (!di.importFaceplate(file.getAbsolutePath(), targetProject, file
                                    .getName().replace(".mfp", ".fp.css-sds"))) { //$NON-NLS-1$ //$NON-NLS-2$
                                if (di.getStatus() == 2) {
                                    // Job is canceled.
                                    break;
                                }
                            }
                        }
                    } catch (Exception e1) {
                        CentralLogger.getInstance().error(this, e1);
                    }
                    file = null;

                    _avaibleFiles.setSelection(new StructuredSelection(list), true);
                    _avaibleFiles.getList().getParent().layout();
                }
            }
        });
    }

    /**
     * @param destinationGroup
     *            the Parent composite.
     * @param initial
     */
    private void generateDestinationBlock(final Group destinationGroup) {
        // Destination ui elements.
        // first row
        Button openTargetButton = new Button(destinationGroup, SWT.PUSH);
        openTargetButton.setText(Messages.ADLConverterMainView_TargetOpernButton);
        openTargetButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        final Text pathText = new Text(destinationGroup, SWT.BORDER);
        pathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        _targetPath = new Path(_preferences
                .getString(ADLConverterPreferenceConstants.P_STRING_Path_Target));
        pathText.setText(_targetPath.toString());

        // second row
        Composite relativPathComp = new Composite(destinationGroup, SWT.NONE);
        relativPathComp.setLayout(new GridLayout(2, false));
        relativPathComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

        org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label(relativPathComp,
                SWT.NONE);
        label.setText("Choose the absolute source path that are removed (<-- & -->)");
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

        // third row
        _isRelativePath = new Button(relativPathComp, SWT.CHECK);
        _isRelativePath.setText("");

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gridData.minimumWidth = 40;

        _relativePathText = new Text(relativPathComp, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY
                | SWT.SEARCH);
        _relativePathText.setLayoutData(gridData);
        _relativePathText.setToolTipText("Press <-- or --> to select the path");
        _relativePathText.setText(_preferences
                .getString(ADLConverterPreferenceConstants.P_STRING_Path_Relativ_Target));

        _examplePathLabel = new Label(relativPathComp, SWT.NONE);
        _examplePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        // Listener

        openTargetButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            public void widgetSelected(final SelectionEvent e) {
                ResourceSelectionDialog dialog = new ResourceSelectionDialog(_shell,
                        Messages.ADLConverterMainView_TargetFolderSelectionMessage, null);
                IPath path = new Path(pathText.getText());
                dialog.setSelectedResource(path);
                if (dialog.open() == Window.OK) {
                    path = dialog.getSelectedResource();
                    if (path != null) {
                        _targetPath = path;
                        pathText.setText(path.toString());
                        refreshexamplePathLabel();
                    }
                }
            }
        });

        _isRelativePath.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                setBackground();
            }

            public void widgetSelected(final SelectionEvent e) {
                setBackground();
            }

            private void setBackground() {
                if (_isRelativePath.getSelection()) {
                    _relativePathText.setBackground(Display.getDefault().getSystemColor(
                            SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
                } else {
                    _relativePathText.setBackground(Display.getDefault().getSystemColor(
                            SWT.COLOR_WIDGET_BACKGROUND));
                }
            }

        });

        _relativePathText.addKeyListener(new KeyListener() {
            public void keyPressed(final KeyEvent e) {
            }

            public void keyReleased(final KeyEvent e) {
                String[] pathPart;
                if (_isRelativePath.getSelection()) {
                    if (e.keyCode == SWT.ARROW_LEFT) {
                        if (_pathPos > 0) {
                            _pathPos--;
                            File file = (File) _avaibleFiles.getElementAt(0);
                            pathPart = file.getAbsolutePath().split(Pattern.quote(File.separator));
                            if (pathPart.length > _pathPos) {
                                if (_pathPos == 0) {
                                    _relativePathText.setText(_relativePathText.getText().replace(
                                            pathPart[_pathPos], ""));
                                } else {
                                    _relativePathText.setText(_relativePathText.getText().replace(
                                            File.separator + pathPart[_pathPos], ""));
                                }
                                refreshexamplePathLabel();
                            }
                        }
                    } else if (e.keyCode == SWT.ARROW_RIGHT) {
                        File file = (File) _avaibleFiles.getElementAt(0);
                        if (file != null) {
                            pathPart = file.getAbsolutePath().split(Pattern.quote(File.separator));
                            if (_pathPos < pathPart.length - 1) {
                                if (_pathPos == 0) {
                                    _relativePathText.append(pathPart[_pathPos]);
                                } else {
                                    _relativePathText.append(File.separator + pathPart[_pathPos]);
                                }
                                refreshexamplePathLabel();
                                _pathPos++;
                            }
                        }
                    }
                }
            }

        });
    }

    /**
     * Extract the relative path part from example file path.
     * 
     * @param file
     *            the example file.
     * @return the relative path.
     */
    private IPath getRelativPath(final File file) {
        String apsolutPath = Pattern.quote(_relativePathText.getText());
        IPath relativePath = _targetPath.append(file.getAbsolutePath()
                .replaceFirst(apsolutPath, ""));
        return relativePath;
    }

    /**
     * Refresh the path example label.
     */
    private void refreshexamplePathLabel() {
        IPath relPath = getRelativPath((File) _avaibleFiles.getElementAt(0));
        _examplePathLabel.setText(relPath.toOSString());
    }

    private void checkRelativPath() {
        File path = (File) _avaibleFiles.getElementAt(0);
        if (!path.getAbsolutePath().contains(_relativePathText.getText())) {
            _relativePathText.setText("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
    }

}
