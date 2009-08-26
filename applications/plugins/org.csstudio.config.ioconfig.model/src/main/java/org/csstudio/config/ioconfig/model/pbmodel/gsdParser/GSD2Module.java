/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.HashMap;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.12.2007
 */
public final class GSD2Module {
    
    /**
     * Default Constructor.
     */
    private GSD2Module(){
        
    }

    /**
     * @param file The GSD-File from which you get the Modules. 
     * @param slaveModel The parent SlaveModel of the Modules. 
     * @return A List of all Modules from this GSD-File.
     */
    public static HashMap<Integer, GsdModuleModel> parse(final String file, final GsdSlaveModel slaveModel) {
        HashMap<Integer, GsdModuleModel> gsdPartModules = new HashMap<Integer, GsdModuleModel>();
        String[] lines = file.split("\r\n");
        boolean foundModule = false;
        boolean followModule = false;
        boolean followExtUserPrmDataConst = false;
        GsdModuleModel gSDModule = null;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if(line.length()<1||line.startsWith(";")) {
                continue;
            }else if(followExtUserPrmDataConst){
                gSDModule.addtExtUserPrmDataConst(line);
                if(!line.endsWith("\\")){
                    followExtUserPrmDataConst = false;
                }
            }else if(followModule){
                gSDModule.addValue(line);
                if(!line.endsWith("\\")){
                    followModule=false;
                }
            }else if(line.startsWith("Module")){
                foundModule=true;
                followModule = false;
                gSDModule = new GsdModuleModel(line, slaveModel);
                if(line.endsWith("\\")){
                    followModule=true;
                }
            }else if(line.trim().equals("EndModule")){
                foundModule=false;
                gsdPartModules.put(gSDModule.getModuleNumber(),gSDModule);
            // Auswertung weiterer Parameter
            }else if(foundModule){
//                  Don't needed.                
//                if(line.startsWith("Ext_Module_Prm_Data_Len")){
//                }else
                if(line.startsWith("Ext_User_Prm_Data_Const")){
                    String[] parts = line.split("=");
                    if(parts.length>1){
                        String value = parts[1].trim();
                        gSDModule.setExtUserPrmDataConst(value);
                    }
                    if(line.endsWith("\\")){
                        followExtUserPrmDataConst = true;
                    }
                }else if(line.startsWith("Ext_User_Prm_Data_Ref")){
                    String[] parts = line.split("[\\(,\\)]");
                    String index = "0";
                    if(parts.length>1){
                        index = parts[1];
                    }
                    parts = line.split("=");
                    if(parts.length>1){
                        String value = parts[1].trim();
                        gSDModule.addExtUserPrmDataRef(index, value);
                    }
                }else{ // Module Number
                    gSDModule.setModuleNumber(line);
                }
            }
        }
        return gsdPartModules;
    }

}
