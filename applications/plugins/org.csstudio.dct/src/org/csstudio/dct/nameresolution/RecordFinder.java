package org.csstudio.dct.nameresolution;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionUtil;

/**
 * Utility class that helps to find records in a hierarchical model.
 *
 * @author Sven Wende
 *
 */
public final class RecordFinder {
    private RecordFinder() {
    }

    /**
     * Searches for a record by name using a path. A path denotes the name from
     * the root of a container to the target record.
     *
     * The method climbs up the container hierarchy in case a record is not
     * found in the starting container.
     *
     * @param path
     *            the path (path segments are separated by a dot)
     * @param container
     *            the starting container
     * @return a record or null
     */
    public static IRecord findRecordByPath(String path, IContainer container) {
        String[] segments = path.split("\\.");

        IRecord result = null;
        IContainer root = container;

        boolean goon = root != null && result == null;

        while (goon) {
            result = findRecordByPath(segments, root);

            if (root.getContainer() instanceof IContainer) {
                root = root.getContainer();
            } else {
                root = null;
            }
            goon = root != null && result == null;
        }

        return result;
    }

    private static IRecord findRecordByPath(String[] segments, IContainer instance) {
        assert segments != null;

        IRecord result = null;

        if (instance != null && segments.length >= 1) {

            if (segments.length == 1) {
                result = findRecordByName(segments[0], instance);
            } else {
                result = findRecordByPath(removeLeadingSegment(segments), findSubInstanceBy(segments[0], instance));
            }
        }

        return result;
    }

    private static String[] removeLeadingSegment(String[] segments) {
        String[] result = new String[segments.length >= 1 ? segments.length - 1 : 0];

        for (int i = 0; i < result.length; i++) {
            result[i] = segments[i + 1];
        }

        return result;
    }

    private static IInstance findSubInstanceBy(String name, IContainer instance) {
        assert name != null;
        assert instance != null;

        for (IInstance i : instance.getInstances()) {
            if (name.equals(AliasResolutionUtil.getNameFromHierarchy(i))) {
                return i;
            }
        }

        return null;
    }

    private static IRecord findRecordByName(String name, IContainer instance) {
        assert name != null;
        assert instance != null;

        for (IRecord r : instance.getRecords()) {
            if (name.equals(AliasResolutionUtil.getNameFromHierarchy(r))) {
                return r;
            }
        }

        return null;
    }

}
