
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.utility.screenshot;

import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Markus Moeller
 *
 */
public class ImageCreator {
    
    private Display display;
    
    public ImageCreator(Display display) {
        this.display = display;
    }
        
    public void captureImages(ImageBundle imageBundle) {
        
        Rectangle rect = null;
        Image img = null;
        Image dispImg = null;
        GC gc = null;
        
        // Screen dump
        gc = new GC(display);        
        rect = display.getBounds();
        dispImg = new Image(display, rect.width, rect.height);
        gc.copyArea(dispImg, rect.x, rect.y);
        imageBundle.setScreenImage(dispImg);
        gc.dispose();
        gc = null;
        rect = null;
        
//        try {
//            ImageLoader loader = null;
//            loader = new ImageLoader();
//            ImageData[] imageData = new ImageData[1];
//            imageData[0] = dispImg.getImageData();
//            loader.data = imageData;
//            loader.save("D:\\screen.bmp", SWT.IMAGE_BMP);
//        } catch(Exception e) {
//            System.out.println("[*** Exception ***]: Cannot initialize ImageLoader: " + e.getMessage());
//        }

        // Window dump
        rect = getPartControl(false, true);
        if(rect != null) {
            
            normalizeRectangle(display.getBounds(), rect);

            img = new Image(display, rect.width, rect.height);
            
            gc = new GC(img);            
            gc.drawImage(dispImg, rect.x, rect.y, rect.width, rect.height, 0, 0, rect.width, rect.height);            
            imageBundle.setWindowImage(img);            
            gc.dispose();
            gc = null;
            rect = null;

//            try {
//                ImageLoader loader = null;
//                loader = new ImageLoader();
//                ImageData[] imageData = new ImageData[1];
//                imageData[0] = img.getImageData();
//                loader.data = imageData;
//                loader.save("D:\\window.bmp", SWT.IMAGE_BMP);
//            } catch(Exception e) {
//                System.out.println("[*** Exception ***]: Cannot initialize ImageLoader: " + e.getMessage());
//            }

            img.dispose();           
            img = null;
        }

        // Section dump
        rect = getPartControl(true, false);
        if(rect != null) {            
            
            normalizeRectangle(display.getBounds(), rect);
            img = new Image(display, rect.width, rect.height);
            
            gc = new GC(img);            
            gc.drawImage(dispImg, rect.x, rect.y, rect.width, rect.height, 0, 0, rect.width, rect.height);
            imageBundle.setSectionImage(img);
            gc.dispose();
            gc = null;
            rect = null;

//            try {
//                ImageLoader loader = null;
//                loader = new ImageLoader();
//                ImageData[] imageData = new ImageData[1];
//                imageData[0] = img.getImageData();
//                loader.data = imageData;
//                loader.save("D:\\part.bmp", SWT.IMAGE_BMP);
//            } catch(Exception e) {
//                System.out.println("[*** Exception ***]: Cannot initialize ImageLoader: " + e.getMessage());
//            }
            
            img.dispose();
            img = null;
        }
        
        dispImg.dispose();
        dispImg = null;
    }
    
    private void normalizeRectangle(Rectangle displayBounds, Rectangle target) {
        
        if(target == null) {
            return;
        } else {
            
            target.x = Math.max(target.x, displayBounds.x);
            target.y = Math.max(target.y, displayBounds.y);
            target.width = Math.min(target.x + target.width, displayBounds.x + displayBounds.width) - target.x;
            target.height = Math.min(target.y + target.height, displayBounds.y + displayBounds.height) - target.y;
            
            // Adjust the origin of the image if the display contains more then one monitor
            target.x = (displayBounds.x < 0) ? (target.x - displayBounds.x) : target.x; 
            target.y = (displayBounds.y < 0) ? (target.y - displayBounds.y) : target.y; 

            return;
        }
    }

    /**
     * 
     * @param display
     * @param section
     * @param shell
     * @return
     */
    
    private Rectangle getPartControl(boolean section, boolean shell) {
        
        Control partControl = display.getFocusControl();
        if(partControl != null && !partControl.isDisposed()) {
            
            Control previousContr = null;
            for(; partControl != null; partControl = partControl.getParent()) {
                
                if(partControl instanceof Shell) {
                    return partControl.getBounds();
                }
                
                if(!shell) {
                    
                    boolean isView = partControl instanceof ViewForm;
                    if(!isView && (partControl instanceof Composite)) {
                        Composite parent = ((Composite)partControl).getParent();

                        if(parent != null) {
                            
                            Control children[] = parent.getChildren();
                            for(int i = 0; i < children.length; i++) {
                                
                                if(children[i] instanceof ToolBar) {
                                    isView = true;
                                    break;
                                }
                                
                                if(previousContr == null || !(children[i] instanceof Sash)) {
                                    continue;
                                }
                                
                                partControl = previousContr;
                                isView = true;
                                break;
                            }
                        }
                    }
                    
                    if(isView) {
                        
                        Point origin = partControl.toDisplay(0, 0);
                        Rectangle bounds = partControl.getBounds();
                        if(section) {
                            
                            Composite parent;
                            for(Control sectionControl = partControl; sectionControl != null; sectionControl = parent) {
                                
                                parent = sectionControl.getParent();
                                if(parent == null) {
                                    break;
                                }
                                
                                Control children[] = parent.getChildren();
                                for(int i = 0; i < children.length; i++) {
                                    
                                    Control child = children[i];
                                    if(child instanceof Sash) {
                                        
                                        Point sashSize = ((Sash)child).getSize();
                                        int sashWidth = 2 * Math.min(sashSize.x, sashSize.y) - 1;
                                        Rectangle parentBounds = parent.getBounds();
                                        Point parentOrigin = parent.toDisplay(0, 0);
                                        int left = parentOrigin.x - sashWidth;
                                        int right = left + parentBounds.width + sashWidth;
                                        int top = parentOrigin.y - sashWidth;
                                        int bottom = top + parentBounds.height + sashWidth;
                                        
                                        for(int j = i; j < children.length; j++) {
                                            
                                            Control c2 = children[j];
                                            if(c2 instanceof Sash) {
                                                
                                                Point loc = c2.toDisplay(0, 0);
                                                Point size = c2.getSize();
                                                if(size.x < size.y) {
                                                    if(loc.x < origin.x && loc.x > left) {
                                                        left = loc.x;
                                                    }
                                                    int r = loc.x + size.x;
                                                    if(r > origin.x + bounds.width && r < right) {
                                                        right = r;
                                                    }
                                                } else {
                                                    if(loc.y < origin.y && loc.y > top) {
                                                        top = loc.y;
                                                    }
                                                    int r = loc.y + size.y;
                                                    if(r > origin.y + bounds.height && r < bottom) {
                                                        bottom = r;
                                                    }
                                                }
                                            }
                                        }

                                        bounds.x = left;
                                        bounds.y = top;
                                        bounds.width = right - left;
                                        bounds.height = bottom - top;
                                        return bounds;
                                    }
                                }

                                if(parent instanceof Shell) {
                                    break;
                                }
                            }

                            return null;
                        } else {
                            bounds.x = origin.x;
                            bounds.y = origin.y;
                            return bounds;
                        }
                    }
                }
                
                previousContr = partControl;
            }
        }
        
        return null;
    }
}
