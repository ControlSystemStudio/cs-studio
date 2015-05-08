package org.csstudio.sds.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Utility to deal with relative paths.
 *
 * @author Kai Meyer (C1 WPS)
 *
 */
public final class PathUtil {

    /**
     * Path to get one folder back.
     */
    private static IPath _folderBackPath = new Path("../");

    /**
     * Private constructor.
     */
    private PathUtil() {
        // nothing to do
    }

    /**
     * Creates a new relativ {@link IPath} based on the given path, but relative
     * to the anchorPath.
     *
     * If the parameters are
     * "C:/Program/Development/SDS/Scripts/Script.css-sdss" as path and
     * "C:/Program/Development/SDS/Display.css-sds" as anchorPath the result
     * will be "/Scripts/Script.css-sdss".
     *
     * @param path
     *            The path to convert
     * @param anchorPath
     *            The The related path
     * @return A new {@link IPath} based on path and relative to the anchorPath
     */
    public static IPath makePathRelativToAnchor(final IPath path,
            final IPath anchorPath) {
        IPath result = path;
        IPath referencePath = anchorPath;
        String lastSegment = referencePath.lastSegment();
        if (lastSegment.contains(".")) {
            referencePath = referencePath.removeLastSegments(1);
        }
        if (referencePath.isPrefixOf(result)) {
            result = result.setDevice(null);
            int count = referencePath.segmentCount();
            result = result.removeFirstSegments(count);
        } else {
            String device = getDevice(result);
            String anchorDevice = getDevice(referencePath);
            if ((device == null && anchorDevice == null)
                    || (device != null && device.equals(anchorDevice))) {
                if (device != null) {
                    result = removeDevice(result);
                    referencePath = removeDevice(referencePath);
                }
                for (int i = 0; i < referencePath.segmentCount(); i++) {
                    String resultSegment = result.segment(i);
                    String anchorSegment = referencePath.segment(i);
                    if (resultSegment.equals(anchorSegment)) {
                        result = result.removeFirstSegments(1);
                    } else {
                        result = _folderBackPath.append(result);
                    }
                }
            }
        }
        result = result.makeRelative();
        return result;
    }

    /**
     * Returns the device of the given path.
     *
     * @param path
     *            The {@link IPath}
     * @return The device (ending with ':') or <code>null</code> if no device
     *         exists
     */
    private static String getDevice(final IPath path) {
        String string = path.toString();
        String device = null;
        if (string.charAt(1) == IPath.DEVICE_SEPARATOR) {
            device = string.substring(0, 2);
        }
        return device;
    }

    /**
     * Removes the device of the given path.
     *
     * @param path
     *            The {@link IPath}
     * @return The path without the device
     */
    private static IPath removeDevice(final IPath path) {
        IPath result = path;
        String string = path.toString();
        if (string.charAt(1) == IPath.DEVICE_SEPARATOR) {
            result = new Path(string.substring(2));
        }
        return result;
    }

    /**
     * Creates a new absolute {@link IPath} based on the relativePath and the
     * anchorPath.
     *
     * If the parameters are "/Scripts/Script.css-sdss" as path and
     * "C:/Program/Development/SDS/Display.css-sds" as anchorPath the result
     * will be "C:/Program/Development/SDS/Scripts/Script.css-sdss".
     *
     * @param relativePath
     *            The path to convert
     * @param anchorPath
     *            The The related path
     * @return A new {@link IPath} based on path and relative to the anchorPath
     */
    public static IPath getFullPath(final IPath relativePath,
            final IPath anchorPath) {
        IPath result = relativePath;
        if (!result.isAbsolute()) {
            if (_folderBackPath.isPrefixOf(relativePath)) {
                int segmentsToRemove = 0;
                String folderBackSegment = _folderBackPath.toString();
                String currentSegment = result.segment(0);
                while (currentSegment.equals(folderBackSegment)) {
                    segmentsToRemove++;
                    result = result.removeFirstSegments(1);
                    currentSegment = result.segment(0);
                }
                IPath path = anchorPath.removeLastSegments(segmentsToRemove);
                result = path.append(result);
            } else {
                if (!anchorPath.isPrefixOf(result)) {
                    result = anchorPath.append(result);
                }
            }
        }
        return result;
    }

}
