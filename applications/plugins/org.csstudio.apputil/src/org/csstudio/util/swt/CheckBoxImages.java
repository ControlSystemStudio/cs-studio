package org.csstudio.util.swt;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/** Get a snapshot of the native checkbox control images.
 *  <p>
 *  While SWING makes it easy to include checkboxes or any
 *  other controls in a table, SWT isn't there (yet?).
 *  So one has to display the image of a checkbox.
 *  This hack from
 *  <code>
 *  feed://tom-eclipse-dev.blogspot.com/feeds/46160309661596839/comments/default
 *  </code>
 *  takes a snapshot of the 'real' button from a browfly displayed shell.
 *
 *  @author Kay Kasemir
 */
public class CheckBoxImages
{
    private static final String CHECKED_KEY = "CHECKED"; //$NON-NLS-1$
    private static final String UNCHECK_KEY = "UNCHECKED"; //$NON-NLS-1$
    private static CheckBoxImages instance = null;
    
    /** Singleton */
    private CheckBoxImages(final Control control)
    {
        final ImageRegistry registry = JFaceResources.getImageRegistry();
        if (registry.getDescriptor(CHECKED_KEY) != null)
            return;
        registry.put(UNCHECK_KEY, makeShot(control, false));
        registry.put(CHECKED_KEY, makeShot(control, true));
    }
    
    /** Create or obtain existing instance of CheckBoxImages
     *  @param control Any control, used to get background color etc.
     *  @return Singleton instance of CheckBoxImages
     */
    static public CheckBoxImages getInstance(final Control control)
    {
        if (instance == null)
            instance = new CheckBoxImages(control);
        return instance;
    }
    
    /** @return Image of selected or unselected button image */
    public Image getImage(final boolean selected)
    {
        return selected
            ? JFaceResources.getImageRegistry().get(CHECKED_KEY)
            : JFaceResources.getImageRegistry().get(UNCHECK_KEY);
    }

    /** @return Image that is snapshot of Checkbox control */
    private Image makeShot(final Control control, final boolean selected)
    {
        final Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);
    
        // otherwise we have a default gray color
        final Color backgroundColor = control.getBackground();
        shell.setBackground(backgroundColor);
    
        final Button button = new Button(shell, SWT.CHECK);
        button.setBackground(backgroundColor);
        button.setSelection(selected);
    
        // Some tweaking that's a compromize between Win32, OSX, ...
        // versions of the actual control.
        final Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        // otherwise an image is stretched by width
        final int size = Math.max(bsize.x, bsize.y);
        button.setLocation(1, 1);
        button.setSize(size, size);
        shell.setSize(size, size);
        // Briefly display shell, take snapshot, close
        shell.open();
        final GC gc = new GC(shell);
        final Image image = new Image(shell.getDisplay(), size, size);
        // If switching windows during application startup,
        // the image would sometimes contain stuff from whatever
        // happened to be on top of the screen.
        // Maybe this helps to avoid that?
        shell.forceActive();
        gc.copyArea(image, 0, 0);
        gc.dispose();
        shell.close();
    
        return image;
    }
}
