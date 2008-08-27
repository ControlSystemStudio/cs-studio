package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Represents the model for the Sixteen Binary Bar.
 * 
 * @author Alen Vrecko
 * 
 */
public class SixteenBinaryBarModel extends AbstractWidgetModel {

	public static final String PROP_ON_COLOR = "onColor"; //$NON-NLS-1$

	public static final String PROP_OFF_COLOR = "offColor"; //$NON-NLS-1$

	public static final String PROP_LABEL_FONT = "labelFont"; //$NON-NLS-1$

	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$

	public static final String PROP_SHOW_LABELS = "showLabels"; //$NON-NLS-1$

	public static final String PROP_INTERNAL_FRAME_THICKNESS = "internalFrameSize"; //$NON-NLS-1$

	public static final String PROP_LABEL_COLOR = "labelColor";

	public static final String PROP_INTERNAL_FRAME_COLOR = "internalFrameColor";

	public static final String ID = "org.csstudio.sds.components.SixteenBinaryBar"; //$NON-NLS-1$

	public static final String PROP_VALUE = "value"; //$NON-NLS-1$

	private static final int DEFAULT_HEIGHT = 50;

	/**
	 * The default value of the orientation property.
	 */
	private static final boolean DEFAULT_ORIENTATION_HORIZONTAL = true;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;

	/**
	 * Standard constructor.
	 */
	public SixteenBinaryBarModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_VALUE, new IntegerProperty("Value",
				WidgetPropertyCategory.Behaviour, 0));

		addProperty(PROP_HORIZONTAL, new BooleanProperty(
				"Horizontal Orientation", WidgetPropertyCategory.Display,
				DEFAULT_ORIENTATION_HORIZONTAL));
		addProperty(PROP_SHOW_LABELS, new BooleanProperty("Show labels",
				WidgetPropertyCategory.Display, false));

		addProperty(PROP_LABEL_FONT, new FontProperty("Label fonts",
				WidgetPropertyCategory.Display, new FontData("Arial", 9,
						SWT.NORMAL)));
		addProperty(PROP_INTERNAL_FRAME_THICKNESS, new IntegerProperty(
				"Internal frame thickness", WidgetPropertyCategory.Display, 1));

		// Nasty way of getting the colors better say SWT way of getting the
		// colors.

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				addProperty(PROP_ON_COLOR, new ColorProperty("On color",
						WidgetPropertyCategory.Display, Display.getDefault()
								.getSystemColor(SWT.COLOR_GREEN).getRGB()));
				addProperty(PROP_OFF_COLOR, new ColorProperty("Off color",
						WidgetPropertyCategory.Display, Display.getDefault()
								.getSystemColor(SWT.COLOR_GRAY).getRGB()));
				addProperty(PROP_LABEL_COLOR, new ColorProperty(
						"Label text color", WidgetPropertyCategory.Display,
						Display.getDefault().getSystemColor(SWT.COLOR_BLACK)
								.getRGB()));
				addProperty(PROP_INTERNAL_FRAME_COLOR, new ColorProperty(
						"Internal Frame Color", WidgetPropertyCategory.Display,
						Display.getDefault().getSystemColor(SWT.COLOR_BLACK)
								.getRGB()));

			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
		buffer.append("Value:\t");
		buffer.append(createTooltipParameter(PROP_VALUE));
		return buffer.toString();
	}

	public RGB getOnColor() {
		return getProperty(PROP_ON_COLOR).getPropertyValue();
	}

	public RGB getOffColor() {
		return getProperty(PROP_OFF_COLOR).getPropertyValue();
	}

	public FontData getLabelFont() {
		return getProperty(PROP_LABEL_FONT).getPropertyValue();
	}

	public boolean getHorizontal() {
		return getProperty(PROP_HORIZONTAL).getPropertyValue();
	}

	public boolean getShowLabels() {
		return getProperty(PROP_SHOW_LABELS).getPropertyValue();
	}

	public int getValue() {
		return getProperty(PROP_VALUE).getPropertyValue();
	}

	public int getInternalFrameThickness() {
		return getProperty(PROP_INTERNAL_FRAME_THICKNESS).getPropertyValue();
	}

	public RGB getInternalFrameColor() {
		return getProperty(PROP_INTERNAL_FRAME_COLOR).getPropertyValue();
	}

	public RGB getLabelColor() {
		return getProperty(PROP_LABEL_COLOR).getPropertyValue();
	}

}
