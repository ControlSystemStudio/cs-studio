package org.csstudio.utility.pvmanager.ui;

import java.util.concurrent.Executor;

import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VImage;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

public class SWTUtil {
    private SWTUtil() {
        // Prevent creation
    }

    private static Executor SWTThread =
            swtThread(PlatformUI.getWorkbench().getDisplay());

    public static Executor swtThread() {
        return SWTThread;
    }

    public static Executor swtThread(final Display display) {
        return new Executor() {

            @Override
            public void execute(Runnable task) {
                try {
                    if (!display.isDisposed()) {
                        display.asyncExec(task);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Executor swtThread(final Widget widget) {
        return swtThread(widget.getDisplay());
    }

    public static Executor swtThread(final WorkbenchPart viewPart) {
        return swtThread(viewPart.getSite().getShell().getDisplay());
    }

    public static Image toImage(GC gc, VImage vImage) {

        ListNumber data = vImage.getData();
        byte imageByteData[] = new byte[data.size()];

        for (int i = 0; i < data.size(); i++) {
            imageByteData[i] = data.getByte(i);
        }

        ImageData imageData = new ImageData(vImage.getWidth(), vImage.getHeight(), 24, new PaletteData(0xFF, 0xFF00, 0xFF0000), vImage.getWidth() * 3, imageByteData);

        Image image = new Image(gc.getDevice(), imageData);
        return image;
    }

}
