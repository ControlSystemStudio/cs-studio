package org.csstudio.sds.components.ui.internal.figureparts;


import java.util.LinkedHashMap;

import java.util.Map;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figureparts.AbstractScale.LabelSide;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.RGB;


/**
 * A linear scale related marker, whose orientation, range, mark position etc. are 
 * determined by the scale. It must have the same length and bounds.x(for horizontal) 
 * or bounds.y(for vertical) with the scale.
 * 
 * @author Xihui Chen
 */
public class LinearScaledMarker extends Figure {
	
	private Map<String, MarkerProperties> markersMap= new LinkedHashMap<String, MarkerProperties>();
	
	private static final RGB DEFAULT_MARKER_COLOR = CustomMediaFactory.COLOR_RED;
	
	private String[] labels;
	
	private double[] markerValues;	
	
	private RGB[] markerColors;
	
	private LinearScale scale;
	
	private LabelSide makerLablesPosition = LabelSide.Secondary;
	
	private boolean markerLineVisible = false;
	
	private boolean markerLableVisible = true;

	private int tickLabelMaxLength;
	
	private boolean dirty = true;
	
	private final static int TICK_LENGTH = 10;
	private final static int TICK_LINE_WIDTH = 2;
	private final static int GAP_BTW_MARK_LABEL = 3;
	
	
	public LinearScaledMarker(LinearScale scale) {
		this.scale = scale;
		setFont(CustomMediaFactory.getInstance().getFont(CustomMediaFactory.FONT_TAHOMA));
	}
	
	/**
	 * Add (if the marker does not exist) or change a marker element.
	 * @param label the label of the marker element, it must be unique. 
	 * @param value the value of the marker element
	 * @param color the color of the marker element
	 */
	public void putMarkerElement(String label, double value, RGB color) {
		if(markersMap.containsKey(label)) {
			markersMap.get(label).value = value;
			markersMap.get(label).color = color;
		}else
			markersMap.put(label, new MarkerProperties(value, color));		
		dirty =true;
	}	
	
	/**
	 * Add (if the marker does not exist) or change a marker element.
	 * @param label the label of the marker element, it must be unique. 
	 * @param value the value of the marker element
	 */
	public void putMarkerElement(String label, double value) {
		if(markersMap.containsKey(label))
			markersMap.get(label).value = value;
		else
			markersMap.put(label, new MarkerProperties(value, DEFAULT_MARKER_COLOR));
		
		dirty =true;
	}
	
	public void removeMarkerElement(String label) {
		markersMap.remove(label);
		dirty =true;
	}	
	
	
	@Override
	protected void paintFigure(Graphics graphics) {
		//use relative coordinate
		graphics.translate(bounds.x, bounds.y);
		updateTick();
		drawMarkerTick(graphics);
		super.paintFigure(graphics);
	}
	
	private void drawMarkerTick(Graphics graphics) {
		graphics.pushState();
		graphics.setLineWidth(TICK_LINE_WIDTH);
		if(scale.isHorizontal()) {
			if(makerLablesPosition == LabelSide.Primary) {
				int i = 0;
				for(int markerPos : getMarkerPositions()) {
					graphics.setForegroundColor(
							CustomMediaFactory.getInstance().getColor(markerColors[i]));
					graphics.drawLine(markerPos, 0, markerPos, TICK_LENGTH);
					//draw labels
					if(isMarkerLableVisible()) {
						graphics.drawText(labels[i], 
								markerPos-FigureUtilities.getTextExtents(labels[i], getFont()).width/2, 
								TICK_LENGTH + GAP_BTW_MARK_LABEL);
					}					
					i++;
				}
			} else {
				int i = 0;
				for(int markerPos : getMarkerPositions()) {
					graphics.setForegroundColor(
							CustomMediaFactory.getInstance().getColor(markerColors[i]));
					graphics.drawLine(markerPos, getClientArea().height, 
							markerPos, getClientArea().height - TICK_LENGTH);
					//draw labels
					if(isMarkerLableVisible()) {
						graphics.drawText(labels[i], 
								markerPos-FigureUtilities.getTextExtents(labels[i], getFont()).width/2, 
								getClientArea().height - TICK_LENGTH - GAP_BTW_MARK_LABEL
								- FigureUtilities.getTextExtents(labels[i], getFont()).height);
					}
					i++;
				}
			}		
		} else {
			if(makerLablesPosition == LabelSide.Primary) {
				int i = 0;
				for(int markerPos : getMarkerPositions()) {
					graphics.setForegroundColor(
							CustomMediaFactory.getInstance().getColor(markerColors[i]));
					graphics.drawLine(getClientArea().width, markerPos, 
							getClientArea().width - TICK_LENGTH, markerPos);
					//draw labels
					if(isMarkerLableVisible()) {
						graphics.drawText(labels[i], 
								getClientArea().width - TICK_LENGTH - GAP_BTW_MARK_LABEL
								- FigureUtilities.getTextExtents(labels[i], getFont()).width,
								markerPos-FigureUtilities.getTextExtents(labels[i], getFont()).height/2
								);
					}
					i++;
				}
			} else {
				int i = 0;
				for(int markerPos : getMarkerPositions()) {
					graphics.setForegroundColor(
							CustomMediaFactory.getInstance().getColor(markerColors[i]));
					graphics.drawLine(0, markerPos, 
							TICK_LENGTH, markerPos);
					
					//draw labels
					if(isMarkerLableVisible()) {
						graphics.drawText(labels[i], 
								TICK_LENGTH + GAP_BTW_MARK_LABEL,
								markerPos-FigureUtilities.getTextExtents(labels[i], getFont()).height/2
								);
					}
					
					i++;
				}
			}
		}
		graphics.popState();
			
	}
	
	private int[] getMarkerPositions(){
		int[] markerPositions = new int[markerValues.length];
		int i=0;
		for(double value : markerValues) {
			markerPositions[i] = scale.getValuePosition(value, true);
			i++;
		}
		return markerPositions;		
	}
	/**
     * Updates the tick, recalculate all inner parameters
     */
	public void updateTick() {
		if(dirty == true) {
			updateMarkerElments();
			updateTickLabelMaxLength();
		}
		dirty = false;		
	}
	
    /**
     * Gets max length of tick label.
     */
    private void updateTickLabelMaxLength() {
        int maxLength = 0;
        
        for (int i = 0; i < labels.length; i++) {
                Dimension p = FigureUtilities.getTextExtents(labels[i], scale.getFont());
                if (p.width > maxLength) {
                    maxLength = p.width;
                }
            }
        
        tickLabelMaxLength = maxLength;
    }


	/**
	 * @return the labels
	 */
	public String[] getLabels() {
		String[] labels = new String[markersMap.size()];
		int i=0;
		for(String label : markersMap.keySet()) {
			labels[i] = label;
			i++;
		}
		return labels;
	}


	/**
	 * @return the markerValues
	 */
	public void updateMarkerElments() {
		labels = new String[markersMap.size()];
		markerColors = new RGB[markersMap.size()];
		markerValues = new double[markersMap.size()];
		int i = 0;
		for(String label : markersMap.keySet()) {
			labels[i] = label;
			markerValues[i] = markersMap.get(label).value;
			markerColors[i] = markersMap.get(label).color;
			i++;
		}
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(LinearScale scale) {
		this.scale = scale;
		dirty =true;
	}

	/**
	 * @return the scale
	 */
	public LinearScale getScale() {
		return scale;
	}

	/**
	 * @param labelSide the makerLablesPosition to set
	 */
	public void setLabelSide(LabelSide labelSide) {
		this.makerLablesPosition = labelSide;
		dirty =true;
	}

	/**
	 * @return the makerLablesPosition
	 */
	public LabelSide getMakerLablesPosition() {
		return makerLablesPosition;
	}

	/**
	 * @param markerLineVisible the markerLineVisible to set
	 */
	public void setMarkerLineVisible(boolean markerLineVisible) {
		this.markerLineVisible = markerLineVisible;
		dirty = true;
	}

	/**
	 * @return the markerLineVisible
	 */
	public boolean isMarkerLineVisible() {
		return markerLineVisible;
	}

	/**
	 * @param markerLableVisible the markerLableVisible to set
	 */
	public void setMarkerLableVisible(boolean markerLableVisible) {
		this.markerLableVisible = markerLableVisible;
		dirty =true;
	}

	/**
	 * @return the markerLableVisible
	 */
	public boolean isMarkerLableVisible() {
		return markerLableVisible;
	}
	
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		updateTick();
		Dimension size = new Dimension(wHint, hHint);
		
		if(scale.isHorizontal()) {
			size.width = scale.getSize().width;
			size.height = (int)Math.ceil(FigureUtilities.getTextExtents("dummy", getFont()).height) 
							+ GAP_BTW_MARK_LABEL + TICK_LENGTH;
		} else {
			updateTickLabelMaxLength();
			size.width = (int)tickLabelMaxLength + GAP_BTW_MARK_LABEL + TICK_LENGTH;	
			size.height = scale.getSize().height;
		}			
		return size;
		
	}
	
	class MarkerProperties {		

		private double value;

		private RGB color;
		
		/**
		 * @param value
		 * @param color
		 */
		public MarkerProperties(double value, RGB color) {
			this.value = value;
			this.color = color;
		}		
	}
	

}
