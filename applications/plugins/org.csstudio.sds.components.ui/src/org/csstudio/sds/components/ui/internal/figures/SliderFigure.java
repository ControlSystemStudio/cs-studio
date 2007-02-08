package org.csstudio.sds.components.ui.internal.figures;

import java.beans.PropertyChangeListener;

import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.geometry.Dimension;

public class SliderFigure extends Panel implements IRefreshableFigure {
	public SliderFigure(){
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
	}

	public void randomNoiseRefresh() {
		// TODO Auto-generated method stub
		
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}
