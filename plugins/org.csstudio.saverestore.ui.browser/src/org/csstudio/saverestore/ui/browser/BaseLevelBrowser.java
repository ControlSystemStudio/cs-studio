package org.csstudio.saverestore.ui.browser;

import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.Node;

/**
 *
 * <code>BaseLevelBrowser</code> provides the UI for the browsing mechanism of base levels. This can be a tree, table,
 * list, or any other component, as long is provides means to retrieve the selected base level and a few other features
 * described below.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T>
 */
public interface BaseLevelBrowser<T extends BaseLevel> {

    /** The name of the extension point */
    public static final String EXT_POINT = "org.csstudio.saverestore.ui.browser.baselevelbrowser";

    /**
     * Returns the title to be used for the container of this browser based on the provided arguments. The title may
     * contain the two given parameters or it may also ignore them. Null return values are not accepted, but empty
     * strings are.
     *
     * @param baseLevel the selected base level
     * @param branch the selected branch
     * @return a title composed of parameters, but not necessary
     */
    String getTitleFor(Optional<T> baseLevel, Optional<String> branch);

    /**
     * Returns the JavaFX node which all UI nodes of this browser. This method may be called more than once and should
     * always return the same value.
     *
     * @return the UI part of the browser
     */
    Node getFXContent();

    /**
     * Toggles whether only those base levels for which the save sets already exist or all base levels should be
     * available.
     *
     * @param onlyAvailable true if only existing are available or false if all are available in the table
     */
    void setShowOnlyAvailable(boolean onlyAvailable);

    /**
     * Returns the property that contains the list of all available base levels. This type of base levels is not defined
     * for this property in order to allow setting any type on the browser. The browser should take care of transforming
     * the type to the type that it understands and can work with.
     *
     * @return the property that contains the list of all available base levels
     */
    ObjectProperty<List<T>> availableBaseLevelsProperty();

    /**
     * Transforms objects of any type of BaseLevels to the type that is understood by this browser.
     *
     * @param list of objects to transform
     * @return the transformed list
     */
    List<T> transform(List<? extends BaseLevel> list);

    /**
     * Returns the property that provides the selected base level.
     *
     * @return the property that provides the selected base level
     */
    Property<T> selectedBaseLevelProperty();

    /**
     * Returns the human readable name for this browser used for presentation only.
     *
     * @return the readable name
     */
    String getReadableName();
}
