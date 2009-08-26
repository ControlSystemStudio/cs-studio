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
package org.csstudio.config.ioconfig.config.view.helper;

import org.csstudio.config.ioconfig.config.view.NodeConfig;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 04.02.2009
 */
public class InfoConfigComposte extends NodeConfig {

    private final Node _node;

    /**
     * That is a Config View that only show a Description.
     * Is is useful to show nodes without properties to configure. 
     * 
     * @param parent The Parent composite.
     * @param profiBusTreeView the Navigate Profibus Tree-view
     * @param style the Composite Style.
     * @param node the node to "Configure"
     * @param string The Description text. 
     */
    public InfoConfigComposte(Composite parent, ProfiBusTreeView profiBusTreeView, int style,
            Node node, String string) {
        super(parent,profiBusTreeView, node!=null?node.getClass().getName():"", node, false);
        _node = node;
        setSaveButtonSaved();
        Text text = new Text(getNewTabItem("Description", 1), SWT.MULTI | SWT.LEAD | SWT.BORDER|SWT.READ_ONLY);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        if(string!=null) {
            text.setText(string);
        }
        
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fill(GSDFile gsdFile) {
        return false;
    }

    /**
     * Have no GSD File!
     * {@inheritDoc}
     */
    @Override
    public GSDFile getGSDFile() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNode() {
        return _node;
    }
    
    

}
