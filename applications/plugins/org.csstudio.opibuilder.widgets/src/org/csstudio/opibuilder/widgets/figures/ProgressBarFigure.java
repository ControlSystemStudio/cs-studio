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
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
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
	
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	//border color for track and thumb
	private final static Color GRAY_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY); 
	
	/** The alpha (0 is transparency and 255 is opaque) for disabled paint */
	private static final int DISABLED_ALPHA = 100;	
	
	private Track track;
	private Label label;



	
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
		setLayoutManager(new ProgressBarLayout());
		add(scale, ProgressBarLayout.SCALE);
		add(marker, ProgressBarLayout.MARKERS);
		add(track, ProgressBarLayout.TRACK);
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
		track.setOutline(!effect3D);
	}

	@Override
	public void setValue(double value) {
		super.setValue(value);
		updateLabelText();
		revalidate();
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
			int valuePosition = ((LinearScale) scale).getValuePosition(value, false);
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
				graphics.setForegroundColor(GRAY_COLOR);
				outlineShape(graphics);
				backGroundPattern.dispose();
				
				//fill value
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
					int fillWidth = valuePosition - bounds.x;			
					Rectangle valueRectangle =new Rectangle(bounds.x + 1,
						bounds.y + 1, fillWidth-1, bounds.height-2) ;
					graphics.fillRectangle(valueRectangle);
					graphics.setBackgroundPattern(backGroundPattern);
					graphics.fillRectangle(valueRectangle);
			
					
					
				}else {
					int fillHeight = bounds.height - (valuePosition - bounds.y) - getLineWidth();				
					Rectangle valueRectangle = new Rectangle(bounds.x +1,
							valuePosition, bounds.width-2, fillHeight);
					graphics.fillRectangle(valueRectangle);
					graphics.setBackgroundPattern(backGroundPattern);
					graphics.fillRectangle(valueRectangle);

				}		
				
				backGroundPattern.dispose();
				
				
				
				
			}else {
				graphics.setBackgroundColor(fillBackgroundColor);
				super.fillShape(graphics);				
				graphics.setBackgroundColor(fillColor);
				if(horizontal)
					graphics.fillRectangle(new Rectangle(bounds.x + lineWidth,
							bounds.y + lineWidth, 						
							valuePosition - bounds.x - lineWidth, 
							bounds.height - 2*lineWidth));
				else
					graphics.fillRectangle(new Rectangle(bounds.x + lineWidth, 
							valuePosition,
							bounds.width - 2* lineWidth, 
							bounds.height - (valuePosition - bounds.y)));
				graphics.setForegroundColor(outlineColor);
			}			
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
		/** Used as a constraint for the label */
		public static final String LABEL = "label";      //$NON-NLS-1$

		private LinearScale scale;
		private LinearScaledMarker marker;
		private Track track;
		private Label label;

		
		@Override
		public void setConstraint(IFigure child, Object constraint) {
			if(constraint.equals(SCALE))
				scale = (LinearScale)child;
			else if (constraint.equals(MARKERS))
				marker = (LinearScaledMarker) child;
			else if (constraint.equals(TRACK))
				track = (Track) child;
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
				scaleSize = scale.getPreferredSize(area.width, -1);
				scale.setBounds(new Rectangle(area.x, 
						area.y + area.height - scaleSize.height - ADDITIONAL_MARGIN, 
						scaleSize.width, scaleSize.height));					
			}
			
			if(marker != null && marker.isVisible()) {
				markerSize = marker.getPreferredSize();
				marker.setBounds(new Rectangle(marker.getScale().getBounds().x,
						area.y,
						markerSize.width, markerSize.height));			
			}
			
			if(track != null) {
				trackBounds = new Rectangle(	
						scale.getValuePosition(scale.getRange().getLower(), false) - track.getLineWidth(),
						area.y + markerSize.height + ADDITIONAL_MARGIN,
						scale.getTickLength()+ 2*track.getLineWidth(),
						area.height - 3*ADDITIONAL_MARGIN -markerSize.height - scaleSize.height );
				track.setBounds(trackBounds);
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
				scaleSize = scale.getPreferredSize(-1, area.height);
				scale.setBounds(new Rectangle(area.x + ADDITIONAL_MARGIN, 
						area.y, 
						scaleSize.width, scaleSize.height));					
			}
			
			if(marker != null && marker.isVisible()) {
				markerSize = marker.getPreferredSize();
				marker.setBounds(new Rectangle(area.x + area.width - markerSize.width - ADDITIONAL_MARGIN,
						marker.getScale().getBounds().y,
						markerSize.width, markerSize.height));			
			}
			
			if(track != null) {
				trackBounds = new Rectangle(	
						area.x + scaleSize.width + 2*ADDITIONAL_MARGIN,
						scale.getValuePosition(scale.getRange().getUpper(), false) - track.getLineWidth(),
						area.width - 4*ADDITIONAL_MARGIN -markerSize.width - scaleSize.width, 
						scale.getTickLength()+ 2*track.getLineWidth());
						
				track.setBounds(trackBounds);
			}
			
			if(label != null) {	
				Dimension labelSize = label.getPreferredSize();
				label.setBounds(new Rectangle(trackBounds.x + trackBounds.width/2 - labelSize.width/2, 
						trackBounds.y + trackBounds.height/2 - labelSize.height/2,
						labelSize.width, labelSize.height));
			}
		}
	
	}
}

