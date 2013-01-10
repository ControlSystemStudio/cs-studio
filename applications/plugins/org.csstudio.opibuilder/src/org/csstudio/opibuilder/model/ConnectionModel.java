/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.datadefinition.LineStyle;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.PointListProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Model for connection.
 * 
 * @author Xihui Chen
 * 
 */
public class ConnectionModel extends AbstractWidgetModel {

	/**
	 * Type of router.
	 */
	public enum RouterType {
		MANHATTAN("Manhattan"),
		STRAIGHT_LINE("Direct Connect");

		String description;

		RouterType(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}

		public static String[] stringValues() {
			String[] sv = new String[values().length];
			int i = 0;
			for (RouterType p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}

	public enum ArrowType {
		None("None"), From("From"), To("To"), Both("Both");

		String description;

		private ArrowType(String desc) {
			this.description = desc;
		}

		public static String[] stringValues() {
			String[] sv = new String[values().length];
			int i = 0;
			for (ArrowType p : values())
				sv[i++] = p.toString();
			return sv;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	public static final double ARROW_ANGLE = Math.PI / 10;

	/**
	 * The type ID of this model.
	 */
	public static final String ID = "org.csstudio.opibuilder.connection"; //$NON-NLS-1$

	/**
	 * Width of the line.
	 */
	public static final String PROP_LINE_WIDTH = "line_width";//$NON-NLS-1$

	/**
	 * Style of the line.
	 */
	public static final String PROP_LINE_STYLE = "line_style";//$NON-NLS-1$

	/**
	 * Color of the line.
	 */
	public static final String PROP_LINE_COLOR = "line_color";//$NON-NLS-1$

	public static final String PROP_ROUTER = "router";//$NON-NLS-1$

	public static final String PROP_ARROW_TYPE = "arrows";//$NON-NLS-1$

	public static final String PROP_FILL_ARROW = "fill_arrow"; //$NON-NLS-1$

	public static final String PROP_ARROW_LENGTH = "arrow_length"; //$NON-NLS-1$

	/**
	 * True if anti alias is enabled for the figure.
	 */
	public static final String PROP_ANTIALIAS = "anti_alias"; //$NON-NLS-1$

	/**
	 * Source Terminal
	 */
	public static final String PROP_SRC_TERM = "src_term";//$NON-NLS-1$

	/**
	 * Target Terminal
	 */
	public static final String PROP_TGT_TERM = "tgt_term";//$NON-NLS-1$

	/**
	 * The source widget UID
	 */
	public static final String PROP_SRC_WUID = "src_wuid"; //$NON-NLS-1$

	/**
	 * The target widget UID
	 */
	public static final String PROP_TGT_WUID = "tgt_wuid"; //$NON-NLS-1$
	
	/**
	 * All points of this connection except start and end anchor.
	 * null if it should be routed by router.
	 */
	public static final String PROP_POINTS = "points"; //$NON-NLS-1$

	/** True, if the connection is attached to its endpoints. */
	private boolean isConnected;

	/** Connection's source endpoint. */
	private AbstractWidgetModel source;
	/** Connection's target endpoint. */
	private AbstractWidgetModel target;

	private DisplayModel displayModel;
	
	private PointList originPoints;

	
	/**Construct a connection model which belongs to the displayModel.
	 * If this is a temporary connection model which doesn't belong to any display model,
	 * displayModel can be null. 
	 * @param displayModel the display model. Can be null.
	 */
	public ConnectionModel(DisplayModel displayModel) {
		this.displayModel = displayModel;
	}
	
	@Override
	protected void configureProperties() {
		addProperty(new IntegerProperty(PROP_LINE_WIDTH, "Line Width",
				WidgetPropertyCategory.Display, 1, 1, 100));
		addProperty(new ComboProperty(PROP_LINE_STYLE, "Line Style",
				WidgetPropertyCategory.Display, LineStyle.stringValues(), 0));
		addProperty(new ColorProperty(PROP_LINE_COLOR, "Line Color",
				WidgetPropertyCategory.Display, CustomMediaFactory.COLOR_BLACK));
		addProperty(new ComboProperty(PROP_ROUTER, "Router",
				WidgetPropertyCategory.Display, RouterType.stringValues(), 0));
		addProperty(new ComboProperty(PROP_ARROW_TYPE, "Arrows",
				WidgetPropertyCategory.Display, ArrowType.stringValues(), 0));
		addProperty(new BooleanProperty(PROP_FILL_ARROW, "Fill Arrow",
				WidgetPropertyCategory.Display, true));
		addProperty(new IntegerProperty(PROP_ARROW_LENGTH, "Arrow Length",
				WidgetPropertyCategory.Display, 15, 5, 1000));
		addProperty(new BooleanProperty(PROP_ANTIALIAS, "Anti Alias",
				WidgetPropertyCategory.Display, true));		
		addProperty(new StringProperty(PROP_SRC_TERM, "Source Terminal",
				WidgetPropertyCategory.Display, ""));
		addProperty(new StringProperty(PROP_TGT_TERM, "Target Terminal",
				WidgetPropertyCategory.Display, ""));
		addProperty(new PointListProperty(PROP_POINTS, "Points",
				WidgetPropertyCategory.Display, new PointList()));
		setPropertyVisibleAndSavable(PROP_POINTS, false, true);
		
		AbstractWidgetProperty srcWUIDProp = new StringProperty(PROP_SRC_WUID,
				"Source WUID", WidgetPropertyCategory.Display, "");
		addProperty(srcWUIDProp);
		srcWUIDProp.addPropertyChangeListener(new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(displayModel == null)
					return;
				String wuid = evt.getNewValue().toString();
				AbstractWidgetModel w = displayModel.getWidgetFromWUID(wuid);
				if(w != null){
					source = w;
					reconnect();
				}
				else
					throw new IllegalArgumentException("Non exist widget UID " + wuid);
			}
		});
		AbstractWidgetProperty tgtWUIDProp = new StringProperty(PROP_TGT_WUID,
				"Target WUID", WidgetPropertyCategory.Display, "");
		addProperty(tgtWUIDProp);
		tgtWUIDProp.addPropertyChangeListener(new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(displayModel == null)
					return;
				String wuid = evt.getNewValue().toString();
				AbstractWidgetModel w = displayModel.getWidgetFromWUID(wuid);
				if(w != null){					
					target = w;
					reconnect();
				}
				else
					throw new IllegalArgumentException("Non exist widget UID " + wuid);
			}
		});		

		setPropertyVisibleAndSavable(PROP_SRC_WUID, false, true);
		setPropertyVisibleAndSavable(PROP_TGT_WUID, false, true);
		setPropertyVisibleAndSavable(PROP_SRC_TERM, false, true);
		setPropertyVisibleAndSavable(PROP_TGT_TERM, false, true);

		removeProperty(PROP_BORDER_COLOR);
		removeProperty(PROP_BORDER_STYLE);
		removeProperty(PROP_BORDER_WIDTH);
		removeProperty(PROP_VISIBLE);
		removeProperty(PROP_ENABLED);
		removeProperty(PROP_TOOLTIP);
		removeProperty(PROP_ACTIONS);
		removeProperty(PROP_FONT);
		removeProperty(PROP_XPOS);
		removeProperty(PROP_YPOS);
		removeProperty(PROP_WIDTH);
		removeProperty(PROP_HEIGHT);
		removeProperty(PROP_RULES);
		removeProperty(PROP_ACTIONS);
		removeProperty(PROP_SCRIPTS);
		removeProperty(PROP_COLOR_BACKGROUND);
		removeProperty(PROP_COLOR_FOREGROUND);
		removeProperty(PROP_SCALE_OPTIONS);

	}

	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * Returns the source endpoint of this connection.
	 * 
	 * @return a non-null Shape instance
	 */
	public AbstractWidgetModel getSource() {
		return source;
	}

	/**
	 * Returns the target endpoint of this connection.
	 * 
	 * @return a non-null Shape instance
	 */
	public AbstractWidgetModel getTarget() {
		return target;
	}

	/**
	 * Reconnect this connection. The connection will reconnect with the shapes
	 * it was previously attached to.
	 */
	public void reconnect() {
		if (!isConnected && source != null && target != null) {
			source.addConnection(this);
			target.addConnection(this);
			isConnected = true;
		}
	}

	/**
	 * Reconnect to a different source and/or target shape. The connection will
	 * disconnect from its current attachments and reconnect to the new source
	 * and target.
	 * 
	 * @param newSource
	 *            a new source endpoint for this connection (non null)
	 * @param newTarget
	 *            a new target endpoint for this connection (non null)
	 * @throws IllegalArgumentException
	 *             if any of the paramers are null or newSource == newTarget
	 */
	public void connect(AbstractWidgetModel newSource,
			String newSourceTerminal, AbstractWidgetModel newTarget,
			String newTargetTerminal) {
		if (newSource == null || newTarget == null || newSource == newTarget
				|| newSourceTerminal == null || newTargetTerminal == null) {
			throw new IllegalArgumentException();
		}
		disconnect();
		setSource(newSource);
		setTarget(newTarget);
		setSourceTerminal(newSourceTerminal);
		setTargetTerminal(newTargetTerminal);
		reconnect();
	}

	/**
	 * Disconnect this connection from the shapes it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			source.removeConnection(this);
			target.removeConnection(this);
			isConnected = false;
		}
	}

	public String getTargetTerminal() {
		return (String) getPropertyValue(PROP_TGT_TERM);
	}

	public String getSourceTerminal() {
		return (String) getPropertyValue(PROP_SRC_TERM);
	}

	public void setSource(AbstractWidgetModel source) {
		this.source = source;
		setPropertyValue(PROP_SRC_WUID, source.getWUID(), false);
	}

	public void setTarget(AbstractWidgetModel target) {
		this.target = target;
		setPropertyValue(PROP_TGT_WUID, target.getWUID(), false);
	}

	public void setTargetTerminal(String terminal) {
		setPropertyValue(PROP_TGT_TERM, terminal);
	}

	public void setSourceTerminal(String terminal) {
		setPropertyValue(PROP_SRC_TERM, terminal);
	}

	public int getLineWidth() {
		return (Integer) getPropertyValue(PROP_LINE_WIDTH);
	}

	/**
	 * @return SWT line style
	 */
	public int getLineStyle() {
		int i = (Integer) getPropertyValue(PROP_LINE_STYLE);
		return LineStyle.values()[i].getStyle();
	}

	public OPIColor getLineColor() {
		return (OPIColor) getPropertyValue(PROP_LINE_COLOR);
	}

	public RouterType getRouterType() {
		int i = (Integer) getPropertyValue(PROP_ROUTER);
		return RouterType.values()[i];
	}

	public ArrowType getArrowType() {
		return ArrowType.values()[(Integer) getPropertyValue(PROP_ARROW_TYPE)];
	}

	public int getArrowLength() {
		return (Integer) getCastedPropertyValue(PROP_ARROW_LENGTH);
	}

	public boolean isFillArrow() {
		return (Boolean) getPropertyValue(PROP_FILL_ARROW);
	}

	/**
	 * @return true if the graphics's antiAlias is on.
	 */
	public final boolean isAntiAlias() {
		return (Boolean) getPropertyValue(PROP_ANTIALIAS);
	}
	
	@Override
	public AbstractContainerModel getParent() {
		return displayModel;
	}
	
	/**
	 * @return the points on this connection except start and end anchors, can be null.
	 */
	public PointList getPoints(){
		return (PointList)getPropertyValue(PROP_POINTS);
	}

	public void setPoints(PointList points) {
		setPropertyValue(PROP_POINTS, points);
		
	}
	
	/**
	 * @return the original points before scaling.
	 */
	public PointList getOriginPoints() {
		if(originPoints == null)
			originPoints = getPoints();
		return originPoints;
	}

}
