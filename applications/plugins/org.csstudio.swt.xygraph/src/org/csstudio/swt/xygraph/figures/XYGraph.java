package org.csstudio.swt.xygraph.figures;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.LinearScale.Orientation;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.swt.xygraph.undo.ZoomCommand;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

/**
 * XY-Graph Figure.
 * @author Xihui Chen
 *
 */
public class XYGraph extends Figure{
	
	private static final int GAP = 2;
	public final static Color WHITE_COLOR = ColorConstants.white;
	public final static Color BLACK_COLOR = ColorConstants.black;
	
	public final static Color[] DEFAULT_TRACES_COLOR = new Color[]{
		ColorConstants.red,
		ColorConstants.blue,	
		ColorConstants.darkGreen,
		ColorConstants.orange,			
		ColorConstants.darkBlue,
		ColorConstants.cyan,
		ColorConstants.green,
		ColorConstants.yellow,
		ColorConstants.black		
	};
	
	private int traceNum = 0;
	private boolean transparent = true;
	private boolean showLegend = true;
	
	private Map<Axis, Legend> legendMap;
	private String title;
	private Color titleColor;
	private Label titleLabel;
	
	private List<Axis> xAxisList;
	private List<Axis> yAxisList;
	private PlotArea plotArea;
	public Axis primaryXAxis;
	public Axis primaryYAxis;
	
	private OperationsManager operationsManager;
	
	private ZoomType zoomType;
	
	/**
	 * Constructor.
	 */
	public XYGraph() {
		setOpaque(!transparent);
		legendMap = new LinkedHashMap<Axis, Legend>();
		titleLabel = new Label();
		setTitleFont(XYGraphMediaFactory.getInstance().getFont(
				new FontData("Arial", 12, SWT.BOLD)));
		//titleLabel.setVisible(false);
		xAxisList = new ArrayList<Axis>();
		yAxisList = new ArrayList<Axis>();
		plotArea = new PlotArea(this);
		add(titleLabel);		
		add(plotArea);
		primaryYAxis = new Axis("Y-Axis", true);
		primaryYAxis.setOrientation(Orientation.VERTICAL);
		primaryYAxis.setTickLableSide(LabelSide.Primary);
		primaryYAxis.setAutoScaleThreshold(0.1);
		addAxis(primaryYAxis);
		
		primaryXAxis = new Axis("X-Axis", false);
		primaryXAxis.setOrientation(Orientation.HORIZONTAL);
		primaryXAxis.setTickLableSide(LabelSide.Primary);
		addAxis(primaryXAxis);
		
		operationsManager = new OperationsManager();		
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea().getCopy();
		boolean hasRightYAxis = false;
		boolean hasTopXAxis = false;
		if(titleLabel != null && titleLabel.isVisible() && !titleLabel.getText().equals("")){
			Dimension titleSize = titleLabel.getPreferredSize();
			titleLabel.setBounds(new Rectangle(clientArea.x + clientArea.width/2 - titleSize.width/2,
					clientArea.y, titleSize.width, titleSize.height));
			clientArea.y += titleSize.height + GAP;
			clientArea.height -= titleSize.height + GAP;
		}
		if(showLegend){
			List<Integer> rowHPosList = new ArrayList<Integer>();
			List<Dimension> legendSizeList = new ArrayList<Dimension>();
			List<Integer> rowLegendNumList = new ArrayList<Integer>();		
			List<Legend> legendList = new ArrayList<Legend>();
			Object[] yAxes = legendMap.keySet().toArray();
			int hPos = 0;
			int rowLegendNum = 0;
			for(int i = 0; i< yAxes.length; i++){
				Legend legend = legendMap.get(yAxes[i]);
				if(legend != null && legend.isVisible()){
					legendList.add(legend);
					Dimension legendSize = legend.getPreferredSize(clientArea.width, clientArea.height);
					legendSizeList.add(legendSize);
					if((hPos+legendSize.width + GAP) > clientArea.width){
						if(rowLegendNum ==0)
							break;
						rowHPosList.add(clientArea.x + (clientArea.width-hPos)/2);
						rowLegendNumList.add(rowLegendNum);											
						rowLegendNum = 1;
						hPos = legendSize.width + GAP;
						clientArea.height -=legendSize.height +GAP;
						if(i==yAxes.length-1){
							hPos =legendSize.width + GAP;
							rowLegendNum = 1;		
							rowHPosList.add(clientArea.x + (clientArea.width-hPos)/2);
							rowLegendNumList.add(rowLegendNum);
							clientArea.height -=legendSize.height +GAP;
						}	
					}else{
						hPos+=legendSize.width + GAP;
						rowLegendNum++;
						if(i==yAxes.length-1){
							rowHPosList.add(clientArea.x + (clientArea.width-hPos)/2);
							rowLegendNumList.add(rowLegendNum);
							clientArea.height -=legendSize.height +GAP;
						}
					}					
				}
			}
			int lm = 0;		
			int vPos = clientArea.y + clientArea.height + GAP;
			for(int i=0; i<rowLegendNumList.size(); i++){
				hPos = rowHPosList.get(i); 
				for(int j=0; j<rowLegendNumList.get(i); j++){
					legendList.get(lm).setBounds(new Rectangle(
							hPos, vPos, legendSizeList.get(lm).width, legendSizeList.get(lm).height));
					hPos += legendSizeList.get(lm).width + GAP;
					lm++;
				}
				vPos += legendSizeList.get(lm-1).height + GAP;						
			}
		}
		
		for(int i=xAxisList.size()-1; i>=0; i--){
			Axis xAxis = xAxisList.get(i);
			Dimension xAxisSize = xAxis.getPreferredSize(clientArea.width, clientArea.height);
			if(xAxis.getTickLablesSide() == LabelSide.Primary){
				xAxis.setBounds(new Rectangle(clientArea.x,
					clientArea.y + clientArea.height - xAxisSize.height,
					xAxisSize.width, xAxisSize.height));
				clientArea.height -= xAxisSize.height;
			}else{
				hasTopXAxis = true;
				xAxis.setBounds(new Rectangle(clientArea.x,
					clientArea.y+1,
					xAxisSize.width, xAxisSize.height));
				clientArea.y += xAxisSize.height ;
				clientArea.height -= xAxisSize.height;
			}
		}
		
		for(int i=yAxisList.size()-1; i>=0; i--){
			Axis yAxis = yAxisList.get(i);
			Dimension yAxisSize = yAxis.getPreferredSize(clientArea.width, 
					clientArea.height + (hasTopXAxis? 2:1) *yAxis.getMargin());
			if(yAxis.getTickLablesSide() == LabelSide.Primary){ // on the left
				yAxis.setBounds(new Rectangle(clientArea.x,
					clientArea.y - (hasTopXAxis? yAxis.getMargin():0),
					yAxisSize.width, yAxisSize.height));
				clientArea.x += yAxisSize.width;
				clientArea.width -= yAxisSize.width;
			}else{ // on the right
				hasRightYAxis = true;
				yAxis.setBounds(new Rectangle(clientArea.x + clientArea.width - yAxisSize.width -1,
					clientArea.y- (hasTopXAxis? yAxis.getMargin():0),
					yAxisSize.width, yAxisSize.height));
				clientArea.width -= yAxisSize.width;				
			}		
		}
		
		//re-adjust xAxis boundss
		for(int i=xAxisList.size()-1; i>=0; i--){
			Axis xAxis = xAxisList.get(i);
			xAxis.getBounds().x = clientArea.x - xAxis.getMargin()-1;
			if(hasRightYAxis)
				xAxis.getBounds().width = clientArea.width + 2*xAxis.getMargin();
			else
				xAxis.getBounds().width = clientArea.width + xAxis.getMargin();
		}
		
		if(plotArea != null && plotArea.isVisible()){
			
			Rectangle plotAreaBound = new Rectangle(
					primaryXAxis.getBounds().x + primaryXAxis.getMargin(),
					primaryYAxis.getBounds().y + primaryYAxis.getMargin(),
					primaryXAxis.getBounds().width - 2*primaryXAxis.getMargin(),
					primaryYAxis.getBounds().height - 2*primaryYAxis.getMargin()
					);			
			plotArea.setBounds(plotAreaBound);
			
		}
			
		super.layout();
	}
	
	

	/**
	 * @param zoomType the zoomType to set
	 */
	public void setZoomType(ZoomType zoomType) {
		this.zoomType = zoomType;
		plotArea.setZoomType(zoomType);
		for(Axis axis : xAxisList)
			axis.setZoomType(zoomType);
		for(Axis axis : yAxisList)
			axis.setZoomType(zoomType);
	}

	/**
	 * @return the zoomType
	 */
	public ZoomType getZoomType() {
		return zoomType;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
		titleLabel.setText(title);
	}
	
	/**
	 * @param showTitle true if title should be shown; false otherwise.
	 */
	public void setShowTitle(boolean showTitle){
		titleLabel.setVisible(showTitle);
		revalidate();
	}
	
	/**
	 * @return true if title should be shown; false otherwise.
	 */
	public boolean isShowTitle(){
		return titleLabel.isVisible();
	}
	
	/**
	 * @param showLegend true if legend should be shown; false otherwise.
	 */
	public void setShowLegend(boolean showLegend){
		this.showLegend = showLegend;
		for(Axis yAxis : legendMap.keySet()){
			Legend legend = legendMap.get(yAxis);
			legend.setVisible(showLegend);
		}		
		revalidate();
	}
	
	/**
	 * @return the showLegend
	 */
	public boolean isShowLegend() {
		return showLegend;
	}

	/**Add an axis to the graph
	 * @param axis
	 */
	public void addAxis(Axis axis){		
		if(axis.isHorizontal())
			xAxisList.add(axis);
		else
			yAxisList.add(axis);
		plotArea.addGrid(new Grid(axis));
		add(axis);
		axis.setXyGraph(this);
		revalidate();
	}
	
	/**Remove an axis from the graph
	 * @param axis
	 * @return true if this axis exists.
	 */
	public boolean removeAxis(Axis axis){
		remove(axis);
		plotArea.removeGrid(axis.getGrid());
		revalidate();
		if(axis.isHorizontal())
			return xAxisList.remove(axis);
		else 
			return yAxisList.remove(axis);		
	}

	/**Add a trace
	 * @param trace
	 */
	public void addTrace(Trace trace){
		if(trace.getTraceColor() == null){
			if(traceNum < DEFAULT_TRACES_COLOR.length)
				trace.setTraceColor(DEFAULT_TRACES_COLOR[traceNum++]);
			else
				trace.setTraceColor(BLACK_COLOR);
		}
		
		if(legendMap.containsKey(trace.getYAxis()))
			legendMap.get(trace.getYAxis()).addTrace(trace);
		else{
			legendMap.put(trace.getYAxis(), new Legend());
			legendMap.get(trace.getYAxis()).addTrace(trace);
			add(legendMap.get(trace.getYAxis()));
		}
		plotArea.addTrace(trace);	
		trace.setXYGraph(this);
		revalidate();
	}
	
	/**Remove a trace.
	 * @param trace
	 */
	public void removeTrace(Trace trace){
		if(legendMap.containsKey(trace.getYAxis())){
			legendMap.get(trace.getYAxis()).removeTrace(trace);
			if(legendMap.get(trace.getYAxis()).getTraceList().size() <=0){
				remove(legendMap.remove(trace.getYAxis()));				
			}
		}		
		plotArea.removeTrace(trace);
		revalidate();
	}
	
	/**Add an annotation
	 * @param annotation
	 */
	public void addAnnotation(Annotation annotation){
		plotArea.addAnnotation(annotation);
	}
	
	/**Remove an annotation
	 * @param annotation
	 */
	public void removeAnnotation(Annotation annotation){
		plotArea.removeAnnotation(annotation);
	}
	
	/**
	 * @param titleFont the titleFont to set
	 */
	public void setTitleFont(Font titleFont) {		
		titleLabel.setFont(titleFont);
	}
	
	/**
	 * @return the title font.
	 */
	public Font getTitleFont(){
		return titleLabel.getFont();
	}

	/**
	 * @param titleColor the titleColor to set
	 */
	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
		titleLabel.setForegroundColor(titleColor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void paintFigure(final Graphics graphics) {		
		if (!transparent) {
			graphics.fillRectangle(getClientArea());
		}
		super.paintFigure(graphics);
	}
	
	/**
	 * @param transparent the transparent to set
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
		repaint();
	}


	/**
	 * @return the transparent
	 */
	public boolean isTransparent() {
		return transparent;
	}


	/**
	 * @return the plotArea, which contains all the elements drawn inside it.
	 */
	public PlotArea getPlotArea() {
		return plotArea;
	}
	
	/**
	 * @return the image of the XYFigure
	 */
	public Image getImage(){
		Image image = new Image(null, bounds.width + 6, bounds.height + 6);
		GC gc = new GC(image);
		SWTGraphics graphics = new SWTGraphics(gc); 
		graphics.translate(-bounds.x + 3, -bounds.y + 3);
		graphics.setForegroundColor(getForegroundColor());
		graphics.setBackgroundColor(getBackgroundColor());		
		paint(graphics);
		gc.dispose();
		return image;
	}


	/**
	 * @return the titleColor
	 */
	public Color getTitleColor() {
		if(titleColor == null)
			return getForegroundColor();
		return titleColor;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the operationsManager
	 */
	public OperationsManager getOperationsManager() {
		return operationsManager;
	}

	/**
	 * @return the xAxisList
	 */
	public List<Axis> getXAxisList() {
		return xAxisList;
	}

	/**
	 * @return the yAxisList
	 */
	public List<Axis> getYAxisList() {
		return yAxisList;
	}
	
	/**
	 * @return the all the axis include xAxes and yAxes. 
	 * yAxisList is appended to xAxisList in the returned list.
	 */
	public List<Axis> getAxisList(){
		List<Axis> list = new ArrayList<Axis>();
		list.addAll(xAxisList);
		list.addAll(yAxisList);
		return list;
	}
	
	/**
	 * @return the legendMap
	 */
	public Map<Axis, Legend> getLegendMap() {
		return legendMap;
	}

	/**
	 * Perform forced autoscale to all axes.
	 */
	public void performAutoScale(){
		ZoomCommand command = new ZoomCommand("Auto Scale", xAxisList, yAxisList);
		command.savePreviousStates();		
		for(Axis axis : xAxisList){
			axis.performAutoScale(true);
		}
		for(Axis axis : yAxisList){
			axis.performAutoScale(true);
		}
		command.saveAfterStates();
		operationsManager.addCommand(command);
	}
	
}
