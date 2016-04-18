/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.editor.ui.editor.highlighting;

import java.util.Iterator;

import org.eclipse.jface.text.rules.ICharacterScanner;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * A character sequence reader to read on an {@link ICharacterScanner} with auto
 * count of read chars to perform unread of all read characters.
 *
 * This scanner performs a buffering of last avail char and is thread-safe in
 * consequence.
 *
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public class CharacterSequence implements Iterable<Character> {

    /**
     * Indicates if EOF has been reached.
     */
    private boolean _eofReached;

    /**
     * The internal count of read chars (use for EOF a.s.o.).
     */
    private long _internalReadCount = this._readCount;

    /**
     * The last read char.
     */
    private Character _lastReadChar;

    /**
     * Count of read chars.
     */
    private long _readCount;

    /**
     * The {@link ICharacterScanner} to read on.
     */
    private final ICharacterScanner _source;

    /**
     * Creates a new {@link CharacterSequence} instance for given source.
     *
     * @require source != null
     */
    public CharacterSequence(final ICharacterScanner source) {
        Contract.requireNotNull("source", source);

        this._source = source;
        this._readCount = 0L;
        this._eofReached = false;
    }

    /**
     * Returns the number of read characters.
     *
     * @ensure returnValue >= 0
     */
    synchronized public long getReadCount() {
        final long returnValue = this._readCount;

        Contract.ensure(returnValue >= 0,
                "Postcondition unresolved: returnValue >= 0");
        return returnValue;
    }

    public boolean hasEndOfStreamBeenReached() {
        return this._eofReached;
    }

    /**
     * Checks if more chars avail (will read and may unread one char on the
     * {@link ICharacterScanner}.
     */
    synchronized public boolean hasMoreCharacters() {
        if (this._lastReadChar != null) {
            return true;
        }

        if (this._eofReached) {
            return false;
        }

        final int read = this._source.read();
        this._internalReadCount++;

        if (read != ICharacterScanner.EOF) {
            this._lastReadChar = Character.toChars(read)[0];
            return true;
        }

        this._source.unread();
        this._internalReadCount--;
        this._eofReached = true;
        return false;
    }

    /**
     * Creates an iterator over chars to be read. This iterator is not thread
     * safe!
     */
    @Override
    public Iterator<Character> iterator() {
        final CharacterSequence cs = this;
        return new Iterator<Character>() {
            @Override
            public boolean hasNext() {
                return cs.hasMoreCharacters();
            }

            @Override
            public Character next() {
                return cs.readSingleCharacter();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "Remove is not supported!");
            }
        };
    }

    /**
     * "Unread" all before read characters. The read-count is set to 0.
     *
     * @ensure getReadCount() == 0
     */
    synchronized public void performUnread() {
        while (this._internalReadCount > 0) {
            this._source.unread();
            this._internalReadCount--;
        }
        ;
        this._readCount = 0;
        this._eofReached = false;

        Contract.ensure(this.getReadCount() == 0,
                "Postcondition unresolved: getReadCount() == 0");
    }

    /**
     * Unread one single char.
     */
    public void performUnreadOneSingleChar() {
        this._source.unread();
        this._internalReadCount--;
        this._readCount--;
        this._eofReached = false;
    }

    /**
     * "Unread" all chars backwards up to the number to keep. If the number of
     * chars to keep is greater than already readed chars, nothing will happen.
     *
     * @ensure getReadCount() == charactersToKeepRead
     */
    synchronized public void performUnreadWithKeepingGivenCharsRead(
            final long charactersToKeepRead) {

        long countOfCharsToUnread = this._readCount - charactersToKeepRead;
        while (countOfCharsToUnread > 0) {
            this._source.unread();
            this._internalReadCount--;
            this._readCount--;
            countOfCharsToUnread--;
            this._eofReached = false;
        }

        Contract
                .ensure(this.getReadCount() == charactersToKeepRead,
                        "Postcondition unresolved: getReadCount() == charactersToKeepRead");
    }

    /**
     * Reads a single character from the source.
     *
     * @ensure getReadCount() > §old.getReadCount()
     * @require hasMoreCharactes()
     */
    synchronized public char readSingleCharacter() {
        Contract.require(this.hasMoreCharacters(),
                "Precondition unresolved: hasMoreCharactes()");
        final long oldReadCount = this.getReadCount();

        final char returnValue = this._lastReadChar;
        this._lastReadChar = null;
        this._readCount++;

        Contract
                .ensure(this.getReadCount() > oldReadCount,
                        "Postcondition unresolved: getReadCount() > §old.getReadCount()");
        return returnValue;
    }
}
