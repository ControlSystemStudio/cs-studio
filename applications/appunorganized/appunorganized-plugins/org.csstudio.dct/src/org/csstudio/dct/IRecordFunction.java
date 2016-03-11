package org.csstudio.dct;

import java.util.Map;

import org.csstudio.dct.model.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Represents a record function. Corresponding extensions (extension point
 * org.csstudio.dct.recordfunctions) can be started from the editor�s menu. On
 * start, the run() method will be applied to all records.
 *
 * The specified attributes will appear in the properties table for each record
 * and can be changed by by the user.
 *
 * @author Sven Wende
 *
 */
public interface IRecordFunction {
    /**
     * Returns the attributes that should be displayed in the properties table
     * of all records, when this extension is loaded.
     *
     * @return a map with attributes as key and a default value as value
     */
    Map<String, String> getAttributes();

    void run(IProject project, IProgressMonitor monitor);
}
