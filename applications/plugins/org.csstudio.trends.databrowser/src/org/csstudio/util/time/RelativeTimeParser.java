package org.csstudio.util.time;

import java.util.Calendar;

/** Extract relative date/time specifications from a string.
 *  
 *  @see #parse(Calendar, String)
 *  
 *  @author Sergei Chevtsov developed the original code for the
 *          Java Archive Viewer, from which this code heavily borrows.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RelativeTimeParser
{
    /** Tokens that mark a relative date/time piece.
     *  <p>
     *  Sergei's implementation only allowed characters,
     *  like 'M' to indicate a month.
     *  This implementation allows both upper- and lowercase versions
     *  of the full "month" or shortened versions like "mon",
     *  but when only a single character is used,
     *  it's case has to match Sergei's orignal specification.
     */
    static private final String tokens[] = new String[]
    {
        "years",
        "Months",
        "days",
        "Hours",
        "minutes",
        "seconds"
    };

    /** Characters that are considered part of a number */
    static private final String NUMBER_CHARS = "+-0123456789";
    
    /** Extract a relative year, month, day, hour, minute, second from string.
     *  @param text
     */
    public static int[] parse(final String text)
    {
        int pieces[] = new int[tokens.length];
        for (int i=0; i<tokens.length; ++i)
            pieces[i] = getValueOfToken(tokens[i], text);
        return pieces;
    }
    
    /** In case text contains "-24 token", return the number 24.
     *  @param token The token to look for.
     *  @param text  The text to analyze.
     *  @return The number or 0 if token wasn't found
     *  @throws NumberFormatException In case token found but number won't parse.
     */
    private static int getValueOfToken(final String token, final String text)
        throws NumberFormatException
    {
        // Locate start of token, i.e. end of the number in "+-1234 token".
        int end = locateTokenPiece(token, text);
        // Nothing found?
        if (end < 0)
            return 0;
        // Skip optional space between the token and the number
        while (end > 0  &&  text.charAt(end-1) == ' ')
            -- end;
        int start = end;
        // Locate start of the number
        while (start > 0  &&
               NUMBER_CHARS.indexOf(text.charAt(start-1)) >= 0)
            -- start;
        // Parse the number
        final String number = text.substring(start, end);
        return Integer.parseInt(number);
    }        
    
    /** Locate position of token in text, also recognizing abbreviated token.
     *  @return Location or -1.
     */
    public static int locateTokenPiece(final String token, final String text)
    {
        final String lc_text = text.toLowerCase();
        
        // Try the full token, case-insensitive,
        // then smaller pieces of the token,
        // until we finally try just the first character.
        for (int len=token.length(); len > 0; --len)
        {
            String token_piece = token.substring(0, len);
            String cooked_text = len > 1 ? lc_text : text;
            // Compare case-insensitive, unless we're down to
            // the first char, where we keep Sergei's convention.
            if (len > 1)
                token_piece = token_piece.toLowerCase();
            // Locate the token, which would mark the _end_ of the number
            int start = cooked_text.indexOf(token_piece);
            // Nothing found? Try smaller piece of token.
            if (start < 0)
                continue;
            // Found what looks like the token piece, but we only care if 
            // it's the start of a word. We don't want to accidentally
            // recognize the final 's' in "Months" as the start of "seconds".
            if (start > 0)
            {   // Char before 'start' must be a space or a number.
                char before = cooked_text.charAt(start-1);
                if (! (before == ' '  ||  NUMBER_CHARS.indexOf(before) >= 0))
                    continue;
            }
            // Assert that it's not the "m" of "minutes" in "2 month".
            // Locate the end of what we found...
            int end = start+1;
            while (end < cooked_text.length() &&
                   cooked_text.charAt(end) != ' ')
                ++end;
            // ... and compare the whole thing
            final String found = cooked_text.substring(start, end);
            if (! found.equals(token_piece))
                continue;
            return start;
        }
        // nothing found.
        return -1;
    }
}
