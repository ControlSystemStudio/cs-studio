/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import org.eclipse.swt.graphics.Color;

/**
 * Base figure for a widget based on {@link AbstractMarkedWidgetModel}.
 * 
 * @author Xihui Chen
 *
 */
public abstract class AbstractMarkedWidgetFigure extends AbstractScaledWidgetFigure {

	protected boolean showMarkers;
	
	protected double loloLevel = 10;
	
	protected double loLevel = 25;
	
	protected double hiLevel = 75;
	
	protected double hihiLevel = 90;
	
	protected boolean showLolo = true;
	
	protected boolean showLo = true;
	
	protected boolean showHi = true;
	
	protected boolean showHihi = true;
	
	protected Color loloColor;
	
	protected Color loColor;
	
	protected Color hiColor;
	
	protected Color hihiColor;	
	
	/**
	 * @return the hiColor
	 */
	public Color getHiColor() {
		return hiColor;
	}

	

	/**
	 * @return the hihiColor
	 */
	public Color getHihiColor() {
		return hihiColor;
	}

	/**
	 * @return the hihiLevel
	 */
	public double getHihiLevel() {
		return hihiLevel;
	}

	/**
	 * @return the hiLevel
	 */
	public double getHiLevel() {
		return hiLevel;
	}

	/**
	 * @return the loColor
	 */
	public Color getLoColor() {
		return loColor;
	}
	
	/**
	 * @return the loLevel
	 */
	public double getLoLevel() {
		return loLevel;
	}

	/**
	 * @return the loloColor
	 */
	public Color getLoloColor() {
		return loloColor;
	}

	/**
	 * @return the loloLevel
	 */
	public double getLoloLevel() {
		return loloLevel;
	}

	/**
	 * @return the showHi
	 */
	public boolean isShowHi() {
		return showHi;
	}

	/**
	 * @return the showHihi
	 */
	public boolean isShowHihi() {
		return showHihi;
	}

	/**
	 * @return the showLo
	 */
	public boolean isShowLo() {
		return showLo;
	}

	/**
	 * @return the showLolo
	 */
	public boolean isShowLolo() {
		return showLolo;
	}

	/**
	 * @return the showMarkers
	 */
	public boolean isShowMarkers() {
		return showMarkers;
	}

	/**
	 * @param hiColor the hiColor to set
	 */
	public void setHiColor(Color hiColor) {
		if(this.hiColor != null && this.hiColor.equals(hiColor))
			return;
		this.hiColor = hiColor;
		repaint();
	}


	/**
	 * @param hihiColor the hihiColor to set
	 */
	public void setHihiColor(Color hihiColor) {
		if(this.hihiColor != null && this.hihiColor.equals(hihiColor))
			return;
		this.hihiColor = hihiColor;
		repaint();
	}


	/**
	 * @param hihiLevel the hihiLevel to set
	 */
	public void setHihiLevel(final double hihiLevel) {
		if(this.hihiLevel == hihiLevel)
			return;
		this.hihiLevel = hihiLevel;
		repaint();
	}



	/**
	 * @param hiLevel the hiLevel to set
	 */
	public void setHiLevel(final double hiLevel) {
		if(this.hiLevel == hiLevel)
			return;
		this.hiLevel = hiLevel;
		repaint();
	}



	/**
	 * @param loColor the loColor to set
	 */
	public void setLoColor(Color loColor) {
		if(this.loColor != null && this.loColor.equals(loColor))
			return;
		this.loColor = loColor;
		repaint();
	}





	/**
	 * @param loLevel the loLevel to set
	 */
	public void setLoLevel(final double loLevel) {
		if(this.loLevel == loLevel)
			return;
		this.loLevel = loLevel;
		repaint();
	}



	/**
	 * @param loloColor the loloColor to set
	 */
	public void setLoloColor(Color loloColor) {
		if(this.loloColor != null && this.loloColor.equals(loloColor))
			return;
		this.loloColor = loloColor;
		repaint();
	}





	/**
	 * @param loloLevel the loloLevel to set
	 */
	public void setLoloLevel(final double loloLevel) {
		if(this.loloLevel == loloLevel)
			return;
		this.loloLevel = loloLevel;
		repaint();
	}



	/**
	 * @param showHi the showHi to set
	 */
	public void setShowHi(boolean showHi) {
		if(this.showHi == showHi)
			return;
		this.showHi = showHi;
		repaint();
	}



	/**
	 * @param showHihi the showHihi to set
	 */
	public void setShowHihi(boolean showHihi) {		
		if(this.showHihi == showHihi)
			return;
		this.showHihi = showHihi;
		repaint();
	}



	/**
	 * @param showLo the showLo to set
	 */
	public void setShowLo(boolean showLo) {
		if(this.showLo == showLo)
			return;
		this.showLo = showLo;
		repaint();
	}



	/**
	 * @param showLolo the showLolo to set
	 */
	public void setShowLolo(boolean showLolo) {
		if(this.showLolo == showLolo)
			return;
		this.showLolo = showLolo;
		repaint();
	}



	/**
	 * @param showMarkers the showMarkers to set
	 */
	public void setShowMarkers(final boolean showMarkers) {
		if(this.showMarkers == showMarkers)
			return;
		this.showMarkers = showMarkers;
		repaint();
	}

}
