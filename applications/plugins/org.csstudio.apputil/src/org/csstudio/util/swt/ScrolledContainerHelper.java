package org.csstudio.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

/** Helper for creating a container with 'automatic' scroll bars.
 *  <p>
 *  ScrolledComposite is a bit iffy, since it seems to need yet another
 *  Composite inside for the actual widgets.
 *  This helps with creating all that.
 *  @author Kay Kasemir
 */
public class ScrolledContainerHelper
{
    /** Create the 'automatic' scroll bars.
     * 
     * @param parent The perent shell
     * @param width Minimum width that triggers horizontal scroll bar.
     * @param height Minimum height that triggers vertical scroll bar.
     * @return Returns a container into which to add widgets.
     */
    public static Composite create(Composite parent, int width, int height)
    {
        ScrolledComposite scroll = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        Composite scrolled_content = new Composite(scroll, 0);
        scroll.setContent(scrolled_content);
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        scroll.setMinWidth(width);
        scroll.setMinHeight(height);   
        return scrolled_content;
    }
}
