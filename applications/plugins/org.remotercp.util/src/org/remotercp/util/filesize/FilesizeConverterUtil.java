package org.remotercp.util.filesize;

import java.text.DecimalFormat;

public class FilesizeConverterUtil {

	private final static double MEGABYTE = 1024 * 1024;

	private final static double KILOBYTE = 1024;

	public static String getAppropriateFilesize(int filesize) {
		DecimalFormat df = new DecimalFormat("0.00");
		String fileName = null;

		if (filesize <= 1000) {
			fileName = filesize + " bytes";
		}
		if (filesize > 1000) {
			double newSize = filesize / KILOBYTE;
			fileName = Math.round(newSize) + " Kb";

		}
		if (filesize > 1000000) {
			double newSize = filesize / MEGABYTE;

			fileName = df.format(newSize) + " Mb";
		}

		return fileName;
	}

	public static String getAppropriateFilesize(long filesize) {
		return getAppropriateFilesize((int) filesize);
	}
}
