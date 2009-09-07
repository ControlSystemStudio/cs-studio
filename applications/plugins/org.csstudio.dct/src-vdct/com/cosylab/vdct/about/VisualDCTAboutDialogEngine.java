package com.cosylab.vdct.about;

import java.awt.Component;
import javax.swing.JFrame;

/**
 * @author administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class VisualDCTAboutDialogEngine extends AboutDialogEngine {

 

	/**
	 * Constructor for DefaultAboutDialogEngine.
	 * @param toAbout
	 */
	public VisualDCTAboutDialogEngine(Object toAbout) {
		super(toAbout);
	}

	/**
	 * @see com.cosylab.gui.components.about.AboutDialogEngine#initializeReceiver()
	 */
	protected void initializeReceiver() {

		if (getAboutedObject() instanceof javax.swing.JFrame) {

			javax.swing.JFrame jf = (javax.swing.JFrame) aboutedObject;

			receiver = (new AboutDialog(jf, true));

			instanceOfJFrame(jf);

		}

		else if (getAboutedObject() instanceof Component) {

			Component c = (Component) getAboutedObject();
			while (c.getParent() != null) {
				c = c.getParent();
			}
			//receiver = new AboutDialog(c, true);
			if (c instanceof JFrame) {
				javax.swing.JFrame jf = (javax.swing.JFrame) c;
				receiver = (new AboutDialog(jf, true));
				instanceOfJFrame(jf);
			
			} else instanceOfComponent(c);
			
		}

		else {
			receiver = new AboutDialog();
		}
	} 

	/**
	 * @see com.cosylab.gui.components.about.AboutDialogEngine#perform()
	 */
	protected void perform() {
	
	aquireDefaultTabs();
	arrangeTabs();
	
	
	}
	
	protected void aquireDefaultTabs(){
	
	ProgramTabPanel ptp = new ProgramTabPanel(new VisualDCTProgramTabModel(aboutedObject));
	
	receiver.setTitle("About " + ptp.getTitle());
	
	addAboutTab(ptp);
	addAboutTab(new LicenseTabPanel(new VisualDCTLicenseTabModel(aboutedObject)));
	addAboutTab(new SystemTabPanel(new VisualDCTSystemTabModel()));
	
	}
	
	protected void instanceOfJFrame(javax.swing.JFrame jf) {

		int x = (int) (jf.getX() + 0.5 * jf.getWidth() - 0.5 * ((AboutDialog)receiver).getWidth());
		int y = (int) (jf.getY() + 0.5 * jf.getHeight() - 0.5 * ((AboutDialog)receiver).getHeight());

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;

		
		((AboutDialog)receiver).setBounds(x,y,((AboutDialog)receiver).getWidth(),((AboutDialog)receiver).getHeight());

	}
	protected void instanceOfComponent(Component c) {

		int x = (int) (c.getX() + 0.5 * c.getWidth() - 0.5 * ((AboutDialog)receiver).getWidth());
		int y = (int) (c.getY() + 0.5 * c.getHeight() - 0.5 * ((AboutDialog)receiver).getHeight());

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;

		
		((AboutDialog)receiver).setBounds(x,y,((AboutDialog)receiver).getWidth(),((AboutDialog)receiver).getHeight());

	}
}
