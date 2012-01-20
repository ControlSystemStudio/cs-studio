/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog.api;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Attachment object that can be represented as XML/JSON in payload data.
 * 
 * @author Eric Berryman
 * @deprecated
 */
//TODO: pass attachments over XML / without webdav? make log entries with attachments atomic?


@XmlType
@XmlRootElement(name = "attachment")
@Deprecated public class XmlAttachment {
    @XmlTransient
    protected String fileName;

    @XmlTransient
    protected DataHandler fileContent;

    private String Uri;

    /**
     * Creates a new instance of XmlAttachment
     */
    public XmlAttachment() {
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
    	return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
    	this.fileName = fileName;
    }

    /**
     * @return the Uri
     */
    @XmlAttribute
    public String getUri() {
    	return Uri;
    }

    /**
     * @param Uri
     *            the Uri to set
     */
    public void setUri(String Uri) {
    	this.Uri = Uri;
    }
    /**
     * @return the fileContent
     */
    @XmlMimeType("application/octet-stream")
    public DataHandler getFileContent() {
        return fileContent;
    }

    /**
     * @param fileContent
     *            the fileContent to set
     */
    public void setFileContent(DataHandler fileContent) {
	this.fileContent = fileContent;
    }


    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlAttach to log
     * @return string representation for log
     */
    public static String toLog(XmlAttachment data) {
        return data.getFileName() + "(" + data.getUri() + ")";
    }
}
