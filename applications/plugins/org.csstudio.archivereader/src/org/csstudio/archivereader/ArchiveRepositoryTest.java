package org.csstudio.archivereader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit Plug-in Test of the ArchiveRepository
 *  @author Kay Kasemir
 */
public class ArchiveRepositoryTest
{
    @SuppressWarnings("nls")
    @Test
    public void testArchiveRepository() throws Exception
    {
        // FIXME (bknerr) : Test with sysos?! Use assertions for expected prefixes
        final ArchiveRepository archives = ArchiveRepository.getInstance();
        //System.out.println("Located support for these archive URL prefixes:");
        final String prefixes[] = archives.getSupportedPrefixes();
//        for (String prefix : prefixes)
//            System.out.println(prefix);
        assertTrue(prefixes.length > 0);
    }
}
