package org.csstudio.dct.export;

import org.csstudio.dct.model.IProject;

/**
 * Represents a renderer that renders a single record as String.
 *
 * @author Sven Wende
 *
 */
public interface IExporter {
    String export(IProject project);
}
