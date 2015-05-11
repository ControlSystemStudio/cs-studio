package org.csstudio.sds.ui.cursors.internal;

import org.csstudio.sds.cursorservice.AbstractCursor;
import org.csstudio.sds.cursorservice.ContributedCursor;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.cursorservice.ICursorService;
import org.csstudio.sds.cursorservice.SWTCursor;
import org.csstudio.sds.cursorservice.WorkspaceCursor;
import org.csstudio.ui.util.ImageUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cursor helper methods.
 *
 * @author swende
 *
 */
public class CursorHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CursorHelper.class);

    /**
     * Private to prevent instantiation of this class.
     */
    private CursorHelper() {

    }

    /**
     * Tries to find a cursor for the specified id.
     *
     * @param cursorId
     *            the cursor id
     * @return a cursor or null if none was found
     */
    public static Cursor getCursor(String cursorId) {
        // find the descriptor for the specified cursor id
        AbstractCursor d = CursorService.getInstance().findCursor(
                cursorId);

        Cursor cursor = null;

        if (d != null) {
            if (d == ICursorService.SYSTEM_DEFAULT_CURSOR) {
                cursor = null;
            } else if (d instanceof SWTCursor) {
                cursor = new Cursor(Display.getCurrent(),
                        ((SWTCursor) d).getSWTCursor());
            } else if (d instanceof ContributedCursor) {
                ContributedCursor cc = (ContributedCursor) d;
                ImageData imageData = ImageUtil.getInstance()
                        .getImage(cc.getBundle(),
                                cc.getImage()).getImageData();
                cursor = new Cursor(Display.getCurrent(), imageData, 1, 1);
            } else if (d instanceof WorkspaceCursor) {
                WorkspaceCursor wc = (WorkspaceCursor) d;
                ImageData imageData = ImageUtil.getInstance()
                        .getImageDescriptorFromFile(
                                wc.getGraphicsFile()).getImageData();
                cursor = new Cursor(Display.getCurrent(), imageData, 1, 1);
            } else {
                LOG.warn("Unknown cursor type: " + d.getClass());
                cursor = null;
            }
        }

        return cursor;
    }

    /**
     * Applies a cursor to the specified Draw2d figure. If no cursor is found
     * for the specified id, no cursor is applied.
     *
     * @param figure
     *            the Draw2d figure
     * @param cursorId
     *            the cursor id
     */
    public static void applyCursor(IFigure figure, String cursorId) {
        Cursor cursor = getCursor(cursorId);

        // a null value sets the cursor back to the system default
        figure.setCursor(cursor);
    }

    /**
     * Applies a cursor to the specified SWT control. If no cursor is found for
     * the specified id, no cursor is applied.
     *
     * @param control
     *            the SWT control
     * @param cursorId
     *            the cursor id
     */
    public static void applyCursor(Control control, String cursorId) {
        Cursor cursor = getCursor(cursorId);

        // a null value sets the cursor back to the system default
        control.setCursor(cursor);
    }

}
