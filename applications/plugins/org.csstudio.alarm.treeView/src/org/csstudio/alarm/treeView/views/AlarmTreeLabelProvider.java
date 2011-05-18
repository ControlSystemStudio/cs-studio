/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeView.views;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.alarm.treeView.preferences.AlarmTreePreference;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * Provides labels for the alarm tree items.
 */
public class AlarmTreeLabelProvider extends LabelProvider {

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AlarmTreeLabelProvider.class);

    /**
     * Cache for Image objects.
     */
    private final Map<String, Image> _imageCache;

    /**
     * Creates a new alarm tree label provider.
     */
    public AlarmTreeLabelProvider() {
        _imageCache = new HashMap<String, Image>();
    }

    /**
     * Returns the element's name.
     *
     * @param element
     *            the element.
     * @return the element's name, or an empty string if the element doesn't have a name.
     */
    @Override
    @Nonnull
    public final String getText(@Nullable final Object element) {
        if (element instanceof IAlarmTreeNode) {
            return getName((IAlarmTreeNode) element);
        }
        if (element instanceof PendingUpdateAdapter) {
            return ((PendingUpdateAdapter) element).getLabel(element);
        }
        return "";
    }

    @Nonnull
    private String getName(@Nonnull final IAlarmTreeNode node) {
        String result = node.getName();
        if (isDirectChildOfRoot(node) && (node.getSource() == TreeNodeSource.XML)) {
            result = result + " [XML]";
        }
        return result;
    }

    private boolean isDirectChildOfRoot(@Nonnull final IAlarmTreeNode node) {
        IAlarmTreeNode parent = node.getParent();
        return (parent != null) && (parent.getSource() == TreeNodeSource.ROOT);
    }

    /**
     * Returns the character that represents the given alarm severity in the icon's filename.
     *
     * @param alarmSeverity
     *            the severity.
     * @return the character that represents the given severity.
     */
    @Nonnull
    private String getIconName(@Nonnull final EpicsAlarmSeverity alarmSeverity) {
        switch (alarmSeverity) {
            case UNKNOWN:
                return "grey";
            case NO_ALARM:
                return "green";
            case INVALID:
                return "blue";
            case MINOR:
                return "yellow";
            case MAJOR:
                return "red";
            default:
                throw new IllegalStateException("Alarm severity of unhandled type.");
        }
    }

    /**
     * Returns the names of the two icons that should be displayed for the given severities.
     * package-scoped for testing.
     *
     * @param activeAlarmSeverity
     *            the severity of the currently active alarm.
     * @param unacknowledgedAlarmSeverity
     *            the severity of the highest unacknowledged alarm.
     * @return the names of the icons.
     */
    @Nonnull
    String[] getIconNames(@Nonnull final EpicsAlarmSeverity activeAlarmSeverity,
                                  @Nonnull final EpicsAlarmSeverity unacknowledgedAlarmSeverity) {

        final String iconName = getIconName(activeAlarmSeverity);

        if (activeAlarmSeverity == unacknowledgedAlarmSeverity) {
            // If the active and unack severity are the same, only the active
            // alarm is displayed.
            return new String[] {iconName};
        } else if (activeAlarmSeverity != EpicsAlarmSeverity.NO_ALARM &&
                   unacknowledgedAlarmSeverity == EpicsAlarmSeverity.UNKNOWN) {
            // There is an active alarm which is acknowledged.
            return new String[] {iconName, "checked"};
        } else {
            final String iconName2 = getIconName(unacknowledgedAlarmSeverity);
            return new String[] {iconName2, iconName};
        }
    }

    /**
     * Returns the icon for the given element.
     *
     * @param element
     *            the element.
     * @return the icon for the element, or {@code null} if there is no icon for the element.
     */
    @Override
    @CheckForNull
    public final Image getImage(@Nullable final Object element) {
        if (element instanceof IAlarmTreeNode) {
            return getAlarmImageFor((IAlarmTreeNode) element);
        }
        return null;
    }

    /**
     * Returns the image for the given node if that node is in an alarm state.
     *
     * @param node
     *            the node.
     * @return the image.
     */
    @Nonnull
    private Image getAlarmImageFor(@Nonnull final IAlarmTreeNode node) {
        final EpicsAlarmSeverity activeAlarmSeverity = node.getAlarmSeverity();
        final EpicsAlarmSeverity unacknowledgedAlarmSeverity = node.getUnacknowledgedAlarmSeverity();
        final String[] iconNames = getIconNames(activeAlarmSeverity, unacknowledgedAlarmSeverity);
        return createImage(iconNames);
    }

    /**
     * Loads an image. The image is added to a cache kept by this provider and is disposed of when
     * this provider is disposed of.
     *
     * @param name
     *            the image file name.
     * @return the image.
     */
    @CheckForNull
    private Image loadImage(@Nonnull final String name) {
        if (_imageCache.containsKey(name)) {
            return _imageCache.get(name);
        }
        try {
            final Image image = AlarmTreePlugin.getImageDescriptor(name).createImage();
            _imageCache.put(name, image);
            return image;
        } catch (final NullPointerException e) {
            LOG.error("Error while loading image " + name, e);
        }
        return null;
    }

    /**
     * Create an image. The image is added to a cache kept by this provider and is disposed of when
     * this provider is disposed of.
     *
     * @param name
     *            the image name.
     * @param rightImage
     * @param leftImage
     * @return the image.
     */
    @Nonnull
    private Image createImage(@Nonnull final String[] names) {
        final StringBuilder builder = new StringBuilder();
        for (final String string : names) {
            builder.append(string);
        }
        final String name = builder.toString();

        if (_imageCache.containsKey(name)) {
            return _imageCache.get(name);
        }

        Image leftImage;
        Image rightImage;
        int width;
        Image dualImage;

        if (names.length == 2) {
            leftImage = loadImage(AlarmTreePreference.RES_ICON_PATH.getValue() + "/" + names[0] + ".gif");
            rightImage = loadImage(AlarmTreePreference.RES_ICON_PATH.getValue() + "/" + names[1] + ".gif");
            width = leftImage.getBounds().width / 3 + 2 + rightImage.getBounds().width;
            dualImage = new Image(leftImage.getDevice(), width, leftImage.getBounds().height);
            final GC gc = new GC(dualImage);
            if (names[1].equals("checked")) {
                gc.drawImage(leftImage, leftImage.getBounds().width / 3 + 2, 0);
                gc.drawImage(rightImage, 2, 0);
            } else {
                gc.drawImage(leftImage, 0, 0);
                gc.drawImage(rightImage, leftImage.getBounds().width / 3 + 2, 0);
            }
            gc.dispose();
        } else {
            leftImage = loadImage(AlarmTreePreference.RES_ICON_PATH.getValue() + "/" + names[0] + ".gif");
            width = leftImage.getBounds().width / 3 + 2 + leftImage.getBounds().width;
            dualImage = new Image(leftImage.getDevice(), width, leftImage.getBounds().height);
            final GC gc = new GC(dualImage);
            gc.drawImage(leftImage, leftImage.getBounds().width / 3 + 2, 0);
            gc.dispose();

        }
        _imageCache.put(name, dualImage);
        return dualImage;
    }

    /**
     * Disposes of the images created by this label provider.
     */
    @Override
    public final void dispose() {
        for (final Image image : _imageCache.values()) {
            image.dispose();
        }
        super.dispose();
    }
}
