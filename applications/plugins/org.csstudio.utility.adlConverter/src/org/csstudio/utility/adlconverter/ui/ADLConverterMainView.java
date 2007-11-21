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

import org.csstudio.platform.ui.composites.ResourceAndContainerGroup;
import org.csstudio.platform.ui.util.ImageUtil;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
     * The Workspace Chooser.
     */
    private ResourceAndContainerGroup _resourceGroup;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(3,true));
        
        final IResource initial = ResourcesPlugin.getWorkspace().getRoot();
        final Text pathText = new Text(parent,SWT.BORDER);
        pathText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,3,1));
        pathText.setText(initial.getProjectRelativePath().toOSString());
        Button targetOpenButton = new Button(parent, SWT.PUSH);
        targetOpenButton.setText(Messages.ADLConverterMainView_TargetOpernButton);
        targetOpenButton.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,false,false,3,1));
        
        

        targetOpenButton.addSelectionListener(new SelectionListener(){


            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                final Shell shell = new Shell(parent.getShell());
                shell.setLayout(new GridLayout(1,false));
                shell.setText(Messages.ADLConverterMainView_DialogText);
                Image image = ImageUtil.getInstance().getImageDescriptor("org.csstudio.sds.importer.ui", "icons/sds.gif").createImage(); //$NON-NLS-1$ //$NON-NLS-2$
                shell.setImage(image);
                
                _resourceGroup = new ResourceAndContainerGroup(shell,null,Messages.ADLConverterMainView_DateiName,Messages.ADLConverterMainView_ADLFileChooserDes);
                _resourceGroup.setContainerFullPath(new Path("CSS/SDS")); //$NON-NLS-1$
                _resourceGroup.setFocus();
                
                Composite c = new Composite(shell,SWT.NONE);
                c.setLayout(new GridLayout(2,true));
                c.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
                
                Button ok = new Button(c,SWT.PUSH);
                ok.setText(Messages.ADLConverterMainView_ADLFileChooserOkButton);
                GridData gd = new GridData(SWT.CENTER,SWT.LEFT,true,false,1,1);
                gd.widthHint=80;
                gd.heightHint=25;
                ok.setLayoutData(gd);
                
                Button cancel = new Button(c,SWT.PUSH);
                cancel.setText(Messages.ADLConverterMainView_ADLFileChooserCancelButton);
                gd = new GridData(SWT.CENTER,SWT.RIGHT,true,false,1,1);
                gd.widthHint=80;
                gd.heightHint=25;
                cancel.setLayoutData(gd);
                
                shell.pack();
                shell.open();
                
                ok.addSelectionListener(new SelectionListener(){

                    public void widgetDefaultSelected(final SelectionEvent e) {}

                    public void widgetSelected(final SelectionEvent e) {
                        pathText.setText(_resourceGroup.getContainerFullPath().toString());
                        shell.close();
                    }
                    
                });
                
                cancel.addSelectionListener(new SelectionListener(){

                    public void widgetDefaultSelected(final SelectionEvent e) {}

                    public void widgetSelected(final SelectionEvent e) {
                        shell.close();
                    }
                    
                });
            }
        });

        GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true,3,1);
        gridData.minimumWidth=40;
        final ListViewer avaibleFiles = new ListViewer(parent);
        avaibleFiles.getList().setLayoutData(gridData);
        Button open = new Button(parent, SWT.PUSH);
        open.setLayoutData(new GridData(80,25));
        open.setText(Messages.ADLConverterMainView_ADLSourceFileDialogButton);

        open.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                FileDialog dialog = new FileDialog(parent.getShell(),SWT.MULTI);
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
        Button clear = new Button(parent,SWT.PUSH);
        clear.setText(Messages.ADLConverterMainView_ClearButtonText);
        GridData gd = new GridData(80,25);
        gd.horizontalAlignment=SWT.CENTER;
        clear.setLayoutData(gd);
        clear.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                avaibleFiles.getList().removeAll();
            }
           
        });

        
        Button convert = new Button(parent,SWT.PUSH);
        convert.setText(Messages.ADLConverterMainView_ConvcertButtonText);
        gd = new GridData(80,25);
        gd.horizontalAlignment=SWT.RIGHT;
        convert.setLayoutData(gd);

        convert.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(final SelectionEvent e) {}

            public void widgetSelected(final SelectionEvent e) {
                int[] index = avaibleFiles.getList().getSelectionIndices();
                for (int i : index) {
                    ADLDisplayImporter di= new ADLDisplayImporter();
                    try {
                        
                        di.importDisplay(
                                ((File)avaibleFiles.getElementAt(i)).getAbsolutePath(),
                                initial.getProjectRelativePath().append(_resourceGroup.getContainerFullPath()),
                                ((File)avaibleFiles.getElementAt(i)).getName().replace(".adl", ".css-sds") //$NON-NLS-1$ //$NON-NLS-2$
                        );
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
           
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
