package org.csstudio.trends.databrowser2.imports;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** JUnit test of the {@link ImportArchiveReaderFactory}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ImportArchiveReaderFactoryUnitTest
{
    @Test
    public void testURLHandling() throws Exception
    {
        final String url = ImportArchiveReaderFactory.createURL("csv", "/path/to/some/file.dat");
        System.out.println(url);

        final String[] info = ImportArchiveReaderFactory.parseURL(url);
        System.out.println("Type: '" + info[0] + "'");
        System.out.println("Path: '" + info[1] + "'");
        assertEquals("csv", info[0]);
        assertEquals("/path/to/some/file.dat", info[1]);
    }
}
