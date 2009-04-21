package org.csstudio.sds.components.ui.internal.figures;

import java.text.NumberFormat;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figureparts.LinearScale;
import org.csstudio.sds.components.ui.internal.figureparts.LinearScaledMarker;
import org.csstudio.sds.components.ui.internal.figureparts.LinearScale.Orientation;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A Thermometer figure
 * @author Xihui Chen
 *
 */
public class RefreshableThermoFigure extends AbstractLinearMarkedFigure {	

	private Color fillColor;
	private Color fillBackgroundColor;
	private Color contrastFillColor;	
	private Color outlineColor;
	
	private Pipe pipe;
	private Bulb bulb;
	private Label unit;
	
	private boolean effect3D = true;
	
	private final static Color WHITE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_WHITE);
	
	

	public RefreshableThermoFigure() {
		
		super();
		((LinearScale) scale).setOrientation(Orientation.VERTICAL);
		scale.setScaleLineVisible(false);
		scale.setForegroundColor(outlineColor);
		
		pipe = new Pipe();
		bulb = new Bulb();
		unit = new Label();	
		unit.setForegroundColor(outlineColor);
		
		setLayoutManager(new ThermoLayout());
		
		add(scale, ThermoLayout.SCALE);
		add(marker, ThermoLayout.MARKERS);
		add(pipe, ThermoLayout.PIPE);
		add(unit, ThermoLayout.UNIT);
		add(bulb, ThermoLayout.BULB);
	  
	}
	
	@Override
	public boolean isOpaque() {
		return false;
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
		int blue = 255 - fillColor.blue;
		int green = 255 - fillColor.green;
		int red = fillColor.red;
		this.contrastFillColor = CustomMediaFactory.getInstance().getColor(
				new RGB(red, green, blue));
	}
	
	
	/**
	 * @param showBulb the showBulb to set
	 */
	public void setShowBulb(boolean showBulb) {
		bulb.setVisible(showBulb);
		revalidate();
	}

	/**
	 * @param fahrenheit the fahrenheit to set
	 */
	public void setFahrenheit(boolean fahrenheit) {
		if(fahrenheit) {
			unit.setText("\u2109");
		}else
			unit.setText("\u2103");
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
	
	class Pipe extends RoundedRectangle {		
		
		
		public final static int FILL_CORNER = 3;
		public final static int PIPE_WIDTH = 15;
		private final Color EFFECT3D_PIPE_COLOR = CustomMediaFactory.getInstance().getColor(
				new RGB(160, 160, 160));
		public Pipe() {
			super();
			setOutline(true);
		}
		@Override
		protected void fillShape(Graphics graphics) {
			corner.height =PIPE_WIDTH/2;
			corner.width = PIPE_WIDTH/2;
			graphics.setForegroundColor(outlineColor);
			graphics.setBackgroundColor(fillBackgroundColor);
			
			int valuePosition = ((LinearScale) scale).getValuePosition(value, false);
			if(effect3D){
				graphics.setForegroundColor(EFFECT3D_PIPE_COLOR);
				//fill back
				super.fillShape(graphics);
				Pattern backPattern = new Pattern(Display.getCurrent(), 
						bounds.x, bounds.y, bounds.x+bounds.width, bounds.y, 
						WHITE_COLOR,255, fillBackgroundColor, 0);
				graphics.setBackgroundPattern(backPattern);
				super.fillShape(graphics);
				backPattern.dispose();
				
				//fill value
				graphics.setBackgroundColor(fillColor);
				graphics.fillRoundRectangle(new Rectangle(bounds.x + lineWidth, 
						valuePosition,
						bounds.width - 2* lineWidth, 
						bounds.height - (valuePosition - bounds.y)),
						FILL_CORNER, FILL_CORNER);		
				backPattern = new Pattern(Display.getCurrent(), 
						bounds.x, bounds.y, bounds.x+bounds.width, bounds.y, 
						WHITE_COLOR,255, fillColor, 0);
				graphics.setBackgroundPattern(backPattern);
				graphics.fillRoundRectangle(new Rectangle(bounds.x + lineWidth, 
						valuePosition,
						bounds.width - 2* lineWidth, 
						bounds.height - (valuePosition - bounds.y)),
						FILL_CORNER, FILL_CORNER);
				backPattern.dispose();			
			} else {
				super.fillShape(graphics);
				graphics.setBackgroundColor(fillColor);
				graphics.fillRoundRectangle(new Rectangle(bounds.x + lineWidth, 
					valuePosition,
					bounds.width - 2* lineWidth, 
					bounds.height - (valuePosition - bounds.y)),
					FILL_CORNER, FILL_CORNER);
			}
			
			
			
		}
		
		@Override
		public Dimension getPreferredSize(int wHint, int hHint) {
			return new Dimension(PIPE_WIDTH , hHint+2*corner.height); 
		}
		
		public Dimension getCorner() {
			return corner;
		}
	
	}
	
	class Bulb extends Ellipse {
		
		public final static int MAX_DIAMETER = 40;
		private final Color EFFECT3D_BULB_COLOR = CustomMediaFactory.getInstance().getColor(
				new RGB(140, 140, 140));
		public Bulb() {
			super();
			setOutline(true);
		}
		
		@Override
		protected void fillShape(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			if(effect3D){
				graphics.setBackgroundColor(fillColor);
				super.fillShape(graphics);
				int l = (int) ((bounds.width - lineWidth)*0.293/2);

				Pattern backPattern = new Pattern(Display.getCurrent(), 
					bounds.x + lineWidth, bounds.y + lineWidth, 
					bounds.x+bounds.width - l, bounds.y+bounds.height- l,
					WHITE_COLOR,255, fillColor, 0);			
				graphics.setBackgroundPattern(backPattern);
				super.fillShape(graphics);
				backPattern.dispose();
			}else{
				graphics.setBackgroundColor(fillColor);				
				super.fillShape(graphics);
			}			

			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
			String valueString = format.format(value);
			Dimension valueSize = 
				FigureUtilities.getTextExtents(valueString, getFont());
			if(valueSize.width < bounds.width) {				
				graphics.setForegroundColor(contrastFillColor);
				graphics.drawText(valueString, new Point(
						bounds.x + bounds.width/2 - valueSize.width/2,
						bounds.y + bounds.height/2 - valueSize.height/2));				
			}			
		}
		
		@Override
		protected void outlineShape(Graphics graphics) {
			if(effect3D)
				graphics.setForegroundColor(EFFECT3D_BULB_COLOR);
			else
				graphics.setForegroundColor(outlineColor);
			super.outlineShape(graphics);			
			//draw a small rectangle to hide the joint  
			if(effect3D) {
				graphics.setBackgroundColor(fillColor);
				graphics.fillRectangle(new Rectangle(pipe.getBounds().x + pipe.getLineWidth(),
					((LinearScale) scale).getValuePosition(scale.getRange().lower, false),
					Pipe.PIPE_WIDTH- pipe.getLineWidth() *2, 2));
				Pattern backPattern = new Pattern(Display.getCurrent(), 
					pipe.getBounds().x, ((LinearScale) scale).getValuePosition(scale.getRange().lower, false),
					pipe.getBounds().x + Pipe.PIPE_WIDTH,
					((LinearScale) scale).getValuePosition(scale.getRange().lower, false),
					WHITE_COLOR,255, fillColor, 0);					
				graphics.setBackgroundPattern(backPattern);
				graphics.fillRectangle(new Rectangle(pipe.getBounds().x + pipe.getLineWidth(),
					((LinearScale) scale).getValuePosition(scale.getRange().lower, false),
					Pipe.PIPE_WIDTH- pipe.getLineWidth() *2, 2));
				backPattern.dispose();
		
			}else{
				graphics.setBackgroundColor(fillColor);
				graphics.fillRoundRectangle(new Rectangle(pipe.getBounds().x + pipe.getLineWidth(),
						((LinearScale) scale).getValuePosition(scale.getRange().lower, false),
						Pipe.PIPE_WIDTH- pipe.getLineWidth() *2, ((LinearScale) scale).getMargin()),
						Pipe.FILL_CORNER, Pipe.FILL_CORNER);			
			}
				
		}
	}
	
		
	class ThermoLayout extends AbstractLayout {
		
		/** Used as a constraint for the scale. */
		public static final String SCALE = "scale";   //$NON-NLS-1$
		/** Used as a constraint for the pipe indicator. */
		public static final String PIPE = "pipe"; //$NON-NLS-1$
		/** Used as a constraint for the alarm ticks */
		public static final String MARKERS = "markers";      //$NON-NLS-1$
		/** Used as a constraint for the bulb in the below of pipe. */
		public static final String BULB = "bulb";    //$NON-NLS-1$
		/** Used as a constraint for the unit label*/
		public static final String UNIT  = "unit";  //$NON-NLS-1$
		private LinearScale scale;
		private LinearScaledMarker marker;
		private Pipe pipe;
		private IFigure bulb, unit;	
		
		@Override
		public void setConstraint(IFigure child, Object constraint) {
			if(constraint.equals(SCALE))
				scale = (LinearScale)child;
			else if (constraint.equals(MARKERS))
				marker = (LinearScaledMarker) child;
			else if (constraint.equals(PIPE))
				pipe = (Pipe) child;
			else if (constraint.equals(BULB))
				bulb = child;
			else if (constraint.equals(UNIT))
				unit = child;
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
			Rectangle area = container.getClientArea();		
			if(bulb != null && bulb.isVisible()) {
				int diameter = area.width/2;
				if(diameter > Bulb.MAX_DIAMETER)
					diameter = Bulb.MAX_DIAMETER;
				int x = area.x + area.width/2 - diameter /2;
				int spareHeight = (area.height < diameter)? 0: (area.height - diameter);
				int y = area.y + spareHeight;			
				bulb.setBounds(new Rectangle(x, y, diameter, diameter));
				area.height = spareHeight + scale.getMargin();			
			}
			Dimension unitSize = new Dimension(0, 0);
			Dimension scaleSize = new Dimension(0, 0);
			Dimension markerSize = new Dimension(0, 0);
			Dimension pipeSize = new Dimension(0, 0);
			if(unit != null && unit.isVisible()) {
				unitSize = unit.getPreferredSize();			
				unit.setBounds(new Rectangle(
						area.x + area.width/2 - Pipe.PIPE_WIDTH/2 - unitSize.width,
						area.y, unitSize.width, unitSize.height));
			}
			
			if(scale != null) {
				scaleSize = scale.getPreferredSize(-1, area.height-unitSize.height);
				scale.setBounds(new Rectangle(area.x + area.width/2 - Pipe.PIPE_WIDTH/2 - scaleSize.width,
						area.y+unitSize.height, 
						scaleSize.width, scaleSize.height));					
			}
			
			if(marker != null && marker.isVisible()) {
				markerSize = marker.getPreferredSize();
				marker.setBounds(new Rectangle(area.x + area.width/2 + Pipe.PIPE_WIDTH/2,
						marker.getScale().getBounds().y, markerSize.width, markerSize.height));			
			}
			
	
			
			if(pipe != null) {
				pipeSize = pipe.getPreferredSize(-1, scale.getTickLength());
				pipe.setBounds(new Rectangle(
						area.x + area.width/2 -  Pipe.PIPE_WIDTH/2,
						scale.getValuePosition(scale.getRange().upper, false) - pipe.getCorner().height,
						pipeSize.width,
						pipeSize.height));
			}	
		}
	
	}
}

