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
/*
 * $Id: MainView.java,v 1.2 2010/08/20 13:33:03 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view;

import org.csstudio.config.ioconfig.model.HibernateManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 19.06.2007
 */
public class MainView extends ViewPart {


    public static final String ID = "org.csstudio.config.ioconfig.view.MainView";
    private ProfiBusTreeView _profiBusTreeView;
    
    /**
     * @param parent The Parent Composite
     */
    @Override
    public void createPartControl(final Composite parent) {
        GridLayout layout = new GridLayout(1,false);
        layout.marginHeight=0;
        layout.marginWidth=0;
        layout.marginLeft=0;
        parent.setLayout(layout);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true,true,1,1));
        parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));
        ProfiBusTreeView profiBusTreeView = new ProfiBusTreeView(parent,SWT.NONE,getViewSite());
        setProfiBusTreeView(profiBusTreeView);
//        HibernateManager.getInstance().addObserver(profiBusTreeView);
    }

    /**
     * The Profibus TreeViewer get the Focus.
     */
    @Override
    public void setFocus() {
        getProfiBusTreeView().setFocus();
    }

    /**
     * @param profiBusTreeView the profiBusTreeView to set
     */
    private void setProfiBusTreeView(ProfiBusTreeView profiBusTreeView) {
        _profiBusTreeView = profiBusTreeView;
    }

    /**
     * @return the profiBusTreeView
     */
    public ProfiBusTreeView getProfiBusTreeView() {
        return _profiBusTreeView;
    }
    
    



}
