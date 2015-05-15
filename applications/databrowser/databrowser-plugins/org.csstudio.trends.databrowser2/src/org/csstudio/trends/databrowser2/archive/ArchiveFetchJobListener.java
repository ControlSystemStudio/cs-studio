/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.archive;

import org.csstudio.trends.databrowser2.model.ArchiveDataSource;

/** Listener to an ArchiveFetchJob
 *  @author Kay Kasemir
 */
public interface ArchiveFetchJobListener
{
    /** Invoked when the job completed successfully
     *  @param job Job that completed
     */
    void fetchCompleted(ArchiveFetchJob job);

    /** Invoked when the job completed successfully
     *  @param job Job that had error
     *  @param archive Archive that job was currently accessing
     *  @param error Error description
     */
    void archiveFetchFailed(ArchiveFetchJob job, ArchiveDataSource archive, Exception error);
}
