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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
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
    public ADLConverterMainView() {}

    /**
     * The target path selected by the user.
     */
    private IPath _targetPath;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        WorkbenchHelpSystem.getInstance().setHelp(parent,Activator.PLUGIN_ID+".adl_converter");
        parent.setLayout(new GridLayout(1,true));
        
        final Group sourceGroup = new Group(parent,SWT.NONE);
        sourceGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
        sourceGroup.setLayout(new GridLayout(3,true));
        sourceGroup.setText(Messages.ADLConverterMainView_SourceGroup);

        Group destinationGroup = new Group(parent,SWT.NONE);
        destinationGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
        destinationGroup.setLayout(new GridLayout(1,true));
        destinationGroup.setText(Messages.ADLConverterMainView_DestinationGroup);

        final IResource initial = ResourcesPlugin.getWorkspace().getRoot();
        final Text pathText = new Text(destinationGroup,SWT.BORDER);
        pathText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
        pathText.setText(initial.getProjectRelativePath().toOSString());
        Button targetOpenButton = new Button(destinationGroup, SWT.PUSH);
        GridData gd = new GridData(SWT.CENTER,SWT.CENTER,false,false,1,1);
        gd.minimumWidth=50;
        targetOpenButton.setLayoutData(gd);
        
        targetOpenButton.setText(Messages.ADLConverterMainView_TargetOpernButton);
        targetOpenButton.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,false,false,3,1));
        
        targetOpenButton.addSelectionListener(new SelectionListener(){


            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
            	ResourceSelectionDialog dialog = new ResourceSelectionDialog(
						parent.getShell(),
						Messages.ADLConverterMainView_TargetFolderSelectionMessage,
						null);
            	IPath path = new Path(pathText.getText());
            	dialog.setSelectedResource(path);
            	if (dialog.open() == Window.OK) {
            		path = dialog.getSelectedResource();
            		if (path != null) {
            			_targetPath = path;
            			pathText.setText(path.toString());
            		}
            	}
            }
        });

        GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true,3,1);
        gridData.minimumWidth=40;
        final ListViewer avaibleFiles = new ListViewer(sourceGroup);
        avaibleFiles.getList().setLayoutData(gridData);
        Button open = new Button(sourceGroup, SWT.PUSH);
        open.setLayoutData(new GridData(80,25));
        open.setText(Messages.ADLConverterMainView_ADLSourceFileDialogButton);

        open.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                FileDialog dialog = new FileDialog(sourceGroup.getShell(),SWT.MULTI);
                dialog.setFilterNames (new String [] {Messages.ADLConverterMainView_ADLFileSourceDialogFileDes, Messages.ADLConverterMainView_AllFileSourceDialogFileDes});
                dialog.setFilterExtensions (new String [] {"*.adl", "*.*"}); //Windows wild cards //$NON-NLS-1$ //$NON-NLS-2$
                dialog.setFilterPath (initial.getProjectRelativePath().toOSString()); 
                dialog.open();
                String path = dialog.getFilterPath();
                String[] files = dialog.getFileNames();
                for (String name : files) {
                    avaibleFiles.add(new File(path,name));
                }
                avaibleFiles.getList().selectAll();
            }
            
        });
        Button clear = new Button(sourceGroup,SWT.PUSH);
        clear.setText(Messages.ADLConverterMainView_ClearButtonText);
        gd = new GridData(80,25);
        gd.horizontalAlignment=SWT.CENTER;
        clear.setLayoutData(gd);
        clear.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                avaibleFiles.getList().removeAll();
            }
           
        });

        
        Button convert = new Button(sourceGroup,SWT.PUSH);
        convert.setText(Messages.ADLConverterMainView_ConvcertButtonText);
        gd = new GridData(80,25);
        gd.horizontalAlignment=SWT.RIGHT;
        convert.setLayoutData(gd);

        convert.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final SelectionEvent e) {}

            @SuppressWarnings("unchecked") //$NON-NLS-1$
            public void widgetSelected(final SelectionEvent e) {
                StructuredSelection sel = (StructuredSelection) avaibleFiles.getSelection();
                ArrayList<Object> list = new ArrayList<Object>(sel.toList());
                while(list.size()>0){
                    ADLDisplayImporter di= new ADLDisplayImporter();
                    Object o = list.remove(0);
                    File file = (File) o;
                    try {
                        di.importDisplay(
                                file.getAbsolutePath(),
                                initial.getProjectRelativePath().append(_targetPath),
                                file.getName().replace(".adl", ".css-sds") //$NON-NLS-1$ //$NON-NLS-2$

                        );
                        
                    } catch (Exception e1) {
                        CentralLogger.getInstance().error(this, e1);
                    }
                    file = null;

                    avaibleFiles.setSelection(new StructuredSelection(list), true);
                    avaibleFiles.getList().getParent().layout();
                }
//                int[] index = avaibleFiles.getList().getSelectionIndices();
//                
//                for (int i : index) {
//                    ADLDisplayImporter di= new ADLDisplayImporter();
//                    try {
//                        
//                        di.importDisplay(
//                                ((File)avaibleFiles.getElementAt(i)).getAbsolutePath(),
//                                initial.getProjectRelativePath().append(_targetPath),
//                                ((File)avaibleFiles.getElementAt(i)).getName().replace(".adl", ".css-sds") //$NON-NLS-1$ //$NON-NLS-2$
//
//                        );
//                        
//                    } catch (Exception e1) {
//                        CentralLogger.getInstance().error(this, e1);
//                    }
//                }
            }
           
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {    }

}
