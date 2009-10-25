package org.csstudio.dct.ui.graphicalviewer.view;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;

public class InstanceBoxFigure extends Panel {
	private Button button;
	
	public InstanceBoxFigure(final String caption) {
		setLayoutManager(new ToolbarLayout());
		setBackgroundColor(CustomMediaFactory.getInstance().getColor(255,0,0));
		setBorder(new LineBorder(CustomMediaFactory.getInstance().getColor(200,200,0), 1));
		
		button  = new Button(caption);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				CentralLogger.getInstance().info(null, "ddsads"+caption);
			}
		});
		
		add(button);
		
		button.setFont(CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE));
		
		button.setBackgroundColor(CustomMediaFactory.getInstance().getColor(0,200,0));
	}

	@Override
	public Dimension getPreferredSize(int hint, int hint2) {
		return new Dimension(70,20);
	}
	
	@Override
	public Dimension getMinimumSize(int w, int h) {
		return new Dimension(70,20);
	}
	
	public Button getButton() {
		return button;
	}
}
