/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.ui.resources.alarms;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.ui.resources.Activator;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 *
 * <code>AlarmIcons</code> provides access to icons that can be used by the alarm system in order to more clearly
 * present the state of an individual alarm.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class AlarmIcons {

    private static final String INVALID_ACKNOWLEDGED = "invalid_ack.png";
    private static final String INVALID_NOTACKNOWLEDGED = "invalid_notack.png";
    private static final String INVALID_CLEARED_NOTACKNOWLEDGED = "invalid_cleared_notack.png";
    private static final String MAJOR_ACKNOWLEDGED = "major_ack.png";
    private static final String MAJOR_NOTACKNOWLEDGED = "major_notack.png";
    private static final String MAJOR_CLEARED_NOTACKNOWLEDGED = "major_cleared_notack.png";
    private static final String MINOR_ACKNOWLEDGED = "minor_ack.png";
    private static final String MINOR_NOTACKNOWLEDGED = "minor_notack.png";
    private static final String MINOR_CLEARED_NOTACKNOWLEDGED = "minor_cleared_notack.png";

    private static final String INVALID_ACKNOWLEDGED_DISABLED = "invalid_ack_disabled.png";
    private static final String INVALID_NOTACKNOWLEDGED_DISABLED = "invalid_notack_disabled.png";
    private static final String INVALID_CLEARED_NOTACKNOWLEDGED_DISABLED = "invalid_cleared_notack_disabled.png";
    private static final String MAJOR_ACKNOWLEDGED_DISABLED = "major_ack_disabled.png";
    private static final String MAJOR_NOTACKNOWLEDGED_DISABLED = "major_notack_disabled.png";
    private static final String MAJOR_CLEARED_NOTACKNOWLEDGED_DISABLED = "major_cleared_notack_disabled.png";
    private static final String MINOR_ACKNOWLEDGED_DISABLED = "minor_ack_disabled.png";
    private static final String MINOR_NOTACKNOWLEDGED_DISABLED = "minor_notack_disabled.png";
    private static final String MINOR_CLEARED_NOTACKNOWLEDGED_DISABLED = "minor_cleared_notack_disabled.png";

    private static final String ICONS = "icons/";

    private static final AlarmIcons INSTANCE = new AlarmIcons();
    private final Map<String, ImageDescriptor> registry;

    /**
     * @return the singleton instance of this class
     */
    public static AlarmIcons getInstance() {
        return INSTANCE;
    }

    private AlarmIcons() {
        registry = new HashMap<String, ImageDescriptor>();
        registry.put(INVALID_NOTACKNOWLEDGED, Activator.getImageDescriptor(ICONS + INVALID_NOTACKNOWLEDGED));
        registry.put(INVALID_ACKNOWLEDGED, Activator.getImageDescriptor(ICONS + INVALID_ACKNOWLEDGED));
        registry.put(INVALID_CLEARED_NOTACKNOWLEDGED,
                Activator.getImageDescriptor(ICONS + INVALID_CLEARED_NOTACKNOWLEDGED));
        registry.put(MAJOR_ACKNOWLEDGED, Activator.getImageDescriptor(ICONS + MAJOR_ACKNOWLEDGED));
        registry.put(MAJOR_NOTACKNOWLEDGED, Activator.getImageDescriptor(ICONS + MAJOR_NOTACKNOWLEDGED));
        registry.put(MAJOR_CLEARED_NOTACKNOWLEDGED, Activator.getImageDescriptor(ICONS + MAJOR_CLEARED_NOTACKNOWLEDGED));
        registry.put(MINOR_ACKNOWLEDGED, Activator.getImageDescriptor(ICONS + MINOR_ACKNOWLEDGED));
        registry.put(MINOR_NOTACKNOWLEDGED, Activator.getImageDescriptor(ICONS + MINOR_NOTACKNOWLEDGED));
        registry.put(MINOR_CLEARED_NOTACKNOWLEDGED, Activator.getImageDescriptor(ICONS + MINOR_CLEARED_NOTACKNOWLEDGED));

        registry.put(INVALID_NOTACKNOWLEDGED_DISABLED,
                Activator.getImageDescriptor(ICONS + INVALID_NOTACKNOWLEDGED_DISABLED));
        registry.put(INVALID_ACKNOWLEDGED_DISABLED, Activator.getImageDescriptor(ICONS + INVALID_ACKNOWLEDGED_DISABLED));
        registry.put(INVALID_CLEARED_NOTACKNOWLEDGED_DISABLED,
                Activator.getImageDescriptor(ICONS + INVALID_CLEARED_NOTACKNOWLEDGED_DISABLED));
        registry.put(MAJOR_ACKNOWLEDGED_DISABLED, Activator.getImageDescriptor(ICONS + MAJOR_ACKNOWLEDGED_DISABLED));
        registry.put(MAJOR_NOTACKNOWLEDGED_DISABLED,
                Activator.getImageDescriptor(ICONS + MAJOR_NOTACKNOWLEDGED_DISABLED));
        registry.put(MAJOR_CLEARED_NOTACKNOWLEDGED_DISABLED,
                Activator.getImageDescriptor(ICONS + MAJOR_CLEARED_NOTACKNOWLEDGED_DISABLED));
        registry.put(MINOR_ACKNOWLEDGED_DISABLED, Activator.getImageDescriptor(ICONS + MINOR_ACKNOWLEDGED_DISABLED));
        registry.put(MINOR_NOTACKNOWLEDGED_DISABLED,
                Activator.getImageDescriptor(ICONS + MINOR_NOTACKNOWLEDGED_DISABLED));
        registry.put(MINOR_CLEARED_NOTACKNOWLEDGED_DISABLED,
                Activator.getImageDescriptor(ICONS + MINOR_CLEARED_NOTACKNOWLEDGED_DISABLED));
    }

    /**
     * @return icon descriptor representing invalid or disconnected alarm, which has not been acknowledged
     */
    public ImageDescriptor getInvalidNotAcknowledged(boolean disabled) {
        return disabled ? registry.get(INVALID_NOTACKNOWLEDGED_DISABLED) : registry.get(INVALID_NOTACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing invalid or disconnected alarm, which has been cleared but not acknowledged
     */
    public ImageDescriptor getInvalidClearedNotAcknowledged(boolean disabled) {
        return disabled ? registry.get(INVALID_CLEARED_NOTACKNOWLEDGED_DISABLED) : registry
                .get(INVALID_CLEARED_NOTACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing invalid or disconnected alarm, which has been acknowledged
     */
    public ImageDescriptor getInvalidAcknowledged(boolean disabled) {
        return disabled ? registry.get(INVALID_ACKNOWLEDGED_DISABLED) : registry.get(INVALID_ACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing a major acknowledged alarm
     */
    public ImageDescriptor getMajorAcknowledged(boolean disabled) {
        return disabled ? registry.get(MAJOR_ACKNOWLEDGED_DISABLED) : registry.get(MAJOR_ACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing a minor acknowledged alarm
     */
    public ImageDescriptor getMinorAcknowledged(boolean disabled) {
        return disabled ? registry.get(MINOR_ACKNOWLEDGED_DISABLED) : registry.get(MINOR_ACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing a major not acknowledged alarm
     */
    public ImageDescriptor getMajorNotAcknowledged(boolean disabled) {
        return disabled ? registry.get(MAJOR_NOTACKNOWLEDGED_DISABLED) : registry.get(MAJOR_NOTACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing a minor not acknowledged alarm
     */
    public ImageDescriptor getMinorNotAcknowledged(boolean disabled) {
        return disabled ? registry.get(MINOR_NOTACKNOWLEDGED_DISABLED) : registry.get(MINOR_NOTACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing a major cleared and not yet acknowledged alarm
     */
    public ImageDescriptor getMajorClearedNotAcknowledged(boolean disabled) {
        return disabled ? registry.get(MAJOR_CLEARED_NOTACKNOWLEDGED_DISABLED) : registry
                .get(MAJOR_CLEARED_NOTACKNOWLEDGED);
    }

    /**
     * @param disabled true if the disabled icon should be returned or false if enabled instance is requested
     * @return icon descriptor representing a minor cleared and not yet acknowledged alarm
     */
    public ImageDescriptor getMinorClearedNotAcknowledged(boolean disabled) {
        return disabled ? registry.get(MINOR_CLEARED_NOTACKNOWLEDGED_DISABLED) : registry
                .get(MINOR_CLEARED_NOTACKNOWLEDGED);
    }
}
