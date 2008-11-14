
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.ams.connector.voicemail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.ams.Log;

/**
 *  @author Markus Moeller
 *
 */
public class TextWorker
{
    private String text = null;
    private StringBuffer newText = null;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private SimpleDateFormat df = new SimpleDateFormat("dd. MMMMM yyyy HH:mm:ss");

    private static final String regEx = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}";

    public TextWorker(String text)
    {
        this.text = text;
        
        workOnText();
    }
    
    private void workOnText()
    {
        newText = new StringBuffer();
        
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        
        Date d = null;
        
        int begin = 0;
        int start = 0;
        int ende = text.length();
        
        while(m.find(start))
        {
            start = m.start();
            ende = m.end();
            
            /*System.out.println("Start: " + start);
            System.out.println("Ende:  " + ende);
            System.out.println("Part:  [" + text.substring(m.start(), m.end()) + "]");
            */
            
            newText.append(text.substring(begin, start));
            
            try
            {
                d = sdf.parse(text.substring(m.start(), m.end()));

                newText.append(df.format(d) + " Uhr");
            }
            catch(ParseException e)
            {
                Log.log(this, Log.ERROR, " *** ParseException *** : " + e.getMessage());
                
                newText.append("Übersetzungsfehler. ");
            }
            
            begin = ende;
            start = m.end();
        }
        
        newText.append(text.substring(begin));
    }
    
    public String getText()
    {
        if(newText != null)
        {
            return newText.toString();
        }
        else
        {
            return null;
        }
    }
}
