/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.namespacebrowser.tine.ui;

import org.csstudio.apputil.ui.dialog.ErrorDetailDialog;
import org.csstudio.utility.nameSpaceBrowser.ui.CSSView;
import org.csstudio.utility.namespacebrowser.tine.Activator;
import org.csstudio.utility.namespacebrowser.tine.Messages;
import org.csstudio.utility.namespacebrowser.tine.utility.AutomatTine;
import org.csstudio.utility.namespacebrowser.tine.utility.TineNameSpace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
/**********************************************************************************
 *
 * @author Helge Rickens
 *
 * 
 *
 **********************************************************************************/
public class MainViewTine extends ViewPart {
	public static final String ID = MainViewTine.class.getName();
	private static String defaultPVFilter =""; //$NON-NLS-1$
	CSSView cssview;
	private AutomatTine automat;

	// @Override
	@Override
    public void createPartControl(Composite parent) {
		automat = new AutomatTine();
		ScrolledComposite sc = new ScrolledComposite(parent,SWT.H_SCROLL);
		Composite composite = new Composite(sc,SWT.NONE);
		sc.setContent(composite);
	    sc.setExpandVertical(true);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1,1));
		composite.setLayout(new GridLayout(1,false));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(
				parent.getShell(),
				Activator.PLUGIN_ID + ".nsB");
        composite.getShell().addKeyListener(new KeyAdapter() {
			@Override
            public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.F1){
					PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
				}
			}
		});
		String[] headlines = {	Messages.getString("CSSView_Facility"),
								Messages.getString("CSSView_Controller"),
								Messages.getString("CSSView_Server"),
								Messages.getString("CSSView_Device"),
								Messages.getString("CSSView_Record")
		};
		
		// Namend the Records
		try {
		cssview = 
			new CSSView(
					composite, 
					automat,
					new TineNameSpace(), 
					getSite(),
					defaultPVFilter,
					"Context", 
					headlines, 
					0,
					Messages.getString("MainViewTine.Default"));
		} catch (Exception e) {
            ErrorDetailDialog errorDetailDialog = new ErrorDetailDialog(null,
                                                                        "Titel",
                                                                        e.getLocalizedMessage(),
                                                                        e.toString());
            errorDetailDialog.open();
        }
	}

	// @Override
	@Override
    public void setFocus() {
	    // do noting
	}

	public void setDefaultPVFilter(String defaultFilter) {
		defaultPVFilter = defaultFilter;
		cssview.setDefaultFilter(defaultPVFilter);
	}

	// @Override
	@Override
    public void dispose(){
		automat = null;
	}
}









