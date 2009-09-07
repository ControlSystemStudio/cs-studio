package com.cosylab.vdct.graphics.printing;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;

/**
 * Insert the type's description here.
 * Creation date: (12.5.2001 15:24:22)
 * @author Matej Sekoranja
 */

public class PagePreview extends javax.swing.JPanel {
	protected int width;
	protected int height;
	protected Image source;
	protected Image image;
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:53:36)
 * @param w int
 * @param h int
 * @param source java.awt.Image
 */
public PagePreview(int w, int h, Image source) {
	width = w;
	height = h;
	this.source = source;
	image = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	image.flush();
	setBackground(Color.white);
	setBorder(new javax.swing.border.MatteBorder(1, 1, 2, 2, Color.black));
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public Dimension getMainimumSize()
{
	return getPreferredSize();
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public Dimension getMaximumSize()
{
	return getPreferredSize();
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public Dimension getPreferredSize()
{
	Insets ins = getInsets();
	return new Dimension(width+ins.left+ins.right,
						 height+ins.top+ins.bottom);
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:55:17)
 * @param g java.awt.Graphics
 */
public void paint(Graphics g) {
	g.setColor(getBackground());
	g.fillRect(0, 0, getWidth(), getHeight());
	g.drawImage(image, 0, 0, this);
	paintBorder(g);
}
/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:52:04)
 * @param w int
 * @param h int
 */
public void setScaledSize(int w, int h) {
	width = w;
	height = h;
	image = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	repaint();
}
}
