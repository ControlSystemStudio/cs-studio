package org.csstudio.sds.components.ui.internal.figures;


import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figureparts.LinearScale;
import org.csstudio.sds.components.ui.internal.figureparts.LinearScaledMarker;
import org.eclipse.swt.graphics.RGB;


/**
 * Abstract linear marked figure. The figure also includes a scale.
 * The marker and scale have been created in the constructor.
 * @author Xihui Chen
 *
 */
public class AbstractLinearMarkedFigure extends AbstractMarkedWidgetFigure {	

	private static final String HIHI = "HIHI";
	private static final String HI = "HI";
	private static final String LO = "LO";
	private static final String LOLO = "LOLO";
	protected LinearScaledMarker marker;

	
	public AbstractLinearMarkedFigure() {
		scale = new LinearScale();
		marker = new LinearScaledMarker((LinearScale) scale);	
		marker.addMarkerElement(LOLO, loloLevel, CustomMediaFactory.COLOR_RED);
		marker.addMarkerElement(LO, loLevel, CustomMediaFactory.COLOR_ORANGE);
		marker.addMarkerElement(HI, hiLevel, CustomMediaFactory.COLOR_ORANGE);
		marker.addMarkerElement(HIHI, hihiLevel, CustomMediaFactory.COLOR_RED);
	}
	
	@Override
	public void setShowMarkers(boolean showMarkers) {		
		super.setShowMarkers(showMarkers);
		marker.setVisible(showMarkers);	
	}
	
	@Override
	public void setLoloLevel(double loloLevel) {
		super.setLoloLevel(loloLevel);
		marker.setMarkerElementValue(LOLO, loloLevel);
	}
	
	@Override
	public void setLoLevel(double loLevel) {
		super.setLoLevel(loLevel);
		marker.setMarkerElementValue(LO, loLevel);
	}
	
	@Override
	public void setHiLevel(double hiLevel) {		
		super.setHiLevel(hiLevel);
		marker.setMarkerElementValue(HI, hiLevel);
	}
	
	@Override
	public void setHihiLevel(double hihiLevel) {
		super.setHihiLevel(hihiLevel);
		marker.setMarkerElementValue(HIHI, hihiLevel);
	}
	
	@Override
	public void setShowLolo(boolean showLolo) {
		super.setShowLolo(showLolo);		
		if(showLolo)
			if(loloColor != null)
				marker.addMarkerElement(LOLO, loloLevel, loloColor.getRGB());
			else
				marker.addMarkerElement(LOLO, loloLevel);
		else
			marker.removeMarkerElement(LOLO);
		revalidate();
	}
	
	@Override
	public void setShowLo(boolean showLo) {
		super.setShowLo(showLo);		
		if(showLo)
			if(loColor != null)
				marker.addMarkerElement(LO, loLevel, loColor.getRGB());
			else
				marker.addMarkerElement(LO, loLevel);
		else
			marker.removeMarkerElement(LO);
		revalidate();

	}
	
	@Override
	public void setShowHi(boolean showHi) {
		super.setShowHi(showHi);		
		if(showHi)
			if(hiColor != null)
				marker.addMarkerElement(HI, hiLevel, hiColor.getRGB());
			else
				marker.addMarkerElement(HI, hiLevel);			
		else
			marker.removeMarkerElement(HI);
		revalidate();
	}
	
	@Override
	public void setShowHihi(boolean showHihi) {
		super.setShowHihi(showHihi);		
		if(showHihi)
			if(hihiColor != null)
				marker.addMarkerElement(HIHI, hihiLevel, hihiColor.getRGB());
			else
				marker.addMarkerElement(HIHI, hihiLevel);
		else
			marker.removeMarkerElement(HIHI);
		revalidate();
	}	
	
	@Override
	public void setLoloColor(RGB color) {
		super.setLoloColor(color);	
		marker.setMarkerElementColor(LOLO, color);
	}
	
	@Override
	public void setLoColor(RGB color) {
		super.setLoColor(color);	
		marker.setMarkerElementColor(LO, color);
	}
	
	@Override
	public void setHiColor(RGB color) {
		super.setHiColor(color);	
		marker.setMarkerElementColor(HI, color);
	}
	
	@Override
	public void setHihiColor(RGB color) {
		super.setHihiColor(color);	
		marker.setMarkerElementColor(HIHI, color);
	}
	
	@Override
	public void setMaximum(double maximum) {
		super.setMaximum(maximum);
		marker.setDirty(true);
	}
	
	@Override
	public void setMinimum(double minimum) {
		super.setMinimum(minimum);
		marker.setDirty(true);
	}
	
	@Override
	public void setLogScale(boolean logScale) {
		super.setLogScale(logScale);
		marker.setDirty(true);
	}
}
