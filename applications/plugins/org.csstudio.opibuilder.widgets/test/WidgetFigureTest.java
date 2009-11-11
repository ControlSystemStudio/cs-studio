

import java.util.Calendar;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.datadefinition.ColorMap.PredefinedColorMap;
import org.csstudio.opibuilder.widgets.figures.BoolButtonFigure;
import org.csstudio.opibuilder.widgets.figures.BoolSwitchFigure;
import org.csstudio.opibuilder.widgets.figures.IntensityGraphFigure;
import org.csstudio.opibuilder.widgets.figures.LEDFigure;
import org.csstudio.opibuilder.widgets.figures.LabelFigure;
import org.csstudio.opibuilder.widgets.figures.TabFigure;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
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
		shell.setSize(800, 500);
	    shell.open();
	    
		final LightweightSystem lws = new LightweightSystem(shell);
		IntensityGraphFigureTest testFigure = new IntensityGraphFigureTest();
		lws.setContents(testFigure);
		
	    shell.setText("Widget Figure Test");
	    Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    System.out.println(Calendar.getInstance().getTime());
	}
}


class LEDTest extends Figure {
	
	public LEDTest(Rectangle bounds) {

		LEDFigure led = new LEDFigure();	
		led.setBounds(bounds);	
		led.setValue(0);
		add(led);		
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_GRAY));
		//graphics.fillRectangle(bounds);
		super.paintFigure(graphics);
	}
}

class BoolButtonTest extends Figure {
	
	public BoolButtonTest(Rectangle bounds) {

		BoolButtonFigure btn = new BoolButtonFigure();	
		btn.setBounds(bounds);
		//btn.setEffect3D(false);
		btn.setFont(CustomMediaFactory.getInstance().getFont(CustomMediaFactory.FONT_ARIAL));
		//btn.setSquareButton(true);
		btn.setToggle(true);
		//btn.setShowConfirmDialog(true);
		btn.setShowBooleanLabel(true);
		btn.setPassword("123");
		btn.setRunMode(true);
		add(btn);		
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				CustomMediaFactory.COLOR_GRAY));
		//graphics.fillRectangle(bounds);
		super.paintFigure(graphics);
	}
}

class BoolSwitchTest extends Figure {
	
	private BoolSwitchFigure boolSwitch;

	public BoolSwitchTest() {

		boolSwitch = new BoolSwitchFigure();	
		
		//boolSwitch.setEffect3D(false);
		boolSwitch.setFont(CustomMediaFactory.getInstance().getFont(CustomMediaFactory.FONT_ARIAL));
		//btn.setSquareButton(true);
		boolSwitch.setToggle(true);
		//btn.setShowConfirmDialog(true);
		boolSwitch.setShowBooleanLabel(false);
		boolSwitch.setPassword("123");
		boolSwitch.setRunMode(true);
		//boolSwitch.setEnabled(false);
		boolSwitch.setValue(0);
		boolSwitch.setOnColor(new RGB(240, 240, 240));
		boolSwitch.setOffColor(new RGB(230, 230, 230));
		//boolSwitch.setOffColor(new RGB(255, 0, 0));
		add(boolSwitch);		
	}
	@Override
	protected void layout() {
		boolSwitch.setBounds(bounds);
		super.layout();
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				new RGB(0,64,128)));
		//graphics.fillRectangle(bounds);
		super.paintFigure(graphics);
	}
}

class LabelFigureTest extends Figure {
	
	private LabelFigure label;

	public LabelFigureTest() {

		label = new LabelFigure();	
		label.setBackgroundColor(CustomMediaFactory.getInstance().getColor(0,255,0));

		label.setOpaque(false);
		label.setText("hslfsfsfdsdfs;ksjdf;\nlsdfkj\n\n\n\n\n\n\n\n\nsdfsfdsdfsdfsdfsdfss;dlfksjfsld;fsjf;lksdjf;lskfj;lskdfjs;ldkfjsd;lfk");
		//boolSwitch.setEffect3D(false);
		label.setFont(CustomMediaFactory.getInstance().getFont(CustomMediaFactory.FONT_ARIAL));
		
		add(label);		
	}
	@Override
	protected void layout() {
		label.setBounds(bounds);
		super.layout();
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				new RGB(0,64,128)));
		super.paintFigure(graphics);
	}
}


class TabFigureTest extends Figure {
	
	private TabFigure tab;

	public TabFigureTest() {

		tab = new TabFigure();	
		tab.setBackgroundColor(CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_WHITE));
		tab.setFont(CustomMediaFactory.getInstance().getFont(CustomMediaFactory.FONT_ARIAL));
		tab.addTab("1");
		tab.addTab("tab2");
		tab.addTab("tab1");
		tab.addTab("tab2sdfsdf");
		tab.setTabColor(2, CustomMediaFactory.getInstance().getColor(new RGB(128, 0, 128)));
		//tab.setTabColor(3, CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_PINK));

		//tab.setActiveTabIndex(0);
		add(tab);		
	}
	@Override
	protected void layout() {
		tab.setBounds(bounds.getCopy().shrink(20, 20));
		super.layout();
	}
	
	
}

	
	
class IntensityGraphFigureTest extends Figure {
	
	private IntensityGraphFigure intensityGraph;
	private double[] datas;

	public IntensityGraphFigureTest() {
		intensityGraph = new IntensityGraphFigure();
		datas = new double[65536];
		
		intensityGraph.setDataArray(datas);
		intensityGraph.setMax(1);
		intensityGraph.setMin(-1);
		intensityGraph.setDataWidth(256);
		intensityGraph.setDataHeight(256);
		intensityGraph.setColorMap(new ColorMap(PredefinedColorMap.ColorSpectrum, true, true));
		add(intensityGraph);	
		final Runnable task = new Runnable(){
			public void run() {
				double phase = Math.random()*10;
				for(int i=0; i<256; i++){
					for(int j=0; j<256; j++){
						int x = j-128;
						int y = i-128;		
						double p = Math.sqrt(x*x + y*y);
						datas[i*256 + j] = Math.sin(p*2*Math.PI/256 + phase);
					}
				}
				intensityGraph.setDataArray(datas);
				Display.getCurrent().timerExec(10, this);
			}
		};
		Display.getCurrent().timerExec(10, task);
		
	}
	@Override
	protected void layout() {
		intensityGraph.setBounds(bounds.getCopy().shrink(20, 20));
		super.layout();
	}
	
	
}
