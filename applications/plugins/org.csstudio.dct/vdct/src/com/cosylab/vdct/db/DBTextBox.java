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

import java.awt.Color;

public class DBTextBox {

	protected String name;
	protected int x;
	protected int y;
	protected int x2;
	protected int y2;
	protected boolean border;
	protected String fontName;
	protected int fontSize;
	protected int fontStyle;
	protected Color color;
	protected String description;
	protected String parentBorderID;
	
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 21:12:09)
 */
public DBTextBox(String name, int x, int y, int x2, int y2, boolean border, String fontName, int fontSize, 
				  int fontStyle, Color color, String description, String parentBorderID)
{
	this.name = name;
	this.x=x;
	this.y=y;
	this.x2=x2;
	this.y2=y2;
	this.border=border;
	this.fontName=fontName;
	this.fontSize=fontSize;
	this.fontStyle=fontStyle;
	this.color=color;
	this.description=description;
	this.parentBorderID = parentBorderID;
}

	/**
	 * Returns the color.
	 * @return Color
	 */
	public Color getColor()
	{
		return color;
	}


	/**
	 * Returns the dashed.
	 * @return boolean
	 */
	public boolean getBorder()
	{
		return border;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the x.
	 * @return int
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the x2.
	 * @return int
	 */
	public int getX2()
	{
		return x2;
	}

	/**
	 * Returns the y.
	 * @return int
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Returns the y2.
	 * @return int
	 */
	public int getY2()
	{
		return y2;
	}

	/**
	 * Sets the color.
	 * @param color The color to set
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}

	/**
	 * Sets the dashed.
	 * @param dashed The dashed to set
	 */
	public void setBorder(boolean border)
	{
		this.border = border;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Sets the x2.
	 * @param x2 The x2 to set
	 */
	public void setX2(int x2)
	{
		this.x2 = x2;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * Sets the y2.
	 * @param y2 The y2 to set
	 */
	public void setY2(int y2)
	{
		this.y2 = y2;
	}

	/**
	 * Returns the border.
	 * @return boolean
	 */
	public boolean isBorder()
	{
		return border;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Returns the fontName.
	 * @return String
	 */
	public String getFontName()
	{
		return fontName;
	}

	/**
	 * Returns the fontSize.
	 * @return int
	 */
	public int getFontSize()
	{
		return fontSize;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Sets the fontName.
	 * @param fontName The fontName to set
	 */
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}

	/**
	 * Sets the fontSize.
	 * @param fontSize The fontSize to set
	 */
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}

	/**
	 * Returns the fontStyle.
	 * @return int
	 */
	public int getFontStyle()
	{
		return fontStyle;
	}

	/**
	 * Sets the fontStyle.
	 * @param fontStyle The fontStyle to set
	 */
	public void setFontStyle(int fontStyle)
	{
		this.fontStyle = fontStyle;
	}

	/**
	 * @return Returns the parentBorderID.
	 */
	public String getParentBorderID() {
		return parentBorderID;
	}
	/**
	 * @param parentBorderID The parentBorderID to set.
	 */
	public void setParentBorderID(String parentBorderID) {
		this.parentBorderID = parentBorderID;
	}
}
