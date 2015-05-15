package org.csstudio.ui.resources.alarms;

import org.csstudio.ui.resources.Activator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

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

    private static final String ICONS = "icons/";

    private static final AlarmIcons INSTANCE = new AlarmIcons();
    private final ImageRegistry registry;

    public static AlarmIcons getInstance() {
        return INSTANCE;
    }

    private AlarmIcons() {
        registry = new ImageRegistry(Display.getDefault());
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
    }

    /**
     * @return the icon representing invalid or disconnected alarm, which has not been acknowledged
     */
    public Image getInvalidNotAcknowledged() {
        return registry.get(INVALID_NOTACKNOWLEDGED);
    }

    /**
     * @return the icon representing invalid or disconnected alarm, which has been cleared but not acknowledged
     */
    public Image getInvalidClearedNotAcknowledged() {
        return registry.get(INVALID_CLEARED_NOTACKNOWLEDGED);
    }

    /**
     * @return the icon representing invalid or disconnected alarm, which has been acknowledged
     */
    public Image getInvalidAcknowledged() {
        return registry.get(INVALID_ACKNOWLEDGED);
    }

    /**
     * @return icon representing a major acknowledged alarm
     */
    public Image getMajorAcknowledged() {
        return registry.get(MAJOR_ACKNOWLEDGED);
    }

    /**
     * @return icon representing a minor acknowledged alarm
     */
    public Image getMinorAcknowledged() {
        return registry.get(MINOR_ACKNOWLEDGED);
    }

    /**
     * @return icon representing a major not acknowledged alarm
     */
    public Image getMajorNotAcknowledged() {
        return registry.get(MAJOR_NOTACKNOWLEDGED);
    }

    /**
     * @return icon representing a minor not acknowledged alarm
     */
    public Image getMinorNotAcknowledged() {
        return registry.get(MINOR_NOTACKNOWLEDGED);
    }

    /**
     * @return icon representing a major cleared and not yet acknowledged alarm
     */
    public Image getMajorClearedNotAcknowledged() {
        return registry.get(MAJOR_CLEARED_NOTACKNOWLEDGED);
    }

    /**
     * @return icon representing a minor cleared and not yet acknowledged alarm
     */
    public Image getMinorClearedNotAcknowledged() {
        return registry.get(MINOR_CLEARED_NOTACKNOWLEDGED);
    }
}
