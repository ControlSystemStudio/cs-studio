package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.ui.resources.alarms.AlarmIcons;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 *
 * <code>SeverityIconProvider</code> provides the icons for the alarm table.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SeverityIconProvider {

    private final Image[][][] icons;
    private int dis = 0;

    /**
     * Constructs a new icon provider.
     *
     * @param parent the parent that owns the icons and is used to dispose of them where not needed anymore
     */
    public SeverityIconProvider(Composite parent) {
        icons = createIcons(parent);
    }

    /**
     * Toggle between colour and grey icons.
     */
    public void toggle() {
        dis ^= 1;
    }

    /**
     * Resets the provider to the colored state.
     */
    public void reset() {
        dis = 0;
    }

    /**
     * Returns the icon image that matches the severities of the given pv. The icons that are used are provided by the
     * {@link AlarmIcons}.
     *
     * @param pv the pv for which the icon is requested
     * @return icon that matches the severities of the PV
     */
    public Image getIcon(AlarmTreePV pv) {
        if (pv == null)
            return null;
        SeverityLevel s = pv.getSeverity();
        int c = pv.getCurrentSeverity().ordinal();
        if (s.isActive()) {
            return icons[s.ordinal()][c][dis];
        } else {
            return icons[s.ordinal()][c][0];
        }
    }

    private Image[][][] createIcons(Composite parent) {
        SeverityLevel[] levels = SeverityLevel.values();
        Image[][][] icons = new Image[levels.length][levels.length][2];
        Display display = parent.getDisplay();
        for (int i = 0; i < levels.length; i++) {
            for (int j = 0; j < levels.length; j++) {
                ImageDescriptor desc = getImageDescriptor(levels[i], levels[j], false);
                if (desc != null) {
                    icons[i][j][0] = desc.createImage(display);
                }
                desc = getImageDescriptor(levels[i], levels[j], true);
                if (desc != null) {
                    icons[i][j][1] = desc.createImage(display);
                }
            }
        }
        parent.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                for (int i = 0; i < icons.length; i++) {
                    for (int j = 0; j < icons.length; j++) {
                        if (icons[i][j][0] != null) {
                            icons[i][j][0].dispose();
                        }
                        if (icons[i][j][1] != null) {
                            icons[i][j][1].dispose();
                        }
                    }
                }
            }
        });
        return icons;
    }

    /**
     * Returns an image descriptor representing the severity/state of the given alarm.
     *
     * @param severity the latched severity
     * @param currentSeverity active severity
     * @param disabled true to fetch the disabled icon or false otherwise
     * @return the icon representing the alarm severity
     */
    private static ImageDescriptor getImageDescriptor(SeverityLevel severity, SeverityLevel currentSeverity,
            boolean disabled) {
        AlarmIcons icons = AlarmIcons.getInstance();
        switch (severity) {
            case UNDEFINED_ACK:
            case INVALID_ACK:
                return icons.getInvalidAcknowledged(disabled);
            case UNDEFINED:
            case INVALID:
                return currentSeverity == SeverityLevel.OK ? icons.getInvalidClearedNotAcknowledged(disabled) : icons
                        .getInvalidNotAcknowledged(disabled);
            case MAJOR:
                return currentSeverity == SeverityLevel.OK ? icons.getMajorClearedNotAcknowledged(disabled) : icons
                        .getMajorNotAcknowledged(disabled);
            case MAJOR_ACK:
                return icons.getMajorAcknowledged(disabled);
            case MINOR:
                return currentSeverity == SeverityLevel.OK ? icons.getMinorClearedNotAcknowledged(disabled) : icons
                        .getMinorNotAcknowledged(disabled);
            case MINOR_ACK:
                return icons.getMinorAcknowledged(disabled);
            case OK:
            default:
                return null;
        }
    }

}
