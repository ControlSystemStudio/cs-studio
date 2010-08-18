package org.remotercp.util.filesize;

import org.junit.Test;
import static org.junit.Assert.*;

public class FileSizeConverterTest {

	@Test
	public void testFilesizeConverter() {
		int filesize = 1000;

		String appropriateFilesize = FilesizeConverterUtil
				.getAppropriateFilesize(filesize);
		assertEquals("1000 bytes", appropriateFilesize);

		filesize = 20480;
		String appropriateFilesize2 = FilesizeConverterUtil
				.getAppropriateFilesize(filesize);
		assertEquals("20 Kb", appropriateFilesize2);

		filesize = 2097152;
		String appropriateFilesize3 = FilesizeConverterUtil
				.getAppropriateFilesize(filesize);
		assertEquals("2,00 Mb", appropriateFilesize3);

		long filesizeLong = 1000;
		String appropriateFilesize4 = FilesizeConverterUtil
				.getAppropriateFilesize(filesizeLong);
		assertEquals("1000 bytes", appropriateFilesize4);

		filesizeLong = 20480;
		String appropriateFilesize5 = FilesizeConverterUtil
				.getAppropriateFilesize(filesizeLong);
		assertEquals("20 Kb", appropriateFilesize5);

		filesizeLong = 2841640;
		String appropriateFilesize6 = FilesizeConverterUtil
				.getAppropriateFilesize(filesizeLong);
		assertEquals("2,71 Mb", appropriateFilesize6);

	}
}
