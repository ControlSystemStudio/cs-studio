package org.csstudio.saverestore.git;

import java.util.Date;

/**
 * <code>GitMetaInfo<code> is a GIT implementation of the <code>MetaInfo</code>
 * interface.
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
        if (comment != null) {
            comment = comment.trim();
            int i = comment.length()-1;
            for (; i > -1 && (comment.charAt(i) == '\n' || comment.charAt(i) == '\r') ; i--);
            comment = comment.substring(0,i+1);
        }
        this.comment = comment;
        this.creator = creator;
        this.timestamp = timestamp;
        this.eMail = email == null ? "unknown" : email;
        this.revision = revision;
    }
}
