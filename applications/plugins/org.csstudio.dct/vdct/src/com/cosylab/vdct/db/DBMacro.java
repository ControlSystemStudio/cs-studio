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

import com.cosylab.vdct.graphics.objects.InLink;
import com.cosylab.vdct.inspector.InspectableProperty;

/**
 * @author Matej
 */
public class DBMacro extends DBComment
{
	protected String name = null;
	protected String description = null;

	protected boolean hasVisual = false;
	protected int x = -1;			// used for layout
	protected int y = -1;
	protected boolean isNamePositionNorth = true;
	protected int mode = InLink.INPUT_MACRO_MODE;
	protected int defaultVisibility = InspectableProperty.UNDEFINED_VISIBILITY;
	protected java.awt.Color color = java.awt.Color.black;
	
	/**
	 * Insert the method's description here.
	 */
	public DBMacro(String name)
	{
		this.name = name;
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
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name)
	{
		this.name = name;
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
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Returns the color.
	 * @return java.awt.Color
	 */
	public java.awt.Color getColor()
	{
		return color;
	}

	/**
	 * Returns the hasVisual.
	 * @return boolean
	 */
	public boolean isHasVisual()
	{
		return hasVisual;
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
	 * Returns the y.
	 * @return int
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Sets the color.
	 * @param color The color to set
	 */
	public void setColor(java.awt.Color color)
	{
		this.color = color;
	}

	/**
	 * Sets the hasVisual.
	 * @param hasVisual The hasVisual to set
	 */
	public void setHasVisual(boolean hasVisual)
	{
		this.hasVisual = hasVisual;
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
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * Returns the mode.
	 * @return int
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * Sets the mode.
	 * @param mode The mode to set
	 */
	public void setMode(int mode)
	{
		this.mode = mode;
	}

	/**
	 * Returns the defaultVisibility.
	 * @return int
	 */
	public int getDefaultVisibility()
	{
		return defaultVisibility;
	}

	/**
	 * Sets the defaultVisibility.
	 * @param defaultVisibility The defaultVisibility to set
	 */
	public void setDefaultVisibility(int defaultVisibility)
	{
		this.defaultVisibility = defaultVisibility;
	}


    /**
     * Sets the position of the text (if namePositionNorth is true, text is on the top
     * of the macro otherwise it is on the side).
     * @param namePositionNorth
     */
    public void setNamePositionNorth(boolean namePositionNorth) {
        this.isNamePositionNorth = namePositionNorth;
        
    }
    
    /**
     * 
     * Returns the position of the text.
     * @return
     */
    public boolean isNamePositionNorth() {
        return isNamePositionNorth;
    }

}
