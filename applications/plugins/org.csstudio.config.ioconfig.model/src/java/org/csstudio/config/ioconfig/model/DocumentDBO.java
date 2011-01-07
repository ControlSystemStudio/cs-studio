/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: Document.java,v 1.7 2010/02/12 10:38:40 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.02.2008
 */
@Entity
@Table(name = "MIME_FILES_LINK")
//@Table(name = "MIME_FILES_LINK_TEST")
public class DocumentDBO implements Comparable<DocumentDBO>, IDocument {

    /**
     * MIME_FILES DB Key.
     */
    private String _id;
    /**
     * File Mime typ.
     */
    private String _mimeType;

    /**
     * The Documentation File. The Name is historic conditional on Data Base.
     */
    private Blob _image;

    /**
     * The date to fill the File into the Data Base.
     */
    private Date _createdDate;
    /**
     * The User name who put the File into the Data Base.
     */
    private String _accountname;
    /**
     * The Eventtime of the activate event. Probably used only for the Elogbook.
     */
    private Date _entrydate;
    /**
     * The serverity of a event. Probably used only for the Elogbook.
     */
    private String _logseverity;
    /**
     * Probably used only for the Elogbook.
     */
    private String _linkId;
    /**
     * The Subject of the File.
     */
    private String _subject;
    /**
     * The long description to the File.
     */
    private String _desclong;
    /**
     * Probably used only for the Elogbook.
     */
    private String _linkForward;
    /**
     * Probably used only for the Elogbook.
     */
    private String _erroridentifyer;
    /**
     * The date to mark this Document as delete.
     */
    private Date _deleteDate;
    /**
     * Date of last change.
     */
    private Date _updateDate;
    /**
     * The location description for this Document.
     */
    private String _location;
    /**
     * Keywords for this Document.
     */
    private String _keywords;
    private Set<AbstractNodeDBO> _nodes;
    

    /**
     * Default Constructor needed by Hibernate.
     */
    public DocumentDBO() {
    }

    /**
     * Constructor to generate document whit basic information. 
     * @param subject set the Subject.
     * @param desc set the long description.
     * @param keywords set the keywords.
     */
    public DocumentDBO(final String subject, final String desc, final String keywords) {
        setSubject(subject);
        setDesclong(desc);
        setKeywords(keywords);
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getId()
     */
    @Id
    @Column(length = 100)
    public String getId() {
        return _id;
    }

    /**
     * 
     * @param id set the Document Id key.
     */
    public void setId(final String id) {
        _id = id;
    }

    /**
     * @return the File mime type
     */
    @Column(name = "MIME_TYPE", length = 10)
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * 
     * @param mimeType
     *            set the MIME type of this Document.
     */
    public void setMimeType(final String mimeType) {
        _mimeType = mimeType;
    }

    /**
     * The Documentation File. The Name is historic conditional on Data Base.
     * @return The Documentation File.
     */
    @Lob
    //@Basic(fetch = FetchType.LAZY)
    public Blob getImage() {
        return _image;
    }

    @Transient
    public InputStream getImageData() throws PersistenceException {
        try {
            return _image.getBinaryStream();
        } catch (SQLException e) {
            PersistenceException persistenceException = new PersistenceException(e);
            persistenceException.setStackTrace(e.getStackTrace());
            throw persistenceException;
        }
    }
    
    
    /**
     * The Documentation File. The Name is historic conditional on Data Base.
     * @param image Set the Documentation File.
     */
    public void setImage(final Blob image) {
        _image = image;
    }

    @Transient
    public void setImage(byte[] imageAsByteArray) {
        _image = Hibernate.createBlob(imageAsByteArray);
    }
    
    

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getCreatedDate()
     */
    @Column(name = "CREATED_DATE")
    public Date getCreatedDate() {
        return _createdDate;
    }

    /**
     * 
     * @param createdDate set the create date of the document.
     */
    public void setCreatedDate(final Date createdDate) {
        _createdDate = createdDate;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getAccountname()
     */
    @Column(length = 30)
    public String getAccountname() {
        return _accountname;
    }

    public void setAccountname(final String accountname) {
        _accountname = accountname;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getEntrydate()
     */
    public Date getEntrydate() {
        return _entrydate;
    }

    public void setEntrydate(final Date entrydate) {
        _entrydate = entrydate;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getLogseverity()
     */
    @Column(length = 16)
    public String getLogseverity() {
        return _logseverity;
    }

    public void setLogseverity(final String logseverity) {
        _logseverity = logseverity;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getLinkId()
     */
    @Column(name = "LINK_ID", length = 100)
    public String getLinkId() {
        return _linkId;
    }

    public void setLinkId(final String linkId) {
        _linkId = linkId;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getSubject()
     */
    @Column(length = 200)
    public String getSubject() {
        return _subject;
    }

    public void setSubject(String subject) {
        _subject = subject;
        Diagnose.addNewLine(_subject+"\t"+this.getClass().getSimpleName());
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getDesclong()
     */
    @Column(length = 4000)
    public String getDesclong() {
        return _desclong;
    }

    public void setDesclong(String desclong) {
        _desclong = desclong;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getLinkForward()
     */
    @Column(name = "Link_Forward", length = 200)
    public String getLinkForward() {
        return _linkForward;
    }

    public void setLinkForward(String linkForward) {
        _linkForward = linkForward;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getErroridentifyer()
     */
    @Column(length = 30)
    public String getErroridentifyer() {
        return _erroridentifyer;
    }

    public void setErroridentifyer(String erroridentifyer) {
        _erroridentifyer = erroridentifyer;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getDeleteDate()
     */
    @Column(name = "DELETE_DATE")
    public Date getDeleteDate() {
        return _deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        _deleteDate = deleteDate;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getUpdateDate()
     */
    @Column(name = "UPDATE_DATE")
    public Date getUpdateDate() {
        return _updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        _updateDate = updateDate;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getLocation()
     */
    @Column(length = 30)
    public String getLocation() {
        return _location;
    }

    public void setLocation(String location) {
        _location = location;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.IDocument#getKeywords()
     */
    @Column(length = 200)
    public String getKeywords() {
        return _keywords;
    }

    public void setKeywords(String keywords) {
        _keywords = keywords;
    }

    //@ManyToMany(mappedBy = "documents", targetEntity=Node.class, fetch = FetchType.EAGER)
    @Transient
    public Set<AbstractNodeDBO> getNodes() {
      return _nodes;
    }

    public void setNodes(Set<AbstractNodeDBO> nodes) {
        _nodes = nodes;
    }

    public void addNode(AbstractNodeDBO n) {
        n.addDocument(this);
        _nodes.add(n);        
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(DocumentDBO other) {
        return getId().compareTo(other.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof DocumentDBO) {
            DocumentDBO doc = (DocumentDBO) arg0;
            return doc.getId().equals(getId());
        }
        return super.equals(arg0);
    }
    
    
}
