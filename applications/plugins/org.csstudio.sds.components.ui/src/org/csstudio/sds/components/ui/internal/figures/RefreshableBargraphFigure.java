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
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
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
public final class RefreshableBargraphFigure extends RectangleFigure implements
		IRefreshableFigure {
	
	/**
	 * Height of the text.
	 */
	private static final int TEXTHEIGHT = 22;
	
	/**
	 * Width of the text.
	 */
	private static final int TEXTWIDTH = 45;
	
	/**
	 * The Strings, which are displayed in this figure.
	 */
	private static final String[] LABELS = new String[] {"LOLO", "LO", "M", "HI", "HIHI"};
	
	/**
	 * Don't show markers.
	 */
	private static final int NONE = 0;
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
	 * The fill grade (0 - 1).
	 */
	private double _fillGrade = 0.5;
	
	/**
	 * The orientation (horizontal==true | vertical==false).
	 */
	private boolean _orientationHorizontal = true;
	
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
	 * Constructor.
	 *
	 */
	public RefreshableBargraphFigure() {
		super();
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.initColorMap();
		this.initLevelMap();
		this.setLayoutManager(new XYLayout());
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
		this.removeAll();
		Rectangle figureBounds = this.getBounds();
		Rectangle barRectangle = this.getBarRectangle(figureBounds);
		Rectangle fillLevelRectangle = this.getFillLevelRectangle(barRectangle);
		Scale scale = this.getScale(barRectangle);
		//this.add(scale);
		//this.setConstraint(scale, fillLevelRectangle);
		graphics.setBackgroundColor(ColorConstants.white);
		this.drawFigureBackground(graphics, figureBounds);
		this.drawBarRectangle(graphics, barRectangle);
		this.drawFillLevel(graphics, fillLevelRectangle);
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawRectangle(barRectangle.x, barRectangle.y, barRectangle.width-1, barRectangle.height-1);
		this.drawFillLine(graphics, fillLevelRectangle);
		if (_showMarks!=NONE) {
			this.drawMarkers(graphics, barRectangle);
		}
		if (_showScale!=NONE) {
			this.drawScale(graphics, scale);
			//scale.paintFigure(graphics);
		}
		graphics.setForegroundColor(ColorConstants.black);
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
	 * @param bounds
	 * 				The bounds of the whole figure
	 * @return Rectangle
	 * 				The rectangle for the bargraph
	 */
	private Rectangle getBarRectangle(final Rectangle bounds) {
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
			if (_showValues) {
				yCorrection = yCorrection*2;
				heightCorrection = heightCorrection*2;
			}
			return new Rectangle(bounds.x+xCorrection,bounds.y+yCorrection,this.calculateRealLength(bounds.width-2*xCorrection),bounds.height-(yCorrection+heightCorrection));
		}
		if (_showMarks==TOP_LEFT) {	
			xCorrection = TEXTWIDTH+10;
			yCorrection = TEXTHEIGHT/2;
		}
		if (_showMarks==BOTTOM_RIGHT) {
			widthCorrection = TEXTWIDTH;
			yCorrection = TEXTHEIGHT/2;
		}
		if (_showValues) {
			xCorrection = xCorrection*2-10;
			widthCorrection = widthCorrection*2;
		}
		return new Rectangle(bounds.x+xCorrection,bounds.y+yCorrection,bounds.width-(xCorrection+widthCorrection)+1,this.calculateRealLength(bounds.height-2*yCorrection));
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
	 * Gets the Scale of this Figure.
	 * @param barRectangle
	 * 				The Rectangle of the Bargraph
	 * @return Scale
	 * 				The Scale of this figure 
	 */
	private Scale getScale(final Rectangle barRectangle) {
		if (_orientationHorizontal) {
			if (_showScale==BOTTOM_RIGHT) {
				return new Scale(barRectangle.x,barRectangle.y+barRectangle.height-5,barRectangle.width,_scaleSectionCount,_orientationHorizontal);
			} else {
				return new Scale(barRectangle.x,barRectangle.y,barRectangle.width,_scaleSectionCount,_orientationHorizontal);
			}
		} else {
			if (_showScale==BOTTOM_RIGHT) {
				return new Scale(barRectangle.x+barRectangle.width-5,barRectangle.y,barRectangle.height,_scaleSectionCount,_orientationHorizontal);
			} else {
				return new Scale(barRectangle.x,barRectangle.y,barRectangle.height,_scaleSectionCount,_orientationHorizontal);
			}
		}
	}
	
//	/**
//	 * Gets the Scale of this Figure.
//	 * @return Scale
//	 * 				The Scale of this figure 
//	 */
//	private Scale getScale(int length) {
//		if (_orientationHorizontal) {
//			return new Scale(length,_scaleSectionCount,_orientationHorizontal);
//		} else {
//			return new Scale(length,_scaleSectionCount,_orientationHorizontal);
//		}
//	}

	/**
	 * Draws the background of this figure.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param figureBounds
	 * 				The rectangle to draw
	 */
	private void drawFigureBackground(final Graphics graphics, final Rectangle figureBounds) {
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.fillRectangle(figureBounds);
	}
	
	/**
	 * Draws the bargraph.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param barRectangle
	 * 				The rectangle to draw
	 */
	private void drawBarRectangle(final Graphics graphics, final Rectangle barRectangle) {
		graphics.setBackgroundColor(this.getBackgroundColor());
		graphics.fillRectangle(barRectangle);
//		graphics.setForegroundColor(ColorConstants.cyan);
//		graphics.setBackgroundColor(ColorConstants.cyan);
//		graphics.drawRectangle(barRectangle.x,barRectangle.y,barRectangle.width,barRectangle.height);
	}
	
	/**
	 * Draws the fill level.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param fillRectangle
	 * 				The rectangle to draw
	 */
	private void drawFillLevel(final Graphics graphics, final Rectangle fillRectangle) {
		graphics.setBackgroundColor(this.getForegroundColor());
		for (int i=0; i<LABELS.length;i++) {
			if (this.getFill()<=this.getWeight(_levelMap.get(LABELS[i]))) {
				graphics.setBackgroundColor(_colorMap.get(LABELS[i]));
				break;
			}
		}
		graphics.fillRectangle(fillRectangle);
	}
	
	/**
	 * Draws a line on the current position of the fill level.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param fillRectangle
	 * 				The rectangle of the fill level
	 */
	private void drawFillLine(final Graphics graphics, final Rectangle fillRectangle) {
		if (_orientationHorizontal) {
			graphics.drawLine(fillRectangle.x+fillRectangle.width-1, fillRectangle.y, fillRectangle.x+fillRectangle.width-1, fillRectangle.y+fillRectangle.height-1);
		} else {
			graphics.drawLine(fillRectangle.x, fillRectangle.y, fillRectangle.x+fillRectangle.width-1, fillRectangle.y);
		}
	}
	
	/**
	 * Draws the markers.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param barRectangle
	 * 				The rectangle of the bargraph
	 */
	private void drawMarkers(final Graphics graphics, final Rectangle barRectangle) {
		List<Marker> markerList = new LinkedList<Marker>();
		boolean topLeft = _showMarks==TOP_LEFT;
		if (_orientationHorizontal) {
			int yCorrection = 0;
			if (_showMarks==BOTTOM_RIGHT) {
				yCorrection = barRectangle.height;
			}
			for (int i=0;i<LABELS.length;i++) {
				markerList.add(new Marker(barRectangle.x+(int)(barRectangle.width*this.getWeight(_levelMap.get(LABELS[i]))), barRectangle.y+yCorrection, _levelMap.get(LABELS[i]), LABELS[i], _showValues, topLeft, _orientationHorizontal));
			}
			
		} else {
			int xCorrection = 0;
			if (_showMarks==BOTTOM_RIGHT) {
				xCorrection = barRectangle.width;
			}
			for (int i=0;i<LABELS.length;i++) {
				markerList.add(new Marker(barRectangle.x+xCorrection, barRectangle.y+(int)(barRectangle.height*(1-this.getWeight(_levelMap.get(LABELS[i])))), _levelMap.get(LABELS[i]), LABELS[i], _showValues, topLeft, _orientationHorizontal));
			}	
		}
		for (int i=0;i<markerList.size();i++) {
			markerList.get(i).setForegroundColor(ColorConstants.black);
			markerList.get(i).setBackgroundColor(_colorMap.get(LABELS[i]));
			markerList.get(i).paintFigure(graphics);
		}
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
			max = 0.01;
		}
		double weight = (value-_minimum) / max;
		if (weight<0) {
			weight = 0;
		}
		if (weight>1) {
			weight = 1;
		}
		return weight; 
	}
	
	/**
	 * Draws the scale.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param scale
	 * 				The Scale, which sould be drawn
	 */
	private void drawScale(final Graphics graphics, final Scale scale) {
		scale.setForegroundColor(ColorConstants.red);
		scale.setBackgroundColor(ColorConstants.red);
		scale.paintFigure(graphics);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
		this.setFill(Math.random() * 100);
		repaint();
	}
	
	/**
	 * Sets the fill grade.
	 * 
	 * @param fill
	 *            The fill grade.
	 */
	public void setFill(final double fill) {
		_fillGrade = fill/100;
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
	 * Sets the orientation (horizontal==true | vertical==false).
	 * 
	 * @param horizontal
	 *            The orientation.
	 */
	public void setOrientation(final boolean horizontal) {
		_orientationHorizontal = horizontal;
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
	 * This class represents a Marker.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class Marker extends RectangleFigure {
		/**
		 * The x coordinate of this Marker.
		 */
		private int _x = 0;
		/**
		 * The y coordinate of this Marker.
		 */
		private int _y = 0;
		/**
		 * The width of this marker.
		 */
		private final int _width;
		/**
		 * The height of this marker.
		 */
		private final int _height;
		
		/**
		 * A boolean, which indicates whether the value shoulb be shown.
		 */
		private boolean _showValue = false;
		
		/**
		 * The drawed value.
		 */
		private double _value;
		
		/**
		 * The drawed text.
		 */
		private String _text;
		
		/**
		 * The direction of this Marker.
		 */
		private int _direction = 1;
		/**
		 * The orientation of this Marker.
		 */
		private final boolean _orientationHorizontal;
		
		/**
		 * Construktor.
		 * @param x
		 * 				The x coordinate for this Marker
		 * @param y
		 * 				The y coordinate for this Marker
		 * @param value
		 * 				The double to display
		 * @param text
		 * 				The text to display
		 * @param showValues
		 * 				True, if the values should be shown, false otherwise
		 * @param above
		 * 				True, if the marker should be above the y value, false otherwise
		 * @param orientationHorizontal
		 * 				True, if the marker should have a horizontal orientation, false otherwise
		 */
		public Marker(final int x, final int y, final double value, final String text, final boolean showValues, final boolean above, final boolean orientationHorizontal) {
			_x = x;
			_y = y;
			_text = text;
			_showValue = showValues;
			_value = value;
			if (above) {
				_direction = -1;
			}
			this._orientationHorizontal = orientationHorizontal;
			if (orientationHorizontal) {
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
		protected void fillShape(final Graphics graphics) {
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.setBackgroundColor(this.getBackgroundColor());
			PointList pointList = new PointList();
			pointList.addPoint(_x, _y);	
			if (_orientationHorizontal) {
				pointList.addPoint(_x-_width, _y+_height*_direction);
				pointList.addPoint(_x+_width, _y+_height*_direction);
				
			} else {
				pointList.addPoint(_x+_width*_direction, _y-_height);
				pointList.addPoint(_x+_width*_direction, _y+_height);
			}
			pointList.addPoint(_x, _y);	
			graphics.fillPolygon(pointList);
			graphics.drawPolyline(pointList);
			if (_orientationHorizontal) {
				int y = _y+(_height+2)*_direction;
				if (_direction<0) {
					y = y - graphics.getFontMetrics().getHeight();
				}
				if (_showValue) {
					graphics.drawString(String.valueOf(_value), this.getTextPositionX(_x, _text, graphics.getFontMetrics().getAverageCharWidth()), y);
					y = y + graphics.getFontMetrics().getHeight()*_direction;
				}
				graphics.drawString(_text, this.getTextPositionX(_x, _text, graphics.getFontMetrics().getAverageCharWidth()), y);	
			} else {
				int x = _x+(_width+2)*_direction;
				if (_direction<0) {
					x = x - TEXTWIDTH;
				}
				if (_showValue) {
					graphics.drawString(String.valueOf(_value), x, this.getTextPositionY(_y, graphics.getFontMetrics().getHeight()));
					x = x + TEXTWIDTH*_direction;
				}
				graphics.drawString(_text.toString(), x, this.getTextPositionY(_y, graphics.getFontMetrics().getHeight()));
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
		
	}
	
	/**
	 * This class represents a scale.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class Scale extends RectangleFigure {
		
		/**
		 * The x coordinate of this Scale.
		 */
		//private final int _x;
		/**
		 * The y coordinate of this Scale.
		 */
		//private final int _y;
		/**
		 * The length of this Scale.
		 */
		private final int _length;
		/**
		 * The count of sections in this Scale.
		 */
		private final int _sectionCount;
		/**
		 * The direction of this Scale.
		 */
		private final boolean _horizontal;
		
		/**
		 * Constructor.
		 * @param x
		 * 				The x coordinate of this Scale.
		 * @param y
		 * 				The y coordinate of this Scale.
		 * @param length
		 * 				The length of this Scale.
		 * @param sectionCount
		 * 				The count of sections in this Scale.
		 * @param orientationHorizontal
		 * 				The direction of this Scale.
		 */
		public Scale(final int x, final int y, final int length, final int sectionCount, final boolean orientationHorizontal) {
			//_x = x;
			//_y = y;
			this.setLocation(new Point(x,y));
			_length = length;
			_sectionCount = sectionCount;
			_horizontal = orientationHorizontal;
		}
		
//		public Scale(final int length, final int sectionCount, final boolean orientationHorizontal) {
//			_length = length;
//			_sectionCount = sectionCount;
//			_horizontal = orientationHorizontal;
//			if (_horizontal) {
//				this.setSize(_length, 5);
//			} else {
//				this.setSize(5, _length);
//			}
//		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void fillShape(final Graphics graphics) {
			int sectionWidth = 0;
			int sectionHeight = 0;
			int height = 0;
			int width = 0;
			if (_horizontal) {
				height = 5;
				sectionWidth = _length/_sectionCount;
			} else {
				width = 5;
				sectionHeight = _length/_sectionCount;
			}
			graphics.setForegroundColor(this.getForegroundColor());
			graphics.setBackgroundColor(this.getBackgroundColor());
			for (int i=0;i<_sectionCount+1;i++) {
				graphics.drawLine(this.getBounds().x+i*sectionWidth, this.getBounds().y+i*sectionHeight, this.getBounds().x+i*sectionWidth+width , this.getBounds().y+i*sectionHeight+height);
			}

		}
		
	}

}
