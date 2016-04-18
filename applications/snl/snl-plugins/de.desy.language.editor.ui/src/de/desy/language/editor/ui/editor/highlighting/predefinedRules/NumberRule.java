package de.desy.language.editor.ui.editor.highlighting.predefinedRules;

/*******************************************************************************
 * Copyright (c) 2005, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *     Wind River Systems, Inc. - bug fixes
 *******************************************************************************/

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Recognizes integer and float numbers.
 *
 * @author P.Tomaszewski
 *
 * TODO Move this class and its Test to
 * de.desy.snl.editor.draft.ui.extension.predefinedRules
 */
public class NumberRule implements IRule {
    /** Style token. */
    private final IToken token;

    /**
     * Creates new number rule.
     *
     * @param token
     *            Style token.
     */
    public NumberRule(final IToken token) {
        super();
        this.token = token;
    }

    /**
     * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    @Override
    public IToken evaluate(final ICharacterScanner scanner) {
        final int startCh = scanner.read();
        int ch;
        int unreadCount = 1;

        if (this.isNumberStart(startCh)) {
            ch = startCh;
            if ((startCh == '-') || (startCh == '+')) {
                ch = scanner.read();
                ++unreadCount;
            }
            if (ch == '0') {
                final int xCh = scanner.read();
                ++unreadCount;
                if ((xCh == 'x') || (xCh == 'X')) {
                    // hexnumber starting with [+-]?0[xX]
                    do {
                        ch = scanner.read();
                    } while (this.isHexNumberPart((char) ch));
                    scanner.unread();
                    return this.token;
                }
                scanner.unread();
                // assert ch == '0';
            } else if (!Character.isDigit((char) ch)) {
                ch = scanner.read();
                ++unreadCount;
            }
            if (Character.isDigit((char) ch)) {
                // need at least one digit
                do {
                    ch = scanner.read();
                } while (Character.isDigit((char) ch));
                if ((ch == '.') && (startCh != '.')) {
                    // fraction
                    do {
                        ch = scanner.read();
                    } while (Character.isDigit((char) ch));
                }
                if ((ch == 'e') || (ch == 'E')) {
                    // exponent
                    ch = scanner.read();
                    if ((ch == '-') || (ch == '+')
                            || Character.isDigit((char) ch)) {
                        do {
                            ch = scanner.read();
                        } while (Character.isDigit((char) ch));
                    }
                }
                scanner.unread();
                return this.token;
            }
        }
        do {
            scanner.unread();
        } while (--unreadCount > 0);
        return Token.UNDEFINED;
    }

    /**
     * Checks if start of number.
     *
     * @param ch
     *            Char to check.
     * @return <b>true</b> if Number.
     */
    private boolean isNumberStart(final int ch) {
        return (ch == '-') || (ch == '+') || (ch == '.')
                || Character.isDigit((char) ch);
    }

    /**
     * Checks if part of hex number;
     *
     * @param ch
     *            Char to check.
     * @return <b>true</b>
     */
    private boolean isHexNumberPart(final int ch) {
        return Character.isDigit((char) ch) || (ch == 'a') || (ch == 'b')
                || (ch == 'c') || (ch == 'd') || (ch == 'e') || (ch == 'f')
                || (ch == 'A') || (ch == 'B') || (ch == 'C') || (ch == 'D')
                || (ch == 'E') || (ch == 'F');
    }
}
