/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

/**
 *
 */
package org.csstudio.dal.epics.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.dal.DynamicValueAdapter;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.epics.EPICSApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.Plugs;
import org.csstudio.dal.spi.PropertyFactory;

/**
 * @author ikriznar
 *
 */
public class CGet implements Runnable {

	PropertyFactory factory;
	EPICSApplicationContext ctx;
	DynamicValueProperty prop;
	BufferedReader in;
	SimpleDateFormat f= new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSS");
	final static String HELP="*** Channel Getter *** \nType one of following options:\nh - this help\ni - information about connection status\ns - change current plug to system default plug\ns <plug> - change plug to <plug> (Simulator or EPICS so far)\nc - disconnect any connected channel\nc <name> - connects to channel <name>\ng - gets latest value\np <value> - puts value\nm - registeres monitor until <enter> key is pressed\nx or q - exit";
	String plug = "Simulator";

	DynamicValueListener listener= new DynamicValueAdapter() {

		@Override
		public void valueChanged(final DynamicValueEvent event) {
			out(f.format(new Date(event.getTimestamp().getMilliseconds()))+' '+event.getValue()+' '+event.getCondition().getStates().toString());
		}
		@Override
		public void valueUpdated(final DynamicValueEvent event) {
			out(f.format(new Date(event.getTimestamp().getMilliseconds()))+' '+event.getValue()+' '+event.getCondition().getStates().toString());
		}

	};


	public static void main(final String[] args) {
		final CGet ex= new CGet();
		ex.run();
		System.exit(0);
	}

	/**
	 *
	 */
	public CGet() {
		super();
		in= new BufferedReader(new InputStreamReader(System.in),128);
	}

	private void out(final String s) {
		System.out.print("< ");
		System.out.println(s);
	}

	private String read() {
		System.out.print("> ");
		try {
			return in.readLine();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
    public void run() {
		ctx= new EPICSApplicationContext("ChannelExplorer");
		factory= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY);


		boolean alive=true;

		printHelp();

		while (alive) {
			try {
				final String s= read();

				if (prop!=null) {
					prop.removeDynamicValueListener(listener);
				}

				if ("h".equals(s) || s==null) {
					printHelp();
				} else if ("q".equals(s) || "x".equals(s)) {
					out("Closing.");
					alive=false;
				} else if ("i".equals(s)) {
					out("current_plug: "+factory.getPlugType());
					out("default_plug: "+ctx.getConfiguration().getProperty(Plugs.PLUGS_DEFAULT));
					out("available_plugs: "+ctx.getConfiguration().getProperty(Plugs.PLUGS));
					if (prop!=null) {
						out("channel: "+prop.getUniqueName());
						out("data_type: "+prop.getDataType().getName());
					} else {
						out("channel: not connected");
					}
				} else if ("m".equals(s)) {
					if (prop==null) {
						out("Not connected!");
					} else {
						prop.addDynamicValueListener(listener);
					}
				} else if ("g".equals(s)) {
					if (prop==null) {
						out("Not connected!");
					} else {
						try {
							Object value = prop.getValue();
							if (value.getClass().isArray()) {
								final int len = Array.getLength(value);
								final StringBuffer str = new StringBuffer(64);
								str.append("[");
								for (int i = 0; i < len; i++) {
									str.append(Array.get(value, i));
									if (i < len-1) {
                                        str.append(",");
                                    }
								}
								str.append("]");
								value = str;
							}
							out(value.toString());
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				} else if (s.startsWith("c")) {
					if (s.length()>2) {
						connect(s.substring(2).trim());
					} else {
						connect(null);
					}
				} else if (s.startsWith("s")) {
					if (s.length()>2) {
						setPlug(s.substring(2).trim());
					} else {
						setPlug(null);
					}
				} else if (s.startsWith("p")) {
					if (prop==null) {
						out("Not connected!");
					} else {
						out("not supported");
					}
				} else {
					printHelp();
				}

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void connect(final String s) {
		if (prop!=null) {
			out("Disconnected from "+prop.getName());
			factory.getPropertyFamily().destroy(prop);
			prop=null;
		}

		if (s==null || s.length()==0) {
			return;
		}

		try {
			prop= factory.getProperty(s);
			out("Connected to "+s+".");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void printHelp() {
		out(HELP);
	}

	private void setPlug(final String p) {

		connect(null);

		if (factory!= null) {
			factory.getPropertyFamily().destroyAll();
		}

		if (p!=null) {
			factory= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY,p);
		} else {
			factory= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY);
		}

		out("Now using plug "+factory.getPlugType());

	}

}
