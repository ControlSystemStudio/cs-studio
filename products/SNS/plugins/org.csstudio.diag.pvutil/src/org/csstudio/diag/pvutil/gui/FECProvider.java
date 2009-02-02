package org.csstudio.diag.pvutil.gui;

import org.csstudio.diag.pvutil.model.PVUtilDataAPI;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Combo;

/** Provides the Device for a specific Table row */
public class FECProvider implements ILazyContentProvider
{
    final private Combo fec_combo;
    
    public FECProvider(Combo fec_combo, PVUtilDataAPI util_model)
    {
        this.fec_combo = fec_combo;
        
    }

    /** {@inheritDoc} */
    public void updateElement(int index)
    {
        fec_combo.getText();
     
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
}
