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
import java.util.Arrays;
import java.util.List;

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
import org.eclipse.draw2d.ScrollPane;
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

    public enum LineJumpAdd {
        NONE("none"),
        HORIZONTAL_LINES("horizontal lines"),
        VERTICAL_LINES("vertical lines");

        String description;

        LineJumpAdd(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public static String[] stringValues() {
            String[] sv = new String[values().length];
            int i = 0;
            for (LineJumpAdd p : values())
                sv[i++] = p.toString();
            return sv;
        }
    }

    public enum LineJumpStyle {
        ARC("arc"),
        GAP("gap"),
        SQUARE("square"),
        SLIDES2("2 slides");

        String description;

        LineJumpStyle(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public static String[] stringValues() {
            String[] sv = new String[values().length];
            int i = 0;
            for (LineJumpStyle p : values())
                sv[i++] = p.toString();
            return sv;
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
     * The source widget Path
     */
    public static final String PROP_SRC_PATH = "src_path";

    /**
     * The target widget UID
     */
    public static final String PROP_TGT_WUID = "tgt_wuid"; //$NON-NLS-1$

    /**
     * The target widget Path
     */
    public static final String PROP_TGT_PATH = "tgt_path";

    public static final String PATH_DELIMITER = "_";

    /**
     * All points of this connection except start and end anchor.
     * null if it should be routed by router.
     */
    public static final String PROP_POINTS = "points"; //$NON-NLS-1$

    public static final String PROP_LINE_JUMP_ADD = "line_jump_add"; //$NON-NLS-1$

    public static final String PROP_LINE_JUMP_STYLE = "line_jump_style"; //$NON-NLS-1$

    public static final String PROP_LINE_JUMP_SIZE = "line_jump_size"; //$NON-NLS-1$

    /** True, if the connection is attached to its endpoints. */
    private boolean isConnected;

    /** True, if the connection was loaded from linked opi in LinkingContainer. */
    private boolean loadedFromLinkedOpi;

    /** Connection's source endpoint. */
    private AbstractWidgetModel source;
    /** Connection's target endpoint. */
    private AbstractWidgetModel target;

    private DisplayModel displayModel;

    private PointList originPoints;

    private ScrollPane scrollPane;

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
        addProperty(new ComboProperty(PROP_LINE_JUMP_ADD, "Add Line Jump To",
                WidgetPropertyCategory.Display, LineJumpAdd.stringValues(), 0));
        addProperty(new ComboProperty(PROP_LINE_JUMP_STYLE, "Line Jump Style",
                WidgetPropertyCategory.Display, LineJumpStyle.stringValues(), 0));
        addProperty(new IntegerProperty(PROP_LINE_JUMP_SIZE, "Line Jump Size",
                WidgetPropertyCategory.Display, 10, 1, 100));
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
                String path = getPropertyValue(PROP_SRC_PATH).toString();

                AbstractWidgetModel w = null;
                if(path == null || path.equals("")){
                    w = getTerminal(displayModel, null, wuid);
                }else {
                    List<String> paths = Arrays.asList(path.split(PATH_DELIMITER));
                    w = getTerminal(displayModel, paths, wuid);
                }
                if(w != null){
                    source = w;
                    reconnect();
                } else
                    throw new IllegalArgumentException("Non exist widget PATH:[" + path + "],\nWUID:[" + wuid + "]");
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
                String path = getPropertyValue(PROP_TGT_PATH).toString();
                AbstractWidgetModel w = null;
                if(path == null || path.equals("")){
                    w = getTerminal(displayModel, null, wuid);
                }else {
                    List<String> paths = Arrays.asList(path.split(PATH_DELIMITER));
                    w = getTerminal(displayModel, paths, wuid);
                }
                if(w != null){
                    target = w;
                    reconnect();
                } else
                    throw new IllegalArgumentException("Non exist widget PATH:[" + path + "],\nWUID:[" + wuid + "]");
            }
        });

        AbstractWidgetProperty srcPathProp = new StringProperty(PROP_SRC_PATH,
                "Source Path", WidgetPropertyCategory.Display, "");
        addProperty(srcPathProp);

        AbstractWidgetProperty tgtPathProp = new StringProperty(PROP_TGT_PATH,
                "Target Path", WidgetPropertyCategory.Display, "");
        addProperty(tgtPathProp);

        setPropertyVisibleAndSavable(PROP_SRC_WUID, false, true);
        setPropertyVisibleAndSavable(PROP_TGT_WUID, false, true);
        setPropertyVisibleAndSavable(PROP_SRC_PATH, false, true);
        setPropertyVisibleAndSavable(PROP_TGT_PATH, false, true);
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

    private AbstractWidgetModel getTerminal(AbstractContainerModel root, List<String> paths, String wuid) {
        if(root == null) return null;

        if(paths == null || paths.isEmpty()) {
            return getTerminal(root,wuid);
        }

        AbstractContainerModel widget = root;
        String tempId = paths.get(0);
        for(AbstractWidgetModel w : widget.getChildren()) {
            if(w instanceof AbstractContainerModel && w.getWUID().equals(tempId)) {
                AbstractWidgetModel tempResult =
                        getTerminal((AbstractContainerModel)w, paths.subList(1, paths.size()), wuid);
                if(tempResult != null) return tempResult;
            }
        }

        return null;
    }

    private AbstractWidgetModel getTerminal(AbstractContainerModel model, String wuid) {
        for(AbstractWidgetModel w : model.getChildren()) {
            if(w.getWUID().equals(wuid)) {
                return w;
            }
            if (w instanceof AbstractContainerModel) {
                AbstractWidgetModel m = getTerminal((AbstractContainerModel)w, wuid);
                if (m != null) return m;
            }
        }
        return null;
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
     * Resync this connection with its source and target.
     */
    public void resync() {
        if (source == null || target == null) {
            return;
        }
        boolean connected = isConnected;
        disconnect();
        String wuid = target.getWUID();
        String path = getPropertyValue(PROP_TGT_PATH).toString();
        AbstractWidgetModel w = null;
        if(path == null || path.equals("")){
            w = getTerminal(displayModel, null, wuid);
        }else {
            List<String> paths = Arrays.asList(path.split(PATH_DELIMITER));
            w = getTerminal(displayModel, paths, wuid);
        }
        setTarget(w);

        wuid = source.getWUID();
        path = getPropertyValue(PROP_SRC_PATH).toString();
        w = null;
        if(path == null || path.equals("")){
            w = getTerminal(displayModel, null, wuid);
        }else {
            List<String> paths = Arrays.asList(path.split(PATH_DELIMITER));
            w = getTerminal(displayModel, paths, wuid);
        }
        setSource(w);
        if (connected) {
            reconnect();
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
        setPropertyValue(PROP_SRC_PATH, buildTerminalPathFromModel(source), false);
    }

    public void setTarget(AbstractWidgetModel target) {
        this.target = target;
        setPropertyValue(PROP_TGT_WUID, target.getWUID(), false);
        setPropertyValue(PROP_TGT_PATH, buildTerminalPathFromModel(target), false);
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

    public String[] getTargetPath() {
        return ((String)getPropertyValue(PROP_TGT_PATH)).split(PATH_DELIMITER);
    }

    public String[] getSourcePath() {
        return ((String)getPropertyValue(PROP_SRC_PATH)).split(PATH_DELIMITER);
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

    /**
     *
     */
    public void setLoadedFromLinkedOpi(boolean loadedFromLinkedOpi) {
        this.loadedFromLinkedOpi = loadedFromLinkedOpi;
    }

    /**
     * @return true if the connection was loaded from linked opi file in LinkingContainer
     */
    public boolean isLoadedFromLinkedOpi() {
        return this.loadedFromLinkedOpi;
    }

    private String buildTerminalPathFromModel(AbstractWidgetModel model) {
        if(model == null) return "";

        AbstractWidgetModel parent = model.getParent();
        String result = "";
        while(parent != null && parent.getWUID() != null && !(parent instanceof DisplayModel)) {
            result = parent.getWUID() + PATH_DELIMITER + result;
            parent = parent.getParent();
        }

        if(result.endsWith(PATH_DELIMITER))
            return result.substring(0, result.length() - 1);
        else
            return result;
    }

    public LineJumpAdd getLineJumpAdd() {
        int i = (Integer) getPropertyValue(PROP_LINE_JUMP_ADD);
        return LineJumpAdd.values()[i];
    }

    public int getLineJumpSize() {
        return (int)getPropertyValue(PROP_LINE_JUMP_SIZE);
    }

    public LineJumpStyle getLineJumpStyle() {
        int i = (Integer) getPropertyValue(PROP_LINE_JUMP_STYLE);
        return LineJumpStyle.values()[i];
    }

    /**
     * @return the scrollPane
     */
    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * @param scrollPane the scrollPane to set
     */
    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

}
