package org.csstudio.sds.components.ui.internal.figures;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.geometry.Dimension;

public class SliderFigure extends Panel implements IRefreshableFigure {
	List<ISliderListener> _sliderListeners;
	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	
	public SliderFigure(){
		_sliderListeners = new ArrayList<ISliderListener>();
		
		ScrollBar scrollBar = new ScrollBar();
//		scrollBar.setExtent(1000);
		scrollBar.setMaximum(1000);
		scrollBar.setMinimum(1);
		scrollBar.setValue(400);
		Ellipse thumb = new Ellipse();
		thumb.setSize(10,20);
		thumb.setMinimumSize(new Dimension(60, 60));
		thumb.setBackgroundColor(ColorConstants.red);

//		thumb.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.RIDGED));

		scrollBar.setThumb(thumb);
//		setLayoutManager(new ScrollPaneLayout());
		setLayoutManager(new BorderLayout());
		add(scrollBar, BorderLayout.CENTER);
		scrollBar.setHorizontal(true);
		scrollBar.validate();
		validate();
		
		// add listener
		scrollBar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE, new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				int newValue = (Integer) event.getNewValue();
				for(ISliderListener l : _sliderListeners) {
					l.sliderValueChanged(newValue);
				}
			}
		});
	}

	public void addSliderListener(ISliderListener listener) {
		_sliderListeners.add(listener);
	}
	
	public void removeSliderListener(ISliderListener listener) {
		_sliderListeners.remove(listener);
	}

	public void randomNoiseRefresh() {
		// TODO Auto-generated method stub
		
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}
	
	public interface ISliderListener {
		void sliderValueChanged(int newValue);
	}
}
