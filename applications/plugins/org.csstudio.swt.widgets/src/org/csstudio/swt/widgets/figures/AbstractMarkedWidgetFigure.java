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
	
	protected double loloLevel;
	
	protected double loLevel;
	
	protected double hiLevel;
	
	protected double hihiLevel;
	
	protected boolean showLolo;
	
	protected boolean showLo;
	
	protected boolean showHi;
	
	protected boolean showHihi;
	
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
		this.hiColor = hiColor;
		repaint();
	}


	/**
	 * @param hihiColor the hihiColor to set
	 */
	public void setHihiColor(Color hihiColor) {
		this.hihiColor = hihiColor;
		repaint();
	}


	/**
	 * @param hihiLevel the hihiLevel to set
	 */
	public void setHihiLevel(final double hihiLevel) {
		this.hihiLevel = hihiLevel;
		repaint();
	}



	/**
	 * @param hiLevel the hiLevel to set
	 */
	public void setHiLevel(final double hiLevel) {
		this.hiLevel = hiLevel;
		repaint();
	}



	/**
	 * @param loColor the loColor to set
	 */
	public void setLoColor(Color loColor) {
		this.loColor = loColor;
		repaint();
	}





	/**
	 * @param loLevel the loLevel to set
	 */
	public void setLoLevel(final double loLevel) {
		this.loLevel = loLevel;
		repaint();
	}



	/**
	 * @param loloColor the loloColor to set
	 */
	public void setLoloColor(Color loloColor) {
		this.loloColor = loloColor;
		repaint();
	}





	/**
	 * @param loloLevel the loloLevel to set
	 */
	public void setLoloLevel(final double loloLevel) {
		this.loloLevel = loloLevel;
		repaint();
	}



	/**
	 * @param showHi the showHi to set
	 */
	public void setShowHi(boolean showHi) {
		this.showHi = showHi;
		repaint();
	}



	/**
	 * @param showHihi the showHihi to set
	 */
	public void setShowHihi(boolean showHihi) {		
		this.showHihi = showHihi;
		repaint();
	}



	/**
	 * @param showLo the showLo to set
	 */
	public void setShowLo(boolean showLo) {
		this.showLo = showLo;
		repaint();
	}



	/**
	 * @param showLolo the showLolo to set
	 */
	public void setShowLolo(boolean showLolo) {
		this.showLolo = showLolo;
		repaint();
	}



	/**
	 * @param showMarkers the showMarkers to set
	 */
	public void setShowMarkers(final boolean showMarkers) {
		this.showMarkers = showMarkers;
		repaint();
	}

}
