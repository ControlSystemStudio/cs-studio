
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

package de.desy.aapi;

/**
 * TODO (mmoeller) : 
 * 
 * @author Markus Moeller
 * @version Archive Protocol V2.4
 * @since 06.12.2010
 */
public enum AapiCommand {
    
    /** Command: Invalid */
    INVALID_CMD(0),

    /** Command: Version */
    VERSION_CMD(1),

    /** Command: DataRequest */
    DATA_REQUEST_CMD(2),

    /** Command: ChannelInfo */
    CHANNEL_INFO_CMD(3),

    /** Command: ChannelList */
    CHANNEL_LIST_CMD(4),

    /** Command: HierarchyChannelList */
    HIERARCHY_CHANNEL_LIST_CMD(5),

    /** Command: ReductionAlgorithmsNameList (start in version 2.1) */
    FILTER_LIST_CMD(6),
    
    /** Command: RegExpChannelList */
    REGEXP_LIST_CMD(7),

    /** Command: HierarchySkeleton */
    HIERARCHY_SKELETON_CMD(8);

    /** Number of command */
    private final int commandNumber;
    
    /**
     * 
     * @param commandNumber
     */
    private AapiCommand(int commandNumber) {
        
        this.commandNumber = commandNumber;
    }
    
    /**
     * 
     * @return
     */
    public int getCommandNumber() {
        
        return commandNumber;
    }
    
    /**
     * 
     * @return
     */
    public int getMaxCommandNumber() {
        
        int result = 0;
        
        for(AapiCommand o : AapiCommand.values()) {
            
            if(o.getCommandNumber() >= result) {
                
                result = o.getCommandNumber();
            }
        }
        
        return result;
    }
}
