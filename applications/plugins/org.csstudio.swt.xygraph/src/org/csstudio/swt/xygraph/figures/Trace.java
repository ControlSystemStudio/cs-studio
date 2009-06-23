package org.csstudio.swt.xygraph.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;


/**
 * The trace figure.
 * @author Xihui Chen
 *
 */
public class Trace extends Figure implements IDataProviderListener, IAxisListener{
	
	/** The way how the trace will be drawn. */
	/**
	 * @author Xihui Chen
	 *
	 */
	public enum TraceType {
		/** Solid Line */
		SOLID_LINE("Solid Line"),
		
		/** Dash Line */
		DASH_LINE("Dash Line"),
		
		/** Only draw point whose style is defined by pointStyle. 
		 * Its size is defined by pointSize. */
		POINT("Point"),
		
		/** Draw each data point as a bar whose width is defined by lineWidth.
		 *  The data point is in the middle of the bar on X direction. 
		 *  The bottom of the bar depends on the baseline.
		 *  The alpha of the bar is defined by areaAlpha. */
		BAR("Bar"),
		
		/** Fill the area under the trace.  
		 * The bottom of the filled area depends on the baseline.
		 *  The alpha of the filled area is defined by areaAlpha. */ 
		AREA("Area"),
		/**
		 * Solid line in step. It looks like the y value(on vertical direction) changed firstly.
		 */
		STEP_VERTICALLY("Step Vertically"),
		
		/**
		 * Solid line in step. It looks like the x value(on horizontal direction) changed firstly.
		 */
		STEP_HORIZONTALLY("Step Horizontally");
		
		/** Draw a single point. Only the last data point will be drawn.*/
		//SINGLE_POINT("Single Point");
		
		private TraceType(String description) {
			 this.description = description;
		}
		private String description;
		
		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(TraceType p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	public enum BaseLine {
		NEGATIVE_INFINITY,
		ZERO,		
		POSITIVE_INFINITY;
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(BaseLine p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	public enum PointStyle{
		NONE("None"),
		POINT("point(" + (char)7 + ")"),
		CIRCLE("Circle(o)"),
		TRIANGLE("Triangle"),
		FILLED_TRIANGLE("Filled Triangle"),
		SQUARE("Square"),
		FILLED_SQUARE("Filled Square"),
		DIAMOND("Diamond"),
		FILLED_DIAMOND("Filled Diamond"),
		XCROSS("XCross(x)"),
		CROSS("Cross(+)"),
		BAR("Bar(|)");
		
		private PointStyle(String description) {
			 this.description = description;
		}
		private String description;
		
		@Override
		public String toString() {
			return description;
		}
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(PointStyle p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}	
	
	
	public enum ErrorBarType{
		NONE,
		PLUS,		
		MINUS,
		BOTH;
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(ErrorBarType p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}

	
	private String name;
	
	private IDataProvider traceDataProvider;
	
	private Axis xAxis;	
	private Axis yAxis;	
	
	private Color traceColor;
	
	private TraceType traceType = TraceType.SOLID_LINE;
	
	private BaseLine baseLine = BaseLine.ZERO;
	
	private PointStyle pointStyle = PointStyle.NONE;
	
	/**
	 * If traceType is bar, this is the width of the bar.
	 */
	private int lineWidth = 1; 
	
	private int pointSize = 4;
	
	private int areaAlpha = 100;
	
	private boolean antiAliasing = true;

	private boolean errorBarEnabled = false;
	private ErrorBarType yErrorBarType = ErrorBarType.BOTH;
	private ErrorBarType xErrorBarType = ErrorBarType.BOTH;
	private int errorBarCapWidth = 4;
	private Color errorBarColor;
	private boolean drawYErrorInArea = false;
	private XYGraph xyGraph;
	
	private List<ISample> hotSampleist;
	
	public Trace(String name, Axis xAxis, Axis yAxis, IDataProvider dataProvider) {
		this.setName(name);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		xAxis.addTrace(this);
		yAxis.addTrace(this);
		xAxis.addListener(this);
		yAxis.addListener(this);
		setDataProvider(dataProvider);
		hotSampleist = new ArrayList<ISample>();
	}
	
	
	private void drawErrorBar(Graphics graphics, Point dpPos, ISample dp){
		graphics.pushState();
		if(errorBarColor == null)
			errorBarColor = traceColor;
		graphics.setForegroundColor(errorBarColor);
		graphics.setLineStyle(SWT.LINE_SOLID);
		graphics.setLineWidth(1);
		Point ep;
		switch (yErrorBarType) {
		case BOTH:
		case MINUS:
			ep = new Point(xAxis.getValuePosition(dp.getXValue(), false),
				yAxis.getValuePosition(dp.getYValue() - dp.getYMinusError(), false));
			graphics.drawLine(dpPos, ep);
			graphics.drawLine(ep.x - errorBarCapWidth/2, ep.y, ep.x + errorBarCapWidth/2, ep.y);
			if(yErrorBarType != ErrorBarType.BOTH)
			break;
		case PLUS:
			ep = new Point(xAxis.getValuePosition(dp.getXValue(), false),
				yAxis.getValuePosition(dp.getYValue() + dp.getYPlusError(), false));
			graphics.drawLine(dpPos, ep);
			graphics.drawLine(ep.x - errorBarCapWidth/2, ep.y, ep.x + errorBarCapWidth/2, ep.y);
			break;			
		default:
			break;
		}
		
		switch (xErrorBarType) {
		case BOTH:
		case MINUS:
			ep = new Point(xAxis.getValuePosition(dp.getXValue() - dp.getXMinusError(), false),
				yAxis.getValuePosition(dp.getYValue(), false));
			graphics.drawLine(dpPos, ep);
			graphics.drawLine(ep.x, ep.y  - errorBarCapWidth/2, ep.x, ep.y + errorBarCapWidth/2);
			if(xErrorBarType != ErrorBarType.BOTH)
			break;
		case PLUS:
			ep = new Point(xAxis.getValuePosition(dp.getXValue() + dp.getXPlusError(), false),
				yAxis.getValuePosition(dp.getYValue(), false));
			graphics.drawLine(dpPos, ep);
			graphics.drawLine(ep.x , ep.y - errorBarCapWidth/2, ep.x, ep.y + errorBarCapWidth/2);
			break;			
		default:
			break;
		}
		
		graphics.popState();
	}
	
	
	private void drawYErrorArea(Graphics graphics, ISample predp, ISample dp, Point predpPos, Point dpPos){
		graphics.pushState();
		if(errorBarColor == null)
			errorBarColor = traceColor;
		graphics.setBackgroundColor(errorBarColor);
		graphics.setAlpha(areaAlpha);
		Point preEp, ep;
		switch (yErrorBarType) {
		case BOTH:
		case PLUS:
			preEp = new Point(xAxis.getValuePosition(predp.getXValue(), false),
				yAxis.getValuePosition(predp.getYValue() + predp.getYPlusError(), false));
			ep = new Point(xAxis.getValuePosition(dp.getXValue(), false),
				yAxis.getValuePosition(dp.getYValue() + dp.getYPlusError(), false));
			graphics.fillPolygon(new int[]{predpPos.x, predpPos.y,
					preEp.x, preEp.y, ep.x, ep.y, dpPos.x, dpPos.y});
			if(yErrorBarType != ErrorBarType.BOTH)
				break;
		case MINUS:
			preEp = new Point(xAxis.getValuePosition(predp.getXValue(), false),
				yAxis.getValuePosition(predp.getYValue() - predp.getYMinusError(), false));
			ep = new Point(xAxis.getValuePosition(dp.getXValue(), false),
				yAxis.getValuePosition(dp.getYValue() - dp.getYMinusError(), false));
			graphics.fillPolygon(new int[]{predpPos.x, predpPos.y,
					preEp.x, preEp.y, ep.x, ep.y, dpPos.x, dpPos.y});
			break;
		default:
			break;
		}
		graphics.popState();
	}
	
	/**Draw point with the pointStyle and size of the trace;
	 * @param graphics
	 * @param pos
	 */
	public void drawPoint(Graphics graphics, Point pos){
		graphics.pushState();
		graphics.setBackgroundColor(traceColor);
		graphics.setForegroundColor(traceColor);
		graphics.setLineWidth(1);
		graphics.setLineStyle(SWT.LINE_SOLID);
		switch (pointStyle) {
		case POINT:			
			graphics.fillOval(new Rectangle(
					pos.x - pointSize/2, pos.y - pointSize/2,
					pointSize, pointSize));
			break;
		case CIRCLE:
			graphics.drawOval(new Rectangle(
					pos.x - pointSize/2, pos.y - pointSize/2,
					pointSize, pointSize));
			break;
		case TRIANGLE:
			graphics.drawPolygon(new int[]{pos.x-pointSize/2, pos.y + pointSize/2,
					pos.x, pos.y - pointSize/2, pos.x + pointSize/2, pos.y + pointSize/2});
			break;			
		case FILLED_TRIANGLE:
			graphics.fillPolygon(new int[]{pos.x-pointSize/2, pos.y + pointSize/2,
					pos.x, pos.y - pointSize/2, pos.x + pointSize/2, pos.y + pointSize/2});
			break;	
		case SQUARE:
			graphics.drawRectangle(new Rectangle(
					pos.x - pointSize/2, pos.y - pointSize/2,
					pointSize, pointSize));
			break;
		case FILLED_SQUARE:
			graphics.fillRectangle(new Rectangle(
					pos.x - pointSize/2, pos.y - pointSize/2,
					pointSize, pointSize));
			break;
		case BAR:
			graphics.drawLine(pos.x, pos.y - pointSize/2,
					pos.x, pos.y + pointSize/2);
			break;
		case CROSS:
			graphics.drawLine(pos.x, pos.y - pointSize/2,
					pos.x, pos.y + pointSize/2);
			graphics.drawLine(pos.x - pointSize/2, pos.y,
					pos.x + pointSize/2, pos.y);
			break;
		case XCROSS:
			graphics.drawLine(pos.x - pointSize/2, pos.y - pointSize/2,
					pos.x + pointSize/2, pos.y + pointSize/2);
			graphics.drawLine(pos.x + pointSize/2 , pos.y - pointSize/2,
					pos.x - pointSize/2, pos.y + pointSize/2);
			break;
		case DIAMOND:
			graphics.drawPolyline(new int[]{
				pos.x, pos.y - pointSize/2,
				pos.x - pointSize/2, pos.y,
				pos.x, pos.y + pointSize/2,
				pos.x + pointSize/2, pos.y,
				pos.x, pos.y - pointSize/2});
			break;
		case FILLED_DIAMOND:
			graphics.fillPolygon(new int[]{
				pos.x, pos.y - pointSize/2,
				pos.x - pointSize/2, pos.y,
				pos.x, pos.y + pointSize/2,
				pos.x + pointSize/2, pos.y});
			break;
		default:
			break;
		}
		graphics.popState();
	}
	
	/**Draw line with the line style and line width of the trace.
	 * @param graphics
	 * @param p1
	 * @param p2
	 */
	public void drawLine(Graphics graphics, Point p1, Point p2){
		graphics.pushState();
		graphics.setForegroundColor(traceColor);
		graphics.setLineWidth(lineWidth);
		switch (traceType) {
		case SOLID_LINE:
			graphics.setLineStyle(SWT.LINE_SOLID);
			graphics.drawLine(p1, p2);
			break;
		case BAR:
			graphics.setAlpha(areaAlpha);
			graphics.setLineStyle(SWT.LINE_SOLID);
			graphics.drawLine(p1, p2);
			break;
		case DASH_LINE:
			graphics.setLineStyle(SWT.LINE_DASH);
			graphics.drawLine(p1, p2);
			break;
		case AREA:
			int basey;
			switch (baseLine) {
			case NEGATIVE_INFINITY:
				basey = yAxis.getValuePosition(yAxis.getRange().getLower(), false);
				break;
			case POSITIVE_INFINITY:
				basey = yAxis.getValuePosition(yAxis.getRange().getUpper(), false);
				break;
			default:
				basey = yAxis.getValuePosition(0, false);
				break;
			}
			graphics.setAlpha(areaAlpha);
			graphics.setBackgroundColor(traceColor);
			graphics.fillPolygon(new int[]{
					p1.x, p1.y,
					p1.x, basey,
					p2.x, basey,
					p2.x, p2.y
			});
			break;
		case STEP_HORIZONTALLY:
			graphics.setLineStyle(SWT.LINE_SOLID);
			Point ph = new Point(p2.x, p1.y);
			graphics.drawLine(p1, ph);
			graphics.drawLine(ph, p2);
			break;
		case STEP_VERTICALLY:
			graphics.setLineStyle(SWT.LINE_SOLID);
			Point pv = new Point(p1.x, p2.y);
			graphics.drawLine(p1, pv);
			graphics.drawLine(pv, p2);
			break;	
			
		default:
			break;
		}
		graphics.popState();
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		
		super.paintFigure(graphics);
		graphics.pushState();
		graphics.setAntialias(antiAliasing? SWT.ON : SWT.OFF);
		ISample predp = null;
		Point predpPos = null;
		boolean predpInRange = false;
		boolean dpInRange = true;
		Point dpPos = null;
		ISample origindp =null;
		hotSampleist.clear();
		if(traceDataProvider == null)
			throw new RuntimeException("No DataProvider defined for trace: " + name);
		if(traceDataProvider.getSize()>0){
			int startIndex =0;
			int endIndex = traceDataProvider.getSize()-1;
			if(traceDataProvider.isChronological()){
				Range indexRange = getIndexRangeOnXAxis();
				if(indexRange == null){
					startIndex = 0; 
					endIndex = -1;
				}else{
					startIndex = (int) indexRange.getLower();
					endIndex = (int) indexRange.getUpper();
				}
		//		System.out.println(name + ": " + startIndex + "  " + endIndex + " size: " + traceDataProvider.getSize());

			}
			for(int i= startIndex; i<=endIndex; i++){
				ISample dp = traceDataProvider.getSample(i);
				//if the data is not in the plot area
				dpInRange =
					xAxis.getRange().inRange(dp.getXValue()) && yAxis.getRange().inRange(dp.getYValue());
				
				//draw point
				if(dpInRange){
					dpPos = new Point(xAxis.getValuePosition(dp.getXValue(), false),
							yAxis.getValuePosition(dp.getYValue(), false));
					hotSampleist.add(dp);
					drawPoint(graphics, dpPos);
					if(errorBarEnabled && !drawYErrorInArea)
						drawErrorBar(graphics, dpPos, dp);
					
				}
				if(traceType == TraceType.POINT && !drawYErrorInArea)
					continue; // no need to draw line			
				
				//draw line
				if(traceType == TraceType.BAR){					
					switch (baseLine) {
					case NEGATIVE_INFINITY:
						predp = new Sample(dp.getXValue(), yAxis.getRange().getLower());					
						break;
					case POSITIVE_INFINITY:
						predp = new Sample(dp.getXValue(), yAxis.getRange().getUpper());					
						break;				
					default:
						predp = new Sample(dp.getXValue(), 0);
						break;
					}
					predpInRange = xAxis.getRange().inRange(predp.getXValue()) && yAxis.getRange().inRange(predp.getYValue());
				}
				if(predp == null){
					predp = dp;				
					predpInRange = dpInRange;
					continue;
				}
				
				origindp = dp; //save original dp
				if(traceType != TraceType.AREA){
					if(!predpInRange && !dpInRange){ //both are out of plot area
						ISample[] dpTuple = getIntersection(predp, dp);
						if(dpTuple[0] == null || dpTuple[1] == null){ // no intersection with plot area
							predp = dp;
							predpInRange=dpInRange;
							continue;
						}else{
							predp = dpTuple[0];					
							dp = dpTuple[1];
						}				
					}else if(!predpInRange || !dpInRange){ // one in and one out
						//calculate the intersection point with the boundary of plot area.
						if(!predpInRange){
							predp = getIntersection(predp, dp)[0];		
							if(predp == null){ // no intersection
								predp = origindp;
								predpInRange = dpInRange;
								continue;
							}
						}else{				
							dp = getIntersection(predp, dp)[0];				
							if(dp == null){ // no intersection
								predp = origindp;
								predpInRange = dpInRange;
								continue;
							}
						}				
					}
				}
				
				
				predpPos = new Point(xAxis.getValuePosition(predp.getXValue(), false),
								yAxis.getValuePosition(predp.getYValue(), false));
				dpPos = new Point(xAxis.getValuePosition(dp.getXValue(), false),
								yAxis.getValuePosition(dp.getYValue(), false));			
							
				if(!dpPos.equals(predpPos)){
					if(errorBarEnabled && drawYErrorInArea && traceType!=TraceType.BAR)
						drawYErrorArea(graphics, predp, dp, predpPos, dpPos);						
					drawLine(graphics, predpPos, dpPos);						
				}
						
				
				
				
				predp = origindp;
				predpInRange = dpInRange;			
			}			
		}
		graphics.popState();
	}
	
	/**
	 * @param dp1
	 * @param dp2
	 * @return The intersection points between the line, 
	 * which between the two data points, and the axis.
	 */
	private ISample[] getIntersection(ISample dp1, ISample dp2){
		ISample[] dpTuple = new Sample[]{null, null};
		double x1 = dp1.getXValue();
		double y1 = dp1.getYValue();
		double x2 = dp2.getXValue();
		double y2 = dp2.getYValue();
		double dx = x2 - x1;
		double dy = y2 - y1;
		
		double x;
		double y;
		//Intersection with xAxis
		double ymin = yAxis.getRange().getLower();
		x = (ymin-y1)*dx/dy + x1;
		y = ymin;
		if(dy != 0 && evalDP(x, y, dp1, dp2))
			dpTuple[0] = new Sample(x, y);
		
		//Intersection with yAxis
		double xmin = xAxis.getRange().getLower();
		x = xmin;
		y = (xmin-x1)*dy/dx+y1;
		if(dx !=0 && evalDP(x, y, dp1, dp2))
			dpTuple[dpTuple[0]==null? 0 : 1] = 	new Sample(x, y);
		
		//Intersection with up xAxis
		double ymax = yAxis.getRange().getUpper();
		x = (ymax-y1)*dx/dy+x1;
		y = ymax;
		if(dy != 0 && evalDP(x, y, dp1, dp2))
			dpTuple[dpTuple[0]==null? 0 : 1] = 	new Sample(x, y);
		
		//Intersection with right yAxis
		double xmax = xAxis.getRange().getUpper();
		x = xmax;
		y = (xmax-x1)*dy/dx + y1;
		if(dx != 0 && evalDP(x, y, dp1, dp2))
			dpTuple[dpTuple[0]==null? 0 : 1] = 	new Sample(x, y);
		return dpTuple;
	}
	
	private boolean evalDP(double x, double y, ISample dp1, ISample dp2){
		//if dp is between dp1 and dp2
		if(new Range(dp1.getXValue(), dp2.getXValue()).inRange(x) && 
				new Range(dp1.getYValue(), dp2.getYValue()).inRange(y)){
			ISample  dp = new Sample(x, y);
			if(dp.equals(dp1) || dp.equals(dp2))
				return false;
			return xAxis.getRange().inRange(x) && yAxis.getRange().inRange(y);
		}		
		return false;	
	}	

	/**
	 * @param axis the xAxis to set
	 */
	public void setXAxis(Axis axis) {
		if(xAxis == axis)
			return;
		if(xAxis != null){
			xAxis.removeListenr(this);
			xAxis.removeTrace(this);
		}
		
		if(traceDataProvider != null){
			traceDataProvider.removeDataProviderListener(xAxis);
			traceDataProvider.addDataProviderListener(axis);
		}			
		xAxis = axis;
		xAxis.addTrace(this);
		xAxis.addListener(this);
		revalidate();		
	}

	/**
	 * @return the xAxis
	 */
	public Axis getXAxis() {
		return xAxis;
	}

	/**
	 * @param axis the yAxis to set
	 */
	public void setYAxis(Axis axis) {		
		if(yAxis == axis)
			return;
		
		xyGraph.getLegendMap().get(yAxis).removeTrace(this);
		if(xyGraph.getLegendMap().get(yAxis).getTraceList().size() <=0){
			xyGraph.remove(xyGraph.getLegendMap().get(yAxis));
			xyGraph.getLegendMap().remove(yAxis);
		}		
		if(xyGraph.getLegendMap().containsKey(axis))
			xyGraph.getLegendMap().get(axis).addTrace(this);
		else{
			xyGraph.getLegendMap().put(axis, new Legend());
			xyGraph.getLegendMap().get(axis).addTrace(this);
			xyGraph.add(xyGraph.getLegendMap().get(axis));
		}
		if(yAxis != null){
			yAxis.removeListenr(this);
			yAxis.removeTrace(this);
		}		
		if(traceDataProvider != null){
			traceDataProvider.removeDataProviderListener(yAxis);
			traceDataProvider.addDataProviderListener(axis);
		}			
		yAxis = axis;
		yAxis.addTrace(this);
		yAxis.addListener(this);	
		
		
		xyGraph.repaint();
	}

	/**
	 * @param traceDataProvider the traceDataProvider to set
	 */
	public void setDataProvider(
			IDataProvider traceDataProvider) {
		traceDataProvider.addDataProviderListener(this);
		traceDataProvider.addDataProviderListener(xAxis);
		traceDataProvider.addDataProviderListener(yAxis);
		this.traceDataProvider = traceDataProvider;
	}

	/**
	 * @return the traceType
	 */
	public TraceType getTraceType() {
		return traceType;
	}

	/**
	 * @param traceColor the traceColor to set
	 */
	public void setTraceColor(Color traceColor) {
		this.traceColor = traceColor;
	}
	
	/**
	 * @return the traceColor
	 */
	public Color getTraceColor() {
		return traceColor;
	}

	/**
	 * @param traceType the traceType to set
	 */
	public void setTraceType(TraceType traceType) {
		this.traceType = traceType;
	}

	/**
	 * @param baseLine the baseLine to set
	 */
	public void setBaseLine(BaseLine baseLine) {
		this.baseLine = baseLine;
	}

	/**
	 * @param pointStyle the pointStyle to set
	 */
	public void setPointStyle(PointStyle pointStyle) {
		this.pointStyle = pointStyle;
	}

	/**
	 * @param lineWidth the lineWidth to set
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @param pointSize the pointSize to set
	 */
	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}

	/**
	 * @param areaAlpha the areaAlpha to set
	 */
	public void setAreaAlpha(int areaAlpha) {
		this.areaAlpha = areaAlpha;
	}

	/**
	 * @param antiAliasing the antiAliasing to set
	 */
	public void setAntiAliasing(boolean antiAliasing) {
		this.antiAliasing = antiAliasing;
	}
	
	/**
	 * @param name the name of the trace to set
	 */
	public void setName(String name) {
		this.name = name;
		revalidate();
	}

	/**
	 * @return the name of the trace
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the pointSize
	 */
	public int getPointSize() {
		return pointSize;
	}

	/**
	 * @return the areaAlpha
	 */
	public int getAreaAlpha() {
		return areaAlpha;
	}

	/**
	 * @return the yAxis
	 */
	public Axis getYAxis() {
		return yAxis;
	}	
	
	@Override
	public String toString() {
		return name;
	}

	public void dataChanged(IDataProvider dataProvider) {		
		//if the axis has been repainted, it will cause the trace to be repainted autoly,
		//the trace doesn't have to be repainted again.
		if(!xAxis.performAutoScale(false) && !yAxis.performAutoScale(false))
			repaint();
	}	

	/**Get the corresponding sample index range based on the range of xAxis.
	 * This will help trace to draw only the part of data confined in xAxis.
	 * So it may also provides the first data out of the range to make the line could be drawn
	 * between inside data and outside data.    
	 * <b>This method only works for chronological data,
	 * which means the data is naturally sorted on xAxis.</b>
	 * @return the Range of the index.
	 */
	private Range getIndexRangeOnXAxis() {
		Range axisRange = xAxis.getRange();
		if(traceDataProvider.getSize() <=0)
			return null;
		if(axisRange.getLower()> traceDataProvider.getSample(traceDataProvider.getSize()-1).getXValue() 
				|| axisRange.getUpper()<traceDataProvider.getSample(0).getXValue())
			return null;
		
		int lowIndex = 0;
		int highIndex = traceDataProvider.getSize()-1;
		if(axisRange.getLower()>traceDataProvider.getSample(0).getXValue())
				lowIndex = nearBinarySearchX(axisRange.getLower(), true);			
		if(axisRange.getUpper()<traceDataProvider.getSample(highIndex).getXValue())
				highIndex = nearBinarySearchX(axisRange.getUpper(), false);
		return new Range(lowIndex, highIndex);
	}

	
	// It will return the index on the closest left(if left is true) or right of the data
	// Like public version, but without range checks. 
    private int nearBinarySearchX(double key, boolean left) {
	int low = 0;
	int high = traceDataProvider.getSize() - 1;

	while (low <= high) {
	    int mid = (low + high) >>> 1;
	    double midVal = traceDataProvider.getSample(mid).getXValue();

            int cmp;
            if (midVal < key) {
                cmp = -1;   // Neither val is NaN, thisVal is smaller
            } else if (midVal > key) {
                cmp = 1;    // Neither val is NaN, thisVal is larger
            } else {
                long midBits = Double.doubleToLongBits(midVal);
                long keyBits = Double.doubleToLongBits(key);
                cmp = (midBits == keyBits ?  0 : // Values are equal
                       (midBits < keyBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                        1));                     // (0.0, -0.0) or (NaN, !NaN)
            }

	    if (cmp < 0){
	    	if(mid < traceDataProvider.getSize()-1 && key < traceDataProvider.getSample(mid+1).getXValue() ){
	    		if(left)		    		
	    			return mid;
	    		else
	    			return mid+1;
	    	}
	    	low = mid + 1;
	    }
	    
	    else if (cmp > 0){
	    	if(mid>0 && key > traceDataProvider.getSample(mid-1).getXValue())
	    		if(left)		    		
	    			return mid-1;
	    		else
	    			return mid;
	    	high = mid - 1;
	    }
		
	    else
		return mid; // key found
	}
	return -(low + 1);  // key not found.
    }

	public void axisRevalidated(Axis axis) {
		repaint();
	}
	/**
	 * @return the traceDataProvider
	 */
	public IDataProvider getDataProvider() {
		return traceDataProvider;
	}

	/**
	 * @param errorBarEnabled the errorBarEnabled to set
	 */
	public void setErrorBarEnabled(boolean errorBarEnabled) {
		this.errorBarEnabled = errorBarEnabled;
	}

	/**
	 * @param errorBarType the yErrorBarType to set
	 */
	public void setYErrorBarType(ErrorBarType errorBarType) {
		yErrorBarType = errorBarType;
	}

	/**
	 * @param errorBarType the xErrorBarType to set
	 */
	public void setXErrorBarType(ErrorBarType errorBarType) {
		xErrorBarType = errorBarType;
	}

	/**
	 * @param drawYErrorInArea the drawYErrorArea to set
	 */
	public void setDrawYErrorInArea(boolean drawYErrorInArea) {
		this.drawYErrorInArea = drawYErrorInArea;
	}

	/**
	 * @param errorBarCapWidth the errorBarCapWidth to set
	 */
	public void setErrorBarCapWidth(int errorBarCapWidth) {
		this.errorBarCapWidth = errorBarCapWidth;
	}

	/**
	 * @param errorBarColor the errorBarColor to set
	 */
	public void setErrorBarColor(Color errorBarColor) {
		this.errorBarColor = errorBarColor;
	}


	/**Hot Sample is the sample on the trace which has been drawn in plot area.
	 * @return the hotPointList
	 */
	public List<ISample> getHotSampleList() {
		return hotSampleist;
	}


	/**
	 * @return the baseLine
	 */
	public BaseLine getBaseLine() {
		return baseLine;
	}


	/**
	 * @return the pointStyle
	 */
	public PointStyle getPointStyle() {
		return pointStyle;
	}


	/**
	 * @return the lineWidth
	 */
	public int getLineWidth() {
		return lineWidth;
	}


	/**
	 * @return the antiAliasing
	 */
	public boolean isAntiAliasing() {
		return antiAliasing;
	}


	/**
	 * @return the errorBarEnabled
	 */
	public boolean isErrorBarEnabled() {
		return errorBarEnabled;
	}


	/**
	 * @return the yErrorBarType
	 */
	public ErrorBarType getYErrorBarType() {
		return yErrorBarType;
	}


	/**
	 * @return the xErrorBarType
	 */
	public ErrorBarType getXErrorBarType() {
		return xErrorBarType;
	}


	/**
	 * @return the errorBarCapWidth
	 */
	public int getErrorBarCapWidth() {
		return errorBarCapWidth;
	}


	/**
	 * @return the errorBarColor
	 */
	public Color getErrorBarColor() {
		if(errorBarColor == null)
			errorBarColor = traceColor;
		return errorBarColor;
	}


	/**
	 * @return the drawYErrorInArea
	 */
	public boolean isDrawYErrorInArea() {
		return drawYErrorInArea;
	}


	/**
	 * @param xyGraph the xyGraph to set
	 */
	public void setXYGraph(XYGraph xyGraph) {
		this.xyGraph = xyGraph;
	}


	/**
	 * @return the xyGraph
	 */
	public XYGraph getXYGraph() {
		return xyGraph;
	}


}
