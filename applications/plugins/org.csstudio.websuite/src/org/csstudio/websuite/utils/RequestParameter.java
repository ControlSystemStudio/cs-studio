
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.websuite.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 14.10.2010
 */
public class RequestParameter {
    
    /** The parameters of the request */
    private HashMap<String, String> parameters;
    
    /**
     * 
     * @param request
     */
    public RequestParameter(HttpServletRequest request) {
        
        parameters = new HashMap<String, String>();
        init(request);
    }
    
    /**
     * 
     * @param request
     */
    private void init(HttpServletRequest request) {
    
       Enumeration<?> p = request.getParameterNames();
       String name = null;
       String value = null;
       
       while(p.hasMoreElements()) {
           
           name = (String)p.nextElement();
           value = request.getParameter(name);
           value = (value == null) ? "" : value.trim();
           parameters.put(name, value);
       }
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public boolean containsParameter(String name) {
        
        boolean result = false;
        
        if(name != null) {
            result = parameters.containsKey(name);
        }
        
        return result;
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public boolean hasParameterAnyValue(String name) {
        
        boolean result = false;
        
        if(parameters.containsKey(name)) {
            
            // A parameter without value contains just an empty string
            String value = parameters.get(name);
            result = (value.length() > 0);
        }
        
        return result;
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }
    
    /**
     * Returns all parameters that names has this format:
     * key_name.n
     * 
     * @param index
     * @return
     */
    public HashMap<String, String> getParameterByKeyIndex(int index) {
        
        HashMap<String, String> resultMap = new HashMap<String, String>();
        String key = null;
        
        Set<String> keys = parameters.keySet();
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext()) {
        
            key = iter.next();
            if(key.contains("." + index)) {
                resultMap.put(key, parameters.get(key));
            }
        }
        
        return resultMap;
    }
}
