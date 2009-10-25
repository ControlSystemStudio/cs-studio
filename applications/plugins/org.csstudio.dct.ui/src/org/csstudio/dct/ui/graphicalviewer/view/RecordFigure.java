package org.csstudio.dct.ui.graphicalviewer.view;

import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.internal.ui.palette.editparts.RaisedBorder;
import org.eclipse.swt.SWT;

/**
 * Represents a {@link RecordNode}.
 * 
 * @author Sven Wende
 * 
 */
public class RecordFigure extends Panel {
	private Label valueLabel;

	/**
	 * Constructor.
	 * 
	 * @param caption the caption
	 */
	public RecordFigure(String caption) {
		setLayoutManager(new XYLayout());
		setBorder(new RaisedBorder());

		ImageFigure image = new ImageFigure();
		image.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/record.png"));
		add(image);

		setConstraint(image, new Rectangle(0, 0, 20, 20));

		Label label = new Label(caption);
		add(label);
		label.setFont(CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE));
		setConstraint(label, new Rectangle(20, 0, 130, 20));

		valueLabel = new Label("");
		add(valueLabel);
		valueLabel.setFont(CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE));
		valueLabel.setForegroundColor(CustomMediaFactory.getInstance().getColor(255, 0, 0));

		setConstraint(valueLabel, new Rectangle(150, 0, 50, 20));
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public Dimension getPreferredSize(int hint, int hint2) {
		return new Dimension(200, 20);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public Dimension getMinimumSize(int w, int h) {
		return new Dimension(200, 20);
	}

	/**
	 * Set the value received from the control system.
	 * 
	 * @param value
	 *            the control system value to display
	 */
	public void setValue(String value) {
		valueLabel.setText(value);
	}

}
