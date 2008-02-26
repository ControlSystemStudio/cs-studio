/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.platform.ui.internal.dataexchange.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

/** Selection Provider that hands out both PV and archive info data.
 *  @author Kay Kasemir
 */
public class DummyCombinedSelectionProvider implements ISelectionProvider
{
    private IProcessVariableWithArchive data =
       CentralItemFactory.createProcessVariableWithArchive("fred",
                                        "http://server", 42, "main archive");
    private ISelection selection;
    
    public DummyCombinedSelectionProvider()
    {
        selection = new StructuredSelection()
        {
            @Override
            public Object getFirstElement()
            {
                return data;
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }

            @Override
            public Iterator iterator()
            {
                return toList().iterator();
            }

            @Override
            public int size()
            {
                return 1;
            }

            @Override
            public Object[] toArray()
            {
                return toList().toArray();
            }

            @Override
            public List toList()
            {
                ArrayList<Object> list = new ArrayList<Object>(1);
                list.add(data);
                return list;
            }
        };
    }
    
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {}

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {}

    public ISelection getSelection()
    {
        return selection;
    }

    public void setSelection(ISelection selection)
    {}
}
