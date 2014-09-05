/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.epics.vtype.ValueUtil;

/**
 * A FileFormat for reading .bmp and .png into VImage
 * 
 * @author Kunal Shroff
 * 
 */
public class ImageFileFormat implements FileFormat {

    @Override
    public Object readValue(InputStream in) throws Exception {
	BufferedImage image = ImageIO.read(in);
	return ValueUtil.toVImage(image);
    }

    @Override
    public void writeValue(Object value, OutputStream out) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWriteSupported() {
	return false;
    }

}
