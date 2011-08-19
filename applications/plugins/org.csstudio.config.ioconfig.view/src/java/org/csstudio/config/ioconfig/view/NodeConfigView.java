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
 * $Id: NodeConfigView.java,v 1.3 2009/11/09 09:17:05 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view;

import javax.annotation.Nonnull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 24.02.2009
 */
public class NodeConfigView extends ViewPart {
    
    public static final String ID = "org.csstudio.config.ioconfig.view.NodeConfigView";
    private Composite _configComposite;
    private ScrolledComposite _scroll;
    
    /**
     * Create the View Part.
     * @param parent the parent Composite.
     */
    @Override
    public final void createPartControl(@Nonnull final Composite parent) {
        _scroll = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL |SWT.NONE);
        _scroll.setExpandHorizontal(true);
        _scroll.setExpandVertical(true);
        setMinSize(200,350);
        
        _configComposite = new Composite(_scroll, SWT.NONE);
        _configComposite.setLayout(new FillLayout());
        _configComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        _scroll.setContent(_configComposite);
    }
    
    /**
     * 
     * @return the Node config Composite.
     */
    @Nonnull
    public final Composite getComposite(){
        return _configComposite;
    }
    
    /**
     * Composite get Focus.
     */
    @Override
    public final void setFocus() {
        _configComposite.setFocus();
    }
    
    public void setMinSize(final int w, final int h) {
        _scroll.setMinSize(w, h);
    }
    
    @Override
    protected void setPartName(@Nonnull final String partName) {
        super.setPartName(partName);
    }
    
}
