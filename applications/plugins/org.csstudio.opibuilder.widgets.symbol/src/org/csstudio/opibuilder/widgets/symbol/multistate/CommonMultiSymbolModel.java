/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FilePathPropertyWithFilter;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.MatrixProperty;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.csstudio.opibuilder.widgets.symbol.bool.MonitorBoolSymbolModel;
import org.csstudio.opibuilder.widgets.symbol.util.ImagePermuter;
import org.csstudio.opibuilder.widgets.symbol.util.PermutationMatrix;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolLabelPosition;
import org.csstudio.swt.widgets.figures.AbstractBoolFigure.BoolLabelPosition;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines a common model for Multistate Symbol Image widget.
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public abstract class CommonMultiSymbolModel extends AbstractPVWidgetModel {
	
	/**
	 * File path of the image.
	 */
	public static final String PROP_SYMBOL_IMAGE_FILE = "image_file";
	/**
	 * Crop part (in pixels) on top side of the image.
	 */
	public static final String PROP_TOPCROP = "crop_top";
	/**
	 * Crop part (in pixels) on bottom side of the image.
	 */
	public static final String PROP_BOTTOMCROP = "crop_bottom";
	/**
	 * Crop part (in pixels) on left side of the image.
	 */
	public static final String PROP_LEFTCROP = "crop_left";
	/**
	 * Crop part (in pixels) on right side of the image.
	 */
	public static final String PROP_RIGHTCROP = "crop_right";
	/**
	 * True if the image should be stretched to the widget size.
	 */
	public static final String PROP_STRETCH = "stretch_to_fit";
	/**
	 * True if the widget size is automatically adjusted to the size of the
	 * image.
	 */
	public static final String PROP_AUTOSIZE = "auto_size";
	/**
	 * Degree value of the image.
	 */
	public static final String PROP_DEGREE = "degree";
	/**
	 * Horizontal flip applied on the image.
	 */
	public static final String PROP_FLIP_HORIZONTAL = "flip_horizontal";
	/**
	 * Vertical flip applied on the image.
	 */
	public static final String PROP_FLIP_VERTICAL = "flip_vertical";
	/**
	 * Image disposition (permutation matrix)
	 */
	public static final String PERMUTATION_MATRIX = "permutation_matrix";
	// Obsolete property
	public static final String IMAGE_DISPOSITION = "image_disposition"; 
	/**
	 * Images values of the symbol widget.
	 */
	public static final String PROP_ITEMS = "items";//$NON-NLS-1$
	/**
	 * If this is true, items will be loaded from input Enum PV.
	 */
	public static final String PROP_ITEMS_FROM_PV = "items_from_pv";//$NON-NLS-1$
	
	/** Widget color when boolean widget is on. */
	public static final String PROP_ON_COLOR = "on_color"; //$NON-NLS-1$
	
	/** Widget color when boolean widget is off. */
	public static final String PROP_OFF_COLOR = "off_color"; //$NON-NLS-1$
	
	public static final RGB DEFAULT_ON_COLOR = new RGB(0, 255, 0);
	public static final RGB DEFAULT_OFF_COLOR = new RGB(255, 0, 0);
	
	/** True if the boolean label should be visible. */
	public static final String PROP_SHOW_SYMBOL_LABEL = "show_boolean_label"; //$NON-NLS-1$
	public static final String PROP_SYMBOL_LABEL_POS = "boolean_label_position"; //$NON-NLS-1$
	
	/**
	 * The color of the selected item.
	 */
//	public static final String PROP_SELECTED_COLOR = "selected_color";//$NON-NLS-1$

	public static final String[] IMAGE_EXTENSIONS = new String[] { "gif",
			"png", "GIF", "PNG", "svg", "SVG" };

	@Override
	protected void configureProperties() {
		super.configureBaseProperties();

		// Image properties
		addProperty(new IntegerProperty(PROP_TOPCROP, "Crop Top",
				WidgetPropertyCategory.Image, 0));
		addProperty(new IntegerProperty(PROP_BOTTOMCROP, "Crop Bottom",
				WidgetPropertyCategory.Image, 0));
		addProperty(new IntegerProperty(PROP_LEFTCROP, "Crop Left",
				WidgetPropertyCategory.Image, 0));
		addProperty(new IntegerProperty(PROP_RIGHTCROP, "Crop Right",
				WidgetPropertyCategory.Image, 0));
		addProperty(new BooleanProperty(PROP_STRETCH, "Stretch to Fit",
				WidgetPropertyCategory.Image, false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size",
				WidgetPropertyCategory.Image, true));
		addProperty(new IntegerProperty(PROP_DEGREE, "Rotation Angle",
				WidgetPropertyCategory.Image, 0));
		addProperty(new BooleanProperty(PROP_FLIP_HORIZONTAL,
				"Flip Horizontal", WidgetPropertyCategory.Image, false));
		addProperty(new BooleanProperty(PROP_FLIP_VERTICAL, 
				"Flip Vertical", WidgetPropertyCategory.Image, false));
//		setPropertyVisibleAndSavable(PROP_DEGREE, false, true);
//		setPropertyVisibleAndSavable(PROP_FLIP_HORIZONTAL, false, true);
//		setPropertyVisibleAndSavable(PROP_FLIP_VERTICAL, false, true);
		
		addProperty(new MatrixProperty(PERMUTATION_MATRIX,
				"Permutation Matrix", WidgetPropertyCategory.Image,
				PermutationMatrix.generateIdentityMatrix().getMatrix()));
		setPropertyVisibleAndSavable(PERMUTATION_MATRIX, false, true);
		// Obsolete property
		addProperty(new StringProperty(IMAGE_DISPOSITION, "Image Disposition",
				WidgetPropertyCategory.Image, "1234"));
		setPropertyVisibleAndSavable(IMAGE_DISPOSITION, false, false);

		// Display properties
		addProperty(new FilePathPropertyWithFilter(PROP_SYMBOL_IMAGE_FILE,
				"Symbol Image", WidgetPropertyCategory.Display, new Path(""),
				IMAGE_EXTENSIONS));
		addProperty(new ColorProperty(PROP_ON_COLOR, "Non-Zero Color",
				WidgetPropertyCategory.Display, DEFAULT_ON_COLOR));
		addProperty(new ColorProperty(PROP_OFF_COLOR, "Zero Color",
				WidgetPropertyCategory.Display, DEFAULT_OFF_COLOR));
		addProperty(new BooleanProperty(PROP_SHOW_SYMBOL_LABEL, "Show Symbol Label",
				WidgetPropertyCategory.Display,false));
		addProperty(new ComboProperty(PROP_SYMBOL_LABEL_POS, "Symbol Label Position", 
				WidgetPropertyCategory.Display, BoolLabelPosition.stringValues(), 0));
		
		addProperty(new StringListProperty(
				PROP_ITEMS, "Items", WidgetPropertyCategory.Behavior, new ArrayList<String>()));
		addProperty(new BooleanProperty(PROP_ITEMS_FROM_PV, "Items From PV",
				WidgetPropertyCategory.Behavior, true));
//		setPropertyVisibleAndSavable(PROP_ITEMS, false, true);
//		setPropertyVisibleAndSavable(PROP_ITEMS_FROM_PV, false, true);
	}

	/**
	 * Get the path to the specified file.
	 * 
	 * @return The path to the specified file
	 */
	public IPath getSymbolImagePath() {
		IPath absolutePath = (IPath) getProperty(PROP_SYMBOL_IMAGE_FILE)
				.getPropertyValue();
		if (!absolutePath.isAbsolute()) {
			absolutePath = ResourceUtil.buildAbsolutePath(this, absolutePath);
		}
		return absolutePath;
	}

	/**
	 * Get the amount of pixels, which should be cropped from the top edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getTopCrop() {
		return (Integer) getProperty(PROP_TOPCROP).getPropertyValue();
	}

	/**
	 * Get the amount of pixels, which should be cropped from the bottom edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getBottomCrop() {
		return (Integer) getProperty(PROP_BOTTOMCROP).getPropertyValue();
	}

	/**
	 * Get the amount of pixels, which should be cropped from the left edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getLeftCrop() {
		return (Integer) getProperty(PROP_LEFTCROP).getPropertyValue();
	}

	/**
	 * Get the amount of pixels, which should be cropped from the right edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getRightCrop() {
		return (Integer) getProperty(PROP_RIGHTCROP).getPropertyValue();
	}

	/**
	 * Check if the image should be stretched.
	 * 
	 * @return True if stretched, false otherwise
	 */
	public boolean getStretch() {
		return (Boolean) getProperty(PROP_STRETCH).getPropertyValue();
	}

	/**
	 * Check if the widget should be auto sized according the image size.
	 * 
	 * @return True if auto sized, false otherwise
	 */
	public boolean isAutoSize() {
		return (Boolean) getProperty(PROP_AUTOSIZE).getPropertyValue();
	}

	/**
	 * Get the current degree of the image.
	 * 
	 * @return The degree value
	 */
	public int getDegree() {
		return (Integer) getProperty(PROP_DEGREE).getPropertyValue();
	}

	/**
	 * Check if an horizontal flip was applied.
	 * 
	 * @return True if horizontal flip, false otherwise
	 */
	public boolean isFlipHorizontal() {
		return (Boolean) getProperty(PROP_FLIP_HORIZONTAL).getPropertyValue();
	}

	/**
	 * Check if an vertical flip was applied.
	 * 
	 * @return True if vertical flip, false otherwise
	 */
	public boolean isFlipVertical() {
		return (Boolean) getProperty(PROP_FLIP_VERTICAL).getPropertyValue();
	}

	/**
	 * Get the current disposition of the image.
	 * 
	 * @return The permutation matrix
	 */
	public PermutationMatrix getPermutationMatrix() {
		return new PermutationMatrix((double[][]) getProperty(
				PERMUTATION_MATRIX).getPropertyValue());
	}

	@Override
	public void rotate90(boolean clockwise) {
		int degree = (Integer) getPropertyValue(MonitorBoolSymbolModel.PROP_DEGREE);
		switch (degree) {
		case 0:
			degree = clockwise ? 90 : 270;
			break;
		case 90:
			degree = clockwise ? 180 : 0;
			break;
		case 180:
			degree = clockwise ? 270 : 90;
			break;
		case 270:
			degree = clockwise ? 0 : 180;
			break;
		default:
			Activator.getLogger().log(Level.WARNING,
					"ERROR in value of degree " + degree);
		}
		setPropertyValue(MonitorBoolSymbolModel.PROP_DEGREE, degree);
	}

	@Override
	public void flipHorizontally() {
		boolean oldValue = (Boolean) getPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_HORIZONTAL);
		setPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_HORIZONTAL, !oldValue);
	}

	@Override
	public void flipVertically() {
		boolean oldValue = (Boolean) getPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_VERTICAL);
		setPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_VERTICAL, !oldValue);
	}

	/**
	 * @return the on color
	 */
	public Color getOnColor() {
		return getSWTColorFromColorProperty(PROP_ON_COLOR);
	}

	/**
	 * @return the off color
	 */
	public Color getOffColor() {
		return getSWTColorFromColorProperty(PROP_OFF_COLOR);
	}

	/**
	 * @return true if the boolean label should be shown, false otherwise
	 */
	public boolean isShowSymbolLabel() {
		return (Boolean) getProperty(PROP_SHOW_SYMBOL_LABEL).getPropertyValue();
	}

	public SymbolLabelPosition getSymbolLabelPosition() {
		return SymbolLabelPosition.values()[(Integer) getPropertyValue(PROP_SYMBOL_LABEL_POS)];
	}

	@SuppressWarnings("unchecked")
	public List<String> getItems() {
		return (List<String>) getPropertyValue(PROP_ITEMS);
	}

	public boolean isItemsFromPV() {
		return (Boolean) getPropertyValue(PROP_ITEMS_FROM_PV);
	}
	
	@Override
	public void setPropertyValue(Object id, Object value) {
		// Override obsolete properties
		if (id != null && id instanceof String) {
			if (((String) id).equals(IMAGE_DISPOSITION)) {
				id = PERMUTATION_MATRIX;
				value = ImagePermuter.getMatrix((String) value);
			}
		}
		super.setPropertyValue(id, value);
	}

}
