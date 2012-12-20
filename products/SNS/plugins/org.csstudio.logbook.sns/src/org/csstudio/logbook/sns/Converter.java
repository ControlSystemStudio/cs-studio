/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.Tag;
import org.csstudio.logbook.sns.elog.ELog;
import org.csstudio.logbook.sns.elog.ELogAttachment;
import org.csstudio.logbook.sns.elog.ELogCategory;

/** Converted between {@link ELog} and Logbook API
 *  @author Kay Kasemir
 */
public class Converter
{
    /** Convert/wrap categories from ELog to Logbook API
     *  @param logbooks ELog logbook names
     *  @return {@link Logbook}s
     */
    public static List<Logbook> convertLogbooks(final List<String> logbooks)
    {
        final List<Logbook> result = new ArrayList<>(logbooks.size());
        for (String logbook : logbooks)
            result.add(new SNSLogbook(logbook));
        return result;
    }

    /** Convert/wrap categories from ELog to Logbook API
     *  @param categories ELog categories
     *  @return {@link Tag}s
     */
    public static List<Tag> convertCategories(final List<ELogCategory> categories)
    {
        final List<Tag> result = new ArrayList<>(categories.size());
        for (ELogCategory category : categories)
            result.add(new SNSTag(category.getName()));
        return result;
    }
    
    /** Convert/wrap attachment from ELog to Logbook API
     *  @param attachment {@link ELogAttachment}
     *  @return {@link Attachment}
     */
    public static Attachment convertAttachment(final ELogAttachment attachment)
    {
        return new SNSAttachment(attachment);
    }

    /** Convert/wrap attachments from ELog to Logbook API
     *  @param images Images
     *  @param others Non-image attachments
     *  @return {@link Attachment}s
     */
    public static List<Attachment> convertAttachments(final List<ELogAttachment> images,
            final List<ELogAttachment> others)
    {
        final List<Attachment> attachments = new ArrayList<>(images.size() + others.size());
        for (ELogAttachment image : images)
            attachments.add(convertAttachment(image));
        for (ELogAttachment other : others)
            attachments.add(convertAttachment(other));
        return attachments;
    }
}
