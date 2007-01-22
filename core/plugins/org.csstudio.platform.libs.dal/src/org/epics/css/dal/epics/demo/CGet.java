/**
 * 
 */
package org.epics.css.dal.epics.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.Plugs;
import org.epics.css.dal.spi.PropertyFactory;

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
	final static String HELP="*** Channel Getter *** \nType one of following options:\nh - this help\ni - information about connection status\ns - change current plug to system default plug\ns <plug> - change plug to <plug> (Simulator or EPICS so far)\nd - disconnect any connected channel\nc <name> - connects to channel <name>\ng - gets latest value\np <value> - puts value\nm - registeres monitor until <enter> key is pressed\nx or q - exit";
	String plug = "Simulator";
	
	DynamicValueListener listener= new DynamicValueAdapter() {
	
		@Override
		public void valueChanged(DynamicValueEvent event) {
			out(f.format(new Date(event.getTimestamp().getMilliseconds()))+' '+event.getValue()+' '+event.getCondition().getStates().toString());
		}
		@Override
		public void valueUpdated(DynamicValueEvent event) {
			out(f.format(new Date(event.getTimestamp().getMilliseconds()))+' '+event.getValue()+' '+event.getCondition().getStates().toString());
		}
	
	};
	

	public static void main(String[] args) {
		CGet ex= new CGet();
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
	
	private void out(String s) {
		System.out.print("< ");
		System.out.println(s);
	}
	
	private String read() {
		System.out.print("> ");
		try {
			return in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		ctx= new EPICSApplicationContext("ChannelExplorer");
		factory= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY);
		
		
		boolean alive=true;
		
		printHelp();
		
		while (alive) {
			try {
				String s= read();
				
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
								StringBuffer str = new StringBuffer(64);
								str.append("[");
								for (int i = 0; i < len; i++) {
									str.append(Array.get(value, i));
									if (i < len-1)
										str.append(",");
								}
								str.append("]");
								value = str;
							}
							out(value.toString());
						} catch (Exception e) {
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
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void connect(String s) {
		if (prop!=null) {
			out("Disconnected from "+prop.getName());
			factory.getPropertyFamily().destroy(prop);
			prop=null;
		}
		
		if (s==null || s.length()==0) {
			return;
		}
		
		try {
			prop= (DynamicValueProperty)factory.getProperty(s);
			out("Connected to "+s+".");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printHelp() {
		out(HELP);
	}
	
	private void setPlug(String p) {
		
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
