
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

package org.csstudio.utility.screenshot.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Markus Moeller
 *
 */
public class ClipboardHandler
{
    private static ClipboardHandler instance = null;
    private Clipboard clipboard;

    private ClipboardHandler()
    {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public synchronized static ClipboardHandler getInstance()
    {
        if(instance == null)
        {
            instance = new ClipboardHandler();
        }

        return instance;
    }

    public boolean setClipboardImage(ImageData imageData)
    {
        BufferedImage bImage = null;
        boolean result = false;

        bImage = this.convertToAWT(imageData);
        TransferableImage tImage = new TransferableImage(bImage);

        try
        {
            clipboard.setContents(tImage, tImage);
            result = true;
        }
        catch(IllegalStateException ise)
        {
            result = false;
        }

        return result;
    }

    public Image getClipboardImage(Display display)
    {
        Transferable data = null;
        DataFlavor[] df = null;
        Image result = null;

        BufferedImage bImage = null;

        data = clipboard.getContents(null);

        // Get all data flavors
        df = data.getTransferDataFlavors();

        for (int i = 0; i < df.length; i++)
        {
            // Direkte Grafikinformationen
            if(df[i].isMimeTypeEqual(DataFlavor.imageFlavor))
            {
                try
                {
                    bImage = (BufferedImage)data.getTransferData(df[i]);

                    result = new Image(display, convertToSWT(bImage));

                    bImage = null;
                }
                catch(Exception e)
                {
                    result = null;
                }

                break;
            }
        }

        df = null;
        data = null;

        return result;
    }

    public boolean isImageAvailable()
    {
        Transferable data = null;
        DataFlavor[] df = null;
        boolean bImageAvailable = false;

        data = clipboard.getContents(null);

        // Get all data flavors
        df = data.getTransferDataFlavors();

        for(int i = 0; i < df.length; i++)
        {
            // Do we have an image in the clipboard?
            if(df[i].isMimeTypeEqual(DataFlavor.imageFlavor))
            {
                bImageAvailable = true;

                break;
            }
        }

        df = null;
        data = null;

        return bImageAvailable;
    }

    public void addListener(FlavorListener listener)
    {
        clipboard.addFlavorListener(listener);
    }

    public BufferedImage convertToAWT(ImageData data)
    {
        ColorModel colorModel = null;
        PaletteData palette = data.palette;

        if (palette.isDirect)
        {
            colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
            BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[3];
            for (int y = 0; y < data.height; y++)
            {
                for (int x = 0; x < data.width; x++)
                {
                    int pixel = data.getPixel(x, y);
                    RGB rgb = palette.getRGB(pixel);
                    pixelArray[0] = rgb.red;
                    pixelArray[1] = rgb.green;
                    pixelArray[2] = rgb.blue;
                    raster.setPixels(x, y, 1, 1, pixelArray);
                }
            }

            return bufferedImage;
        }
        else
        {
            RGB[] rgbs = palette.getRGBs();
            byte[] red = new byte[rgbs.length];
            byte[] green = new byte[rgbs.length];
            byte[] blue = new byte[rgbs.length];
            for (int i = 0; i < rgbs.length; i++)
            {
                RGB rgb = rgbs[i];
                red[i] = (byte) rgb.red;
                green[i] = (byte) rgb.green;
                blue[i] = (byte) rgb.blue;
            }

            if (data.transparentPixel != -1)
            {
                colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
            }
            else
            {
                colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
            }

            BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++)
            {
                for (int x = 0; x < data.width; x++)
                {
                    int pixel = data.getPixel(x, y);
                    pixelArray[0] = pixel;
                    raster.setPixel(x, y, pixelArray);
                }
            }

            return bufferedImage;
        }
    }

    public ImageData convertToSWT(BufferedImage bufferedImage)
    {
        if (bufferedImage.getColorModel() instanceof DirectColorModel)
        {
            DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[3];
            for (int y = 0; y < data.height; y++)
            {
                for (int x = 0; x < data.width; x++)
                {
                    raster.getPixel(x, y, pixelArray);
                    int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
                    data.setPixel(x, y, pixel);
                }
            }

            return data;
        }
        else if(bufferedImage.getColorModel() instanceof IndexColorModel)
        {
            IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];

            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);

            RGB[] rgbs = new RGB[size];
            for (int i = 0; i < rgbs.length; i++)
            {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
            }

            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for(int y = 0; y < data.height; y++)
            {
                for(int x = 0; x < data.width; x++)
                {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }

            return data;
        }

        return null;
    }
}
