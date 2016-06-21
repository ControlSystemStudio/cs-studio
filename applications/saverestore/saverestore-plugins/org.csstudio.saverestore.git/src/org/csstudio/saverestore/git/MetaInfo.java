/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.git;

import java.util.Date;

/**
 * <code>GitMetaInfo<code> is a GIT implementation of the <code>MetaInfo</code> interface.
 *
 * @author <a href="mailto:miha.novak@cosylab.com">Miha Novak</a>
 */
class MetaInfo {

    final String comment;
    final String creator;
    final Date timestamp;
    final String eMail;
    final String revision;

    /**
     * Constructs <code>GitMetaInfo</code> class.
     *
     * @param comment the transaction comment
     * @param creator the person that made the commit
     * @param email the email of the person that made the commit
     * @param timestamp timestamp of the commit
     * @param revision the git revision hash
     */
    MetaInfo(String comment, String creator, String email, Date timestamp, String revision) {
        String myComment = comment;
        if (myComment != null) {
            // strip the comment of trailing white spaces and new lines
            myComment = myComment.trim();
            int i = myComment.length() - 1;
            for (; i > -1 && (myComment.charAt(i) == '\n' || myComment.charAt(i) == '\r'); i--)
                ;
            myComment = myComment.substring(0, i + 1);
        }
        this.comment = myComment;
        this.creator = creator;
        this.timestamp = timestamp;
        this.eMail = email == null ? "unknown" : email;
        this.revision = revision;
    }
}
