package org.csstudio.trends.databrowser2.imports;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** Factory for {@link ArchiveReader} that imports data from file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ImportArchiveReaderFactory implements ArchiveReaderFactory
{
    /** Prefix used by this reader */
    final public static String PREFIX = "import:";

    /** Create URL
     *  @param type File type
     *  @param path Path to file
     *  @return Import URL
     */
    public static String createURL(final String type, final String path)
    {
        return "import:" + type + ":/" + path;
    }

    /** Parse URL
     *  @param url Import URL
     *  @return String[] with type, path
     *  @throws Exception if URL doesn't parse
     */
    public static String[] parseURL(final String url) throws Exception
    {
        final int prefix_len = PREFIX.length();
        if (! url.startsWith("import:"))
            throw new Exception("URL does not start with 'import': " + url);
        final int sep = url.substring(prefix_len).indexOf(":/");
        if (sep < 0)
            throw new Exception("Missing import data type from URL: " + url);
        final String type = url.substring(prefix_len, prefix_len+sep);
        final String path = url.substring(prefix_len+sep+2);
        return new String[] { type, path };
    }

    @Override
    public ArchiveReader getArchiveReader(final String url) throws Exception
    {
        // TODO Would be good to cache the ImportArchiveReader for a URL
        //      so that it can return the known data.
        //      But should do this per-model, not globally, to prevent
        //      running out of memory
        //
        // Map<String, ImportArchiveReader>
        //
        // Model#stop(): Remove all cache data for items in model

        // Get path, importer from URL
        final String[] type_path = parseURL(url);
        final String type = type_path[0];
        final String path = type_path[1];
        final SampleImporter importer = SampleImporters.getImporter(type);
        if (importer == null)
            throw new Exception("Unknown import data type " + type);
        return new ImportArchiveReader(url, path, importer);
    }
}
