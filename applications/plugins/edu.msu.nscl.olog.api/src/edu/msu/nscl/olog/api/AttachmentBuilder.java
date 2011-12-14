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
@Deprecated public class AttachmentBuilder {
		private String fileName;
                private String Uri;
                private DataHandler fileContent;

		public static AttachmentBuilder attachment(Attachment attach) {
                        AttachmentBuilder builder = new AttachmentBuilder();
                        builder.fileName = attach.getFileName();
                        builder.Uri = attach.getUri();
                        builder.fileContent = attach.getFileContent();
			return builder;
		}

		public static AttachmentBuilder attachment(String uri) {
			AttachmentBuilder builder = new AttachmentBuilder();
                        File fileToUpload = new File(uri);
                        FileDataSource fileDataSource = new FileDataSource(fileToUpload);
                        builder.fileName = fileToUpload.getName();
                        builder.Uri = uri;
                        builder.fileContent = new DataHandler(fileDataSource);
			return builder;
		}

		public static AttachmentBuilder attachment(File file) {
			AttachmentBuilder builder = new AttachmentBuilder();
			builder.fileName = file.getName();
                        builder.Uri = file.getAbsolutePath();
                        FileDataSource fileDataSource = new FileDataSource(file);
                        builder.fileContent = new DataHandler(fileDataSource);
			return builder;
		}

		public AttachmentBuilder uri(URI uri) {
			this.Uri = uri.toASCIIString();
			return this;
		}

                public AttachmentBuilder uri(String remote) {
			this.Uri = remote;
			return this;
		}

		XmlAttachment toXml() {
			XmlAttachment xml = new XmlAttachment();
                        File fileToUpload = new File(Uri);
                        FileDataSource fileDataSource = new FileDataSource(fileToUpload);
                        DataHandler content = new DataHandler(fileDataSource);
                        xml.setUri(Uri);
                        xml.setFileName(fileToUpload.getName());
                        xml.setFileContent(content);
			return xml;
		}

		Attachment build(){
			return new Attachment(this.toXml());
		}

}
