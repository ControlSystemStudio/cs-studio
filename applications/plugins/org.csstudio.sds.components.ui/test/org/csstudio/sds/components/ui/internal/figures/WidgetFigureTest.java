package org.csstudio.sds.components.ui.internal.figures;

import java.util.Calendar;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figureparts.Bulb;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScale;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScaledRamp;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScaledRamp.Threshold;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * To avoid to start CSS frequently to see the 
 * drawing result of the widget figure, this program simply draw the widget figure on a shell.
 * <p> 
 * This is a common java program, <b>not</b> a JUnit test. 
 * </p>
 * @author Xihui Chen
 *
 */
public class WidgetFigureTest {
	public static void main(String[] args) {
		final Shell shell = new Shell();
		final LightweightSystem lws = new LightweightSystem(shell);
		final Figure parent = new Figure();		
		parent.setLayoutManager(new XYLayout());
		lws.setContents(parent);
		 
		shell.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				parent.removeAll();
				Rectangle testFigureBounds = new Rectangle(0,
						0, shell.getBounds().width, shell.getBounds().height);
				Rectangle scaleBounds = new Rectangle(10,
						10, shell.getBounds().width-50, shell.getBounds().height-50);
				
				KnobTest testFigure = new KnobTest(scaleBounds);
				//BulbTest testFigure = new BulbTest(scaleBounds);
				//XMeterTest testFigure = new XMeterTest(scaleBounds);				
				//GaugeTest testFigure = new GaugeTest(scaleBounds);
				//AnyTest testFigure = new AnyTest(scaleBounds);				
				//RoundScaleTest testFigure = new RoundScaleTest(scaleBounds);
				//XSliderFigureTest testFigure = new XSliderFigureTest(scaleBounds);
				//TankFigureTest testFigure = new TankFigureTest(scaleBounds);
				//ThermoFigureTest testFigure = new ThermoFigureTest(scaleBounds);
				parent.add(testFigure,testFigureBounds);
				lws.paint(event.gc);
				
			}
		});
		shell.setSize(500, 500);
	    shell.open();
	    shell.setText("Widget Figure Test");
	    Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	}
}




class KnobTest extends Figure {
	
	public KnobTest(Rectangle bounds) {

		KnobFigure knob = new KnobFigure();	
		//knob.setBulbColor(CustomMediaFactory.COLOR_GREEN);
		knob.setValue(10);
		knob.setBounds(bounds);		
		//knob.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
		//		new RGB(127,127,127)));
		//		new RGB(142,180,227)));

		//knob.setEffect3D(false);
		//	knob.setMaximum(60);
		//knob.setGradient(false);
		//knob.setLogScale(true);
		knob.setThumbColor(new RGB(127, 127, 127));
		knob.setForegroundColor(CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK));
		add(knob);		
	}
}


class BulbTest extends Figure {
	
	public BulbTest(Rectangle bounds) {

		Bulb bulb = new Bulb();	
		bulb.setBounds(bounds);		
		bulb.setBulbColor(CustomMediaFactory.COLOR_GRAY);
		add(bulb);		
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_GRAY));
		//graphics.fillRectangle(bounds);
		super.paintFigure(graphics);
	}
}



class XMeterTest extends Figure {
	
	public XMeterTest(Rectangle bounds) {

		RefreshableXMeterFigure xMeter = new RefreshableXMeterFigure();	
		xMeter.setNeedleColor(CustomMediaFactory.COLOR_RED);
		xMeter.setValue(65);
		//xMeter.setNeedleColor(CustomMediaFactory.COLOR_BLUE);
		xMeter.setBounds(bounds);		
		xMeter.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				new RGB(255,255,255)));
		//		new RGB(142,180,227)));

		//xMeter.setEffect3D(false);
		//xMeter.setGradient(false);
		//xMeter.setForegroundColor(CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_WHITE));
		add(xMeter);		
	}
}




class GaugeTest extends Figure {
	
	public GaugeTest(Rectangle bounds) {

		RefreshableGaugeFigure gauge = new RefreshableGaugeFigure();	
		gauge.setNeedleColor(CustomMediaFactory.COLOR_RED);
		gauge.setValue(34.28);
		gauge.setBounds(bounds);		
		gauge.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				new RGB(127,127,127)));
		//		new RGB(142,180,227)));

		///gauge.setEffect3D(false);
		gauge.setGradient(false);
		gauge.setForegroundColor(CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_WHITE));
		add(gauge);		
	}
}




class AnyTest extends Figure {
	
	public AnyTest(Rectangle bounds) {		
		ArcTest arc = new ArcTest();
		arc.setBounds(bounds);
		add(arc);
	}	
}

class ArcTest extends Figure {
	
	public ArcTest() {		
	}
	
	
	@Override
	protected void paintFigure(Graphics graphics) {
		Color yellow = new Color(Display.getCurrent(), 255,255,0);
		Color red = new Color(Display.getCurrent(), 255,0,0);

		// TODO Auto-generated method stub
		super.paintFigure(graphics);
		graphics.fillArc(bounds, 0, 20);
		graphics.setAntialias(SWT.ON);
	    graphics.setLineWidth(20);
	    //graphics.drawRectangle(bounds);
	    Pattern pattern = new Pattern(Display.getCurrent(), bounds.x, bounds.y, 
	    		bounds.x + bounds.width, bounds.y + bounds.height, yellow, red);
	    graphics.setForegroundPattern(pattern);
	    graphics.drawArc(new Rectangle(bounds.x +10, bounds.y+10, 
	    		bounds.width -20, bounds.height -20), 315, 27);
	    graphics.setForegroundColor(new Color(Display.getCurrent(), 255, 0, 0));
	    graphics.setLineWidth(1);
	    //graphics.drawRectangle(bounds);
	   // graphics.drawArc(bounds, 180, 90);
	}
	@Override
	protected void paintClientArea(Graphics graphics) {
		// TODO Auto-generated method stub
		super.paintClientArea(graphics);
		
	}
}


class RoundScaleTest extends Figure {
	
	public RoundScaleTest(Rectangle bounds) {

		RoundScale scale = new RoundScale();		
		//scale.setStartAngle(135);
		//scale.setEndAngle(45);
		//scale.setRange(new Range(0, 12*3600000d+ 5 * 3600000d));
		scale.setBounds(bounds);
		//scale.setTimeUnit(Calendar.MINUTE);
		//scale.setFormatPattern("H");
		//scale.setMajorGridStep(3600d);
		//scale.setTickLableSide(LabelSide.Secondary);
		//scale.setLogScale(true);
		//scale.setDateEnabled(true);
		//scale.setStartAngle(90);
		//scale.setEndAngle(90.00000001);
		//scale.setScaleLineVisible(false);
					
		
		RoundScaledRamp ramp = new RoundScaledRamp(scale);				
		Rectangle rect = new Rectangle(bounds);
		//rect.shrink(100, 100);
		ramp.setBounds(rect);
		//ramp.setThresholdVisibility(Threshold.LO, false);
		//ramp.setThresholdVisibility(Threshold.HI, false);
		//ramp.setThresholdVisibility(Threshold.HIHI, false);
		//ramp.setThresholdVisibility(Threshold.LOLO, false);
		//ramp.setThresholdValue(Threshold.LO, 100);
		//ramp.setThresholdValue(Threshold.LOLO, 50);
		ramp.setGradient(false);
		add(ramp);	
		//scale.setVisible(false);
		add(scale);
		
	}
}



class XSliderFigureTest extends Figure {
	
	public XSliderFigureTest(Rectangle bounds) {

		ScaledSliderFigure slider = new ScaledSliderFigure();		
		slider.setBounds(bounds);
		slider.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_WHITE));
		slider.setValue(50);		
		slider.setFillColor(CustomMediaFactory.COLOR_RED);
		slider.setFillBackgroundColor(CustomMediaFactory.COLOR_GRAY);
		slider.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_BLACK));
		slider.setTransparent(true);	
		slider.setHihiLevel(90);
		slider.setLogScale(true);
		slider.setEffect3D(true);
		//slider.setHorizontal(true);
		add(slider);
		
	}
}


class TankFigureTest extends Figure {
	
	public TankFigureTest(Rectangle bounds) {

		RefreshableTankFigure tank = new RefreshableTankFigure();		
		tank.setBounds(bounds);
		tank.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_WHITE));
		tank.setValue(50);		
		tank.setFillColor(CustomMediaFactory.COLOR_BLUE);
		tank.setFillBackgroundColor(CustomMediaFactory.COLOR_RED);
		tank.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_BLACK));
		tank.setTransparent(true);	
		tank.setHihiLevel(90);
		add(tank);
		
	}

}

class ThermoFigureTest extends Figure {
	
	public ThermoFigureTest(Rectangle bounds) {

		RefreshableThermoFigure thermo = new RefreshableThermoFigure();		
		thermo.setBounds(bounds);
		thermo.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_WHITE));
		thermo.setValue(28.01);		
		thermo.setFillColor(CustomMediaFactory.COLOR_RED);
		thermo.setFillBackgroundColor(CustomMediaFactory.COLOR_GRAY);
		thermo.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_BLACK));
		thermo.setTransparent(true);	
		thermo.setHihiLevel(90);
		add(thermo);
		
	}

}





