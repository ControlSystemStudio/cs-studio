package org.csstudio.opibuilder.widgets.figures;



import org.csstudio.opibuilder.widgets.util.GraphicsUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.xygraph.linearscale.LinearScale;
import org.csstudio.swt.xygraph.linearscale.LinearScaledMarker;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.LinearScale.Orientation;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A progress bar figure
 * @author Xihui Chen
 *
 */
public class ProgressBarFigure extends AbstractLinearMarkedFigure {
	
	private Color fillColor;
	private Color fillBackgroundColor;

	private Color outlineColor;
	private boolean effect3D;
	private boolean horizontal;
	private boolean indicatorMode;
	
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	//border color for track and thumb
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY); 
	
	/** The alpha (0 is transparency and 255 is opaque) for disabled paint */
	private static final int DISABLED_ALPHA = 100;	
	
	private Track track;
	private Label label;
	private Thumb thumb;


	private double origin = 0; //the start point of the bar.
	private boolean originIgnored;
	
	public ProgressBarFigure() {
		
		super();
		scale.setScaleLineVisible(false);
		scale.setForegroundColor(outlineColor);
		scale.setTickLableSide(LabelSide.Secondary);
		
		if(horizontal) {
			((LinearScale)scale).setOrientation(Orientation.HORIZONTAL);
			scale.setTickLableSide(LabelSide.Primary);		
			marker.setLabelSide(LabelSide.Secondary);			
		}else {
			((LinearScale)scale).setOrientation(Orientation.VERTICAL);
			scale.setTickLableSide(LabelSide.Primary);		
			marker.setLabelSide(LabelSide.Secondary);	
		}		
		
		track = new Track();		

		label = new Label();
		label.setOpaque(false);		
		
		thumb = new Thumb();
		thumb.setVisible(indicatorMode);
		
		setLayoutManager(new ProgressBarLayout());
		add(scale, ProgressBarLayout.SCALE);
		add(marker, ProgressBarLayout.MARKERS);
		add(track, ProgressBarLayout.TRACK);
		add(thumb, ProgressBarLayout.THUMB);
		add(label, ProgressBarLayout.LABEL);
	
		addFigureListener(new FigureListener() {			
			public void figureMoved(IFigure source) {
				revalidate();				
			}
		});	
	}

	@Override
	public boolean isOpaque() {
		return false;
	}	
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		repaint();
		
	}
	
	public void setShowLabel(boolean visible){
		label.setVisible(visible);
		repaint();
	}
	
	

	@Override
	protected void paintClientArea(Graphics graphics) {
		super.paintClientArea(graphics);
		if(!isEnabled()) {
			graphics.setAlpha(DISABLED_ALPHA);
			graphics.setBackgroundColor(GRAY_COLOR);
			graphics.fillRectangle(bounds);
		}
	}
	
	@Override
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		outlineColor = fg;
	}
	
	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(RGB fillColor) {
		this.fillColor = CustomMediaFactory.getInstance().getColor(fillColor);
	}
	
	/**
	 * @param fillBackgroundColor the fillBackgroundColor to set
	 */
	public void setFillBackgroundColor(RGB fillBackgroundColor) {
		this.fillBackgroundColor = CustomMediaFactory.getInstance().getColor(
				fillBackgroundColor);
	}
	


	/**
	 * @param effect3D the effect3D to set
	 */
	public void setEffect3D(boolean effect3D) {
		this.effect3D = effect3D;
	}
	
	public void setIndicatorMode(boolean indicatorMode) {
		this.indicatorMode = indicatorMode;
		thumb.setVisible(indicatorMode);
		revalidate();
		repaint();
	}

	@Override
	public void setValue(double value) {
		super.setValue(value);
		updateLabelText();
		revalidate();
	}
	
	public void setOrigin(double origin) {		
		this.origin = origin;
	}
	
	public void setOriginIgnored(boolean originIgnored) {
		this.originIgnored = originIgnored;
	}

	@Override
	public void setRange(double min, double max) {
		super.setRange(min, max);
		updateLabelText();
	}
	
	
	
	/**
	 * Update the text of the label.
	 */
	private void updateLabelText() {
		label.setText(scale.format(getValue()));
	}

	
	/**
	 * @param horizontal the horizontal to set
	 */
	public void setHorizontal(boolean horizontal) {
		if(this.horizontal == horizontal)
			return;
		this.horizontal = horizontal;
		if(horizontal) {
			((LinearScale)scale).setOrientation(Orientation.HORIZONTAL);
			scale.setTickLableSide(LabelSide.Primary);		
			marker.setLabelSide(LabelSide.Secondary);			
		}else {
			((LinearScale)scale).setOrientation(Orientation.VERTICAL);
			scale.setTickLableSide(LabelSide.Primary);		
			marker.setLabelSide(LabelSide.Secondary);	
		}		
		revalidate();
	}

	
	class Track extends RectangleFigure {		
		public Track() {
			super();
			setForegroundColor(GRAY_COLOR);
			setOutline(false);
		}	
	
		@Override
		protected void fillShape(Graphics graphics) {		
			
			graphics.setAntialias(SWT.ON);			
			int valuePosition = ((LinearScale) scale).getValuePosition(getCoercedValue(), false);
			double tempOrigin;
			if(originIgnored)
				tempOrigin = minimum;
			else
				tempOrigin = origin;
			int originPosition = ((LinearScale) scale).getValuePosition(tempOrigin, false);
			int fillLength = valuePosition - originPosition;			

			boolean support3D = GraphicsUtil.testPatternSupported(graphics);
			if(effect3D && support3D) {		
				//fill background
				graphics.setBackgroundColor(fillBackgroundColor);
				super.fillShape(graphics);
				Pattern backGroundPattern; 
				if(horizontal)
					backGroundPattern= new Pattern(Display.getCurrent(),
						bounds.x, bounds.y,
						bounds.x, bounds.y + bounds.height,
						WHITE_COLOR, 255,
						fillBackgroundColor, 0);
				else
					backGroundPattern= new Pattern(Display.getCurrent(),
						bounds.x, bounds.y,
						bounds.x + bounds.width, bounds.y,
						WHITE_COLOR, 255,
						fillBackgroundColor, 0);
				graphics.setBackgroundPattern(backGroundPattern);
				super.fillShape(graphics);
				
				backGroundPattern.dispose();
				
				//fill value
				if(!indicatorMode){
					if(horizontal)
						backGroundPattern = new Pattern(Display.getCurrent(),
							bounds.x, bounds.y,
							bounds.x, bounds.y + bounds.height,
							WHITE_COLOR, 255,
							fillColor, 0);
					else
						backGroundPattern = new Pattern(Display.getCurrent(),
							bounds.x, bounds.y,
							bounds.x + bounds.width, bounds.y,
							WHITE_COLOR, 255,
							fillColor, 0);
					
					graphics.setBackgroundColor(fillColor);
					graphics.setForegroundColor(fillColor);
					if(horizontal){
						Rectangle valueRectangle =new Rectangle(originPosition,
							bounds.y + 1, fillLength, bounds.height-2) ;
						graphics.fillRectangle(valueRectangle);
						graphics.setBackgroundPattern(backGroundPattern);
						graphics.fillRectangle(valueRectangle);					
						
					}else {
						Rectangle valueRectangle = new Rectangle(bounds.x +1,
								originPosition, bounds.width-2, fillLength);
						graphics.fillRectangle(valueRectangle);
						graphics.setBackgroundPattern(backGroundPattern);
						graphics.fillRectangle(valueRectangle);
	
					}		
					backGroundPattern.dispose();
				}
				
				graphics.setForegroundColor(GRAY_COLOR);
				outlineShape(graphics);
				
				
				
			}else {
				graphics.setBackgroundColor(fillBackgroundColor);
				if(!indicatorMode){
					if(horizontal){
						graphics.fillRectangle(
								bounds.x, bounds.y, bounds.width-getLineWidth(), bounds.height);			
						graphics.setBackgroundColor(fillColor);
						graphics.fillRectangle(new Rectangle(originPosition,
								bounds.y, 						
								fillLength, 
								bounds.height));
					}
					else {
						graphics.fillRectangle(
								bounds.x, bounds.y, bounds.width, bounds.height-getLineWidth());			
						graphics.setBackgroundColor(fillColor);
						graphics.fillRectangle(new Rectangle(bounds.x,
								originPosition,
								bounds.width,
								fillLength));
					}
				}else
					graphics.fillRectangle(getBounds());
				
//				graphics.setForegroundColor(outlineColor);
//				graphics.setForegroundColor(GRAY_COLOR);
//				outlineShape(graphics);
			}			
		}		
	}
	
	class Thumb extends Polygon {
		public static final  int LENGTH = 20;
		public static final int BREADTH = 13;
		public final PointList  horizontalThumbPointList = new PointList(new int[] {
				0,0,  0, BREADTH,  LENGTH*4/5, BREADTH,  LENGTH, BREADTH/2,
				LENGTH*4/5, 0}) ;
		public final PointList verticalThumbPointList = new PointList(new int[] {
				0,0,  0, LENGTH*4/5, BREADTH/2, LENGTH, BREADTH, LENGTH*4/5, BREADTH,  
				0}) ;
			
		public Thumb() {
			super();
			//setOutline(true);
			setFill(true);
			setForegroundColor(GRAY_COLOR);
			setLineWidth(1);		
		}
		
		@Override
		protected void fillShape(Graphics g) {	
			g.setAntialias(SWT.ON);
			g.setClip(new Rectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height));
			g.setBackgroundColor(WHITE_COLOR);
			super.fillShape(g);
			Point leftPoint = getPoints().getPoint(0);
			Point rightPoint;
			//if(horizontal) 
				rightPoint = getPoints().getPoint(2);
			//else
			//	rightPoint = getPoints().getPoint(1);//.translate(0, -BREADTH/2);
			Pattern thumbPattern = null;
			boolean support3D = GraphicsUtil.testPatternSupported(g);
			setOutline(effect3D && support3D);
			if(effect3D && support3D) {
				thumbPattern = new Pattern(Display.getCurrent(),
					leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y, WHITE_COLOR, 0, 
					fillColor, 255);
				g.setBackgroundPattern(thumbPattern);		
			}else
				g.setBackgroundColor(fillColor);
				
			g.fillPolygon(getPoints());
			
			if(effect3D && support3D)
				thumbPattern.dispose();
					
		}
	}
		
	
	class ProgressBarLayout extends AbstractLayout {
		

		private static final int ADDITIONAL_MARGIN = 1;
		
		/** Used as a constraint for the scale. */
		public static final String SCALE = "scale";   //$NON-NLS-1$
		/** Used as a constraint for the pipe indicator. */
		public static final String TRACK = "track"; //$NON-NLS-1$
		/** Used as a constraint for the alarm ticks */
		public static final String MARKERS = "markers";      //$NON-NLS-1$
		/** Used as a constraint for the thumb */
		public static final String THUMB = "thumb";      //$NON-NLS-1$
		/** Used as a constraint for the label */
		public static final String LABEL = "label";      //$NON-NLS-1$

		private LinearScale scale;
		private LinearScaledMarker marker;
		private Track track;
		private Label label;
		private Thumb thumb;

		
		@Override
		public void setConstraint(IFigure child, Object constraint) {
			if(constraint.equals(SCALE))
				scale = (LinearScale)child;
			else if (constraint.equals(MARKERS))
				marker = (LinearScaledMarker) child;
			else if (constraint.equals(TRACK))
				track = (Track) child;
			else if (constraint.equals(THUMB))
				thumb = (Thumb)child;
			else if (constraint.equals(LABEL))
				label = (Label) child;
		}
		
		@Override
		protected Dimension calculatePreferredSize(IFigure container, int w,
				int h) {
			Insets insets = container.getInsets();
			Dimension d = new Dimension(64, 4*64);
			d.expand(insets.getWidth(), insets.getHeight());
			return d;
		}
	
		public void layout(IFigure container) {
			if(horizontal)
				horizontalLayout(container);
			else
				verticalLayout(container);
		}
		
		
		private void horizontalLayout(IFigure container) {
			Rectangle area = container.getClientArea().getCopy();		
			area.x += ADDITIONAL_MARGIN;
			area.width -= 2*ADDITIONAL_MARGIN;
			Dimension scaleSize = new Dimension(0, 0);
			Dimension markerSize = new Dimension(0, 0);
			Rectangle trackBounds = area;
			if(scale != null) {
				if(scale.isVisible()){
					scaleSize = scale.getPreferredSize(area.width, -1);
					scale.setBounds(new Rectangle(area.x, 
						area.y + area.height - scaleSize.height, 
						scaleSize.width, scaleSize.height));
					scaleSize.height +=ADDITIONAL_MARGIN;
				}else{
					scaleSize = scale.getPreferredSize(area.width+2*scale.getMargin(), -1);
					scale.setBounds(new Rectangle(area.x - scale.getMargin(), 
							area.y + area.height - scaleSize.height, 
							scaleSize.width, scaleSize.height));
					scaleSize.width = 0;
					scaleSize.height = 0;
				}
									
			}
			
			if(marker != null && marker.isVisible()) {
				markerSize = marker.getPreferredSize();
				marker.setBounds(new Rectangle(marker.getScale().getBounds().x,
						area.y,
						markerSize.width, markerSize.height));			
			}
			
			if(track != null) {
				trackBounds = new Rectangle(	
						scale.getValuePosition(scale.getRange().getLower(), false),
						area.y + markerSize.height,
						scale.getTickLength()+ track.getLineWidth(),
						area.height -markerSize.height - scaleSize.height );
				track.setBounds(trackBounds);
			}
			if(thumb != null && thumb.isVisible()) {
				int h = track.getBounds().height;
				int b = Thumb.BREADTH;
				PointList newPointList = new PointList(new int[]{
						b/2, 0, b, h/2, b/2, h-1, 0, h/2
				});
				newPointList.translate(scale.getValuePosition(getCoercedValue(), false) - Thumb.BREADTH/2,
						track.getBounds().y
						);
				thumb.setPoints(newPointList);
			}
			if(label != null) {	
				Dimension labelSize = label.getPreferredSize();
				label.setBounds(new Rectangle(trackBounds.x + trackBounds.width/2 - labelSize.width/2, 
						trackBounds.y + trackBounds.height/2 - labelSize.height/2,
						labelSize.width, labelSize.height));
			}
		}

		private void verticalLayout(IFigure container) {
			Rectangle area = container.getClientArea().getCopy();		
			area.y += ADDITIONAL_MARGIN;
			area.height -= 2*ADDITIONAL_MARGIN;
			Dimension scaleSize = new Dimension(0, 0);
			Dimension markerSize = new Dimension(0, 0);
			Rectangle trackBounds = area;
			if(scale != null) {
				if(scale.isVisible()){
					scaleSize = scale.getPreferredSize(-1, area.height);
					scale.setBounds(new Rectangle(area.x + ADDITIONAL_MARGIN, 
						area.y, 
						scaleSize.width, scaleSize.height));
					scaleSize.width += ADDITIONAL_MARGIN;
				}else{
					scaleSize = scale.getPreferredSize(-1, area.height+2*scale.getMargin());
					scale.setBounds(new Rectangle(area.x, 
						area.y-scale.getMargin(), 
						scaleSize.width, scaleSize.height));
					scaleSize.width = 0;
					scaleSize.height = 0;
				}
									
			}
			
			if(marker != null && marker.isVisible()) {
				markerSize = marker.getPreferredSize();
				marker.setBounds(new Rectangle(area.x + area.width - markerSize.width - ADDITIONAL_MARGIN,
						marker.getScale().getBounds().y,
						markerSize.width, markerSize.height));			
			}
			
			if(track != null) {
				trackBounds = new Rectangle(	
					area.x + scaleSize.width,
					scale.getValuePosition(scale.getRange().getUpper(), false),
					area.width -markerSize.width - scaleSize.width, 
					scale.getTickLength()+ track.getLineWidth());		
				track.setBounds(trackBounds);
			}
			if(thumb != null && thumb.isVisible()) {	
				int h = track.getBounds().width;
				int b = Thumb.BREADTH;
				PointList newPointList = new PointList(new int[]{
						0, b/2, h/2, 0, h-1, b/2, h/2, b
				});
				newPointList.translate(track.getBounds().x,
						scale.getValuePosition(getCoercedValue(), false) - Thumb.BREADTH/2);
				thumb.setPoints(newPointList);
			}
			if(label != null && label.isVisible()) {	
				Dimension labelSize = label.getPreferredSize();
				label.setBounds(new Rectangle(trackBounds.x + trackBounds.width/2 - labelSize.width/2, 
						trackBounds.y + trackBounds.height/2 - labelSize.height/2,
						labelSize.width, labelSize.height));
			}
		}
	
	}
}

