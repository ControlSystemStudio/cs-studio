/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

/**
 * Represent a buffered image. Use {@link ValueUtil#toImage(org.epics.pvmanager.data.VImage)}
 * and {@link ValueUtil#toVImage(java.awt.image.BufferedImage)} to convert objects
 * of this class to and from awt images.
 * <p>
 * The data is currently encoded as 3 bytes for each pixel (RGB). To read the
 * buffer in AWT use the following code:
 * </p>
 * <pre>
 * BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
 * System.arraycopy(buffer, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData(), 0, 100*100*3);
 * </pre>
 * <p>
 * To read in SWT:
 * </p>
 * <pre>
 * ImageData imageData = new ImageData(width, height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000), width*3, buffer);
 * Image image = new Image(gc.getDevice(), imageData);
 * </pre>
 * <p>
 * To prepare an image using Java2D:
 * </p>
 * <pre>
 * BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
 * Graphics2D g = image.createGraphics();
 * ...
 * byte[] buffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
 * </pre>
 * <p>
 * TODO: should be extended to support different encodings.
 *
 * @author carcassi
 */
public interface VImage extends VType {
    /**
     * Height of the image in pixels.
     *
     * @return image height
     */
    public int getHeight();

    /**
     * Width of the image in pixels.
     *
     * @return image width
     */
    public int getWidth();

    /**
     * Image buffer;
     *
     * @return image buffer
     */
    public byte[] getData();
}
