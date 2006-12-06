package org.csstudio.diag.epics.pvtree;

import org.csstudio.value.Severity;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Label provider for PVTreeItem entries.
 *  @author Kay Kasemir
 */
class PVTreeLabelProvider extends LabelProvider implements IColorProvider
{
    public String getText(Object obj)
    {
        return obj.toString();
    }

    public Image getImage(Object obj)
    {
        // Indicate if this is a 'record' of known type...
        //if (obj instanceof PVTreeItem && ((PVTreeItem)obj).getType() != null)
        //    return PlatformUI.getWorkbench().getSharedImages()
        //        .getImage(ISharedImages.IMG_OBJ_FILE);
        // or something else (unknown)
        return null;
    }

    public Color getBackground(Object element)
    {
        return null;
    }

    public Color getForeground(Object element)
    {
        if (! (element instanceof PVTreeItem))
            return null;
        
        Severity severity = ((PVTreeItem)element).getSeverity();
        if (severity.isInvalid())
            return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
        if (severity.isMajor())
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        if (severity.isMinor())
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
        return null;
    }
}