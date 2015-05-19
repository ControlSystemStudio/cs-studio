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


/**
 * This class provides some utilities for rules.
 *
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public final class RuleUtils {

    /**
     * Reads all chars up to the next line break char (mapped: chars except the
     * line breaks to {@link Character#isWhitespace(char)}; line breaks to
     * '\n').
     *
     * The line break or first invalid character is and <strong>stays read</strong>,
     * if required you need to unread the character yourself.
     *
     * @param cs
     *            The {@link CharacterSequence} to read on.
     *
     * @return {@code true} if the line break was successfully found,
     *         {@code false} if an illegal character occurred before line break.
     */
    public static boolean readCharsUpToNextLineBreak(final CharacterSequence cs) {
        if (!cs.hasMoreCharacters()) {
            return false;
        }
        char lastRead = cs.readSingleCharacter();
        while (lastRead != '\n') {
            if (!cs.hasMoreCharacters()) {
                return false;
            }
            lastRead = cs.readSingleCharacter();
        }

        return true;
    }

    /**
     * Reads up to the first occurrence of given char.
     *
     * @param c
     *            The char to be found.
     * @param cs
     *            The {@link CharacterSequence} to read on.
     *
     * @return The found char or null if EOF occurred.
     */
    public static Character readUpToChar(final char c,
            final CharacterSequence cs) {
        if (!cs.hasMoreCharacters()) {
            return null;
        }
        char lastRead = cs.readSingleCharacter();
        while (lastRead != c) {
            if (!cs.hasMoreCharacters()) {
                return null;
            }
            lastRead = cs.readSingleCharacter();
        }
        return lastRead;
    }

    /**
     * Reads up to the first char which is not a whitespace char (mapped to
     * {@link Character#isWhitespace(char)}.
     *
     * @param cs
     *            The {@link CharacterSequence} to read on.
     *
     * @return The found, not whitespace-char or null if EOF occurred.
     */
    public static Character readUpToFirstNonWhitespace(
            final CharacterSequence cs) {
        if (!cs.hasMoreCharacters()) {
            return null;
        }
        char lastRead = cs.readSingleCharacter();
        while (Character.isWhitespace(lastRead)) {
            if (!cs.hasMoreCharacters()) {
                return null;
            }
            lastRead = cs.readSingleCharacter();
        }
        return lastRead;
    }

    /**
     * Reads all whitespace chars up to the next line break char (mapped:
     * whitespaces except the line breaks to
     * {@link Character#isWhitespace(char)}; line breaks to '\n').
     *
     * The line break or first invalid character is and <strong>stays read</strong>,
     * if required you need to unread the character yourself.
     *
     * @param cs
     *            The {@link CharacterSequence} to read on.
     *
     * @return {@code true} if the line break was successfully found behind zero
     *         or more whitespaces, {@code false} if an illegal, non whitespace
     *         character occurred before line break.
     */
    public static boolean readWhitespacesUpToNextLineBreak(
            final CharacterSequence cs) {
        if (!cs.hasMoreCharacters()) {
            return false;
        }
        char lastRead = cs.readSingleCharacter();
        while ((lastRead != '\n') && Character.isWhitespace(lastRead)) {
            if (!cs.hasMoreCharacters()) {
                return false;
            }
            lastRead = cs.readSingleCharacter();
        }

        return lastRead == '\n';
    }

    /**
     * Reads up to the first occurrence of given char.
     *
     * @param c
     *            The char to be found.
     * @param cs
     *            The {@link CharacterSequence} to read on.
     *
     * @return The found char or null if EOF occurred.
     */
    public static Character readWhiteSpaceUntilCharIgnoringComment(
            final char c, final CharacterSequence cs) {
        if (!cs.hasMoreCharacters()) {
            return null;
        }
        boolean inComment = false;
        char lastRead = cs.readSingleCharacter();
        // Character.isWhitespace(lastRead) || lastRead != c || inComment
        while (true) {
            if (Character.isWhitespace(lastRead) || inComment) {
                if (inComment && (lastRead == '*')) {
                    if (cs.hasMoreCharacters()) {
                        lastRead = cs.readSingleCharacter();
                        if (lastRead == '/') {
                            inComment = false;
                        }
                    } else {
                        return null;
                    }
                }
            } else if (lastRead == '/') {
                if (cs.hasMoreCharacters()) {
                    lastRead = cs.readSingleCharacter();
                    if (lastRead == '*') {
                        inComment = true;
                    }
                } else {
                    return null;
                }
            } else if (lastRead == c) {
                return lastRead;
            }
            if (!cs.hasMoreCharacters()) {
                return null;
            }
            lastRead = cs.readSingleCharacter();
        }
    }

}
