package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.platform.ui.util.CustomMediaFactory;
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
			@Override
			public void handleEvent(Event event) {
				parent.removeAll();
				Rectangle testFigureBounds = new Rectangle(0,
						0, shell.getBounds().width, shell.getBounds().height);
				Rectangle scaleBounds = new Rectangle(10,
						10, shell.getBounds().width-50, shell.getBounds().height-50);
				ThermoFigureTest testFigure = new ThermoFigureTest(scaleBounds);
				
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





