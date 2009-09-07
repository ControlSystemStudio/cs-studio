package com.cosylab.vdct.db;

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

/**
 * This type was created in VisualAge.
 * Used only for layout data
 */
public class DBGroupData {
	protected int x = -1;			// used for layout
	protected int y = -1;
	protected java.awt.Color color;
	protected String description;
	protected String name;
/**
 * DBGroupData constructor comment.
 */
public DBGroupData(String name, int x, int y, java.awt.Color color, String description) {
	this.name=name;
	this.x=x; this.y=y;
	this.color=color;
	this.description=description;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:23:36)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
	return color;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:08:58)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:13:56)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:13:56)
 * @return int
 */
public int getX() {
	return x;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:13:56)
 * @return int
 */
public int getY() {
	return y;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 17:23:36)
 * @param newColor java.awt.Color
 */
public void setColor(java.awt.Color newColor) {
	color = newColor;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:08:58)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:13:56)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:13:56)
 * @param newX int
 */
public void setX(int newX) {
	x = newX;
}
/**
 * Insert the method's description here.
 * Creation date: (9.12.2000 17:13:56)
 * @param newY int
 */
public void setY(int newY) {
	y = newY;
}
}
