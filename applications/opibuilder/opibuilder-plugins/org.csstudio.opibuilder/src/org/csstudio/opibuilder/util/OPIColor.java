/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**The dedicated color type which supports predefined color name in OPI builder color file.
 * If the color name doesn't exist in the color file, the color value is null.
 * @author Xihui Chen
 *
 */
public class OPIColor implements IAdaptable {

    private static final RGB TRANSPARENT_COLOR = new RGB(253,
                    254, 252);

    private String colorName;

    private RGB colorValue;

    private boolean preDefined = false;


    static ImageRegistry imageRegistry = new ImageRegistry(DisplayUtils.getDisplay());
    private static int imageCount = 0;

    private static final int MAX_IMG_COUNT = 1000;

    public OPIColor(String colorName) {
        this.colorName = colorName;
        this.colorValue = MediaService.getInstance().getColor(colorName);
        preDefined = true;
    }

    public OPIColor(RGB rgb){
        setColorValue(rgb);
    }

    public OPIColor(int red, int green, int blue){
        this(new RGB(red, green, blue));
    }


    public OPIColor(String name, RGB rgb, boolean predefined) {
        this.colorName = name;
        this.colorValue = rgb;
        this.preDefined = predefined;
    }

    /**
     * @return the name of color if it is a predefined color macro;
     * otherwise, it is a string of the RGB values.
     */
    public String getColorName() {
        return colorName;
    }

    /**
     * @return the rgb value of the color. null if the predefined color does not exist.
     */
    public RGB getRGBValue() {
        return colorValue;
    }

    /**
     * @return the swt color. No dispose is needed, the system will handle the dispose.
     */
    public Color getSWTColor(){
        return CustomMediaFactory.getInstance().getColor(colorValue);
    }

    /**
     * @return true if this color is predefined in color file, false otherwise.
     */
    public boolean isPreDefined() {
        return preDefined;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
        this.colorValue = MediaService.getInstance().getColor(colorName);
        preDefined = true;
    }

    public void setColorValue(RGB rgb) {
        this.colorName = "(" + rgb.red + "," + rgb.green + "," + rgb.blue + ")";
        this.colorValue = rgb;
        preDefined = false;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if(adapter == IWorkbenchAdapter.class)
            return adapter.cast(new IWorkbenchAdapter() {

                @Override
                public Object getParent(Object o) {
                    return null;
                }

                @Override
                public String getLabel(Object o) {
                    return getColorName();
                }

                @Override
                public ImageDescriptor getImageDescriptor(Object object) {

                    Image image = imageRegistry.get(getID());
                    if(image == null){
                        image = createIcon(getRGBValue());
                        if(imageCount >= MAX_IMG_COUNT){
                            imageRegistry.dispose();
                            imageCount =0;
                        }
                        imageRegistry.put(getID(), image);
                        imageCount++;
                    }

                    return ImageDescriptor.createFromImage(image);
                }

                @Override
                public Object[] getChildren(Object o) {
                    return new Object[0];
                }
            });

        return null;
    }

    /**Get the color image for this color.
     * @return the color image
     */
    public Image getImage(){
        Image image = imageRegistry.get(getID());
        if(image == null){
            image = createIcon(getRGBValue());
            if(imageCount >= MAX_IMG_COUNT){
                imageRegistry.dispose();
                imageCount =0;
            }
            imageRegistry.put(getID(), image);
            imageCount++;
        }
        return image;
    }


    private String getID(){
        return "OPIBUILDER.COLORPROPERTY.ICON_"  //$NON-NLS-1$
            +colorValue.red+"_"+colorValue.green+"_"+colorValue.blue;  //$NON-NLS-1$ //$NON-NLS-2$
    }
    /**
     * Creates a small icon using the specified color.
     *
     * @param rgb
     *            the color
     * @return an icon
     */
    private Image createIcon(RGB rgb) {
        //System.out.println("OPIColor: create icon" + rgb);
        if(rgb == null)
             rgb = CustomMediaFactory.COLOR_BLACK;

        Color color = CustomMediaFactory.getInstance().getColor(rgb);

        // create new graphics context, to draw on
        Image image = new Image(Display.getCurrent(), 16, 16);
        GC gc = SingleSourceHelper.getImageGC(image);
        if(gc != null){
            // draw transparent background
            Color bg = CustomMediaFactory.getInstance().getColor(TRANSPARENT_COLOR);
            gc.setBackground(bg);
            gc.fillRectangle(0, 0, 16, 16);
            // draw icon
            gc.setBackground(color);
            Rectangle r = new Rectangle(1, 4, 14, 9);
            gc.fillRectangle(r);
            gc.setBackground(CustomMediaFactory.getInstance().getColor(0,
                            0, 0));
            gc.drawRectangle(r);
            gc.dispose();
        }
        ImageData imageData = image.getImageData();
        imageData.transparentPixel = imageData.palette.getPixel(TRANSPARENT_COLOR);
        image.dispose();
        return new Image(Display.getCurrent(), imageData);
    }

    @Override
    public String toString() {
        return getColorName();
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OPIColor other = (OPIColor) obj;
        if (colorName == null) {
            if (other.colorName != null)
                return false;
        } else if (!colorName.equals(other.colorName))
            return false;
        if (colorValue == null) {
            if (other.colorValue != null)
                return false;
        } else if (!colorValue.equals(other.colorValue))
            return false;
        return true;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if(obj instanceof OPIColor){
//            OPIColor input = (OPIColor)obj;
//            return colorName.equals(input.getColorName()) &&
//                colorValue.equals(input.getRGBValue());
//        }
//        return false;
//    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((colorName == null) ? 0 : colorName.hashCode());
        result = prime * result
                + ((colorValue == null) ? 0 : colorValue.hashCode());
        return result;
    }

    public OPIColor getCopy(){
        return new OPIColor(colorName, new RGB(colorValue.red, colorValue.green, colorValue.blue), preDefined);
    }

}
