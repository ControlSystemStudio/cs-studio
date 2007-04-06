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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
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
	private static final int TEXTHEIGHT = 22;
	
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
	private int _scaleSectionCount;
	
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
	private int _scaleWideness = 5;
	
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
	protected synchronized void fillShape(final Graphics graphics) {
		_barRectangle = this.getBarRectangle();
		graphics.setBackgroundColor(this.getBackgroundColor());
		graphics.fillRectangle(this.getBounds());
		this.refreshConstraints();
		graphics.setBackgroundColor(this.getBackgroundColor());
		graphics.setForegroundColor(this.getBorderColor());
	}
	
	/**
	 * Refreshes the Constraints.
	 */
	private void refreshConstraints() {
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
		_scale.setSectionCount(this.getScaleSectionCount());
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
			_scale.setReferencePositions(_barRectangle.y);
			_scale.setLength(_fillRectangleFigure.getBounds().height);
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
	 * The value is calculated, to fit the scale completly intp the bargraph
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
		double weight = (value-_minimum) / max;
		/*if (weight<0) {
			weight = 0;
		}
		if (weight>1) {
			weight = 1;
		}*/
		return weight; 
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
		 * {@inheritDoc}
		 */
		@Override
		public void paintFigure(final Graphics graphics) {
			Rectangle bounds = this.getBounds();
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.setBackgroundColor(this.getBackgroundColor());
			graphics.fillRectangle(bounds);
			graphics.drawRectangle(new Rectangle(bounds.x,bounds.y,bounds.width-1,bounds.height-1));
			graphics.setBackgroundColor(this.getForegroundColor());
			for (int i=0; i<LABELS.length;i++) {
				if (getFill()<=getWeight(_levelMap.get(LABELS[i]))) {
					graphics.setBackgroundColor(_colorMap.get(LABELS[i]));
					break;
				}
			}
			Rectangle fillRectangle = this.getFillLevelRectangle(bounds); 
			graphics.fillRectangle(fillRectangle);
			graphics.drawRectangle(new Rectangle(fillRectangle.x,fillRectangle.y,fillRectangle.width-1,fillRectangle.height-1));
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
		 * Sets the reference values for this figure.
		 * @param start
		 * 				The start value
		 * @param end
		 * 				The end value
		 */
		public void setReferencePositions(final int start, final int end) {
			_start = start;
			_end = end;
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
		 * The width of this marker.
		 */
		private int _width;
		/**
		 * The height of this marker.
		 */
		private int _height;		
		/**
		 * The key of this Marker, which is the drwaed text.
		 */
		private String _key;
		/**
		 * The direction of this Marker.
		 */
		private int _direction = 1;
		/**
		 * The orientation of this Marker.
		 */
		private boolean _isHorizontal;
		
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
			_key = key;
			if (topLeft) {
				_direction = -1;
			} else {
				_direction = -1;
			}
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
			if (_isHorizontal) {
				y = y+(_height+2)*_direction;
				if (_direction<0) {
					y = y - graphics.getFontMetrics().getHeight();
				}
				graphics.drawString(_key, this.getTextPositionX(x, _key, graphics.getFontMetrics().getAverageCharWidth()), y);	
			} else {
				x = x+(_width+2)*_direction;
				if (_direction<0) {
					x = x +_width*2- TEXTWIDTH;
				}
				graphics.drawString(_key.toString(), x, this.getTextPositionY(y, graphics.getFontMetrics().getHeight()));
			}
		};
		
		/**
		 * Gets the x value for drawing the text.
		 * @param currentX 
		 * 				The x value of the center of the text
		 * @param text
		 * 				The text to draw
		 * @param charWidth
		 * 				The average charwidth from the current font
		 * @return int 
		 * 				The x value for drawing the text
		 */
		private int getTextPositionX(final int currentX, final String text, final int charWidth) {
			return currentX-charWidth*(text.length()/2);
		}
		
		/**
		 * Gets the y value for drawing the text.
		 * @param currentY 
		 * 				The y value of the center of the text
		 * @param charHeight
		 * 				The average charheight from the current font
		 * @return int 
		 * 				The y value for drawing the text
		 */
		private int getTextPositionY(final int currentY, final int charHeight) {
			return currentY-(charHeight/2);
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
		private int _sectionCount;
		/**
		 * The direction of this Scale.
		 */
		private boolean _isHorizontal;
		/**
		 * The start position.
		 */
		private int _start = 10;
		
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
				height = 5;
				sectionWidth = _length/_sectionCount;
				for (int i=0;i<_sectionCount+1;i++) {
					graphics.drawLine(this.getBounds().x+_start+i*sectionWidth, this.getBounds().y+i*sectionHeight, this.getBounds().x+_start+i*sectionWidth+width , this.getBounds().y+i*sectionHeight+height);
				}
			} else {
				width = 5;
				sectionHeight = _length/_sectionCount;
				for (int i=0;i<_sectionCount+1;i++) {
					graphics.drawLine(this.getBounds().x+i*sectionWidth, this.getBounds().y+_start+i*sectionHeight, this.getBounds().x+i*sectionWidth+width , this.getBounds().y+_start+i*sectionHeight);
				}
			}
//			graphics.setBackgroundColor(ColorConstants.blue);
//			graphics.setForegroundColor(ColorConstants.blue);
//			graphics.fillRectangle(this.getBounds());
		}		
		
		/**
		 * Sets the reference values for this figure.
		 * @param start
		 * 				The start value
		 */
		public void setReferencePositions(final int start) {
			_start = start;
		}
	}

}
