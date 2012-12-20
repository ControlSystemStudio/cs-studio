package org.csstudio.utility.pvmanager.ui;

import java.util.concurrent.Executor;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.ui.PlatformUI;
import org.epics.pvmanager.data.VImage;

public class SWTUtil {
	private SWTUtil() {
		// Prevent creation
	}
	
	private static Executor SWTThread = new Executor() {

        @Override
        public void execute(Runnable task) {
            try {
            	if (!PlatformUI.getWorkbench().getDisplay().isDisposed()) {
            	    PlatformUI.getWorkbench().getDisplay().asyncExec(task);
            	}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    };
    
	public static Executor swtThread() {
		return SWTThread;
	}
	
	public static Image toImage(GC gc, VImage vImage) {
		 ImageData imageData = new ImageData(vImage.getWidth(), vImage.getHeight(), 24, new PaletteData(0xFF, 0xFF00, 0xFF0000), vImage.getWidth()*3, vImage.getData());
		 Image image = new Image(gc.getDevice(), imageData);
		 return image;
	}

}
