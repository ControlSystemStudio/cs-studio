package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

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
	 * @param showMarkers the showMarkers to set
	 */
	public void setShowMarkers(final boolean showMarkers) {
		this.showMarkers = showMarkers;
	}

	

	/**
	 * @param loloLevel the loloLevel to set
	 */
	public void setLoloLevel(final double loloLevel) {
		this.loloLevel = loloLevel;
	}

	/**
	 * @param loLevel the loLevel to set
	 */
	public void setLoLevel(final double loLevel) {
		this.loLevel = loLevel;
	}

	/**
	 * @param hiLevel the hiLevel to set
	 */
	public void setHiLevel(final double hiLevel) {
		this.hiLevel = hiLevel;
	}

	/**
	 * @param hihiLevel the hihiLevel to set
	 */
	public void setHihiLevel(final double hihiLevel) {
		this.hihiLevel = hihiLevel;
	}
	
	/**
	 * @param showLolo the showLolo to set
	 */
	public void setShowLolo(boolean showLolo) {
		this.showLolo = showLolo;
	}

	/**
	 * @param showLo the showLo to set
	 */
	public void setShowLo(boolean showLo) {
		this.showLo = showLo;
	}

	/**
	 * @param showHi the showHi to set
	 */
	public void setShowHi(boolean showHi) {
		this.showHi = showHi;
	}

	/**
	 * @param showHihi the showHihi to set
	 */
	public void setShowHihi(boolean showHihi) {		
		this.showHihi = showHihi;
	}

	/**
	 * @param loloColor the loloColor to set
	 */
	public void setLoloColor(RGB loloColor) {
		this.loloColor = CustomMediaFactory.getInstance().getColor(loloColor);
	}

	/**
	 * @param loColor the loColor to set
	 */
	public void setLoColor(RGB loColor) {
		this.loColor = CustomMediaFactory.getInstance().getColor(loColor);
	}

	/**
	 * @param hiColor the hiColor to set
	 */
	public void setHiColor(RGB hiColor) {
		this.hiColor = CustomMediaFactory.getInstance().getColor(hiColor);
	}

	/**
	 * @param hihiColor the hihiColor to set
	 */
	public void setHihiColor(RGB hihiColor) {
		this.hihiColor = CustomMediaFactory.getInstance().getColor(hihiColor);
	}

}
