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
 */
 package org.csstudio.sds.util;

import java.util.Map;


/**
 * @author Kai Meyer, Helge Rickens
 *
 * @deprecated use {@link ChannelReferenceValidationUtil#createCanonicalName(String, Map)} instead
 *
 */
public class AliasUtil {

    /**
     * Returns the resolved process variable.
     * @param pvKey An alias
     * @param aliases The map of known aliases
     * @return The resolves process variable
     *
     * @deprecated use {@link ChannelReferenceValidationUtil#createCanonicalName(String, Map)} instead
     */
    public static String getAliasName(final String pvKey, final Map<String, String> aliases) {
        if (pvKey!=null && aliases!=null) {
            String text=pvKey;
            String prefix ="";
            String key="";
            String postfix ="";
            while(text.contains("$")){
                int start = pvKey.indexOf("$");
                prefix = prefix.concat(pvKey.substring(0,start++));
                int end = pvKey.indexOf("$",start);
                key = pvKey.substring(start,end++);
                postfix = pvKey.substring(end).concat(postfix);
                if (aliases.containsKey(key)) {
                    text= aliases.get(key);
                }else{
                    break;
                }
            }
            return prefix+text+postfix;
        }
        return pvKey;
    }

}
