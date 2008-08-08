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
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.dialogs.ResourceSelectionDialog;
import org.csstudio.utility.adlconverter.Activator;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
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
     * {@inheritDoc}
     */
    @Override
    public final void createPartControl(final Composite parent) {
        _shell = parent.getShell();
        WorkbenchHelpSystem.getInstance().setHelp(parent, Activator.PLUGIN_ID + ".adl_converter");
        parent.setLayout(new GridLayout(1, true));

        // Source and Destination Groups
        Group sourceGroup = new Group(parent, SWT.NONE);
        sourceGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sourceGroup.setLayout(new GridLayout(3, true));
        sourceGroup.setText(Messages.ADLConverterMainView_SourceGroup);

        Group destinationGroup = new Group(parent, SWT.NONE);
        destinationGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        destinationGroup.setLayout(new GridLayout(2, false));
        destinationGroup.setText(Messages.ADLConverterMainView_DestinationGroup);

        IResource initial = ResourcesPlugin.getWorkspace().getRoot();
        generateSourceBlock(sourceGroup, initial);
        generateDestinationBlock(destinationGroup, initial);
    }

    /**
     * @param sourceGroup
     * @param initial
     */
    private void generateSourceBlock(final Group sourceGroup, final IResource initial) {
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gridData.minimumWidth = 40;

        _avaibleFiles = new ListViewer(sourceGroup);
        _avaibleFiles.getList().setLayoutData(gridData);

        Button open = new Button(sourceGroup, SWT.PUSH);
        open.setLayoutData(new GridData(80, 25));
        open.setText(Messages.ADLConverterMainView_ADLSourceFileDialogButton);

        Button clear = new Button(sourceGroup, SWT.PUSH);
        clear.setText(Messages.ADLConverterMainView_ClearButtonText);
        gridData = new GridData(80, 25);
        gridData.horizontalAlignment = SWT.CENTER;
        clear.setLayoutData(gridData);

        Button convert = new Button(sourceGroup, SWT.PUSH);
        convert.setText(Messages.ADLConverterMainView_ConvcertButtonText);
        gridData = new GridData(80, 25);
        gridData.horizontalAlignment = SWT.RIGHT;
        convert.setLayoutData(gridData);

        // Listener

        open.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            public void widgetSelected(final SelectionEvent e) {
                FileDialog dialog = new FileDialog(_shell, SWT.MULTI);
                dialog.setFilterNames(new String[] {
                        Messages.ADLConverterMainView_ADLFileSourceDialogFileDes,
                        Messages.ADLConverterMainView_AllFileSourceDialogFileDes });
                dialog.setFilterExtensions(new String[] { "*.adl", "*.*" }); // Windows
                // wild
                // cards
                // //$NON-NLS-1$
                // //$NON-NLS-2$
                dialog.setFilterPath(initial.getProjectRelativePath().toOSString());
                dialog.open();
                String path = dialog.getFilterPath();
                String[] files = dialog.getFileNames();
                ArrayList<File> fileList = new ArrayList<File>();
                for (String name : files) {
                    fileList.add(new File(path, name));
                    _avaibleFiles.add(new File(path, name));
                }

                _avaibleFiles.getList().selectAll();
            }

        });

        clear.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
            }

            public void widgetSelected(final SelectionEvent e) {
                _avaibleFiles.getList().removeAll();
                _pathPos = 0;
                _relativePathText.setText("");
            }

        });

        convert.addSelectionListener(new SelectionListener() {

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
                        if (!di.importDisplay(file.getAbsolutePath(), targetProject, file.getName()
                                .replace(".adl", ".css-sds"))) { //$NON-NLS-1$ //$NON-NLS-2$
                            if (di.getStatus() == 2) {
                                // Job is canceled.
                                break;
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
     * @param initial
     */
    private void generateDestinationBlock(Group destinationGroup, IResource initial) {
        // Destination ui elements.
        // first row
        Button targetOpenButton = new Button(destinationGroup, SWT.PUSH);
        targetOpenButton.setText(Messages.ADLConverterMainView_TargetOpernButton);
        targetOpenButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        final Text pathText = new Text(destinationGroup, SWT.BORDER);
        pathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        pathText.setText(initial.getProjectRelativePath().toOSString());

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

        final Label examplePathLabel = new Label(relativPathComp, SWT.NONE);
        examplePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        // Listener

        targetOpenButton.addSelectionListener(new SelectionListener() {

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
                        examplePathLabel.setText(_targetPath + _relativePathText.getText());
                        pathText.setText(path.toString());
                    }
                }
            }
        });

        _isRelativePath.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                // TODO Auto-generated method stub

            }

            public void widgetSelected(final SelectionEvent e) {
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
                                IPath relPath = getRelativPath((File) _avaibleFiles.getElementAt(0));
                                examplePathLabel.setText(relPath.toOSString());
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
                                IPath relPath = getRelativPath((File) _avaibleFiles.getElementAt(0));
                                examplePathLabel.setText(relPath.toOSString());
                                _pathPos++;
                            }
                        }
                    }
                }
            }

        });
    }

    private IPath getRelativPath(File file) {
        String apsolutPath = Pattern.quote(_relativePathText.getText());
        IPath relativePath = _targetPath.append(file.getAbsolutePath()
                .replaceFirst(apsolutPath, ""));
        return relativePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
    }

}
