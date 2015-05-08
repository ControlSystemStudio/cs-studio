/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * This class defines an bargraph widget model.
 *
 * @author Kai Meyer
 *
 */
public class BargraphModel extends AbstractWidgetModel {

    /**
     * The ID of the fill grade property.
     */
    public static final String PROP_FILL = "fill"; //$NON-NLS-1$

    /**
     * The ID of the orientation property.
     */
    public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$

    /**
     * The ID of the default-fill-Color property.
     */
    public static final String PROP_DEFAULT_FILL_COLOR = "defaultFillColor";

    /**
     * The ID of the fillbackground-Color property.
     */
    public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor";

    /**
     * The ID of the show_value property.
     */
    public static final String PROP_SHOW_VALUES = "showValues";

    /**
     * The ID of the minimum property.
     */
    public static final String PROP_MIN = "minimum";
    /**
     * The ID of the lolo level property.
     */
    public static final String PROP_LOLO_LEVEL = "loloLevel";
    /**
     * The ID of the lo level property.
     */
    public static final String PROP_LO_LEVEL = "loLevel";
    /**
     * The ID of the hi level property.
     */
    public static final String PROP_HI_LEVEL = "hiLevel";
    /**
     * The ID of the hihi level property.
     */
    public static final String PROP_HIHI_LEVEL = "hihiLevel";
    /**
     * The ID of the maximum property.
     */
    public static final String PROP_MAX = "maximum";
    /**
     * The ID of the show status of the marks.
     */
    public static final String PROP_SHOW_MARKS = "marksShowStatus";
    /**
     * The ID of the show status of the marks.
     */
    public static final String PROP_SHOW_SCALE = "scaleShowStatus";
    /**
     * The ID of the show status of the marks.
     */
    public static final String PROP_SCALE_SECTION_COUNT = "sectionCount";
    /**
     * The ID of the <i>transparent</i> property.
     */
    public static final String PROP_TRANSPARENT = "transparency";
    /**
     * The ID of the <i>transparent</i> property.
     */
    public static final String PROP_SHOW_ONLY_VALUE = "value_representation";

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Bargraph"; //$NON-NLS-1$

    /**
     * The default value of the fill grade property.
     */
    private static final double DEFAULT_FILL = 0.25;

    /**
     * The default value of the orientation property.
     */
    private static final boolean DEFAULT_ORIENTATION_HORIZONTAL = true;

    /**
     * The default value of the default fill color property.
     */
    private static final String DEFAULT_FILL_COLOR = "#0000ff";

    /**
     * The default value of the fillbackground color property.
     */
    private static final String DEFAULT_FILLBACKGROUND_COLOR = "#787878";

    /**
     * The default value of the show_value property.
     */
    private static final boolean DEFAULT_SHOW_VALUES = false;

    /**
     * The default value for the show status of the marks.
     */
    private static final int DEFAULT_SHOW_MARKS = 1;

    /**
     * The default value for the show status of the scale.
     */
    private static final int DEFAULT_SHOW_SCALE = 1;

    /**
     * The default value for the section count.
     */
    private static final int DEFAULT_SECTION_COUNT = 10;

    /**
     * The labels for the MARKS_SHOW_STATUS- property.
     */
    public static final String[] SHOW_LABELS = new String[] { "None", "Bottom / Right", "Top / Left" };

    /**
     * The default value of the levels property.
     */
    private static final double[] DEFAULT_LEVELS = new double[] { 0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.0 };

    /**
     * Constructor.
     */
    public BargraphModel() {
        setSize(100, 60);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureProperties() {

        addDoubleProperty(PROP_FILL, "Value", WidgetPropertyCategory.DISPLAY, DEFAULT_FILL, true, PROP_COLOR_FOREGROUND);
        addColorProperty(PROP_DEFAULT_FILL_COLOR, "Fill Color", WidgetPropertyCategory.DISPLAY, DEFAULT_FILL_COLOR, false, PROP_FILL);
        addColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground", WidgetPropertyCategory.DISPLAY, DEFAULT_FILLBACKGROUND_COLOR, false, PROP_TRANSPARENT);
        addBooleanProperty(PROP_ORIENTATION, "Horizontal Orientation", WidgetPropertyCategory.DISPLAY, DEFAULT_ORIENTATION_HORIZONTAL, false, PROP_FILLBACKGROUND_COLOR);

        addBooleanProperty(PROP_TRANSPARENT, "Transparent Background", WidgetPropertyCategory.FORMAT, true, true, PROP_COLOR_BACKGROUND);

        addDoubleProperty(PROP_MAX, "Maximum", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[6], false);
        addDoubleProperty(PROP_MIN, "Minimum", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[0], false);
        addArrayOptionProperty(PROP_SHOW_SCALE, "Scale", WidgetPropertyCategory.SCALE, SHOW_LABELS, DEFAULT_SHOW_SCALE, false);
        addIntegerProperty(PROP_SCALE_SECTION_COUNT, "Scale Sections", WidgetPropertyCategory.SCALE, DEFAULT_SECTION_COUNT, 1, Integer.MAX_VALUE, false, PROP_ORIENTATION);
        addArrayOptionProperty(PROP_SHOW_MARKS, "Tickmarks", WidgetPropertyCategory.SCALE, SHOW_LABELS, DEFAULT_SHOW_MARKS, false);
        addDoubleProperty(PROP_HIHI_LEVEL, "Level HIHI", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[5], false);
        addDoubleProperty(PROP_HI_LEVEL, "Level HI", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[4], false);
        addDoubleProperty(PROP_LO_LEVEL, "Level LO", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[2], false);
        addDoubleProperty(PROP_LOLO_LEVEL, "Level LOLO", WidgetPropertyCategory.SCALE, DEFAULT_LEVELS[1], false);
        addBooleanProperty(PROP_SHOW_ONLY_VALUE, "Show only value", WidgetPropertyCategory.SCALE, false, false);
        addBooleanProperty(PROP_SHOW_VALUES, "Scale Caption", WidgetPropertyCategory.SCALE, DEFAULT_SHOW_VALUES, false);



    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultToolTip() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
        buffer.append("Maximum:\t");
        buffer.append(createTooltipParameter(PROP_MAX) + "\n");
        buffer.append("Minimum:\t");
        buffer.append(createTooltipParameter(PROP_MIN) + "\n");
        buffer.append("Value:\t");
        buffer.append(createTooltipParameter(PROP_FILL) + "\n");
        buffer.append("Level HIHI:\t");
        buffer.append(createTooltipParameter(PROP_HIHI_LEVEL) + "\n");
        buffer.append("Level HI:\t");
        buffer.append(createTooltipParameter(PROP_HI_LEVEL) + "\n");
        buffer.append("Level LO:\t");
        buffer.append(createTooltipParameter(PROP_LO_LEVEL) + "\n");
        buffer.append("Level LOLO:\t");
        buffer.append(createTooltipParameter(PROP_LOLO_LEVEL));
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * Gets the fill level.
     *
     * @return double The fill level
     */
    public double getFillLevel() {
        return getDoubleProperty(PROP_FILL);
    }

    /**
     * Gets the orientation.
     *
     * @return the orientation
     */
    public boolean getOrientation() {
        return getBooleanProperty(PROP_ORIENTATION);
    }

    /**
     * Gets the minimum value for this model.
     *
     * @return double The minimum value
     */
    public double getMinimum() {
        return getDoubleProperty(PROP_MIN);
    }

    /**
     * Gets the lolo level for this model.
     *
     * @return double The lolo level
     */
    public double getLoloLevel() {
        return getDoubleProperty(PROP_LOLO_LEVEL);
    }

    /**
     * Gets the lo level for this model.
     *
     * @return double The lo level
     */
    public double getLoLevel() {
        return getDoubleProperty(PROP_LO_LEVEL);
    }

    /**
     * Gets the hi level for this model.
     *
     * @return double The hi level
     */
    public double getHiLevel() {
        return getDoubleProperty(PROP_HI_LEVEL);
    }

    /**
     * Gets the minimum value for this model.
     *
     * @return double The minimum value
     */
    public double getHihiLevel() {
        return getDoubleProperty(PROP_HIHI_LEVEL);
    }

    /**
     * Gets the maximum value for this model.
     *
     * @return double The maximum value
     */
    public double getMaximum() {
        return getDoubleProperty(PROP_MAX);
    }

    /**
     * Gets, if the values should be shown or not.
     *
     * @return boolean true, if the values should be shown, false otherwise
     */
    public boolean isShowValues() {
        return getBooleanProperty(PROP_SHOW_VALUES);
    }

    /**
     * Gets, if the marks should be shown or not.
     *
     * @return int 0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
     */
    public int getShowMarks() {
        return getArrayOptionProperty(PROP_SHOW_MARKS);
    }

    /**
     * Gets, if the scale should be shown or not.
     *
     * @return int 0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
     */
    public int getShowScale() {
        return getArrayOptionProperty(PROP_SHOW_SCALE);
    }

    /**
     * Gets the count of section in the scale.
     *
     * @return int The count of sections in the scale
     */
    public int getScaleSectionCount() {
        return getIntegerProperty(PROP_SCALE_SECTION_COUNT);
    }

    /**
     * Return if only the current value should be showed.
     *
     * @return True if only the value should be shown, false otherwise
     */
    public boolean getShowOnlyValue() {
        return getBooleanProperty(PROP_SHOW_ONLY_VALUE);
    }

    /**
     * Returns, if this widget should have a transparent background.
     *
     * @return boolean True, if it should have a transparent background, false
     *         otherwise
     */
    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

}
