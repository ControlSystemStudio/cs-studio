/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.tine.reader;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.desy.tine.queryUtils.TQuery;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 15.05.2007
 */
public class TineReader extends Job {

    private static final String TINE_PATH_SEP = ",";

    private static final Logger LOG = LoggerFactory.getLogger(TineReader.class);
    /** The name of {@link ControlSystemItem}.*/
    private String _simpleName;
    /** List with the read elements. */
    private TineSearchResult _resultList;
    /** Not used !? */
    private String _type;

    /**
     *
     * @param name The name of {@link ControlSystemItem}
     * @param type not used !?
     * @param result the list to put the result.
     */
    public TineReader(final String name, final String type, final TineSearchResult result) {
        super(name);
        if(name != null){
            final String[] splittedName = name.split(TINE_PATH_SEP);
            _simpleName = splittedName[splittedName.length - 1];
        }else{
            _simpleName = "";
        }
        if (type != null) {
            _type = type;
        }else {
            _type= "";
        }
        if(result!=null){
            _resultList = result;
        }else{
            _resultList = new TineSearchResult();
        }
        LOG.debug("name:"+_simpleName);
        LOG.debug("Type:"+_type);
    }

    /** {@inheritDoc}*/
    @Override
    protected final IStatus run(final IProgressMonitor monitor) {
        read();
        return Status.OK_STATUS;
    }

    /**
     * Read the data from Tine Name Server.
     */
    public final void read(){
        List<ControlSystemItem> csi = new ArrayList<ControlSystemItem>();
        String[] content;
        String[] path = _type.split(TINE_PATH_SEP);
        LOG.debug("Name: '"+_simpleName+"'\t Type: '"+_type+"'\t länge: "+path.length);
        switch (path.length) {
            case 1:
                content = TQuery.getContexts();
                break;
            case 2:
                content = TQuery.getDeviceSubsystems(_simpleName);
                break;
            case 3:
                content = TQuery.getDeviceServers(path[path.length-2],path[path.length-1]);
                break;
            case 4:
                LOG.debug("read: "+path[path.length-3]+TINE_PATH_SEP+path[path.length-1]);
                content = TQuery.getDeviceNames(path[path.length-3],path[path.length-1]);
                break;
            case 5:
                LOG.debug("read Properties: "+path[path.length-4]+TINE_PATH_SEP+path[path.length-2]+TINE_PATH_SEP+path[path.length-1]);
                content = TQuery.getDeviceProperties(path[path.length-4],path[path.length-2],path[path.length-1]);
                break;
            default:
                content = null;
        }

        String tine = ControlSystemEnum.DAL_TINE.getPrefix()+"://";

        if (content == null) {
            LOG.warn("No Elements found");
            csi = Collections.<ControlSystemItem>emptyList();
        } else if(path.length==4 || path.length==5) {
            for (final String string : content) {
                String name = tine;
                String[] pathPV;
                if(_type.endsWith(TINE_PATH_SEP)||string.startsWith(TINE_PATH_SEP)){
                    pathPV = (_type+string).split(TINE_PATH_SEP);
                }else{
                    pathPV = (_type+TINE_PATH_SEP+string).split(TINE_PATH_SEP);
                }
                if(pathPV.length>1){
                    for (int i = 0; i < pathPV.length; i++) {
                        if(i!=0&&i!=2){
                            name = name.concat(pathPV[i]);
                            name = name.concat("/");
                        }
                    }
                }else{
                    name = string;
                }
                if(name.endsWith("/")){
                    name = name.substring(0, name.length()-1);
                }
              //TODO jhatje: implement new datatypes

                if(_type.endsWith(TINE_PATH_SEP) || string.startsWith(TINE_PATH_SEP)){
//                    csi.add(new ProcessVariable(name, _type + string));
                }else{
//                    csi.add(new ProcessVariable(name, _type + TINE_PATH_SEP + string));
                }
            }
        }else if(path.length<4){
            LOG.debug("content.length = {}", content.length);
            for (final String part : content) {
                if(_type.endsWith(TINE_PATH_SEP) || part.startsWith(TINE_PATH_SEP)){
                    csi.add(new ControlSystemItem(part, _type + part));
                }else{
                    csi.add(new ControlSystemItem(part, _type + TINE_PATH_SEP + part));
                }
            }
        }
        _resultList.setCSIResultList(csi);
        tine=null;
        content=null;
        path=null;
        csi = null;
    }
}
