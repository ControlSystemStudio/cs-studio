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
