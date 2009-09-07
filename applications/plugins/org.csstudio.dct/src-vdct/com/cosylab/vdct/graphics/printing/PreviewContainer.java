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

public class PreviewContainer extends javax.swing.JPanel {
	protected int H_GAP = 16;
	protected int V_GAP = 10;

/**
 * Insert the method's description here.
 * Creation date: (12.5.2001 16:43:10)
 */
public void doLayout() {
	Insets ins = getInsets();
	int x = ins.left + H_GAP;
	int y = ins.top + V_GAP;

	int n = getComponentCount();
	if (n == 0)
		return;

	Component comp = getComponent(0);
	Dimension dc = comp.getPreferredSize();
	int w = dc.width;
	int h = dc.height;

	Dimension dp = getParent().getSize();
	int nCol = Math.max((dp.width-H_GAP)/(w+H_GAP), 1);
	int nRow = n/nCol;
	if (nRow*nCol < n)
		nRow++;

	int index = 0;
	for (int k = 0; k < nRow; k++)
	{	
		for (int m = 0; m < nCol; m++)
		{
			if (index >= n)
				return;
			comp = getComponent(index++);
			comp.setBounds(x, y, w, h);
			x += w+H_GAP;
		}
		y += h+V_GAP;
		x = ins.left + H_GAP;
	}
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
	int n = getComponentCount();
	if (n == 0)
		return new Dimension(H_GAP, V_GAP);

	Component comp = getComponent(0);
	Dimension dc = comp.getPreferredSize();
	int w = dc.width;
	int h = dc.height;

	Dimension dp = getParent().getSize();
	int nCol = Math.max((dp.width-H_GAP)/(w+H_GAP), 1);
	int nRow = n/nCol;
	if (nRow*nCol < n)
		nRow++;

	int ww = nCol*(w+H_GAP) + H_GAP;
	int hh = nRow*(h+V_GAP) + V_GAP;
	Insets ins = getInsets();

	return new Dimension(ww+ins.left+ins.right,
						 hh+ins.top+ins.bottom);
	
}
}
