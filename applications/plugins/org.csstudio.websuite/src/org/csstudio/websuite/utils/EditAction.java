
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

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 03.11.2010
 */
public enum EditAction {
    
    ACTION_INVALID("invalid"),
    ACTION_EDIT("edit"),
    ACTION_ADD("add"),
    ACTION_NEW("new"),
    ACTION_DELETE("delete"),
    ACTION_OK("ok"),
    ACTION_CANCEL("cancel"),
    ACTION_PROCESS("process");
    
    /** */
    private String name;
    
    /**
     * 
     * @param name
     */
    private EditAction(String n) {
        this.name = n;
    }
    
    /**
     * 
     * @return String that contains the name of the action
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * 
     * @param n
     * @return The EditAction that matches the given name
     */
    public static EditAction getByName(String n) {
        
        EditAction result = ACTION_INVALID;
        
        for(EditAction o : EditAction.values()) {
        
            if(o.getName().compareToIgnoreCase(n) == 0) {
                result = o;
                break;
            }
        }
        
        return result;
    }
}
