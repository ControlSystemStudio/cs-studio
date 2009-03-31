package org.csstudio.sds.components.ui.internal.figures;

import java.util.Calendar;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.ui.internal.figureparts.Range;
import org.csstudio.sds.components.ui.internal.figureparts.RoundScale;
import org.csstudio.sds.components.ui.internal.figureparts.AbstractScale.LabelSide;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
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
		parent.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				 CustomMediaFactory.COLOR_WHITE));
		parent.setLayoutManager(new XYLayout());
		lws.setContents(parent);
		 
		shell.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				parent.removeAll();
				Rectangle testFigureBounds = new Rectangle(0,
						0, shell.getBounds().width, shell.getBounds().height);
				Rectangle scaleBounds = new Rectangle(10,
						10, shell.getBounds().width-50, shell.getBounds().height-50);
				RoundScaleTest testFigure = new RoundScaleTest(scaleBounds);
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



class RoundScaleTest extends Figure {
	
	public RoundScaleTest(Rectangle bounds) {

		RoundScale scale = new RoundScale();		
		
		scale.setRange(new Range(0 + 5 * 3600000d, 12*3600000d+ 5 * 3600000d));
		scale.setBounds(bounds);
		//scale.setTimeUnit(Calendar.MINUTE);
		scale.setFormatPattern("H");
		scale.setMajorGridStep(3600000);
		scale.setTickLableSide(LabelSide.Secondary);
		//scale.setLogScale(true);
		scale.setDateEnabled(true);
		scale.setStartAngle(90);
		scale.setEndAngle(90.00000001);
		//scale.setScaleLineVisible(false);

		add(scale);
		
	}
}



class XSliderFigureTest extends Figure {
	
	public XSliderFigureTest(Rectangle bounds) {

		ScaledSliderFigure slider = new ScaledSliderFigure();		
		slider.setBounds(bounds);
		slider.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_WHITE));
		slider.setMaximum(1000);
		slider.setMinimum(0);
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





