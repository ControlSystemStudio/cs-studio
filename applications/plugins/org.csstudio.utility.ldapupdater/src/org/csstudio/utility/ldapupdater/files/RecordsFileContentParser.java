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
package org.csstudio.utility.ldapupdater.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.SortedSet;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldapupdater.model.Record;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * Reads the lines of a file and puts them into a sorted set (lexicographically by name).
 *
 * @author bknerr
 * @since 28.04.2011
 */
public final class RecordsFileContentParser {

    /**
     * Line based record name processor.
     *
     * @author bknerr
     * @since 03.08.2011
     */
    private static final class RecordLineProcessor implements LineProcessor<SortedSet<Record>> {

        private final SortedSet<Record> _records =
            Sets.newTreeSet(new Comparator<Record>() {
                @Override
                public int compare(@Nonnull final Record r1, @Nonnull final Record r2) {

                    final String r1Name = r1.getName();
                    final String r2Name = r2.getName();

                    final int c1 = r1Name.compareToIgnoreCase(r2Name);
                    if (c1 != 0) {
                        return c1; // completely different strings
                    }
                    // strings only differing by case
                    return r1Name.compareTo(r2Name);
                }
            });

        /**
         * Constructor.
         */
        public RecordLineProcessor() {
            // EMPTY
        }

        @Override
        public boolean processLine(@Nonnull final String line) throws IOException {
            if (!isEmptyOrComment(line)) {
                final String[] fields = line.split(",");
                if (fields != null && fields.length > 0) {
                    final String name = fields[0].trim();
                    final String desc = fields.length >= 2 ? fields[1].trim() : "";
                    _records.add(new Record(name, desc));
                }
            }
            return true;
        }

        private boolean isEmptyOrComment(@Nonnull final String line) {
            return Strings.isNullOrEmpty(line) || line.matches("^[ \\t]*#.*");
        }

        @Override
        @Nonnull
        public SortedSet<Record> getResult() {
            return _records;
        }
    }

    /**
     * Constructor.
     */
    private RecordsFileContentParser() {
        // EMPTY
    }

    @Nonnull
    public static SortedSet<Record> parse(@Nonnull final File file) throws IOException {
        final SortedSet<Record> records =
            Files.readLines(file,
                            Charset.defaultCharset(),
                            new RecordLineProcessor());
        return records;
    }
}
