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
package org.csstudio.diag.rmtcontrol.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.diag.rmtcontrol.Activator;
import org.csstudio.diag.rmtcontrol.Messages;
import org.csstudio.diag.rmtcontrol.Preference.SampleService;
import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class View extends ViewPart implements Observer {

	private Text ioc1;
	private Text ioc2;
	StyledText request;
	StyledText answer;
	String requestMassage;
	IOCAnswer iocAnswer;
	Display disp;
	Element root;

	private Group befehl;

	public View() {
		iocAnswer = new IOCAnswer();
		iocAnswer.addObserver(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		disp = parent.getDisplay();
		/**********************************************************************
		 * Layout
		 **********************************************************************/
		// Parent
		parent.setLayout(new GridLayout(1,false));
		// -Menu
		Composite menu = new Composite(parent,SWT.NONE);
		menu.setLayout(new GridLayout(2, false));
		menu.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		// --IOC1
		final Button iocButton1 = new Button(menu, SWT.RADIO);
		iocButton1.setText(Messages.getString("View.0"));
		iocButton1.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1));
		iocButton1.setSelection(true);
		ioc1 = new Text(menu, SWT.BORDER);
		ioc1.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		ioc1.setText(Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS1)); //$NON-NLS-1$
		// --IOC2
		Button iocButton2 = new Button(menu, SWT.RADIO);
		iocButton2.setText(Messages.getString("View.1"));
		iocButton2.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1));
		ioc2 = new Text(menu, SWT.BORDER);
		ioc2.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		ioc2.setText(Activator.getDefault().getPluginPreferences().getString(SampleService.IOC_ADDRESS2)); //$NON-NLS-1$

		// BefehlsGroup
		befehl = new Group(menu,SWT.NONE);
		makeOrderGroup(menu);
		Button start = new Button(menu,SWT.PUSH);
		start.setText(Messages.getString("View.3")); //$NON-NLS-1$
		start.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,false,2,1));

		Composite protocol = new Composite(parent,SWT.NONE);
		protocol.setLayout(new GridLayout(1, false));
		protocol.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		Group labelRequest = new Group(protocol,SWT.NONE);
		labelRequest.setText(Messages.getString("View.4")); //$NON-NLS-1$
		GridData gridData = new GridData(SWT.FILL,SWT.TOP,true,false,1,1);
		gridData.minimumWidth=250;
	    labelRequest.setLayoutData(gridData);
	    labelRequest.setLayout(new GridLayout(1,false));
		request = new StyledText(labelRequest,SWT.MULTI | SWT.V_SCROLL);
		request.setWordWrap(true);
		gridData = new GridData(SWT.FILL,SWT.TOP,true,false,1,1);
	    gridData.heightHint = 100;
		gridData.minimumWidth=200;
	    request.setLayoutData(gridData);

		Group labelAnswer = new Group(protocol,SWT.NONE);
	    labelAnswer.setText(Messages.getString("View.5")); //$NON-NLS-1$
	    gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
	    gridData.minimumHeight = 100;
		gridData.minimumWidth = 250;
	    labelAnswer.setLayoutData(gridData);
	    labelAnswer.setLayout(new GridLayout(1,false));
		answer = new StyledText(labelAnswer,SWT.MULTI | SWT.V_SCROLL);
		answer.setWordWrap(true);
		gridData = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
	    gridData.minimumHeight = 100;
		gridData.minimumWidth = 200;
	    answer.setLayoutData(gridData);
		start.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				answer.setText(""); //$NON-NLS-1$
				RMTControl iocContr = RMTControl.getInstance();
				if(iocButton1.getSelection())
					iocContr.send(ioc1.getText(), request.getText(), iocAnswer);
				else
					iocContr.send(ioc2.getText(), request.getText(), iocAnswer);
			}

		});

		answer.addLineStyleListener(new LineStyleListener(){

			public void lineGetStyle(LineStyleEvent event) {
				// TODO Auto-generated method stub
//				System.out.println("----------------------------------------------------");
//				System.out.println("Test LineStyleListener");
//				System.out.println("Text: "+event.lineText);
//				System.out.println("alig: "+event.alignment);
//				System.out.println("bullet: "+event.bulletIndex);
//				System.out.println("indent: "+event.indent);
//				System.out.println("lineOffset: "+event.lineOffset);
//				System.out.println("time: "+event.time);
//				System.out.println("range: "+event.ranges);
//				System.out.println("styles: "+event.styles);
//				StyleRange sr = new StyleRange(event.lineOffset,event.lineText.length(),new Color(answer.getDisplay(),100,200,100),new Color(answer.getDisplay(),200,100,200));
//				answer.setLineBackground(event.lineOffset-1,0,new Color(answer.getDisplay(),100,200,100));
//				answer.setStyleRange(sr);
//				event.
			}

		});

	}

	void makeOrderGroup(Composite menu) {
		final HashMap<String, Element> befehlsliste = new HashMap<String, Element>();
		String[] befehlsreihenfolge = null;
		/***********************************/
		File inputFile = new File(Activator.getDefault().getPluginPreferences().getString(SampleService.RMT_XML_FILE_PATH));
		if(!inputFile.isFile()){
			System.out.println(Messages.getString("View.WrongFile")+inputFile); //$NON-NLS-1$
		}
		SAXBuilder saxb = new SAXBuilder(false);
		Document befehlsDoc;// = new Document();
		try {
			befehlsDoc = saxb.build(inputFile);
			root = befehlsDoc.getRootElement();
//			if(checkVerion(root.getAttribute("version")){//TODO: Versionkontrolle einfüren}
			List befehlsListe = root.getChildren();
			Iterator befehleIte = befehlsListe.iterator();
			befehlsreihenfolge = new String[befehlsListe.size()];
			int i=0;
			while(befehleIte.hasNext()){
				Element elm = (Element) befehleIte.next();
				befehlsreihenfolge[i++]=elm.getAttribute("name").getValue(); //$NON-NLS-1$
				befehlsliste.put(elm.getAttribute("name").getValue(),elm.getChild("Root")); //$NON-NLS-1$ //$NON-NLS-2$
			}

		} catch (JDOMException e1) {
			Activator.logException(Messages.getString("View.ExceptionJDOM"), e1); //$NON-NLS-1$
		} catch (IOException e1) {
			Activator.logException(Messages.getString("View.ExceptionIO"), e1); //$NON-NLS-1$
		}

		//-----------------------------------------------------------
		befehl.setText(Messages.getString("View.list")); //$NON-NLS-1$
		Control[] child = befehl.getChildren();
		for (Control control : child) {
			control.dispose();
		}
		RowLayout fl = new RowLayout(SWT.HORIZONTAL);
		fl.wrap=true;
		fl.fill=false;
		fl.justify=false;
		fl.pack=false;
		fl.marginRight=0;
		befehl.setLayout(fl);
		befehl.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,2,1));
		for (String name : befehlsreihenfolge) {
			final Button button = new Button (befehl, SWT.RADIO);
			button.setText (name);
			button.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {}

				public void widgetSelected(SelectionEvent e) {
					Element ele = befehlsliste.get(button.getText());
					XMLOutputter outp = new XMLOutputter();
					requestMassage=outp.outputString(ele);
					request.setText(requestMassage);
				}

			});
		}
		final Button button = new Button (befehl, SWT.RADIO);
		button.setText (Messages.getString("View.vari")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				System.out.println(e);
				System.out.println(button.getText());
				request.setText(""); //$NON-NLS-1$
			}

		});
		button.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				request.setEditable(true);
			}

		});

	}

	@Override
	public void setFocus() {	}

	public void update(Observable arg0, Object arg1) {
		disp.syncExec(new Runnable() {
			public void run() {
				String text = iocAnswer.getAnswer();
//				System.out.println("run");
//				StyleRange tmp[] = answer.getStyleRanges();
//				StyleRange style[] = new StyleRange[tmp.length+1];
//				for(int i=0;i<tmp.length;i++){
//					style[i]=tmp[i];
//				}
//				style[0] = new StyleRange(style[0].start+style[0].length, text.length(), answer.getDisplay().getSystemColor(SWT.COLOR_BLUE), answer.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
//
//				String tmp = iocAnswer.getAnswer();
//				StyleRange sr = new StyleRange(answer.getCharCount()-1,tmp.length(),new Color(answer.getDisplay(),100,200,100),new Color(answer.getDisplay(),200,100,200));
				answer.setText(text);
//				answer.setStyleRanges(style);

				answer.getParent().layout();
			}
		});
	}

}
