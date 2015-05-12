/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.common.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * File copy. <br/>
 * Actually, it is better to use use: {@link com.google.common.io.Files}. But for those not in
 * favour of google codes, here we are.
 * Java 7 brings cure anyway with the NIO API (we had just to wait 11 years).
 *
 * @author bknerr
 * @since 30.05.2011
 */
public final class FileCopy {


    private FileCopy() {
        // Don't instantiate
    }

    public static void copy(final File source,
                            final File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    public static void copyDirectory(final File source,
                                     final File destination) throws IOException {
        copyDirectory(source, destination, null);
    }

    /**
     * Copies a given source file or directory (recursively) into a destination dir.
     * If the destination dir does not yet exist, it is created.
     *
     * @param source
     * @param destination
     * @param filter
     * @throws IOException
     */
    public static void copyDirectory(final File source,
                                     final File destination,
                                     final FileFilter filter) throws IOException {
        final File nextDirectory = new File(destination, source.getName());
        if (!nextDirectory.exists() && !nextDirectory.mkdirs()) {// create the directory if necessary...
            throw new IOException("Destination dir could not be created.");
        }
        if (source.isDirectory()) {
            for (final File file : (filter != null ? source.listFiles(filter) : source.listFiles())) {
                if (file.isDirectory()) {
                    copyDirectory(file, nextDirectory, filter);
                } else {
                    copyFile(file, nextDirectory);
                }
            }
        }
    }

    public static void copyFile(final File source,
                                final File destination) throws IOException {
        // what we really want to do is create a file with the same name in that dir
        File target = destination;
        if (destination.isDirectory()) {
            target = new File(destination, source.getName());
        }
        final FileInputStream input = new FileInputStream(source);
        copyFile(input, target);
    }

    public static void copyFile(final InputStream input,
                                final File destination) throws IOException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(destination);
            final byte[] buffer = new byte[1024];
            int bytesRead = input.read(buffer);
            while (bytesRead >= 0) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            if (output != null) {
                output.close();
            }
        }
    }

}
