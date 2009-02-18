package com.cosylab.vdct.graphics;

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
 * Creation date: (10.12.2000 13:07:53)
 * @author 
 */
public class BorderDecorator extends Decorator {
/**
 * Insert the method's description here.
 * Creation date: (10.12.2000 13:28:06)
 */
public BorderDecorator() {}
/**
 * Default implementation
 * Creation date: (10.12.2000 11:25:20)
 */
public void draw(Graphics g) {
	g.setColor(Color.gray);
	g.fillRect(0, 0,
			   getComponentWidth()-1,
			   getComponentHeight()-1);
	getComponent().draw(g);
	g.setColor(Color.black);
	g.drawRect(0, 0,
			   getComponentWidth()-1,
			   getComponentHeight()-1);
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 16:23:31)
 */
public int getComponentHeight() {
	if (getComponent()==null) return 0;
	else return getComponent().getComponentHeight()+10;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 16:23:02)
 * @return int
 */
public int getComponentWidth() {
	if (getComponent()==null) return 0;
	else return getComponent().getComponentWidth()+10;
}
/**
 * Default implementation
 * Creation date: (10.12.2000 11:26:54)
 */
public void resize(int x0, int y0, int width, int height) {
	getComponent().resize(x0+5, y0+5, width-10, height-10);
}
}
