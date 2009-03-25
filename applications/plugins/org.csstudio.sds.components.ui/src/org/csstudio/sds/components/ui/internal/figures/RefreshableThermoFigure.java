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
import org.eclipse.swt.graphics.RGB;

/**
 * A Thermometer figure
 * @author Xihui Chen
 *
 */
public class RefreshableThermoFigure extends AbstractScaledWidgetFigure {	

	private Color fillColor;
	private Color fillBackgroundColor;
	private Color contrastFillColor;	
	private Color outlineColor;
	
	private LinearScaledMarker marker;
	private Pipe pipe;
	private Bulb bulb;
	private Label unit;
	
	public RefreshableThermoFigure() {
		
		scale = new LinearScale();
		((LinearScale) scale).setOrientation(Orientation.VERTICAL);
		scale.setScaleLineVisible(false);
		scale.setForegroundColor(outlineColor);
	
		marker = new LinearScaledMarker((LinearScale) scale);	
		marker.putMarkerElement("LOLO", loloLevel, CustomMediaFactory.COLOR_RED);
		marker.putMarkerElement("LO", loLevel, CustomMediaFactory.COLOR_ORANGE);
		marker.putMarkerElement("HI", hiLevel, CustomMediaFactory.COLOR_ORANGE);
		marker.putMarkerElement("HIHI", hihiLevel, CustomMediaFactory.COLOR_RED);	

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
	
	@Override
	public void setShowMarkers(boolean showMarkers) {		
		super.setShowMarkers(showMarkers);
		marker.setVisible(showMarkers);	
	}
	
	@Override
	public void setLoloLevel(double loloLevel) {
		super.setLoloLevel(loloLevel);
		marker.putMarkerElement("LOLO", loloLevel);
	}
	
	@Override
	public void setLoLevel(double loLevel) {
		super.setLoLevel(loLevel);
		marker.putMarkerElement("LO", loLevel);
	}
	
	@Override
	public void setHiLevel(double hiLevel) {
		super.setHiLevel(hiLevel);
		marker.putMarkerElement("HI", hiLevel);
	}
	
	@Override
	public void setHihiLevel(double hihiLevel) {
		super.setHihiLevel(hihiLevel);
		marker.putMarkerElement("HIHI", hihiLevel);
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

	class Pipe extends RoundedRectangle {		
		
		
		public static final int FILL_CORNER = 3;
		public final static int PIPE_WIDTH = 15;
		
		public Pipe() {
			super();
			setOutline(true);
		}
		@Override
		protected void fillShape(Graphics graphics) {
			corner.height =PIPE_WIDTH/2;
			corner.width = PIPE_WIDTH/2;
			graphics.setBackgroundColor(fillBackgroundColor);
			super.fillShape(graphics);
			int valuePosition = scale.getValuePosition(value, false);
			graphics.setBackgroundColor(fillColor);
			graphics.fillRoundRectangle(new Rectangle(bounds.x + lineWidth, 
					valuePosition,
					bounds.width - 2* lineWidth, 
					bounds.height - (valuePosition - bounds.y)),
					FILL_CORNER, FILL_CORNER);
			graphics.setForegroundColor(outlineColor);
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
		
		public final static int MAX_DIAMETER = 45;
		public Bulb() {
			super();
			setOutline(true);
		}
		
		@Override
		protected void fillShape(Graphics graphics) {
			graphics.setAntialias(SWT.ON);
			graphics.setBackgroundColor(fillColor);
			super.fillShape(graphics);

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
			graphics.setForegroundColor(outlineColor);
		}
		
		@Override
		protected void outlineShape(Graphics graphics) {
			super.outlineShape(graphics);
			graphics.setBackgroundColor(fillColor);
			graphics.fillRoundRectangle(new Rectangle(pipe.getBounds().x + pipe.getLineWidth(),
					scale.getValuePosition(scale.getRange().lower, false),
					Pipe.PIPE_WIDTH- pipe.getLineWidth() *2, ((LinearScale) scale).getMargin()), Pipe.FILL_CORNER, Pipe.FILL_CORNER);
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
	
		@Override
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

