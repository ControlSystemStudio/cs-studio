package org.csstudio.dct.export;

import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;

/**
 * Represents a renderer that renders a single record as String.
 *
 * @author Sven Wende
 *
 */
public interface IExporter {
    String export(IProject project);
}
