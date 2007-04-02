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

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
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
	 * Value for lolo fill level.
	 */
	private double _loloLevel = 0.2;
	
	/**
	 * Value for lo fill level.
	 */
	private double _loLevel = 0.4;
	
	/**
	 * Value for m fill level.
	 */
	private double _mLevel = 0.6;
	
	/**
	 * Value for hi fill level.
	 */
	private double _hiLevel= 0.8;
	
	/**
	 * Value for hihi fill level.
	 */
	private double _hihiLevel = 1.0;
	
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
	 * The color for lolo fill level.
	 */
	private Color _loloColor = CustomMediaFactory.getInstance().getColor(new RGB(255,0,0));
	
	/**
	 * The color for lo fill level.
	 */
	private Color _loColor = CustomMediaFactory.getInstance().getColor(new RGB(255,100,100));
	
	/**
	 * The color for m fill level.
	 */
	private Color _mColor = CustomMediaFactory.getInstance().getColor(new RGB(0,255,0));
	
	/**
	 * The color for hi fill level.
	 */
	private Color _hiColor = CustomMediaFactory.getInstance().getColor(new RGB(0,255,255));
	
	/**
	 * The color for hihi fill level.
	 */
	private Color _hihiColor = CustomMediaFactory.getInstance().getColor(new RGB(255,255,255));

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
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected synchronized void fillShape(final Graphics graphics) {
		Rectangle figureBounds = this.getBounds();
		Rectangle barRectangle = this.getBarRectangle(figureBounds);
		Rectangle fillLevelRectangle = this.getFillLevelRectangle(barRectangle);
		this.drawFigureBackground(graphics, figureBounds);
		this.drawBarRectangle(graphics, barRectangle);
		this.drawFillLevel(graphics, fillLevelRectangle);
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawRectangle(barRectangle);
		this.drawFillLine(graphics, fillLevelRectangle);
		if (_showMarks!=NONE) {
			this.drawMarkers(graphics, barRectangle);
		}
//		if (_showScale!=NONE) {
//			this.drawScale(graphics, barRectangle);
//		}
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
			return new Rectangle(bounds.x+xCorrection,bounds.y+yCorrection,bounds.width-2*xCorrection,bounds.height-(yCorrection+heightCorrection));
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
		return new Rectangle(bounds.x+xCorrection,bounds.y+yCorrection,bounds.width-(xCorrection+widthCorrection),bounds.height-2*yCorrection);
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
	 * Draws the background of this figure.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param figureBounds
	 * 				The rectangle to draw
	 */
	private void drawFigureBackground(final Graphics graphics, final Rectangle figureBounds) {
		graphics.setBackgroundColor(ColorConstants.white);
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
	}
	
	/**
	 * Draws the fill level.
	 * @param graphics
	 * 				The Graphics for drawing
	 * @param fillRectangle
	 * 				The rectangle to draw
	 */
	private void drawFillLevel(final Graphics graphics, final Rectangle fillRectangle) {
		if (this.getFill()<=this.getWeight(_loloLevel)) {
			graphics.setBackgroundColor(this.getLoloColor());
		} else
		if (this.getFill()<=this.getWeight(_loLevel)) {
			graphics.setBackgroundColor(this.getLoColor());
		} else
		if (this.getFill()<=this.getWeight(_mLevel)) {
			graphics.setBackgroundColor(this.getMColor());
		} else
		if (this.getFill()<=this.getWeight(_hiLevel)) {
			graphics.setBackgroundColor(this.getHiColor());
		} else
		if (this.getFill()<=this.getWeight(_hihiLevel)) {
			graphics.setBackgroundColor(this.getHihiColor());
		} else {
			graphics.setBackgroundColor(this.getForegroundColor());
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
			graphics.drawLine(fillRectangle.x+fillRectangle.width, fillRectangle.y, fillRectangle.x+fillRectangle.width, fillRectangle.y+fillRectangle.height);
		} else {
			graphics.drawLine(fillRectangle.x, fillRectangle.y, fillRectangle.x+fillRectangle.width, fillRectangle.y);
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
		boolean topLeft = _showMarks==TOP_LEFT;
		if (_orientationHorizontal) {
			int yCorrection = 0;
			if (_showMarks==BOTTOM_RIGHT) {
				yCorrection = barRectangle.height;
			}
			new HorizontalMarker(barRectangle.x+(int)(barRectangle.width*this.getWeight(_loloLevel)), barRectangle.y+yCorrection, _loloLevel, LABELS[0], _showValues, topLeft).draw(graphics);
			new HorizontalMarker(barRectangle.x+(int)(barRectangle.width*this.getWeight(_loLevel)), barRectangle.y+yCorrection, _loLevel, LABELS[1], _showValues, topLeft).draw(graphics);
			new HorizontalMarker(barRectangle.x+(int)(barRectangle.width*this.getWeight(_mLevel)), barRectangle.y+yCorrection, _mLevel, LABELS[2], _showValues, topLeft).draw(graphics);
			new HorizontalMarker(barRectangle.x+(int)(barRectangle.width*this.getWeight(_hiLevel)), barRectangle.y+yCorrection, _hiLevel, LABELS[3], _showValues, topLeft).draw(graphics);
			new HorizontalMarker(barRectangle.x+(int)(barRectangle.width*this.getWeight(_hihiLevel)), barRectangle.y+yCorrection, _hihiLevel, LABELS[4], _showValues, topLeft).draw(graphics);
		} else {
			int xCorrection = 0;
			if (_showMarks==BOTTOM_RIGHT) {
				xCorrection = barRectangle.width;
			}
			new VerticalMarker(barRectangle.x+xCorrection, barRectangle.y+(int)(barRectangle.height*(1-this.getWeight(_loloLevel))), _loloLevel, LABELS[0], _showValues, topLeft).draw(graphics);
			new VerticalMarker(barRectangle.x+xCorrection, barRectangle.y+(int)(barRectangle.height*(1-this.getWeight(_loLevel))), _loLevel, LABELS[1], _showValues, topLeft).draw(graphics);
			new VerticalMarker(barRectangle.x+xCorrection, barRectangle.y+(int)(barRectangle.height*(1-this.getWeight(_mLevel))), _mLevel, LABELS[2], _showValues, topLeft).draw(graphics);
			new VerticalMarker(barRectangle.x+xCorrection, barRectangle.y+(int)(barRectangle.height*(1-this.getWeight(_hiLevel))), _hiLevel, LABELS[3], _showValues, topLeft).draw(graphics);
			new VerticalMarker(barRectangle.x+xCorrection, barRectangle.y+(int)(barRectangle.height*(1-this.getWeight(_hihiLevel))), _hihiLevel, LABELS[4], _showValues, topLeft).draw(graphics);	
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
	 * @param barRectangle
	 * 				The rectangle of the bargraph
	 */
	private void drawScale(final Graphics graphics, final Rectangle barRectangle) {
		if (_orientationHorizontal) {
			System.out.println("hallo");
			new Scale(barRectangle.x,barRectangle.y,barRectangle.width,_scaleSectionCount,_orientationHorizontal).drawScale(graphics);
		}
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
		_loloColor = CustomMediaFactory.getInstance().getColor(rgb);
	}
	
	/**
	 * Gets the color for lolo fill level.
	 * @return Color
	 * 				The color for lolo fill level
	 */
	public Color getLoloColor() {
		return _loloColor;
	}
	
	/**
	 * Sets the color for lo fill level.
	 * @param rgb 
	 * 				The color for lo fill level
	 */
	public void setLoColor(final RGB rgb) {
		_loColor = CustomMediaFactory.getInstance().getColor(rgb);
	}
	
	/**
	 * Gets the color for lo fill level.
	 * @return Color
	 * 				The color for lo fill level
	 */
	public Color getLoColor() {
		return _loColor;
	}
	
	/**
	 * Sets the color for m fill level.
	 * @param rgb 
	 * 				The color for m fill level
	 */
	public void setMColor(final RGB rgb) {
		_mColor = CustomMediaFactory.getInstance().getColor(rgb);
	}
	
	/**
	 * Gets the color for m fill level.
	 * @return Color
	 * 				The color for m fill level
	 */
	public Color getMColor() {
		return _mColor;
	}
	
	/**
	 * Sets the color for hi fill level.
	 * @param rgb 
	 * 				The color for hi fill level
	 */
	public void setHiColor(final RGB rgb) {
		_hiColor = CustomMediaFactory.getInstance().getColor(rgb);
	}
	
	/**
	 * Gets the color for hi fill level.
	 * @return Color
	 * 				The color for hi fill level
	 */
	public Color getHiColor() {
		return _hiColor;
	}
	
	/**
	 * Sets the color for hihi fill level.
	 * @param rgb 
	 * 				The color for hihi fill level
	 */
	public void setHihiColor(final RGB rgb) {
		_hihiColor = CustomMediaFactory.getInstance().getColor(rgb);
	}
	
	/**
	 * Gets the color for hihi fill level.
	 * @return Color
	 * 				The color for hihi fill level
	 */
	public Color getHihiColor() {
		return _hihiColor;
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
		_loloLevel = loloLevel;
	}
	
	/**
	 * Gets the lolo level.
	 * @return double
	 * 				The lolo level
	 */
	public double getLoloLevel() {
		return _loloLevel;
	}
	
	/**
	 * Sets the lo level.
	 * @param loLevel
	 * 				The lo level
	 */
	public void setLoLevel(final double loLevel) {
		_loLevel = loLevel;
	}
	
	/**
	 * Gets the lo level.
	 * @return double
	 * 				The lo level
	 */
	public double getLoLevel() {
		return _loLevel;
	}
	
	/**
	 * Sets the m level.
	 * @param mLevel
	 * 				The m level
	 */
	public void setMLevel(final double mLevel) {
		_mLevel = mLevel;
	}
	
	/**
	 * Gets the m level.
	 * @return double
	 * 				The m level
	 */
	public double getMLevel() {
		return _mLevel;
	}
	
	/**
	 * Sets the hi level.
	 * @param hiLevel
	 * 				The hi level
	 */
	public void setHiLevel(final double hiLevel) {
		_hiLevel = hiLevel;
	}
	
	/**
	 * Gets the hi level.
	 * @return double
	 * 				The hi level
	 */
	public double getHiLevel() {
		return _hiLevel;
	}
	
	/**
	 * Sets the hihi level.
	 * @param hihiLevel
	 * 				The hihi level
	 */
	public void setHihiLevel(final double hihiLevel) {
		_hihiLevel = hihiLevel;
	}
	
	/**
	 * Gets the hihi level.
	 * @return double
	 * 				The hihi level
	 */
	public double getHihiLevel() {
		return _hihiLevel;
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
	 * This class represents a horizontal aligned Marker.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class HorizontalMarker {
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
		private int _width = 3;
		/**
		 * The height of this marker.
		 */
		private static final int HEIGHT = 5;
		
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
		 */
		public HorizontalMarker(final int x, final int y, final double value, final String text, final boolean showValues, final boolean above) {
			_x = x;
			_y = y;
			_text = text;
			_showValue = showValues;
			_value = value;
			if (above) {
				_direction = -1;
			}
		}
		
		/**
		 * Draws this Marker.
		 * @param graphics
		 * 				The Graphics for drawing
		 */
		public void draw(final Graphics graphics) {
			PointList pointList = new PointList();
			pointList.addPoint(_x, _y);
			pointList.addPoint(_x-_width, _y+HEIGHT*_direction);
			pointList.addPoint(_x+_width, _y+HEIGHT*_direction);
			pointList.addPoint(_x, _y);
			graphics.drawPolyline(pointList);
			int y = _y+(HEIGHT+2)*_direction;
			if (_direction<0) {
				y = y - graphics.getFontMetrics().getHeight();
			}
			if (_showValue) {
				graphics.drawString(String.valueOf(_value), this.getTextPositionX(_x, _text, graphics.getFontMetrics().getAverageCharWidth()), y);
				y = y + graphics.getFontMetrics().getHeight()*_direction;
			}
			graphics.drawString(_text, this.getTextPositionX(_x, _text, graphics.getFontMetrics().getAverageCharWidth()), y);
		}
		
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
		
	}
	
	/**
	 * This class represents a vertical aligned Marker.
	 * 
	 * @author Kai Meyer
	 *
	 */
	private final class VerticalMarker {
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
		private static final int WIDTH = 5;
		/**
		 * The height of this marker.
		 */
		private static final int HEIGHT = 3;
		
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
		private final String _text;
		
		/**
		 * The direction of this Marker.
		 */
		private int _direction = 1;
		
		/**
		 * Construktor.
		 * @param x
		 * 				The x coordinate for this Marker
		 * @param y
		 * 				The y coordinate for this Marker
		 * @param value
		 * 				The value to display
		 * @param text
		 * 				The text to display
		 * @param showValues
		 * 				True, if the values should be shown, false otherwise
		 * @param left
		 * 				True, if the marker should be left of the x value, false otherwise
		 */
		public VerticalMarker(final int x, final int y, final double value, final String text, final boolean showValues, final boolean left) {
			_x = x;
			_y = y;
			_text = text;
			_showValue = showValues;
			_value = value;
			if (left) {
				_direction = -1;
			}
		}
		
		/**
		 * Draws this Marker.
		 * @param graphics
		 * 				The Graphics for drawing
		 */
		public void draw(final Graphics graphics) {
			//graphics.fillRectangle(_x,_y,WIDTH,HEIGHT);
			PointList pointList = new PointList();
			pointList.addPoint(_x, _y);
			pointList.addPoint(_x+WIDTH*_direction, _y-HEIGHT);
			pointList.addPoint(_x+WIDTH*_direction, _y+HEIGHT);
			pointList.addPoint(_x, _y);
			graphics.drawPolyline(pointList);
			int x = _x+(WIDTH+2)*_direction;
			if (_direction<0) {
				x = x - TEXTWIDTH;
			}
			if (_showValue) {
				graphics.drawString(String.valueOf(_value), x, this.getTextPositionY(_y, graphics.getFontMetrics().getHeight()));
				x = x + TEXTWIDTH*_direction;
			}
			graphics.drawString(_text.toString(), x, this.getTextPositionY(_y, graphics.getFontMetrics().getHeight()));
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
	private final class Scale {
		
		/**
		 * The x coordinate of this Scale.
		 */
		private final int _x;
		/**
		 * The y coordinate of this Scale.
		 */
		private final int _y;
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
			_x = x;
			_y = y;
			_length = length;
			_sectionCount = sectionCount;
			_horizontal = orientationHorizontal;
		}
		
		/**
		 * Draws this scale.
		 * @param graphics
		 * 				The Graphics for drawing
		 */
		public void drawScale(final Graphics graphics) {
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
			System.out.println("Scale.drawScale() "+sectionHeight+" "+sectionWidth);
			for (int i=0;i<_sectionCount+1;i++) {
				graphics.drawLine(_x+i*sectionWidth, _y+i*sectionHeight, _x+i*sectionWidth+width , _y+i*sectionHeight+height);
			}
		}
		
	}

}
