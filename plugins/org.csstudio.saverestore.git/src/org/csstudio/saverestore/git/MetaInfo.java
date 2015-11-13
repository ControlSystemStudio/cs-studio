package org.csstudio.saverestore.git;

import java.util.Date;

/**
 * <code>GitMetaInfo<code> is a GIT implementation of the <code>MetaInfo</code>
 * interface.
 *
 * @author <a href="mailto:miha.novak@cosylab.com">Miha Novak</a>
 */
public class MetaInfo {

    private String comment;
    private String creator;
    private Date timestamp;
    private String eMail;


    /**
     * Constructs <code>GitMetaInfo</code> class.
     *
     * @param comment the transaction comment
     * @param creator the person that made the commit
     * @param email the email of the person that made the commit
     * @param timestamp timestamp of the commit
     */
    public MetaInfo(String comment, String creator, String email, Date timestamp) {
        this.comment = comment;
        this.creator = creator;
        this.timestamp = timestamp;
        this.eMail = email;
    }

    public String getEmail() {
        return eMail;
    }

    public String getComment() {
        return comment;
    }

    public String getCreator() {
        return creator;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}