
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

package org.csstudio.utility.screenshot.menu.action;

import org.csstudio.utility.screenshot.ScreenshotWorker;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSaveImageAsAction extends Action {
    
    /** Logger of this class */ 
    private static final Logger LOG = LoggerFactory.getLogger(FileSaveImageAsAction.class);
    
    
    /** The screenshot main class */
    private final ScreenshotWorker worker;
    
    /** Image file extensions */
    private final String[] ext;
    
    /** Filter strings for the save dialog */
    private final String[] filter;  
    
    /** Image types */
    private final int[] type;
    
    /**
     * @author Markus Moeller
     * @param w
     */
    
    public FileSaveImageAsAction(ScreenshotWorker w) {
        worker = w;
        ext = new String[] { "*.bmp", "*.jpg" };
        filter = new String[] { "Windows Bitmap (*.bmp)", "JPEG (*.jpg)" };
        type = new int[] { SWT.IMAGE_BMP, SWT.IMAGE_JPEG };
        
        this.setText(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_SAVE"));
        this.setToolTipText(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_SAVE_TT"));
        this.setEnabled(true);
    }
    
    @Override
    public void run() {
        int indexOfDot = -1;
        int indexExt = -1;
        
        FileDialog dialog = new FileDialog(worker.getDisplay().getActiveShell(), SWT.SAVE);
        
        dialog.setFilterExtensions(ext);
        dialog.setFilterNames(filter);
        
        String result = dialog.open();
        
        if(result != null) {
            
            indexOfDot = result.lastIndexOf('.');
            if(indexOfDot != -1) {
                
                String e = result.substring(indexOfDot + 1).toLowerCase();
                
                for(int i = 0;i < ext.length;i++) {
                    
                    if(ext[i].indexOf(e) != -1) {
                        indexExt = i;
                        break;
                    }
                }
                
                if(indexExt != -1) {
                    
                    LOG.debug("Try to save image now...");
                    
                    ImageLoader loader = new ImageLoader();
                    ImageData[] imageData = new ImageData[1];
                    
                    if(worker.getDisplayedImage() != null) {
                        LOG.debug("Saving Displayed image");
                        imageData[0] = worker.getDisplayedImage().getImageData();
                    } else if(worker.getSimpleImage() != null) {
                        LOG.debug("Saving Simple image");
                        imageData[0] = worker.getSimpleImage().getImageData();
                    } else {
                        LOG.debug("NO IMAGE!!!!");
                    }
                    
                    loader.data = imageData;
             
                    try {
                        loader.save(result, type[indexExt]);
                    } catch(SWTException swte) {
                        MessageDialog.openError(worker.getDisplay().getActiveShell(),
                                                worker.getNameAndVersion(), "*** SWTException *** : " + swte.getMessage());
                    }
                } else {
                    MessageDialog.openError(worker.getDisplay().getActiveShell(),
                                            worker.getNameAndVersion(), "Unsupported file type: " + e);
                }
            }
        }
    }
}
