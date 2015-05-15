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
package org.csstudio.utility.tine.ui.test_SineClient;

import de.desy.tine.client.*;
import de.desy.tine.dataUtils.*;
import de.desy.tine.definitions.*;
import de.desy.tine.queryUtils.*;
import de.desy.tine.structUtils.*;
import de.desy.tine.types.*;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 10.05.2007
 */
public class SineClient {
    private static String devSrvName = new String("/TEST/SINE");
    private static int size = 1024;
    private float[] sdat = new float[size];
    private float[] refdat = new float[size];

    public class cdSineClient implements TLinkCallback
    {
      public void callback(TLink lnk)
      {
        if (lnk.getLinkStatus() != 0)
        {
          System.out.println("Link Error : " + lnk.getLastError());
          return;
        }
        for (int i=0; i<size && i<10; i++) System.out.print("" + (sdat[i] - refdat[i]));
        System.out.println("");
      }
    }


    /**
     * @param sdat
     * @param refdat
     */
    public SineClient() {
        super();
        initialize();
        try
        {
          Thread.currentThread().sleep(10000);
        }
        catch (InterruptedException e) {};

        System.exit(0);
    }

    public int initialize()
    {
      int cc = 0;
      String devname = new String(devSrvName);
      devname = devname.concat("/#1");
      TDataType dout = new TDataType(refdat);
      // get a reference array : synchronous call ...
      TLink ref = new TLink(devname,"SINE",dout,null,TAccess.CA_READ);
      if ((cc=ref.execute()) != 0)
      {
        System.out.println("get reference : " + ref.getLinkStatus());
      }
      ref.cancel();
      dout = new TDataType(sdat);
      TLink sin = new TLink(devname,"SINE",dout,null,TAccess.CA_READ);
      cdSineClient showsine = new cdSineClient();
      if (sin.attach(TMode.CM_POLL, showsine, 1000) < 0)
      {
        System.out.println("attach : " + sin.getLinkStatus());
      }
      return cc;
    }

    public static void main(String[] args)
    {

    }
}
