/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.gui;

import org.csstudio.apputil.ui.swt.ScrolledContainerHelper;
import org.csstudio.diag.rack.model.RackModel;
import org.csstudio.diag.rack.model.RackModelListener;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("nls")
public class GUI implements RackModelListener

{

    private RackModel rackControl;
    private Display display = Display.getDefault();

    /** Sash for the two GUI sub-sections divided by the moving bar */
    private SashForm form;
    private Button slotPosButton;
    private int form_weights[] = new int[] { 20,80 };
    int[] depth_points = { 0,0 };

    /** GUI Elements */
    private Text dvcOrPVEntry, rackIdFilterEntry;
    public List rackList, dvcIdList;
    public Group paintGroup;
    private TableViewer rack_list_table;
    public Canvas paintCanvas;
	final Color white_color = display.getSystemColor (SWT.COLOR_WHITE);
	final Color empty_color = display.getSystemColor (SWT.COLOR_WIDGET_LIGHT_SHADOW);
	final Color device_color = display.getSystemColor (SWT.COLOR_DARK_GRAY);
	private int rackHeight;


    public GUI(Composite shell, final RackModel control)
    {
    	this.rackControl = control;
    	this.rackHeight = rackControl.getRackHeight();

        shell.setLayout(new FillLayout());

        // Split into upper and lower sash
        form = new SashForm(shell, SWT.VERTICAL | SWT.BORDER);
        form.setLayout(new FillLayout());
        createTopSash(form);
        //createMiddleSash(form);
        createBottomSash(form);
        form.setWeights(form_weights);
        hookListeners();
    }

    /** Create the left sash: The Rack list */
    private void createTopSash(final SashForm form)
    {
        Composite RacksListContents = new Composite(form, SWT.NULL);
        Composite ListContents = new Composite(RacksListContents, SWT.NULL);
        Composite DeviceContents = new Composite(RacksListContents, SWT.NULL);

        GridLayout layout = new GridLayout();
        RacksListContents.setLayout(layout);
        GridData gd = new GridData();
        layout.numColumns = 2;
        gd.horizontalSpan = layout.numColumns;
        RacksListContents.setLayoutData(gd);

        layout = new GridLayout();
        ListContents.setLayout(layout);
        gd = new GridData(GridData.FILL_VERTICAL);
        ListContents.setLayoutData(gd);

        Label l = new Label(ListContents, 0);
        l.setText("Rack List (Filter):");
        gd = new GridData();
        l.setLayoutData(gd);

        rackIdFilterEntry  = new Text(ListContents, SWT.BORDER);
        rackIdFilterEntry.setToolTipText("Enter portion or Rack ID");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        rackIdFilterEntry.setLayoutData(gd);

        // List Box of Racks
        rackList = new List(ListContents, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_VERTICAL);
         int listHeight = rackList.getItemHeight() * 6;
        Rectangle trim = rackList.computeTrim(0, 0, 0, listHeight);
        gd.heightHint = trim.height;
        gd.horizontalSpan = layout.numColumns;
        gd.widthHint = 150;
        rackList.setLayoutData(gd);

        layout = new GridLayout();
        DeviceContents.setLayout(layout);
        gd = new GridData(GridData.FILL_VERTICAL);
        DeviceContents.setLayoutData(gd);

        l = new Label(DeviceContents, 0);
        l.setText("Device or Process Variable:");
        gd = new GridData();
        l.setLayoutData(gd);

        dvcOrPVEntry  = new Text(DeviceContents, SWT.BORDER);
        dvcOrPVEntry.setToolTipText("Enter device ID or PV");
        gd = new GridData(GridData.FILL_HORIZONTAL);
        dvcOrPVEntry.setLayoutData(gd);

        // Row: dvc_id, bgn, end, dvc_type_id, bl_dvc_ind
        l = new Label(DeviceContents, 0);
        l.setText("Device List:");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        l.setLayoutData(gd);

        // Rest: PV Table
        // TableColumnLayout needs this to be under its own parent
        final Composite table_parent = new Composite(DeviceContents, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        Table device_table_widget = new Table(table_parent, SWT.VIRTUAL | SWT.MULTI);
        device_table_widget.setHeaderVisible(true);
        device_table_widget.setLinesVisible(true);


        TableColumn col = new TableColumn(device_table_widget, SWT.LEFT);
        col.setText("Device ID");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(100, 180));

        col = new TableColumn(device_table_widget, SWT.LEFT);
        col.setText("Begin");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(10, 40));

        col = new TableColumn(device_table_widget, SWT.LEFT);
        col.setText("End");
        col.setMoveable(true);
        table_layout.setColumnData(col, new ColumnWeightData(10, 40));

        // TableViewer interface the plain device_table_widget
        // to our "model":
        rack_list_table = new TableViewer(device_table_widget);
        // Turns request for table row into "Device"
        rack_list_table.setContentProvider(new RackDVCListProvider(rack_list_table, rackControl));
        // Turns request for column 0, 1, 2, ... into Device's name, parent, ...
        rack_list_table.setLabelProvider(new RackDVCListLabelProvider());
    }

    /** Create the Right sash: Rack Device Table and Rack Profile */
    private void createBottomSash(final SashForm form)
    {
        //with rack height varying the height of the canvas has to adjust.
        //15 pts for each U high and an extra 125.
    	final int canvasHeight = rackHeight*15+125;
    	final int canvasWidth = 500;

    	final Composite scroll = ScrolledContainerHelper.create(form,canvasWidth,canvasHeight);
    	scroll.setLayout(new FillLayout());

    	Composite profileContainer = new Composite(scroll, SWT.NULL);

    	Button slotPosButton = new Button (profileContainer, SWT.PUSH);
    	this.slotPosButton = slotPosButton;
    	slotPosButton.setBounds (320, 30, 100, 32);
    	slotPosButton.setText ("See Rack Back");


        final Canvas paintCanvas = new Canvas(profileContainer, SWT.NONE);
        this.paintCanvas = paintCanvas;
        profileContainer.getBounds();
        paintCanvas.setBounds(0,0,canvasWidth,canvasHeight);

        // Create a paint handler for the canvas

        paintCanvas.addPaintListener(new PaintListener() {
          @Override
        public void paintControl(PaintEvent e) {
        	  	int loops = rackHeight;
			    int newU = rackHeight;
				int hU, dvc, dvcC;

        	e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        	e.gc.drawText(rackControl.getRackDvcId(),215-(rackControl.getRackDvcId().length()*3),30);
        	if ( rackControl.getSlotPosInd() == "B")
        		e.gc.drawText("Back",205,45);
        	else
        		e.gc.drawText("Front",205,45);


	        	e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
  				e.gc.setLineWidth(2);

				try {
					if ((rackControl.getRackDvcListCount() == 0) ) {
						e.gc.drawRectangle(90, 115, 300, rackHeight*15);
						e.gc.drawText("Empty", 240, (rackHeight*15/2)+115);
	  			    	}
					else {
						dvcC = rackControl.getRackDvcListCount();
						dvc = 0;
						hU = 0;
		 			    for (int i = 1; i <= rackHeight; i++) {
		 			    if ( dvc != dvcC ) {
							if (rackControl.getRackListDVC(dvc).getEND() == newU ) {
		 			    	//figure how big dvc is
									e.gc.setBackground(device_color);
									hU = (newU - rackControl.getRackListDVC(dvc).getBGN() + 1 );
									e.gc.fillRectangle(90, 100+(i*15), 300, hU*15);
									e.gc.drawRectangle(90, 100+(i*15), 300, hU*15);
									depth_points = new int[] {
											390, 100+(i*15),
											415, (100+(i*15)-30),
											415, (100+(i*15)+(hU*15)-30),
											390, (98+(i*15)+(hU*15)),
											390, 100+(i*15)};
									e.gc.drawPolygon(depth_points);
									e.gc.fillPolygon(depth_points);
									e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
									e.gc.drawText(rackControl.getRackListDVC(dvc).getDvcId(), 175, (i*15)+(hU*15/2)+ 94);
									e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
									i += hU -1 ;
									dvc++;
									newU = newU - hU;
									}
								else if (dvc <= (dvcC -1) ) {
									//figure how many blanks
									e.gc.setBackground(empty_color);
									hU = (newU - rackControl.getRackListDVC(dvc).getEND() );
									e.gc.fillRectangle(90, 100+(i*15), 300, hU*15);
									depth_points = new int[] {
											390, 100+(i*15),
											415, (100+(i*15)-30),
											415, (100+(i*15)+(hU*15)-30),
											390, (98+(i*15)+(hU*15)),
											390, 100+(i*15)};
									e.gc.drawPolygon(depth_points);
									e.gc.fillPolygon(depth_points);
									e.gc.drawRectangle(90, 100+(i*15), 300, hU*15);
									e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
									e.gc.drawText("Empty", 240, (i*15)+(hU*15/2)+94);
									e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
									i += hU -1;
									newU = newU - hU;
									}
		 			    }
						else if (dvc == (dvcC) ) {
							hU = newU;
							e.gc.setBackground(empty_color);
							e.gc.fillRectangle(90, 100+(i*15), 300, hU*15);
							e.gc.drawRectangle(90, 100+(i*15), 300, hU*15);
							depth_points = new int[] {
									390, 100+(i*15),
									415, (100+(i*15)-30),
									415, (100+(i*15)+(hU*15)-30),
									390, (98+(i*15)+(hU*15)),
									390, 100+(i*15)};
							e.gc.drawPolygon(depth_points);
							e.gc.fillPolygon(depth_points);
							e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
							e.gc.drawText("Empty", 240, (i*15)+(hU*15/2)+94);
							e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
							i += hU -1;
							newU = newU - hU;
							dvc++;
							i = 46;
						}
	 			    }
				}
				} catch (RuntimeException e1) {
	 			    for (int i = 1; i <= rackHeight; i++) {
	 			    	e.gc.setBackground(empty_color);
	 			    	e.gc.fillRectangle(90, 115, 300, rackHeight*15);
						e.gc.drawRectangle(90, 115, 300, rackHeight*15);
						e.gc.drawText("Problem", 240, (rackHeight*15/2)+115);
	  					e1.printStackTrace();
	  			    }
				}


				//Draws the Left side Rectangles and puts the "#U" text inside.
				loops = rackHeight;

  				for (int i = 1; i <= rackHeight; i++) {
  					e.gc.setBackground(white_color);
  					e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
  					e.gc.fillRectangle(65, 100+(i*15), 25, 15);
  					e.gc.drawRectangle(65, 100+(i*15), 25, 15);
  			    	e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
  			    	e.gc.drawString(loops+"U",68 , 101+(i*15));
  			    	loops--;
  			    }

  				e.gc.setLineCap(SWT.CAP_ROUND);
				depth_points = new int[] {
						91, 115,
						116, 85,
						414, 85,
						389, 115,
						91, 115};
				e.gc.drawPolygon(depth_points);
				e.gc.fillPolygon(depth_points);

  				//e.gc.drawRoundRectangle(75, 70, 370, 45,30,60);

          }
        });

    }



    void hookListeners()
    {
        rackIdFilterEntry.addTraverseListener(new TraverseListener()
        {
        	@Override
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    System.out.println("Enter Key Hit in Rack Filter Text Box");
                    final String rack = rackIdFilterEntry.getText().trim();
                    rackControl.setRackFilter(rack);
                }
            }
        });

    	/**
         * Listens for a selection of an Rack from the rackList.
         * The value is then passed to the control and used to query for associated
         * rack contents.
         */
    	rackList.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                final String[] rack = rackList.getSelection();
                rackControl.setSelectedRack(rack[0]);
             }
        });


        dvcOrPVEntry.addTraverseListener(new TraverseListener()
        {
        	@Override
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                	System.out.println("DVC or PV Entered in Text Box");
                    final String dvcId = dvcOrPVEntry.getText().trim();
                    rackList.deselectAll();
                    rackControl.setSelectedRack(dvcId);
                }
            }
        });


        /**
         * This button clears current selections and text boxes.
         */
        slotPosButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
            	if ( rackControl.getSlotPosInd() == "F") {
            	slotPosButton.setText("See Rack Front");
            	rackControl.setSlotPosIndFilter("B");
            	}
            	else {
               	slotPosButton.setText("See Rack Back");
            	rackControl.setSlotPosIndFilter("F");
            	}

            }
        });

        //Initialize these in the ioc_model.
        //TODO need to this not SNS specific
        rackControl.setRackFilter("");

        // Subscribe to changes
        rackControl.addListener(this);
    }

    /**
     * Provide rack list table viewer so that Eclipse View can use it as a selection
     * provider
     */
    public TableViewer getRackTableViewer()
    {
        return rack_list_table;
    }

    /** Provide access to the PV Filter text to allow drag/drop connections */
    public Text getDVCOrPVEntry()
    {
        return dvcOrPVEntry;
    }


     /**
     * Resets the Filters used by the ioc_model so that they are affectively looking for everything.
     * Nothing is filtered.
     * The Device filter is set to "xxx" so that no devices show up.  An all inclusive list would be too long.
     */
    public void reInitialize()
    {
        // Fake event to initialize
        rackControl.setRackFilter("");

    }



    public void setFocus()
    {
    	rackIdFilterEntry.setFocus();
    }

    /** Clears the FEC list and then re-populates it based on the new criteria
     *  @see PVUtilListener
     */
    @Override
    public void rackUtilChanged(final ChangeEvent what)
    {
        // This could be called from a non-GUI thread, for example the model's
        // database reader.
        display.asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
            	switch (what)
            	{
            	case RACKLIST:
	                final int N = rackControl.getRacksCount();
	                rackList.removeAll();
	                for (int i = 0; i < N; i++)
	                {
	                    final String rack = rackControl.getRack(i);
	                    rackList.add(rack);
	                }
	                break;
            	case DVCLIST:
	                // Setting the count causes an update
            		rack_list_table.setItemCount(rackControl.getRackDvcListCount());
	                rack_list_table.refresh();
	                paintCanvas.redraw();
	                //rackList.select(1);
	                break;
            	}
            }
        });
    }















}
