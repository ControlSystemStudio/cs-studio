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
 package org.csstudio.utility.tine.ui.test;
import java.awt.Component;

public class TineNameSpaceBrowser extends NameSpaceBrowser {

    public final static String COLS[] = {"Contexts","Servers",
                                         "Devices","Properties"};
    public final static String TITLE = "TINE";
    public final static int CONTEXT = 0;
    public final static int SERVERS = 1;
    public final static int DEVICES = 2;
    public final static int PROPERTIES = 3;

    TineDataSource tds;

    public TineNameSpaceBrowser(Component calledFrom, TineDataSource tds) {
        super(calledFrom, TITLE, COLS);
        this.tds = tds;
        this.calledFrom = calledFrom;
        //init the first column
        setColumn(0);
    }
    @Override
    public void setColumn(int i) {
        String[] s = null;
        switch(i) {
            case CONTEXT:
                s = tds.getContexts();
                break;
            case SERVERS:
                s = tds.getDeviceServers(getSelectedItemAt(CONTEXT));
                break;
            case DEVICES:
                s = tds.getDevices(getSelectedItemAt(CONTEXT),getSelectedItemAt(SERVERS));
                break;
            case PROPERTIES:
                s = tds.getDeviceProperties(getSelectedItemAt(CONTEXT),
                                getSelectedItemAt(SERVERS), getSelectedItemAt(DEVICES));
                break;
            default:
                setSelectedName(createName());
                return;
        }
        setList(i, s);
        /*if(s == null) {
            s = new String[1];
            s[0] = NA;
        }
        getLists()[i].setListData(s);*/
    }

}