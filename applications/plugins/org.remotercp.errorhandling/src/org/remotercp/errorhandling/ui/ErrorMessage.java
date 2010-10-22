package org.remotercp.errorhandling.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

public class ErrorMessage {

	private Image image;

	private Date date;

	private IStatus status;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm");

	public ErrorMessage(Image image, IStatus status) {
		this.image = image;
		this.date = new Date();
		this.status = status;
	}

	public String getText() {
		return this.status.getMessage();
	}

	public Image getImage() {
		return this.image;
	}

	public String getDate() {
		return dateFormat.format(this.date);
	}

	public IStatus getSeverity() {
		return this.status;
	}
}
