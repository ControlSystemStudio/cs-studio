/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog.api;

import java.io.File;
import java.net.URI;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
/**
 *
 * @author berryman
 */
public class AttachmentBuilder {
		private String fileName;
                private String contentType;
                private Boolean thumbnail;
                private Long fileSize;
                private DataHandler fileContent;

		public static AttachmentBuilder attachment(Attachment attach) {
                        AttachmentBuilder builder = new AttachmentBuilder();
                        builder.fileName = attach.getFileName();
                        builder.contentType = attach.getContentType();
                        builder.thumbnail = attach.getThumbnail();
                        builder.fileSize = attach.getFileSize();
			return builder;
		}

		public static AttachmentBuilder attachment(String uri) {
			AttachmentBuilder builder = new AttachmentBuilder();
                        File fileToUpload = new File(uri);
                        FileDataSource fileDataSource = new FileDataSource(fileToUpload);
                        builder.fileName = fileToUpload.getName();
                        builder.fileContent = new DataHandler(fileDataSource);
			return builder;
		}

		public static AttachmentBuilder attachment(File file) {
			AttachmentBuilder builder = new AttachmentBuilder();
			builder.fileName = file.getName();
                        FileDataSource fileDataSource = new FileDataSource(file);
                        builder.fileContent = new DataHandler(fileDataSource);
			return builder;
		}


		XmlAttachment toXml() {
			XmlAttachment xml = new XmlAttachment();
                        xml.setFileName(fileName);
                        xml.setContentType(contentType);
                        xml.setFileSize(fileSize);
                        xml.setThumbnail(thumbnail);
			return xml;
		}

		Attachment build(){
			return new Attachment(this.toXml());
		}

}
