/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.runtime.IPath;

/** Path that supports URLs
 * 
 *  <p>The default <code>org.eclipse.code.runtime.Path</code>
 *  collapses multiple '//' into '/', which turns
 *  "http://host" into an invalid "http:/host".
 *   
 *  @author Xihui Chen - Original org.csstudio.opibuilder.persistence.URLPath
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class URLPath implements IPath
{
    /** Protocol: ftp, http, ... */
    final private String protocol;

    /** Host name, may include ":port" */
    final private String host;
    
    /** Segments of the path from host on: /some/path?query */
    final private String[] segments;
    
    final private boolean trailing_separator;
    
    public URLPath(final String path) throws Exception
    {
        // Locate protocol
        int sep = path.indexOf("://");
        if (sep < 0)
            throw new Exception("Missing URL protocol");
        protocol = path.substring(0, sep);
        
        // Locate host
        final String[] sections = path.substring(sep + 3).split("/+");
        if (sections.length <= 0)
            throw new Exception("Missing URL host");
        
        // Locate sections
        host = sections[0];
        segments = checkPathElements(sections, 1, sections.length-1);
        trailing_separator = path.endsWith("/");
    }
    
    public URLPath(final String protocol, final String host,
            final String[] segments,
            final boolean trailing_separator)
    {
        this.protocol = protocol;
        this.host = host;
        this.segments = checkPathElements(segments, 0, segments.length);
        this.trailing_separator = trailing_separator;
    }
    
    /** Check path elements, handling ".."
     *  @param elements Path elements, may include ".." elements
     *  @param start First element to use
     *  @param count Number of elements to use
     *  @return Path segments copied from <code>elements</code>, but collapsed at ".."
     */
    private String[] checkPathElements(final String[] elements, final int start, final int count)
    {
        final String[] segments = new String[count];
        int dest = 0;
        for (int i=0; i<count; ++i)
        {
            if ("..".equals(elements[start + i]))
            {
                if (dest > 0)
                    --dest;
            }
            else
            {
                String seg = elements[start + i];
                if (seg.startsWith("/"))
                    seg = seg.substring(1);
                if (seg.endsWith("/"))
                    seg = seg.substring(0, seg.length()-1);
                segments[dest++] = seg;
            }
        }
        if (dest < segments.length)
            return Arrays.copyOf(segments, dest);
        return segments;
    }

    @Override
    public String segment(final int index)
    {
        if (index >= 0  &&  index < segments.length)
            return segments[index];
        return null;
    }

    @Override
    public int segmentCount()
    {
        return segments.length;
    }

    @Override
    public String[] segments()
    {
        return segments.clone();
    }

    /** @return URL's protocol and host as "device" */
    @Override
    public String getDevice()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(protocol).append("://").append(host);
        return buf.toString();
    }

    @Override
    public IPath setDevice(final String device)
    {
        return this;
    }

    @Override
    public IPath addTrailingSeparator()
    {
        return new URLPath(protocol, host, segments, true);
    }

    @Override
    public boolean hasTrailingSeparator()
    {
        return trailing_separator;
    }

    @Override
    public IPath removeTrailingSeparator()
    {
        return new URLPath(protocol, host, segments, false);
    }

    @Override
    public IPath addFileExtension(final String extension)
    {
        try
        {
            return new URLPath(toString() + "." + extension);
        }
        catch (Exception ex)
        {
            return this;
        }
    }

    @Override
    public String getFileExtension()
    {
        if (hasTrailingSeparator())
            return null;
        final String last = lastSegment();
        if (last == null)
            return null;
        final int sep = last.lastIndexOf('.');
        if (sep < 0)
            return null;
        return last.substring(sep + 1);
    }

    @Override
    public IPath removeFileExtension()
    {
        if (getFileExtension() == null)
            return this;
        final String url = toString();
        final int sep = url.lastIndexOf('.');
        try
        {
            return new URLPath(url.substring(0, sep));
        }
        catch (Exception ex)
        {
            return this;
        }
    }

    @Override
    public IPath removeFirstSegments(final int count)
    {
        return new URLPath(protocol, host,
            Arrays.copyOfRange(segments, count, segments.length),
            trailing_separator);
    }

    @Override
    public IPath removeLastSegments(final int count)
    {
        return new URLPath(protocol, host,
            Arrays.copyOf(segments, segments.length-count),
            trailing_separator);
    }

    @Override
    public IPath uptoSegment(final int count)
    {
        return new URLPath(protocol, host,
            Arrays.copyOf(segments, count),
            trailing_separator);
    }

    @Override
    public IPath append(final String path)
    {
        try
        {
            if (trailing_separator)
                return new URLPath(toString() + path);
            else
                return new URLPath(toString() + SEPARATOR + path);
        }
        catch (Exception ex)
        {
            return this;
        }
    }

    @Override
    public IPath append(final IPath path)
    {
        final int N = path.segmentCount();
        final String[] new_segments = new String[segments.length + N];
        System.arraycopy(segments, 0, new_segments, 0, segments.length);
        System.arraycopy(path.segments(), 0, new_segments, segments.length, N);
        return new URLPath(protocol, host, new_segments, trailing_separator);
    }

    @Override
    public boolean isAbsolute()
    {
        return true;
    }

    @Override
    public boolean isEmpty()
    {
        return segments.length <= 0;
    }

    @Override
    public boolean isPrefixOf(final IPath anotherPath)
    {
        if (! getDevice().equals(anotherPath.getDevice()))
            return false;
        
        if (segmentCount() > anotherPath.segmentCount())
            return false;
        
        for (int i=0; i<segments.length; ++i)
            if(! segments[i].equals(anotherPath.segment(i)))
                return false;
        return true;
    }

    @Override
    public boolean isRoot()
    {
        return segments.length == 0;
    }

    @Override
    public boolean isUNC()
    {
        return false;
    }

    @Override
    public boolean isValidPath(final String path)
    {
        try
        {
            final URLPath test = new URLPath(path);
            for (int i = 0, max = test.segmentCount(); i < max; i++)
                if (!isValidSegment(test.segment(i)))
                    return false;
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    @Override
    public boolean isValidSegment(final String segment)
    {
        if (segment == null   ||
            segment.isEmpty() ||
            segment.contains("/"))
            return false;
        return true;
    }

    @Override
    public String lastSegment()
    {
        return segment(segments.length - 1);
    }

    @Override
    public IPath makeAbsolute()
    {
        return this;
    }

    @Override
    public IPath makeRelative()
    {
        return this;
    }

    @Override
    public IPath makeRelativeTo(final IPath base)
    {
        // Can only be relative to another URL for same 'device'
        if (! getDevice().equals(base.getDevice()))
            return this;

        // Combine base segments
        final int N = base.segmentCount();
        final String[] new_segments = new String[N + segments.length];
        System.arraycopy(base.segments(), 0, new_segments, 0, N);
        // .. with these segments
        System.arraycopy(segments, 0, new_segments, N, segments.length);

        return new URLPath(protocol, host, new_segments, trailing_separator);
    }

    @Override
    public IPath makeUNC(boolean toUNC)
    {
        return this;
    }

    @Override
    public int matchingFirstSegments(final IPath anotherPath)
    {
        int anotherPathLen = anotherPath.segmentCount();
        int max = Math.min(segments.length, anotherPathLen);
        int count = 0;
        for (int i = 0; i < max; i++)
        {
            if (! segments[i].equals(anotherPath.segment(i)))
                return count;
            count++;
        }
        return count;
    }

    @Override
    public File toFile()
    {
        if (! "file".equals(protocol))
            return null;
        final StringBuilder buf = new StringBuilder();
        buf.append("/").append(host);
        for (String segment : segments)
            buf.append("/").append(segment);
        return new File(buf.toString());
    }

    @Override
    public String toOSString()
    {
        return toString();
    }

    @Override
    public String toPortableString()
    {
        return toString();
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof IPath))
            return false;
        final IPath other = (IPath) obj;
        return other.toString().equals(toString());
    }

    @Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            return null;
        }
    }
    
    /** @return Full URL */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(protocol).append("://").append(host);
        for (String segment : segments)
            buf.append(SEPARATOR).append(segment);
        if (trailing_separator)
            buf.append(SEPARATOR);
        return buf.toString();
    }
}
