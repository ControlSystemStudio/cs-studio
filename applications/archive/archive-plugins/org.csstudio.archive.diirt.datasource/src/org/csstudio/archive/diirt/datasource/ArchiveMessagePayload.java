package org.csstudio.archive.diirt.datasource;

import java.util.List;

import org.diirt.vtype.VType;

/**
 *
 * <code>ArchiveMessagePayload</code> is a wrapper around the list of values and is used as the message payload object.
 * The list could be used directly as the payload type but due to incorrect generics usage in diirt the list has to be
 * wrapped.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
class ArchiveMessagePayload {

    final List<VType> values;

    /**
     * Constructs a new message payload which wraps the given list.
     *
     * @param values the values to wrap
     */
    ArchiveMessagePayload(List<VType> values) {
        this.values = values;
    }
}
