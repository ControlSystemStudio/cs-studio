/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.figures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * A bargraph figure.
 * 
 * @author Kai Meyer
 * 
 */
public final class RefreshableBargraphFigure extends RectangleFigure implements	IRefreshableFigure {
	
	/**
	 * Height of the text.
	 */
	private static final int TEXTHEIGHT = 24;
	
	/**
	 * Width of the text.
	 */
	private static final int TEXTWIDTH = 46;
	
	/**
	 * The Strings, which are displayed in this figure.
	 */
	private static final String[] LABELS = new String[] {"LOLO", "LO", "M", "HI", "HIHI"};
	
//	/**
//	 * Don't show markers.
//	 */
//	private static final int NONE = 0;
	/**
	 * Show markers at bottom or right.
	 */
	private static final int BOTTOM_RIGHT = 1;
	/**
	 * Show markers at top or left.
	 */
	private static final int TOP_LEFT = 2;
	
	/**
	 * Minimum value for this figure.
	 */
	private double _minimum = 0.0;
	
	/**
	 * Maximum value for this figure.
	 */
	private double _maximum = 1.0;
	
	/**
	 * The boolean, which indicates, if the values should be shown or not.
	 */
	private boolean _showValues = false;
	
	/**
	 * The int, which indicates, how the marks should be shown.
	 */
	private int _showMarks = BOTTOM_RIGHT;
	
	/**
	 * The int, which indicates, how the scale should be shown.
	 */
	private int _showScale = BOTTOM_RIGHT;
	
	/**
	 * The count of sections in the scale.
	 */
	private int _scaleSectionCount = 1;
	
	/**
	 * The default height of this figure.
	 */
	private static final int DEFAULT_HEIGHT = 30;
	
	/**
	 * The default width of this figure.
	 */
	private static final int DEFAULT_WIDTH = 200;
	
	/**
	 * The orientation (horizontal==true | vertical==false).
	 */
	private boolean _orientationHorizontal = true;
	
	/**
	 * The Color for the border.
	 */
	private Color _borderColor;
	
	/**
	 * The Map for the Colors.
	 */
	private final Map<String, Color> _colorMap = new HashMap<String, Color>();
	
	/**
	 * The Map for the levels.
	 */
	private final Map<String, Double> _levelMap = new HashMap<String, Double>();

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	/**
	 * The Scale of this figure.
	 */
	private Scale _scale;
	/**
	 * The MarkerPanel of this figure.
	 */
	private MarkerPanel _markerPanel;
	/**
	 * The FillRectangle of this figure.
	 */
	private FillRectangleFigure _fillRectangleFigure;
	/**
	 * The Rectangle for the FillRectangleFigure.
	 */
	private Rectangle _barRectangle;
	
	/**
	 * The wideness of the Scale.
	 */
	private int _scaleWideness = 10;
	
	/**
	 * Constructor.
	 *
	 */
	public RefreshableBargraphFigure() {
		super();
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.initColorMap();
		this.initLevelMap();
		this.setLayoutManager(new XYLayout());
		_fillRectangleFigure = new FillRectangleFigure();
		_markerPanel = new MarkerPanel(_showMarks==TOP_LEFT, _orientationHorizontal);
		_scale = new Scale();
		this.add(_fillRectangleFigure);
		this.add(_markerPanel);
		this.add(_scale);
		this.refreshConstraints();
		
//		 listen to figure movement events
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure source) {
				refreshConstraints();
			}
		});
	}
	
	/**
	 * Initializes the Map of Colors.
	 */
	private void initColorMap() {
		_colorMap.put(LABELS[0], CustomMediaFactory.getInstance().getColor(new RGB(255,0,0)));
		_colorMap.put(LABELS[1], CustomMediaFactory.getInstance().getColor(new RGB(255,100,100)));
		_colorMap.put(LABELS[2], CustomMediaFactory.getInstance().getColor(new RGB(0,255,0)));
		_colorMap.put(LABELS[3], CustomMediaFactory.getInstance().getColor(new RGB(255,255,0)));
		_colorMap.put(LABELS[4], CustomMediaFactory.getInstance().getColor(new RGB(255,255,255)));
	}
	
	/**
	 * Initializes the Map of levels.
	 */
	private void initLevelMap() {
		_levelMap.put(LABELS[0], Double.valueOf(0.1));
		_levelMap.put(LABELS[1], Double.valueOf(0.3));
		_levelMap.put(LABELS[2], Double.valueOf(0.5));
		_levelMap.put(LABELS[3], Double.valueOf(0.7));
		_levelMap.put(LABELS[4], Double.valueOf(0.9));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public synchronized void paintFigure(final Graphics graphics) {
		graphics.setBackgroundColor(this.getBackgroundColor());
		graphics.fillRectangle(this.getBounds());
		//this.refreshConstraints();
		graphics.setBackgroundColor(this.getBackgroundColor());
		graphics.setForegroundColor(this.getBorderColor());
		this.setToolTip(this.getToolTipFigure());
	}
	
	/**
	 * Gets the IFigure for the tooltip.
	 * @return IFigure
	 * 			The IFigure for the tooltip
	 */
	private IFigure getToolTipFigure() {
		Panel panel = new Panel();
		panel.setLayoutManager(new ToolbarLayout(false));
		panel.add(new Label("Fill level: "+this.getFill()+"%"));
		panel.add(new Label("Minimum value: "+_minimum));
		panel.add(new Label("Maximum value: "+_maximum));
		for (int i=0;i<LABELS.length;i++) {
			panel.add(new Label(LABELS[i]+" level: "+_levelMap.get(LABELS[i])));
		}
		panel.setBackgroundColor(ColorConstants.tooltipBackground);
		return panel;
	}
	
	/**
	 * Refreshes the Constraints.
	 */
	private void refreshConstraints() {
		_barRectangle = this.getBarRectangle();
		this.setConstraint(_fillRectangleFigure, _barRectangle);
		this.setConstraint(_markerPanel, this.getMarkerPanelConstraint(this.getBounds()));
		this.setConstraint(_scale, this.getScaleConstraint(this.getBounds()));
	}
	
	/**
	 * Gets the Constraints for the MarkerPanel.
	 * @param bounds
	 * 				The bounds for the MarkerPanel
	 * @return The constraints for the MarkerPanel
	 */
	private Rectangle getMarkerPanelConstraint(final Rectangle bounds) {
		if (_orientationHorizontal) {
			_markerPanel.setReferencePositions(_barRectangle.x, _barRectangle.x+_barRectangle.width);
			if (_showMarks==BOTTOM_RIGHT) {
				return new Rectangle(1,bounds.height-TEXTHEIGHT,bounds.width-2,TEXTHEIGHT-2);
			}
			if (_showMarks==TOP_LEFT) {
				return new Rectangle(1,1,bounds.width-2,TEXTHEIGHT-1);
			}
		} else {
			_markerPanel.setReferencePositions(_barRectangle.y, _barRectangle.y+_barRectangle.height);
			if (_showMarks==BOTTOM_RIGHT) {
				return new Rectangle(_barRectangle.width,1,TEXTWIDTH-1,bounds.height-2);
			}
			if (_showMarks==TOP_LEFT) {
				return new Rectangle(1,1,TEXTWIDTH-1,bounds.height-2);
			}
		}
		return new Rectangle(0,0,0,0);
	}
	
	/**
	 * Gets the constraints for the Scale.
	 * @param bounds
	 * 					The bounds for the Scale 
	 * @return Rectangle
	 * 					The Constraints for the Scale
	 */
	private Rectangle getScaleConstraint(final Rectangle bounds) {
		_scale.setHorizontalOrientation(_orientationHorizontal);
		if (_orientationHorizontal) {
			_scale.setLength(_barRectangle.width);
			_scale.setReferencePositions(_barRectangle.x);
			if (_showScale==BOTTOM_RIGHT) {
				return new Rectangle(0,_barRectangle.y+_barRectangle.height-_scaleWideness-1,bounds.width,_scaleWideness);
			}
			if (_showScale==TOP_LEFT) {
				return new Rectangle(0,_barRectangle.y,bounds.width,_scaleWideness);
			}
		} else {
			_scale.setLength(_barRectangle.height);
			_scale.setReferencePositions(_barRectangle.y);
			if (_showScale==BOTTOM_RIGHT) {
				return new Rectangle(_barRectangle.x+_barRectangle.width-_scaleWideness,0,_scaleWideness,bounds.height);
			}
			if (_showScale==TOP_LEFT) {
				return new Rectangle(_barRectangle.x,0,_scaleWideness,bounds.height);
			}
		}
		return new Rectangle(0,0,0,0);
	}
	
	/**
	 * Calculate the real length of this bargraph.
	 * The value is calculated, to fit the scale completly into the bargraph
	 * @param length
	 * 					The given length
	 * @return int 
	 * 					The new length
	 */
	private int calculateRealLength(final int length) {
		int neededScaleLines = _scaleSectionCount + 1;
		return length - ((length - neededScaleLines) % _scaleSectionCount);
	}
	
	/**
	 * Gets the rectangle for the bargraph.
	 * @return Rectangle
	 * 				The rectangle for the bargraph
	 */
	private Rectangle getBarRectangle() {
		Rectangle bounds = this.getBounds();
		int yCorrection = 0;
		int heightCorrection = 0;
		int xCorrection = 0;
		int widthCorrection = 0;
		if (_orientationHorizontal) {
			if (_showMarks==TOP_LEFT) {
				yCorrection = TEXTHEIGHT;
				xCorrection = (TEXTWIDTH/2);
			}
			if (_showMarks==BOTTOM_RIGHT) {
				heightCorrection = TEXTHEIGHT;
				xCorrection = (TEXTWIDTH/2);
			}
//			if (_showValues) {
//				yCorrection = yCorrection*2;
//				heightCorrection = heightCorrection*2;
//			}
			return new Rectangle(xCorrection,yCorrection,this.calculateRealLength(bounds.width-2*xCorrection),bounds.height-(yCorrection+heightCorrection));
		}
		if (_showMarks==TOP_LEFT) {	
			xCorrection = TEXTWIDTH;
			yCorrection = TEXTHEIGHT/2;
		}
		if (_showMarks==BOTTOM_RIGHT) {
			widthCorrection = TEXTWIDTH;
			yCorrection = TEXTHEIGHT/2;
		}
//		if (_showValues) {
//			xCorrection = xCorrection*2-10;
//			widthCorrection = widthCorrection*2;
//		}
		return new Rectangle(xCorrection,yCorrection,bounds.width-(xCorrection+widthCorrection),this.calculateRealLength(bounds.height-2*yCorrection));
	}
	
	/**
	 * Gets the weight (0.0 - 1.0) for the value.
	 * @param value
	 * 					The value, which weight should be calculated.
	 * @return double
	 * 					The weight for the value
	 */
	private double getWeight(final double value) {
		double max = _maximum-_minimum;
		if (max==0) {
			max = 0.001;
		}
		return (value-_minimum) / max; 
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
		this.setFill(Math.random() * 100);
		repaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBackgroundColor(final Color bg) {
		_markerPanel.setBackgroundColor(bg);
		super.setBackgroundColor(bg);
	}
	
	/**
	 * Sets the color for the border.
	 * @param borderRGB
	 * 				The RGB-value of the Color for the border
	 */
	public void setBorderColor(final RGB borderRGB) {
		_borderColor = CustomMediaFactory.getInstance().getColor(borderRGB);
		_fillRectangleFigure.setBorderColor(_borderColor);
	}
	
	/**
	 * Gets the color for the border.
	 * @return Color
	 * 				The color for the border
	 */
	public Color getBorderColor() {
		return _borderColor;
	}
	
	/**
	 * Sets the default fill Color.
	 * @param defaultFillRGB
	 * 				The RGB-value of the default fill Color
	 */
	public void setDefaultFillColor(final RGB defaultFillRGB) {
		_fillRectangleFigure.setDefaultFillColor(CustomMediaFactory.getInstance().getColor(defaultFillRGB));
	}
	
	/**
	 * Gets the default fill Color.
	 * @return Color
	 * 				The color default fill Color
	 */
	public Color getDefaultFillColor() {
		return _fillRectangleFigure.getDefaultFillColor();
	}
	
	/**
	 * Sets the color for the backgrounf of the fill-area.
	 * @param fillBackgroundRGB
	 * 				The RGB-value of the Color for the backgrounf of the fill-area
	 */
	public void setFillBackgroundColor(final RGB fillBackgroundRGB) {
		_fillRectangleFigure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(fillBackgroundRGB));
	}
	
	/**
	 * Gets the color for the backgrounf of the fill-area.
	 * @return Color
	 * 				The color for the backgrounf of the fill-area
	 */
	public Color getFillBackgroundColor() {
		return _fillRectangleFigure.getBackgroundColor();
	}
	
	/**
	 * Sets the fill grade.
	 * 
	 * @param fill
	 *            The fill grade.
	 */
	public void setFill(final double fill) {
		_fillRectangleFigure.setFill(fill/100);
	}

	/**
	 * Gets the fill grade.
	 * 
	 * @return double
	 * 				The fill grade
	 */
	public double getFill() {
		return _fillRectangleFigure.getFill();
	}
	
	/**
	 * Sets the orientation (horizontal==true | vertical==false).
	 * 
	 * @param horizontal
	 *            The orientation.
	 */
	public void setOrientation(final boolean horizontal) {
		_orientationHorizontal = horizontal;
		_scale.setHorizontalOrientation(_orientationHorizontal);
		_markerPanel.setHorizontalOrientation(horizontal);
		this.refreshConstraints();
	}

	/**
	 * Gets the orientation (horizontal==true | vertical==false).
	 * 
	 * @return boolean
	 * 				The orientation
	 */
	public boolean getOrientation() {
		return _orientationHorizontal;
	}
	
	/**
	 * Sets the color for lolo fill level.
	 * @param rgb 
	 * 				The color for lolo fill level
	 */
	public void setLoloColor(final RGB rgb) {
		_colorMap.put(LABELS[0], CustomMediaFactory.getInstance().getColor(rgb));
	}
	
	/**
	 * Gets the color for lolo fill level.
	 * @return Color
	 * 				The color for lolo fill level
	 */
	public Color getLoloColor() {
		return _colorMap.get(LABELS[0]);
	}
	
	/**
	 * Sets the color for lo fill level.
	 * @param rgb 
	 * 				The color for lo fill level
	 */
	public void setLoColor(final RGB rgb) {
		_colorMap.put(LABELS[1], CustomMediaFactory.getInstance().getColor(rgb));
	}
	
	/**
	 * Gets the color for lo fill level.
	 * @return Color
	 * 				The color for lo fill level
	 */
	public Color getLoColor() {
		return _colorMap.get(LABELS[1]);
	}
	
	/**
	 * Sets the color for m fill level.
	 * @param rgb 
	 * 				The color for m fill level
	 */
	public void setMColor(final RGB rgb) {
		_colorMap.put(LABELS[2], CustomMediaFactory.getInstance().getColor(rgb));
	}
	
	/**
	 * Gets the color for m fill level.
	 * @return Color
	 * 				The color for m fill level
	 */
	public Color getMColor() {
		return _colorMap.get(LABELS[2]);
	}
	
	/**
	 * Sets the color for hi fill level.
	 * @param rgb 
	 * 				The color for hi fill level
	 */
	public void setHiColor(final RGB rgb) {
		_colorMap.put(LABELS[3], CustomMediaFactory.getInstance().getColor(rgb));
	}
	
	/**
	 * Gets the color for hi fill level.
	 * @return Color
	 * 				The color for hi fill level
	 */
	public Color getHiColor() {
		return _colorMap.get(LABELS[3]);
	}
	
	/**
	 * Sets the color for hihi fill level.
	 * @param rgb 
	 * 				The color for hihi fill level
	 */
	public void setHihiColor(final RGB rgb) {
		_colorMap.put(LABELS[4], CustomMediaFactory.getInstance().getColor(rgb));
	}
	
	/**
	 * Gets the color for hihi fill level.
	 * @return Color
	 * 				The color for hihi fill level
	 */
	public Color getHihiColor() {
		return _colorMap.get(LABELS[4]);
	}
	
	/**
	 * Sets the minimum value.
	 * @param min
	 * 				The minimum value
	 */
	public void setMinimum(final double min) {
		_minimum = min;
		this.refreshConstraints();
	}
	
	/**
	 * Gets the minimum value.
	 * @return double
	 * 				The minimum value
	 */
	public double getMinimum() {
		return _minimum;
	}
	
	/**
	 * Sets the lolo level.
	 * @param loloLevel
	 * 				The lolo level
	 */
	public void setLoloLevel(final double loloLevel) {
		_levelMap.put(LABELS[0], loloLevel);
		this.refreshConstraints();
	}
	
	/**
	 * Gets the lolo level.
	 * @return double
	 * 				The lolo level
	 */
	public double getLoloLevel() {
		return _levelMap.get(LABELS[0]);
	}
	
	/**
	 * Sets the lo level.
	 * @param loLevel
	 * 				The lo level
	 */
	public void setLoLevel(final double loLevel) {
		_levelMap.put(LABELS[1], loLevel);
		this.refreshConstraints();
	}
	
	/**
	 * Gets the lo level.
	 * @return double
	 * 				The lo level
	 */
	public double getLoLevel() {
		return _levelMap.get(LABELS[1]);
	}
	
	/**
	 * Sets the m level.
	 * @param mLevel
	 * 				The m level
	 */
	public void setMLevel(final double mLevel) {
		_levelMap.put(LABELS[2], mLevel);
		this.refreshConstraints();
	}
	
	/**
	 * Gets the m level.
	 * @return double
	 * 				The m level
	 */
	public double getMLevel() {
		return _levelMap.get(LABELS[2]);
	}
	
	/**
	 * Sets the hi level.
	 * @param hiLevel
	 * 				The hi level
	 */
	public void setHiLevel(final double hiLevel) {
		_levelMap.put(LABELS[3], hiLevel);
		this.refreshConstraints();
	}
	
	/**
	 * Gets the hi level.
	 * @return double
	 * 				The hi level
	 */
	public double getHiLevel() {
		return _levelMap.get(LABELS[3]);
	}
	
	/**
	 * Sets the hihi level.
	 * @param hihiLevel
	 * 				The hihi level
	 */
	public void setHihiLevel(final double hihiLevel) {
		_levelMap.put(LABELS[4], hihiLevel);
		this.refreshConstraints();
	}
	
	/**
	 * Gets the hihi level.
	 * @return double
	 * 				The hihi level
	 */
	public double getHihiLevel() {
		return _levelMap.get(LABELS[4]);
	}
	
	/**
	 * Sets the maximum value.
	 * @param max
	 * 				The maximum value
	 */
	public void setMaximum(final double max) {
		_maximum = max;
		this.refreshConstraints();
	}
	
	/**
	 * Gets the maximum value.
	 * @return double
	 * 				The maximum value
	 */
	public double getMaximum() {
		return _maximum;
	}
	
	/**
	 * Sets, if the values should be shown.
	 * @param showValues
	 * 				True, if the values should be shown, false otherwise
	 */
	public void setShowValues(final boolean showValues) {
		_showValues = showValues;
	}
	
	/**
	 * Gets, if the values should be shown.
	 * @return boolean
	 * 				True, if the values should be shown, false otherwise
	 */
	public boolean getShowValues() {
		return _showValues;
	}
	
	/**
	 * Sets, how the marks should be shown.
	 * @param showMarks
	 * 				0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
	 */
	public void setShowMarks(final int showMarks) {
		_showMarks = showMarks;
		_markerPanel.setTopLeftAlignment(showMarks==TOP_LEFT);
		this.refreshConstraints();
	}
	
	/**
	 * Gets, how the marks should be shown.
	 * @return boolean
	 * 				0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
	 */
	public int getShowMarks() {
		return _showMarks;
	}
	
	/**
	 * Sets, how the scale should be shown.
	 * @param showScale
	 * 				0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
	 */
	public void setShowScale(final int showScale) {
		_showScale = showScale;
		this.refreshConstraints();
	}
	
	/**
	 * Gets, how the scale should be shown.
	 * @return int
	 * 				0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
	 */
	public int getShowScale() {
		return _showScale;
	}
	
	/**
	 * Sets the count of sections in the scale.
	 * @param scaleSectionCount
	 * 				The count of sections in the scale
	 */
	public void setScaleSectionCount(final int scaleSectionCount) {
		_scaleSectionCount = scaleSectionCount;
		_scale.setSectionCount(scaleSectionCount);
		this.refreshConstraints();
	}
	
	/**
	 * Gets the count of sections in the scale.
	 * @return int
	 * 				The count of sections in the scale.
	 */
	public int getScaleSectionCount() {
		return _scaleSectionCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
	
	/**
	 * This class represents the bargraph.
	 * 
	 * @author Kai Meyer
	 */
	private final class FillRectangleFigure extends RectangleFigure {
		
		/**
		 * The fill grade (0 - 1).
		 */
		private double _fillGrade = 0.5;
		
		/**
		 * The default fill Color.
		 */
		private Color _defaultFillColor;
		
		/**
		 * The Color for the border.
		 */
		private Color _borderColor;
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			Rectangle bounds = this.getBounds();
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.setBackgroundColor(this.getBackgroundColor());
			graphics.fillRectangle(bounds);
			graphics.setBackgroundColor(this.getFillColor());
			Rectangle fillRectangle = this.getFillLevelRectangle(bounds); 
			graphics.fillRectangle(fillRectangle);
			graphics.setForegroundColor(this.getBorderColor());
			graphics.drawRectangle(new Rectangle(bounds.x,bounds.y,bounds.width-1,bounds.height-1));
			graphics.drawRectangle(new Rectangle(fillRectangle.x,fillRectangle.y,fillRectangle.width-1,fillRectangle.height-1));
		}
		
		/**
		 * Gets the fill Color.
		 * @return Color
		 * 				The Color for the fill-area
		 */
		private Color getFillColor() {
			List<String> labelList = new LinkedList<String>();
			for (int i=0;i<LABELS.length;i++) {
				if (labelList.isEmpty()) {
					labelList.add(LABELS[i]);
				} else {
					labelList.add(this.getLabelIndex(labelList, LABELS[i]), LABELS[i]);
				}
			}
			String[] tempLabels = labelList.toArray(new String[labelList.size()]);
			if (_minimum<_maximum) {
				for (int i=0; i<tempLabels.length;i++) {
					if (this.getFill()<=getWeight(_levelMap.get(tempLabels[i]))) {
						return _colorMap.get(tempLabels[i]);
					}
				}
				return this.getDefaultFillColor();
			} else {
				for (int i=tempLabels.length-1; i>=0;i--) {
					if (this.getFill()<=getWeight(_levelMap.get(tempLabels[i]))) {
						return _colorMap.get(tempLabels[i]);
					}
				}
				return this.getDefaultFillColor();
			}
		}
		
		/**
		 * Gets the index for the label.
		 * @param labelList
		 * 				The list, where the label should be added
		 * @param label
		 * 				The label, which should be added
		 * @return int
		 * 				The index for the label
		 */
		private int getLabelIndex(final List<String> labelList, final String label) {
			for (int j=0;j<labelList.size();j++) {
				if (_levelMap.get(label)<_levelMap.get(labelList.get(j))) {
					return j;
				}
			}
			return labelList.size();
		}
		
		/**
		 * Gets the rectangle for the fill level.
		 * @param area
		 * 				The rectangle of the bargraph
		 * @return Rectangle
		 * 				The rectangle for the fill level
		 */
		private Rectangle getFillLevelRectangle(final Rectangle area) {
			if (_orientationHorizontal) {
				int newW = (int) Math.round(area.width * (this.getFill()));
				return new Rectangle(area.getLocation(),new Dimension(newW, area.height));
			}
			int newH = (int) Math.round(area.height * (this.getFill()));
			return new Rectangle(area.x,area.y+area.height-newH,area.width,newH);
		}
		
		/**
		 * Sets the fill grade.
		 * 
		 * @param fill
		 *            The fill grade.
		 */
		public void setFill(final double fill) {
			_fillGrade = fill;
		}

		/**
		 * Gets the fill grade.
		 * 
		 * @return double
		 * 				The fill grade
		 */
		public double getFill() {
			return _fillGrade;
		}
		
		/**
		 * Sets the default fill Color.
		 * @param defaultFillColor
		 * 				The default fill Color
		 */
		public void setDefaultFillColor(final Color defaultFillColor) {
			_defaultFillColor = defaultFillColor;
		}
		
		/**
		 * Gets the default fill Color.
		 * @return Color
		 * 				The color default fill Color
		 */
		public Color getDefaultFillColor() {
			return _defaultFillColor;
		}
		
		/**
		 * Sets the color for the border.
		 * @param borderColor
		 * 				The Color for the border
		 */
		public void setBorderColor(final Color borderColor) {
			_borderColor = borderColor;
		}
		
		/**
		 * Gets the color for the border.
		 * @return Color
		 * 				The color for the border
		 */
		public Color getBorderColor() {
			return _borderColor;
		}
	}
	
	/**
	 * This Figure contains the Markers. 
	 * 
	 * @author Kai Meyer
	 */
	private final class MarkerPanel extends Panel {
		/**
		 * The List of Markers.
		 */
		private final List<Marker> _markerList = new LinkedList<Marker>();
		/**
		 * The alignment for the Markers.
		 */
		private boolean _topLeft;
		/**
		 * The orientation of theis figure.
		 */
		private boolean _isHorizontal;
		/**
		 * The start position.
		 */
		private int _start = 10;
		/**
		 * The end position.
		 */
		private int _end = 100;
		
		/**
		 * Constructor.
		 * @param topLeft
		 * 				true, if the Marker is on top/left of the bargraph, false otherwise
		 * @param isHorizontal
		 * 				true, if the Marker has a horizontal orientation
		 */
		public MarkerPanel(final boolean topLeft, final boolean isHorizontal) {
			this.setLayoutManager(new XYLayout());
			_topLeft = topLeft;
			_isHorizontal = isHorizontal;
			Marker marker = null;
			for (int i=0;i<LABELS.length;i++) {
				marker = new Marker(LABELS[i], _topLeft, _isHorizontal);
				marker.setForegroundColor(this.getForegroundColor());
				this.add(marker);
				_markerList.add(marker);
			}
//			 listen to figure movement events
			addFigureListener(new FigureListener() {
				public void figureMoved(final IFigure source) {
					refreshConstraints();
				}
			});
			
			this.refreshConstraints();
		}
		
		/**
		 * Refreshes the constraints.
		 */
		private void refreshConstraints() {
			if (_isHorizontal) {
				for (int i=0;i<_markerList.size();i++) {
					double weight = getWeight(_levelMap.get(LABELS[i])); 
					if (weight<0 || weight>1) {
						this.setConstraint(_markerList.get(i), new Rectangle(0,0,0,0));
					} else {
						int x = _start + (int)((_end-_start)*weight)-1 - TEXTWIDTH/2;
						this.setConstraint(_markerList.get(i), new Rectangle(x,0,TEXTWIDTH, bounds.height ));
					}
				}
			} else {
				for (int i=0;i<_markerList.size();i++) {
					double weight = getWeight(_levelMap.get(LABELS[i])); 
					if (weight<0 || weight>1) {
						this.setConstraint(_markerList.get(i), new Rectangle(0,0,0,0));
					} else {
						int y = _start + (int) ((_end-_start)*(1-weight)) - TEXTHEIGHT/2;
						this.setConstraint(_markerList.get(i), new Rectangle(1, y, bounds.width, TEXTHEIGHT));
					}
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			Rectangle bounds = this.getBounds();
			graphics.setBackgroundColor(this.getBackgroundColor());
			graphics.setForegroundColor(ColorConstants.black);
			graphics.fillRectangle(bounds);	
		}
		
		/**
		 * Sets the reference values for this figure.
		 * @param start
		 * 				The start value
		 * @param end
		 * 				The end value
		 */
		public void setReferencePositions(final int start, final int end) {
			_start = start;
			_end = end;
			this.refreshConstraints();
		}
		
		/**
		 * Sets the orientation of this figure.
		 * @param isHorizontal
		 * 				The orientation of this figure (true=horizontal;false=vertical)
		 */
		public void setHorizontalOrientation(final boolean isHorizontal) {
			_isHorizontal = isHorizontal;
			for (Marker marker : _markerList) {
				marker.setHorizontalOrientation(isHorizontal);
			}
			this.refreshConstraints();
		}
		
		/**
		 * Sets the alignment of this figure.
		 * @param topLeft
		 * 				The alignment of this figure (true=top/left;false=bottom/right)
		 */
		public void setTopLeftAlignment(final boolean topLeft) {
			_topLeft = topLeft;
			for (Marker marker : _markerList) {
				marker.setTopLeftAlignment(topLeft);
			}
			this.refreshConstraints();
		}
	}
	
	/**
	 * This class represents a Marker.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class Marker extends RectangleFigure {			
		/**
		 * The key of this Marker, which is the drwaed text.
		 */
		private String _key;
		/**
		 * The orientation of this Marker.
		 */
		private boolean _isHorizontal;
		/**
		 * The alignment of this Marker.
		 */
		private boolean _topLeft;
		/**
		 * The Label for the text.
		 */
		private Label _textLabel;
		/**
		 * The Tigmark.
		 */
		private TigMark _tigMark; 
		
		/**
		 * Construktor.
		 * @param key
		 * 				The text to display
		 * @param topLeft
		 * 				True, if the marker should be above the y value, false otherwise
		 * @param isHorizontal
		 * 				True, if the marker should have a horizontal orientation, false otherwise
		 */
		public Marker(final String key, final boolean topLeft, final boolean isHorizontal) {
			BorderLayout borderLayout = new BorderLayout();
			this.setLayoutManager(borderLayout);
			_key = key;
			_textLabel = new Label(key.toString());
			_textLabel.setForegroundColor(this.getForegroundColor());
			_tigMark = new TigMark();
			_tigMark.setForegroundColor(this.getForegroundColor());
			_tigMark.setBackgroundColor(_colorMap.get(_key));
			this.add(_tigMark, BorderLayout.CENTER);
			this.refreshLabel();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			//nothing to do;
		};
		
		/**
		 * Sets the orientation of this figure.
		 * @param isHorizontal
		 * 				The orientation of this figure (true=horizontal;false=vertical)
		 */
		public void setHorizontalOrientation(final boolean isHorizontal) {
			_isHorizontal = isHorizontal;
			_tigMark.setHorizontalOrientation(isHorizontal);
			this.refreshLabel();
		}
		
		/**
		 * Sets the alignment of this figure.
		 * @param topLeft
		 * 				The alignment of this figure (true=top/left;false=bottom/right)
		 */
		public void setTopLeftAlignment(final boolean topLeft) {
			_topLeft = topLeft;
			_tigMark.setTopLeftAlignment(topLeft);
			this.refreshLabel();
		}
		
		/**
		 * Refreshes the Label.
		 */
		private void refreshLabel() {
			if (this.getChildren().contains(_textLabel)) {
				this.remove(_textLabel);	
			}
			Integer place;
			if (_isHorizontal) {
				if (_topLeft) {
					place = BorderLayout.TOP;
				} else {
					place = BorderLayout.BOTTOM;
				}
			} else {
				if (_topLeft) {
					place = BorderLayout.LEFT;
				} else {
					place = BorderLayout.RIGHT;
				}
			}
			this.add(_textLabel, place);
		}
		
		/**
		 * This class represents a tigmark.
		 * @author Kai Meyer
		 */
		private final class TigMark extends RectangleFigure {
			/**
			 * The width of this marker.
			 */
			private int _width;
			/**
			 * The height of this marker.
			 */
			private int _height;
			/**
			 * The direction of this Marker.
			 */
			private int _direction = 1;
			/**
			 * The orientation of this Marker.
			 */
			private boolean _isHorizontal;
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void paintFigure(final Graphics graphics) {
				Rectangle bounds = this.getBounds();
				graphics.setForegroundColor(this.getForegroundColor());
				graphics.setBackgroundColor(_colorMap.get(_key));
				PointList pointList = new PointList();
				int x;
				int y;
				if (_isHorizontal) {
					x = bounds.x+bounds.width/2;
					y = bounds.y;
					if (_direction<0) {
						y = y + bounds.height;
					}
					pointList.addPoint(x, y);
					pointList.addPoint(x-_width, y+_height*_direction);
					pointList.addPoint(x+_width, y+_height*_direction);
					pointList.addPoint(x, y);
				} else {
					x = bounds.x;
					y = bounds.y+bounds.height/2;
					if (_direction<0) {
						x = x + bounds.width-2;
					}
					pointList.addPoint(x, y);
					pointList.addPoint(x+_width*_direction, y-_height);
					pointList.addPoint(x+_width*_direction, y+_height);
					pointList.addPoint(x, y);
				}
				graphics.fillPolygon(pointList);
				graphics.drawPolyline(pointList);
			}
			
			/**
			 * Sets the orientation of this figure.
			 * @param isHorizontal
			 * 				The orientation of this figure (true=horizontal;false=vertical)
			 */
			public void setHorizontalOrientation(final boolean isHorizontal) {
				_isHorizontal = isHorizontal;
				if (isHorizontal) {
					_width = 3;
					_height = 5;
				} else {
					_width = 5;
					_height = 3;	
				}
			}
			
			/**
			 * Sets the alignment of this figure.
			 * @param topLeft
			 * 				The alignment of this figure (true=top/left;false=bottom/right)
			 */
			public void setTopLeftAlignment(final boolean topLeft) {
				if (topLeft) {
					_direction = -1;
				} else {
					_direction = 1;
				}
			}
			
		}
		
	}
	
	/**
	 * This class represents a scale.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class Scale extends RectangleFigure {	
		/**
		 * The length of this Scale.
		 */
		private int _length;
		/**
		 * The count of sections in this Scale.
		 */
		private int _sectionCount = -1;
		/**
		 * The direction of this Scale.
		 */
		private boolean _isHorizontal;
		/**
		 * The start position.
		 */
		private int _start = 10;
		/**
		 * True, if the negativ sections should be draan, false otherwise.
		 */
		private boolean _showNegativSections = false;
		
		/**
		 * Sets the length of this Scale.
		 * @param length
		 * 					The lenght of this Scale
		 */
		public void setLength(final int length) {
			_length = length;
		}
		
		/**
		 * Sets the orientation of this Scale.
		 * @param isHorizontal
		 * 					The orientation of this Scale (true=horizontal;false=vertical)
		 */
		public void setHorizontalOrientation(final boolean isHorizontal) {
			_isHorizontal = isHorizontal;
		}
		
		/**
		 * Sets the count of setcion in this Scale.
		 * @param sectionCount
		 * 					The count of setcion in this Scale
		 */
		public void setSectionCount(final int sectionCount) {
			_sectionCount = sectionCount;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			int sectionWidth = 0;
			int sectionHeight = 0;
			int height = 0;
			int width = 0;
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.setBackgroundColor(this.getBackgroundColor());
			if (_isHorizontal) {
				height = _scaleWideness;
				if (_sectionCount>0) {
					sectionWidth = _length/_sectionCount;
					for (int i=0;i<_sectionCount+1;i++) {
						graphics.drawLine(this.getBounds().x+_start+i*sectionWidth, this.getBounds().y, this.getBounds().x+_start+i*sectionWidth+width , this.getBounds().y+height);
					}
				} else {
					int pos = _start;
					while (pos<this.getBounds().width) {
						graphics.drawLine(this.getBounds().x+pos, this.getBounds().y, this.getBounds().x+pos , this.getBounds().y+height);
						pos = pos +_length;
					}
					if (_showNegativSections) {
						pos = _start;
						while (pos>0) {
							graphics.drawLine(this.getBounds().x, this.getBounds().y+pos, this.getBounds().x+width , this.getBounds().y+pos);
							pos = pos - _length;
						}	
					}
				}
			} else {
				width = _scaleWideness;
				if (_sectionCount>0) {
					sectionHeight = _length/_sectionCount;
					for (int i=0;i<_sectionCount+1;i++) {
						graphics.drawLine(this.getBounds().x, this.getBounds().y+_start+i*sectionHeight, this.getBounds().x+width , this.getBounds().y+_start+i*sectionHeight);
					}	
				} else {
					int pos = _start;
					while (pos<this.getBounds().height) {
						graphics.drawLine(this.getBounds().x, this.getBounds().y+pos, this.getBounds().x+width , this.getBounds().y+pos);
						pos = pos +_length;
					}
					if (_showNegativSections) {
						pos = _start;
						while (pos>0) {
							graphics.drawLine(this.getBounds().x, this.getBounds().y+pos, this.getBounds().x+width , this.getBounds().y+pos);
							pos = pos - _length;
						}	
					}
				}
			}
		}		
		
		/**
		 * Sets the reference values for this figure.
		 * @param start
		 * 				The start value
		 */
		public void setReferencePositions(final int start) {
			_start = start;
		}
		
		/**
		 * Sets if the negative sections should be drawn.
		 * @param showNegativ
		 * 				True, if the negativ sections should be drawn, false otherwise.
		 */
		public void setShowNegativeSections(final boolean showNegativ) {
			_showNegativSections = showNegativ;
		}
	}

}
