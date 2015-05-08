package com.cosylab.vdct.graphics.objects;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.util.Map;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.Settings;
import com.cosylab.vdct.db.DBResolver;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.LinkCommand;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.FontMetricsBuffer;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.popup.PopUpMenu;
import com.cosylab.vdct.graphics.popup.Popupable;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.inspector.InspectorManager;
import com.cosylab.vdct.undo.*;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.*;

/**
 * Graphical representation of templates.
 * @author Matej
 */
// TODO do not show hidden macros (with fields?) in properties or not?!
// TODO make serialization of macro w/o vis. rep. work, but macro is defined only as visible object?!
public class Template
    extends LinkManagerObject
    implements /*Descriptable,*/ Movable, Inspectable, Popupable, Flexible, Selectable,
                Clipboardable, Hub, MonitoredPropertyListener, SaveInterface, SaveObject, Morphable
{

    class PopupMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LinkCommand cmd = (LinkCommand)CommandManager.getInstance().getCommand("LinkCommand");
            cmd.setData((VisibleObject)Template.this.getSubObject(e.getActionCommand()), Template.this.getField(e.getActionCommand()));
            cmd.execute();
        }
    }

    VDBTemplateInstance templateData = null;
    //String description = null;

    private static ImageIcon icon = null;

    private CommentProperty commentProperty = null;

    private boolean initialized = false;

    // properties field
    protected int rfieldLabelX;
    protected int rfieldLabelY;
    protected double rfieldRowHeight;
    protected Font fieldFont = null;

    // templateid (fileName) label
    protected int ridLabelX;
    protected int ridLabelY;
    protected String idlabel;
    protected Font idFont = null;

    protected int initY;
    protected int rlinkY;

    private static GUISeparator templateSeparator = null;

    private static GUISeparator templateInstanceSeparator = null;

    private static GUISeparator macrosSeparator = null;
    private static GUISeparator portsSeparator = null;

    private static GUISeparator propertiesSeparator = null;

    private final static String fieldMaxStr = "01234567890123456789012345";

    protected long portsID = -1;
    protected long macrosID = -1;

    protected Vector invalidLinks = null;

    protected int fields = 0;
    protected int leftFields = 0;
    protected int rightFields = 0;

    /**
     * @param parent
     * @param templateData
     */
    public Template(ContainerObject parent, VDBTemplateInstance templateData) {
        this(parent, templateData, true);
    }

    /**
     * Insert the method's description here.
     * Creation date: (21.12.2000 20:40:53)
     * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
     * @param templateData The templateData to set
     */
    public Template(ContainerObject parent, VDBTemplateInstance templateData, boolean initializeFields) {
        super(parent);
        this.templateData = templateData;

        invalidLinks = new Vector();

        setColor(Color.black);
        setWidth(Constants.TEMPLATE_WIDTH);
        setHeight(Constants.TEMPLATE_INITIAL_HEIGHT);

        if (initializeFields)
            initializeLinkFields();

        //forceValidation();

    }

    /**
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#draw(Graphics, boolean)
     */
    protected void draw(Graphics g, boolean hilited)
    {
        ViewState view = ViewState.getInstance();

        double Rscale = getRscale();
        boolean zoom = Rscale < 1.0 && view.isZoomOnHilited() && view.isHilitedObject(this);
        if (zoom) {
            zoomImage = ZoomPane.getInstance().startZooming(this, true);
        }

        int rrx = getRx()-view.getRx();
        int rry = getRy()-view.getRy();
        int rwidth = getRwidth();
        int rheight = getRheight();

        // clipping
        if ((!(rrx>view.getViewWidth()) || (rry>view.getViewHeight())
            || ((rrx+rwidth)<0) || ((rry+rheight)<0)) || isZoomRepaint()) {

            if (isZoomRepaint()) {
                rrx = ZoomPane.getInstance().getLeftOffset();
                rry = ZoomPane.VERTICAL_MARGIN;
            }

            if (!hilited) g.setColor(Constants.RECORD_COLOR);
            else if (view.isPicked(this)) g.setColor(Constants.PICK_COLOR);
            else if (view.isSelected(this) ||
                     view.isBlinking(this)) g.setColor(Constants.SELECTION_COLOR);
            else g.setColor(Constants.RECORD_COLOR);

            Color fillColor = g.getColor();

            g.fillRect(rrx, rry, rwidth, rheight);


            if (!hilited) g.setColor(Constants.FRAME_COLOR);
            else g.setColor((view.isHilitedObject(this)) ?
                            Constants.HILITE_COLOR : Constants.FRAME_COLOR);

            g.drawRect(rrx, rry, rwidth, rheight);

            // colors
            if (invalidLinks.size()>0)
            {
                if (fillColor!=Color.red)
                    g.setColor(Color.red);
                else
                    g.setColor(Color.black);
            }

            if (getFont()!=null) {
                g.setFont(getFont());
                g.drawString(getLabel(), rrx+getRlabelX(), rry+getRlabelY());
            }

            if (idFont!=null) {
                g.setFont(idFont);
                g.drawString(idlabel, rrx+ridLabelX, rry+ridLabelY);
            }

            // middle line
            int ox = (int) (10 * getRscale());

            //g.drawLine(rrx + ox, rry + rinitY, rrx + rwidth - ox, rry + rinitY);
            //g.drawLine(rrx + ox, rry + rlinkY, rrx + rwidth - ox, rry + rlinkY);

            if (fieldFont != null)
            {
                g.setFont(fieldFont);
                FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
                String val;
                int px = rrx + rfieldLabelX;
                int py0 = rry + rfieldLabelY;
                int py = py0; int n = 0;
                 java.util.Iterator e = templateData.getPropertiesV().iterator();
                while (e.hasNext())
                {
                    String name = e.next().toString();
                    val = name + "=" + templateData.getProperties().get(name).toString();
                    while ((fm.stringWidth(val) + ox) > rwidth)
                        val = val.substring(0, val.length() - 2);
                    g.drawString(val, px, py);
                    py = py0 + (int)((++n)*rfieldRowHeight);
                }

            }

        }

        paintSubObjects(g, hilited);

        if (zoom) {
            rwidth /= Rscale;
            rheight /= Rscale;
            rrx -= (rwidth - getRwidth())/2;
            rry -= (rheight - getRheight())/2;
            if (view.getRx() < 0)
                rrx = rrx < 0 ? 2 : rrx;
            if (view.getRy() < 0)
                rry = rry <= 0 ? 2 : rry;
            g.drawImage(zoomImage, rrx,rry, ZoomPane.getInstance());
        }

    }

    /**
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#getHashID()
     */
    public String getHashID()
    {
        return templateData.getName();
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#revalidatePosition()
     */
    public void revalidatePosition()
    {
          double Rscale = getRscale();
          setRx((int)(getX()*Rscale));
          setRy((int)(getY()*Rscale));

          // sub-components
          revalidateFieldsPosition();
    }

    private int validateFont(double scale, int rwidth, int height) {

        int irheight = (int)(scale*height);

          // set appropriate font size
          int x0 = (int)(24*scale);        // insets
          int y0 = (int)(12*scale);


          // fields
          int fieldRows = Math.max(leftFields, rightFields);
          height += fieldRows * Constants.FIELD_HEIGHT;
         // height = Math.max(height, Constants.TEMPLATE_MIN_HEIGHT);
          int frheight = (int)(scale*height);

//          rlinkY = frheight;

          // properties

          int xx0 = (int)(14*scale);        // insets
          int yy0 = (int)(8*scale);


         // !!! optimize - static

          rfieldRowHeight = (irheight-2*y0)*0.175;

          if (rwidth<(2*xx0)) fieldFont = null;
          else
              fieldFont = FontMetricsBuffer.getInstance().getAppropriateFont(
                               Constants.DEFAULT_FONT, Font.PLAIN,
                               fieldMaxStr, rwidth-x0, (int)rfieldRowHeight);

          int ascent = 0;
          //rfieldRowHeight = 0;
          if (fieldFont!=null)
          {
              FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
              rfieldLabelX = xx0;
               rfieldLabelY = frheight+2*fm.getAscent();
              //rfieldRowHeight = fm.getHeight();
              ascent = fm.getAscent();
          }

          int rheight = frheight + yy0 + (int)(rfieldRowHeight*templateData.getProperties().size())+ascent;


          // description

          int idLabelHeight = (int)(Constants.FIELD_HEIGHT*scale);

          setLabel(getDescription());

          final int MAX_FONT_SIZE = 30;
          Font font;
          font = FontMetricsBuffer.getInstance().getAppropriateFont(
                          Constants.DEFAULT_FONT, Font.PLAIN,
                          getLabel(), rwidth-x0, irheight-y0/*-idLabelHeight*/, (int)(MAX_FONT_SIZE*scale));

          if (rwidth<(2*x0)) font = null;
          else
          if (font!=null) {
              FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
              setRlabelX((rwidth-fm.stringWidth(getLabel()))/2);
               setRlabelY((irheight-fm.getHeight()/*+idLabelHeight*/)/2+fm.getAscent());
          }
          setFont(font);

          // id label

          // idlabel = templateData.getTemplate().getId();
          idlabel = templateData.getName();
          if (rwidth<(2*x0)) idFont = null;
          else
              idFont = FontMetricsBuffer.getInstance().getAppropriateFont(
                               Constants.DEFAULT_FONT, Font.PLAIN,
                               idlabel, rwidth-x0, idLabelHeight);

          if (idFont!=null) {
              FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(idFont);
              ridLabelX = (rwidth-fm.stringWidth(idlabel))/2;
               ridLabelY = (idLabelHeight-fm.getHeight())/2+fm.getAscent();
          }

          return rheight;
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#validate()
     */
    private boolean validating = false;
    protected void validate()
    {
        if (validating)
            return;

        try {
            validating = true;

              // template change check
              VDBTemplate tmpl = (VDBTemplate)VDBData.getTemplates().get(getTemplateData().getTemplate().getId());
              if (tmpl!=getTemplateData().getTemplate())
              {
                  getTemplateData().setTemplate(tmpl);
                synchronizeLinkFields();
              }
              else
              {
                  if (getTemplateData().getTemplate().getPortsGeneratedID()!=portsID)
                    synchronizePortLinkFields();
                if (getTemplateData().getTemplate().getMacrosGeneratedID()!=macrosID)
                    synchronizeMacroLinkFields();
              }

              revalidatePosition();

              double scale = getRscale();
              int rwidth = (int)((getX()+getWidth())*scale)-getRx();
              int height = (int)(getY()*scale+Constants.TEMPLATE_INITIAL_HEIGHT)-getRy();

              initY = height;

              int rheight = validateFont(scale, rwidth, height);

              setHeight((int)(rheight/scale));
              setRwidth(rwidth);
              setRheight(rheight);

              // sub-components
              revalidatePosition();
              validateFields();

        }
        finally {
            validating = false;
        }
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.Movable#checkMove(int, int)
     */
    public boolean checkMove(int dx, int dy)
    {
        ViewState view = ViewState.getInstance();

        if ((getX()<-dx) || (getY()<-dy) ||
            (getX()>(view.getWidth()-getWidth()-dx)) || (getY()>(view.getHeight()-getHeight()-dy)))
            return false;
        else
            return true;
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.Movable#move(int, int)
     */
    public boolean move(int dx, int dy)
    {
        if (checkMove(dx, dy)) {
            x+=dx;
            y+=dy;
            revalidatePosition();
            moveConnectors(dx, dy);
            return true;
        }
        else
            return false;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getCommentProperty()
     */
    public InspectableProperty getCommentProperty()
    {
        if (commentProperty==null)
            commentProperty = new CommentProperty(templateData);
        return commentProperty;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getIcon()
     */
    public Icon getIcon()
    {
        if (icon==null)
            icon = new javax.swing.ImageIcon(getClass().getResource("/images/template.gif"));
        return icon;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getName()
     */
    public String getName()
    {
        return templateData.getName();
    }

    /**
     * Insert the method's description here.
     * Creation date: (10.1.2001 14:49:50)
     * @return java.lang.String
     */
    public String toString() {
        return getDescription() + " [" + templateData.getName() + "]";
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getTemplateSeparator() {
        if (templateSeparator==null) templateSeparator = new GUISeparator("Template");
        return templateSeparator;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getTemplateInstanceSeparator() {
        if (templateInstanceSeparator==null) templateInstanceSeparator = new GUISeparator("Template Instance");
        return templateInstanceSeparator;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getPortsSeparator() {
        if (portsSeparator==null) portsSeparator = new GUISeparator("Port fields");
        return portsSeparator;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getMacrosSeparator() {
        if (macrosSeparator==null) macrosSeparator = new GUISeparator("Macro fields");
        return macrosSeparator;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getPropertiesSeparator() {
        if (propertiesSeparator==null) propertiesSeparator = new GUISeparator("Macros");
        return propertiesSeparator;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
     */
    public InspectableProperty[] getProperties(int mode)
    {
        Vector items = new Vector();
        items.addElement(GUIHeader.getDefaultHeader());

        items.addElement(getTemplateSeparator());
        items.addElement(new NameValueInfoProperty("Template", templateData.getTemplate().getId()));
        items.addElement(new NameValueInfoProperty("FileName", templateData.getTemplate().getFileName()));

        items.addElement(getTemplateInstanceSeparator());

        final String descriptionString = "Description";

        items.addElement(getMacrosSeparator());
        Object obj;
        Enumeration e = subObjectsV.elements();
        // !!! better implementation
        while (e.hasMoreElements())
        {
            obj = e.nextElement();
            if (obj instanceof TemplateEPICSMacro)
            {
                TemplateEPICSMacro tem = (TemplateEPICSMacro)obj;
                items.addElement(new GUISeparator(tem.getFieldData().getName()));
                items.addElement(tem.getFieldData());
                items.addElement(new NameValueInfoProperty(descriptionString, tem.getDescription()));
            }
        }

        items.addElement(getPortsSeparator());
        // !!! better implementation
        e = subObjectsV.elements();
        while (e.hasMoreElements())
        {
            obj = e.nextElement();
            if (obj instanceof TemplateEPICSPort)
            {
                TemplateEPICSPort tep = (TemplateEPICSPort)obj;
                items.addElement(new GUISeparator(tep.getFieldData().getName()));
                items.addElement(tep.getFieldData());
                items.addElement(new NameValueInfoProperty(descriptionString, tep.getDescription()));
            }
        }


        items.addElement(getPropertiesSeparator());

          java.util.Iterator i = templateData.getPropertiesV().iterator();
        while (i.hasNext())
        {
            String name = i.next().toString();
            // if not already added above as macro
            if (getSubObject(name)==null)
            items.addElement(new MonitoredProperty(name, (String)templateData.getProperties().get(name), this));
        }

        final String addString = "Add macro...";
        items.addElement(new MonitoredActionProperty(addString, this));

        InspectableProperty[] properties = new InspectableProperty[items.size()];
        items.copyInto(properties);
        return properties;
    }

    /**
     * @see com.cosylab.vdct.graphics.popup.Popupable#getItems()
     */
    public Vector getItems()
    {
        ActionListener l = createPopupmenuHandler();

        // template has link destination
        if (getTargetLink()==null)
        {
            JMenu macros = new JMenu("MACRO");
            int macroItems = 0;

            Object obj;
            Enumeration e = subObjectsV.elements();
            while (e.hasMoreElements())
            {
                obj = e.nextElement();
                if (obj instanceof TemplateEPICSMacro)
                {
                    TemplateEPICSMacro tem = (TemplateEPICSMacro)obj;
                    // if not default (empty)
                    if (tem.getFieldData().hasDefaultValue())
                    {
                        JMenuItem menuitem = new JMenuItem(tem.getFieldData().getName());
                        menuitem.addActionListener(l);
                        macros = PopUpMenu.addItem(menuitem, macros, macroItems);
                        macroItems++;
                    }
                }
            }

            if (macros.getItemCount() > 0)
            {
                Vector items = new Vector();
                items.addElement(macros);
                return items;
            }
            else
                return null;
        }
        // template has link target
        else
        {
            JMenu ports = new JMenu("PORT");
            int portItems = 0;

            Object obj;
            Enumeration e = subObjectsV.elements();
            while (e.hasMoreElements())
            {
                obj = e.nextElement();
                if (obj instanceof TemplateEPICSPort)
                {
                    TemplateEPICSPort tep = (TemplateEPICSPort)obj;
                    JMenuItem menuitem = new JMenuItem(tep.getFieldData().getName());
                    menuitem.addActionListener(l);
                    ports = PopUpMenu.addItem(menuitem, ports, portItems);
                    portItems++;
                }
            }

            if (ports.getItemCount() > 0)
            {
                Vector items = new Vector();
                items.addElement(ports);
                return items;
            }
            else
                return null;
        }

    }

    /**
     * @see com.cosylab.vdct.graphics.objects.Flexible#getFlexibleName()
     */
    public String getFlexibleName()
    {
        return templateData.getName();
    }

    /**
     * @see com.cosylab.vdct.graphics.objects.Visitable#accept(Visitor)
     */
    public void accept(Visitor visitor)
    {
    }

    /**
     * Returns the templateData.
     * @return VDBTemplateInstance
     */
    public VDBTemplateInstance getTemplateData()
    {
        return templateData;
    }

    /**
     * Returns the description.
     * @return String
     */
    public String getDescription()
    {
        return templateData.getTemplate().getDescription();
        //return description;
    }

    /**
     * Sets the description.
     * @param description The description to set
     */
    /*
    public void setDescription(String description)
    {
        this.description = description;
    }
    */


/**
 * Returned value inicates change
 * Creation date: (21.12.2000 22:21:12)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param x int
 * @param y int
 */
public VisibleObject hiliteComponentsCheck(int x, int y) {

    ViewState view = ViewState.getInstance();
    VisibleObject spotted = null;

    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
        if (vo instanceof TemplateEPICSLink && !vo.isVisible())
            continue;
        vo = vo.intersects(x, y);
        if (vo!=null) {
            spotted=vo;
            if (view.getHilitedObject()!=vo) return vo;
        }
    }

    return spotted;
}


/**
 * Default impmlementation for square (must be rescaled)
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {

    // first check on small sub-objects like connectors
    VisibleObject spotted = hiliteComponentsCheck(px, py);
      if ((spotted==null) &&
          (getRx()<=px) && (getRy()<=py) &&
        ((getRx()+getRwidth())>=px) &&
        ((getRy()+getRheight())>=py))
        spotted = this;
    return spotted;
}


/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:18:51)
 */
public void revalidateFieldsPosition() {

  int lx = getX();
  int rx = getX()+getWidth()/2;
  int ly = getY()+initY;
  int ry = getY()+initY;
  int rn = 0;
  int ln = 0;
  Enumeration e = subObjectsV.elements();
  EPICSLink field; Object obj;
  while (e.hasMoreElements()) {
    obj = e.nextElement();
    if (obj instanceof EPICSLink) {
        field = (EPICSLink)obj;
        if (field.isVisible())
            if (field.isRight())
            {
                field.revalidatePosition(rx, ry, rn);
                ry+=field.getHeight();
                rn++;
            }
            else
            {
                field.revalidatePosition(lx, ly, ln);
                ly+=field.getHeight();
                ln++;
            }
    }
  }

}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:58:56)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
private void paintSubObjects(Graphics g, boolean hilited) {
    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
        if (vo.isVisible())
            vo.paint(g, hilited);
    }

}


/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void validateFields() {

    Enumeration e = subObjectsV.elements();
    Object obj;
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof Field)
            ((VisibleObject)obj).validate();
    }

}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
public void updateTemplateFields() {

    Enumeration e = subObjectsV.elements();
    Object obj;
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof TemplateEPICSLink)
            ((TemplateEPICSLink)obj).updateTemplateLink();
    }

}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
private EPICSLink createLinkField(VDBFieldData field)
{

    EPICSLink link = null;

    if (field instanceof VDBTemplatePort)
        link = new TemplateEPICSPort(this, field);
    else if (field instanceof VDBTemplateMacro)
        link = new TemplateEPICSMacro(this, field);

    fields++;
    if (link!=null)
    {
        if (link.isVisible())
            if (link.isRight())
                rightFields++;
            else
                leftFields++;
    }

    return link;
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
public void initializeLinkFields()
{
    // set this flag before...
    initialized = true;

    // ports
    Enumeration e = templateData.getTemplate().getPortsV().elements();
    while (e.hasMoreElements())
        addPortField((VDBPort)e.nextElement());

    portsID = getTemplateData().getTemplate().getPortsGeneratedID();


    // macros
    e = templateData.getTemplate().getMacrosV().elements();
    while (e.hasMoreElements())
        addMacroField((VDBMacro)e.nextElement());

    macrosID = getTemplateData().getTemplate().getMacrosGeneratedID();

}

/**
 * @param port
 */
public EPICSLink addPortField(VDBPort port) {
    // check if not already added (order preservation)
    EPICSLink link = (EPICSLink)getSubObject(port.getName());
    if (link==null)
    {
        VDBTemplatePort tf = new VDBTemplatePort(getTemplateData(), port);

        link = createLinkField(tf);
        if (link!=null)
            addSubObject(tf.getName(), link);
    }
    return link;
}

/**
 * @param macro
 */
public EPICSLink addMacroField(VDBMacro macro) {
    // check if not already added (order preservation)
    EPICSLink link = (EPICSLink)getSubObject(macro.getName());
    if (link==null)
    {
        VDBTemplateMacro tf = new VDBTemplateMacro(getTemplateData(), macro);

        link = createLinkField(tf);
        if (link!=null)
            addSubObject(tf.getName(), link);

        // transfer value from properties
        String value = (String)templateData.getProperties().get(tf.getName());
        if (value != null)
            tf.setValue(value);

    }
    return link;
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void synchronizeLinkFields()
{
    synchronizePortLinkFields();
    synchronizeMacroLinkFields();
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void synchronizePortLinkFields()
{
    Object[] objs = new Object[getSubObjectsV().size()];
    getSubObjectsV().copyInto(objs);

    // add ports
    Enumeration e = templateData.getTemplate().getPortsV().elements();
    while (e.hasMoreElements())
    {
        VDBPort port = (VDBPort)e.nextElement();
        Object obj = getSubObject(port.getName());
        if (obj==null)
        {
            TemplateEPICSPort tep = null;
            for (int i=0; i<objs.length; i++)
                if (objs[i] instanceof TemplateEPICSPort)
                {
                    TemplateEPICSPort t = (TemplateEPICSPort)objs[i];
                    if (t.getFieldData().getName().equals(port.getName()))
                    {
                        tep = t;
                        break;
                    }
                }

            // renamed
            if (tep!=null)
            {
                Object key = null;
                Enumeration e2 = getSubObjects().keys();
                while (e2.hasMoreElements())
                {
                    Object key2 = e2.nextElement();
                    Object val = getSubObjects().get(key2);
                    if (val instanceof TemplateEPICSPort && val==tep)
                    {
                        key = key2;
                        break;
                    }
                }

                if (key!=null)
                    removeObject(key.toString());
                else
                    System.out.println("Internal error...");

                addSubObject(tep.getFieldData().getName(), tep);

                // update lookup table and fix source links
                tep.fixTemplateLink();

                //System.out.println("!! renamed !! "+port.getName());
            }
            else
            {
                //System.out.println("!! added !!"+port.getName());

                // add port
                VDBTemplatePort tf = new VDBTemplatePort(getTemplateData(), port);
                EPICSLink link = createLinkField(tf);
                if (link!=null)
                    addSubObject(tf.getName(), link);
            }
        }
        else
        {
            // fix port if necessary (result of add+remove action)
            VDBTemplatePort tpd = ((VDBTemplatePort)((TemplateEPICSPort)obj).getFieldData());
            if (tpd.getPort()!=port)
            {
                //System.out.println("!! fixing port !!"+port.getName());
                tpd.setPort(port);
            }
        }
    }

    // remove ports
    for (int i=0; i<objs.length; i++)
    {
        if (objs[i] instanceof TemplateEPICSPort)
        {
            TemplateEPICSPort link = (TemplateEPICSPort)objs[i];
            if (!templateData.getTemplate().getPorts().containsKey(link.getFieldData().getName()))
            {
                //System.out.println("!! removed !! "+link.getFieldData().getName());

                // remove port
                link.destroyAndRemove();

                fields--;
                if (link.isVisible())
                    if (link.isRight())
                        rightFields--;
                    else
                        leftFields--;

                removeObject(link.getFieldData().getName());
            }
        }
    }

    // save ports ID
    portsID = getTemplateData().getTemplate().getPortsGeneratedID();
    //com.cosylab.vdct.graphics.DrawingSurface.getInstance().setModified(true);

}


/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void synchronizeMacroLinkFields()
{

    Object[] objs = new Object[getSubObjectsV().size()];
    getSubObjectsV().copyInto(objs);

    // add macros
    Enumeration e = templateData.getTemplate().getMacrosV().elements();
    while (e.hasMoreElements())
    {
        VDBMacro macro = (VDBMacro)e.nextElement();
        Object obj = getSubObject(macro.getName());
        if (obj==null)
        {
            TemplateEPICSMacro tem = null;
            for (int i=0; i<objs.length; i++)
                if (objs[i] instanceof TemplateEPICSMacro)
                {
                    TemplateEPICSMacro t = (TemplateEPICSMacro)objs[i];
                    if (t.getFieldData().getName().equals(macro.getName()))
                    {
                        tem = t;
                        break;
                    }
                }

            // renamed
            if (tem!=null)
            {
                Object key = null;
                Enumeration e2 = getSubObjects().keys();
                while (e2.hasMoreElements())
                {
                    Object key2 = e2.nextElement();
                    Object val = getSubObjects().get(key2);
                    if (val instanceof TemplateEPICSMacro && val==tem)
                    {
                        key = key2;
                        break;
                    }
                }

                if (key!=null)
                {
                    removeObject(key.toString());

                    // remove from properties
                    String keyStr = key.toString();
                    Iterator it = templateData.getProperties().keySet().iterator();
                    while (it.hasNext())
                    {
                        // !!! not perfect solution
                        String propertyName = it.next().toString();
                        if (propertyName.equals(keyStr))
                            templateData.removeProperty(propertyName);
                    }
                }
                else
                    System.out.println("Internal error...");

                addSubObject(tem.getFieldData().getName(), tem);
                // add to properties
                templateData.addProperty(tem.getFieldData().getName(), tem.getFieldData().getValue());

                // update lookup table and fix source links
                tem.fixTemplateLink();

                //System.out.println("!! renamed !! "+macro.getName());
            }
            else
            {
                //System.out.println("!! added !!"+macro.getName());

                // add macro
                VDBTemplateMacro tf = new VDBTemplateMacro(getTemplateData(), macro);
                EPICSLink link = createLinkField(tf);
                if (link!=null)
                    addSubObject(tf.getName(), link);
            }
        }
        else
        {
            // fix macro if necessary (result of add+remove action)
            VDBTemplateMacro tmd = ((VDBTemplateMacro)((TemplateEPICSMacro)obj).getFieldData());
            if (tmd.getMacro()!=macro)
            {
                //System.out.println("!! fixing macro !!"+macro.getName());
                tmd.setMacro(macro);
            }
        }
    }

    // remove macros
    for (int i=0; i<objs.length; i++)
    {
        if (objs[i] instanceof TemplateEPICSMacro)
        {
            TemplateEPICSMacro link = (TemplateEPICSMacro)objs[i];
            if (!templateData.getTemplate().getMacros().containsKey(link.getFieldData().getName()))
            {
                //System.out.println("!! removed !! "+link.getFieldData().getName());

                // remove macro
                link.destroyAndRemove();

                removeObject(link.getFieldData().getName());

                fields--;
                if (link.isVisible())
                    if (link.isRight())
                        rightFields--;
                    else
                        leftFields--;

                // remove from properties
                templateData.removeProperty(link.getFieldData().getName());
            }
        }
    }

    // save macros ID
    macrosID = getTemplateData().getTemplate().getMacrosGeneratedID();
    //com.cosylab.vdct.graphics.DrawingSurface.getInstance().setModified(true);

}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:35:39)
 */
public void manageLinks() {
    Object obj;
    Enumeration e = subObjectsV.elements();
    while (e.hasMoreElements())
    {
        obj = e.nextElement();
        if (obj instanceof TemplateEPICSMacro)
            manageLink(((TemplateEPICSMacro)obj).getFieldData());
    }
}


/**
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void addLink(Linkable link)
{
}

/**
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void removeLink(Linkable link)
{
}

/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:12:03)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public void fieldChanged(VDBFieldData field) {
    boolean repaint = false;

    if (manageLink(field) ||
        templateData.getProperties().get(field.getName())!=null)
            repaint=true;

    if (repaint && initialized) {
        unconditionalValidation();
        com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    }
}

/**
 */
public VDBFieldData getField(String name) {
    EPICSLink el = (EPICSLink)getSubObject(name);
    if (el!=null)
        return el.getFieldData();
    else
        return null;
}


/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#addProperty()
 */
public void addProperty()
{
    String message = "Enter macro name:";
    int type = JOptionPane.QUESTION_MESSAGE;
    while (true)
    {
        String reply = JOptionPane.showInputDialog( null,
                                       message,
                                       "Add macro...",
                                       type );
        if (reply!=null)
        {
            if (!templateData.getProperties().containsKey(reply) &&
                getSubObject(reply)==null)
            {
                // check name
                if (reply.trim().length()==0)
                {
                    message = "Empty name! Enter valid name:";
                    type = JOptionPane.WARNING_MESSAGE;
                    continue;
                }
                else if (reply.indexOf(' ')!=-1)
                {
                    message = "No spaces allowed! Enter valid name:";
                    type = JOptionPane.WARNING_MESSAGE;
                    continue;
                }
                else
                {
                    templateData.addProperty(reply, nullString);

                    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
                            new CreateTemplatePropertyAction(this, reply));

                    updateTemplateFields();
                    InspectorManager.getInstance().updateObject(this);
                    unconditionalValidation();
                    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
                }
            }
            else
            {
                message = "Macro '"+reply+"' already exists. Enter other name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
        }

        break;
    }
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#propertyChanged(InspectableProperty)
 */
public void propertyChanged(InspectableProperty property)
{
    String oldValue = (String)templateData.getProperties().get(property.getName());

    // just override value
    templateData.getProperties().put(property.getName(), property.getValue());

    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
        new ChangeTemplatePropertyAction(this, property.getName(), property.getValue(), oldValue));

    updateTemplateFields();
    InspectorManager.getInstance().updateProperty(this, null);
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#removeProperty(InspectableProperty)
 */
public void removeProperty(InspectableProperty property)
{
    templateData.removeProperty(property.getName());

    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
                    new DeleteTemplatePropertyAction(this, property.getName()));

    updateTemplateFields();
    InspectorManager.getInstance().updateObject(this);
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");

}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#renameProperty(InspectableProperty)
 */
public void renameProperty(InspectableProperty property)
{
    String message = "Enter new macro name of '"+property.getName()+"':";
    int type = JOptionPane.QUESTION_MESSAGE;
    while (true)
    {
        String reply = JOptionPane.showInputDialog( null,
                                       message,
                                       "Rename macro...",
                                        type);
        if (reply!=null)
        {
            // check name
            if (reply.trim().length()==0)
            {
                message = "Empty name! Enter valid name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            else if (reply.indexOf(' ')!=-1)
            {
                message = "No spaces allowed! Enter valid name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            else if (!templateData.getProperties().containsKey(reply) &&
                     getSubObject(reply)==null)
            {
                com.cosylab.vdct.undo.ComposedAction composedAction =
                                                new com.cosylab.vdct.undo.ComposedAction();

                Object value = templateData.getProperties().get(property.getName());
                templateData.removeProperty(property.getName());
                composedAction.addAction(new DeleteTemplatePropertyAction(this, property.getName()));

                templateData.addProperty(reply, value.toString());
                composedAction.addAction(new CreateTemplatePropertyAction(this, reply));

                com.cosylab.vdct.undo.UndoManager.getInstance().addAction(composedAction);

                updateTemplateFields();
                InspectorManager.getInstance().updateObject(this);
                unconditionalValidation();
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else
            {
                message = "Macro '"+reply+"' already exists. Enter other name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }

        }

        break;
    }
}

/**
 */
public void addInvalidLink(EPICSLink field)
{
    if (!invalidLinks.contains(field))
        invalidLinks.addElement(field);
}

/**
 */
public void removeInvalidLink(EPICSLink field)
{
    invalidLinks.remove(field);
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:21)
 */
public void destroy() {
    if (!isDestroyed()) {
        super.destroy();
        destroyFields();

        //clear();
        getParent().removeObject(Group.substractObjectName(getName()));
    }
}

/**
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#setDestroyed(boolean)
 */
public void setDestroyed(boolean newDestroyed) {
    super.setDestroyed(newDestroyed);
    if (!newDestroyed)
        undestroyFields();
}

/**
 * @see com.cosylab.vdct.graphics.objects.Flexible#copyToGroup(String)
 */
public Flexible copyToGroup(java.lang.String group) {

    String newName;
    if (group.equals(nullString))
        newName = Group.substractObjectName(templateData.getName());
    else
        newName = group+Constants.GROUP_SEPARATOR+
                  Group.substractObjectName(templateData.getName());

    // object with new name already exists, add suffix ///!!!
    //Object obj;

    while (Group.getRoot().findObject(newName, true)!=null)
//        newName += Constants.COPY_SUFFIX;
            newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);


    //ViewState view = ViewState.getInstance();

    VDBTemplateInstance theDataCopy = VDBData.copyVDBTemplateInstance(templateData);
    theDataCopy.setName(newName);
    Template theTemplateCopy = new Template(null, theDataCopy);
    Group.getRoot().addSubObject(theDataCopy.getName(), theTemplateCopy, true);
    //theTemplateCopy.setDescription(getTemplateData().getTemplate().getDescription());
    theTemplateCopy.setX(getX()); theTemplateCopy.setY(getY());
    //theTemplateCopy.move(20-view.getRx(), 20-view.getRy());

    // apply fields data
    Enumeration e = subObjectsV.elements();
    EPICSLink field; Object obj;

    // order the fields
    theTemplateCopy.getSubObjectsV().clear();

    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof EPICSLink) {
            field = (EPICSLink) obj;

            EPICSLink fieldCopy = (EPICSLink) theTemplateCopy.getSubObject(field.getFieldData().getName());
            if (fieldCopy != null)
            {
                fieldCopy.setColor(field.getColor());
                fieldCopy.setRight(field.isRight());
                fieldCopy.getFieldData().setVisibility(field.getFieldData().getVisibility());

                // put in the right order
                theTemplateCopy.getSubObjectsV().add(fieldCopy);
            }
        }
    }

    // fix only valid links where target is also selected
    theTemplateCopy.fixMacrosOnCopy(Group.substractParentName(templateData.getName()), group);
    // links have to be fixed here... so <group>.manageLinks() should be called
    // for clipboard copy this is done later...

    theTemplateCopy.updateTemplateFields();
    unconditionalValidation();
    return theTemplateCopy;
}

/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 9:42:29)
 * @param e java.util.Enumeration list of VDBFieldData fields
 * @param prevGroup java.lang.String
 * @param group java.lang.String
 */
public void fixMacrosOnCopy(String prevGroup, String group) {
    if (prevGroup.equals(group)) return;

    String prefix;
    if (group.equals(nullString)) prefix=nullString;
    else prefix=group+Constants.GROUP_SEPARATOR;

    Enumeration e = subObjectsV.elements();
    while (e.hasMoreElements()) {
        Object obj = e.nextElement();
        if (!(obj instanceof TemplateEPICSMacro))
            continue;

        VDBFieldData field = ((TemplateEPICSMacro)obj).getFieldData();
        String old = field.getValue();
        if (!old.equals(nullString) && !old.startsWith(Constants.HARDWARE_LINK) &&
            old.startsWith(prevGroup)) {

            LinkProperties lp = new LinkProperties(field);
            InLink target = EPICSLinkOut.getTarget(lp, true);
            if (target == null)
                continue;

            // only parent can be selected
            Object selectableObject;
            if (target instanceof Field)
                selectableObject = ((Field)target).getParent();
            else
                selectableObject = target;


            // fix only selected
            if (!ViewState.getInstance().isSelected(selectableObject))
                continue;

            // fix ports...
            if (selectableObject instanceof Template) {
                Template t = (Template)selectableObject;
                field.setValue("$(" + prefix + t.getName() + Constants.FIELD_SEPARATOR + ((TemplateEPICSPort)target).getFieldData().getName()+")");
            }
            // normal record fields
            else if (prevGroup.equals(nullString))
                field.setValue(prefix+old);
            else
                field.setValue(prefix+old.substring(prevGroup.length()+1));
        }
    }

}


/**
 * @see com.cosylab.vdct.graphics.objects.Flexible#moveToGroup(String)
 */
public boolean moveToGroup(java.lang.String group) {
    String currentParent = Group.substractParentName(templateData.getName());
    if (group.equals(currentParent)) return false;

    //String oldName = getName();
    String newName;
    if (group.equals(nullString))
        newName = Group.substractObjectName(templateData.getName());
    else
        newName = group+Constants.GROUP_SEPARATOR+
                  Group.substractObjectName(templateData.getName());;

    // object with new name already exists, add suffix // !!!
    Object obj;
    boolean renameNeeded = false;
    while ((obj=Group.getRoot().findObject(newName, true))!=null)
    {
        if (obj==this)    // it's me :) already moved, fix data
        {
            templateData.setName(newName);
            this.updateTemplateFields();
            fixLinks();
            return true;
        }
        else
        {
            renameNeeded = true;
            newName = StringUtils.incrementName(newName, Constants.MOVE_SUFFIX);
        }
    }

    if (renameNeeded)
        return rename(newName);

    getParent().removeObject(Group.substractObjectName(getName()));
    setParent(null);
    Group.getRoot().addSubObject(newName, this, true);

    templateData.setName(newName);
    this.updateTemplateFields();
    fixLinks();
    unconditionalValidation();

    return true;
}

/**
 * @see com.cosylab.vdct.graphics.objects.Flexible#rename(String)
 */
public boolean rename(java.lang.String newName) {

    String newObjName = Group.substractObjectName(newName);
    String oldObjName = Group.substractObjectName(getName());


    if (!oldObjName.equals(newObjName))
    {
        getParent().removeObject(oldObjName);
        String fullName = com.cosylab.vdct.util.StringUtils.replaceEnding(getName(), oldObjName, newObjName);
        templateData.setName(fullName);
        getParent().addSubObject(newObjName, this);

        // fix connectors IDs
        Enumeration e = subObjectsV.elements();
        Object obj; Connector connector;
        while (e.hasMoreElements()) {
            obj = e.nextElement();
            if (obj instanceof Connector)
            {
                connector = (Connector)obj;
                String id = connector.getID();
                id = com.cosylab.vdct.util.StringUtils.replaceEnding(id, oldObjName, newObjName);
                connector.setID(id);
            }
        }
    }

    this.updateTemplateFields();

    // move if needed
    if (!moveToGroup(Group.substractParentName(newName)))
        fixLinks();            // fix needed

    return true;

}


/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:54)
 */
protected void destroyFields() {

    Object[] objs = new Object[subObjectsV.size()];
    subObjectsV.copyInto(objs);
    for (int i=0; i < objs.length; i++)
    {
        ((VisibleObject)objs[i]).destroy();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:54)
 */
protected void undestroyFields() {

    Object[] objs = new Object[subObjectsV.size()];
    subObjectsV.copyInto(objs);
    for (int i=0; i < objs.length; i++)
    {
        ((VisibleObject)objs[i]).setDestroyed(false);
    }
}



/**
 * @see com.cosylab.vdct.graphics.objects.SaveInterface#writeObjects(DataOutputStream, String)
 */
public void writeObjects(DataOutputStream file, NamingContext context, boolean export)
    throws IOException
{
    // do not generate template data if not is export mode
    // but write expand block
    if (!export)
    {

        final String nl = "\n";
        final String comma = ", ";
        final String quote = "\"";
        final String macro = "  "+DBResolver.MACRO+"(";
        final String ending = ")"+nl;

         // write comment
         if (getTemplateData().getComment()!=null)
             file.writeBytes(nl+getTemplateData().getComment());

        // expand start
        file.writeBytes(nl+DBResolver.EXPAND+"(\""+getTemplateData().getTemplate().getId()+"\""+
                        comma +
                            StringUtils.quoteIfMacro(getTemplateData().getName())
                         + ") {"+nl);

        // macros (properties)
        Map macros = getTemplateData().getProperties();
        Iterator i = getTemplateData().getPropertiesV().iterator();
        while (i.hasNext())
        {
            String name = i.next().toString();
            file.writeBytes(macro + name + comma + quote + StringUtils.removeQuotes(macros.get(name).toString()) + quote + ending);
        }

        // export end
        file.writeBytes("}"+nl);

        return;
    }


     //
      // export (generate, flatten) DB option
     //
     //String templateName = namer.getResolvedName(getName());
     String templateName = context.matchAndReplace(getName());



     /*// new ports
     Map ports = preparePorts(getTemplateData().getTemplate().getGroup(), namer.getSubstitutions());

     // new macros
     // macros have to be made out of macros and ports (^^^ it's probably same with ports)
     Map properties = prepareSubstitutions(getTemplateData(), namer.getSubstitutions(), namer.getPorts());
    */
    //Map properties = prepareSubstitutions(getTemplateData(), namer.getSubstitutions(), null);
    //Map ports = preparePorts(getTemplateData().getTemplate().getGroup(), properties, namer);
    // TODO !!! some strange path could break this code

     // new removedPrefix
     //String removedPrefix = namer.getRemovedPrefix();

     // new addedPrefix
     //String addedPrefix = null;

/*    //
    // no adding !!! anyway I noticed added prefix is not reset (to be checked, if this code is used someday)...
    //

     if (getParent()==null)
         addedPrefix = namer.getAddedPrefix();
     else
     {
         // resolve parent group name - this is then addedPrefix
         // there is a secial case: removedPrefix is 'parentName:", but parentName is 'parentName'
         String parentName = ((Group)getParent()).getAbsoluteName();

        if (parentName.equals(namer.getRemovedPrefix()+Constants.GROUP_SEPARATOR))
            addedPrefix = namer.getResolvedName(parentName+Constants.GROUP_SEPARATOR);
        else
             addedPrefix = namer.getResolvedName(parentName);

        int len = addedPrefix.length();
         if (len==0)
             addedPrefix = null;
         else if (addedPrefix.charAt(len-1)!=Constants.GROUP_SEPARATOR)
             addedPrefix = addedPrefix + Constants.GROUP_SEPARATOR;
     }
*/
     //NameManipulator newNamer = new DefaultNamer(namer.getFile(),removedPrefix, addedPrefix, properties, ports, export);

     file.writeBytes("\n# expand(\""+getTemplateData().getTemplate().getFileName()+"\", "+templateName+")\n");

     Group currentRoot = Group.getRoot();
     try
     {
         Group.setRoot(getTemplateData().getTemplate().getGroup());
         getTemplateData().getTemplate().getGroup().writeObjects(file, context.createNamingContextFor(getTemplateData()), export);
     }
     finally
     {
         Group.setRoot(currentRoot);
     }
     file.writeBytes("\n# end("+templateName+")\n");

}

/*public static Map prepareMap(NamingContext renamer, Hashtable cache) {
    if (renamer.getTemplateInstance()!=null && renamer.getParent()!=null) {
        Iterator i = renamer.getTemplateInstance().getPropertiesV().iterator();
        while (i.hasNext()) {
            String name=(String)i.next();
            String value=(String)renamer.getTemplateInstance().getProperties().get(name);
            value=renamer.resolveMacro(name, value);
        }
    }

    Iterator i = renamer.getTemplate().getGroup().getStructure().iterator();
    while (i.hasNext())
    {
        Object obj = i.next();
        if (obj instanceof Template)
        {
            Template t = (Template)obj;
            NamingContext newRenamer = renamer.createNamingContextFor(t.getTemplateData());

            Iterator i2 = t.getTemplateData().getTemplate().getPortsV().iterator();
            while (i2.hasNext())
            {
                    VDBPort port = (VDBPort)i2.next();
                    String value=newRenamer.resolvePort(port);
            }
        }
    }

    return renamer.getMap();
}*/

/**
 * Insert the method's description here.
 * @param substitutions <code>group</code> current substitutions
 */
public static Map prepareSubstitutions(VDBTemplateInstance templateData, Map substitutions, Map ports)
{
     // Note:
      // the macro values given in an expand(){} statement should not be
     // automatically passed down into any templates that are expanded within the
     // lower level file unless they are explicitly named as macros there too.
     Map properties = new Hashtable();

     if (Settings.getInstance().getGlobalMacros() && substitutions!=null) {
         properties.putAll(substitutions);
     }

    properties.putAll(templateData.getProperties());

     if (substitutions!=null || ports!=null)
     {
        // update values
         Iterator i = properties.keySet().iterator();
         while (i.hasNext())
         {
             Object key = i.next();
             String value = properties.get(key).toString();

            /*LinkProperties lp = new LinkProperties(properties.get(key));
            Object rec = Group.getRoot().findObject(lp.getRecord(), true);
            if (rec!=null) System.out.println("bingo:"+rec);
            if (rec !=null && rec instanceof Record) {
                lp.setRecord(namer.getResolvedName(((Record)rec).getRecordData()));
                value=lp.getFull();
            }*/

            if (substitutions != null) value = VDBTemplateInstance.applyProperties(value, substitutions);
            if (ports != null) value = VDBTemplateInstance.applyPorts(value, ports);
             properties.put(key, value);
         }
     }

    return properties;
}

/**
 * Insert the method's description here
 * @param substitutions <code>group</code> current substitutions
 */
public static Map preparePorts(Group group, Map substitutions, NameManipulator namer)
{
    HashMap map = new HashMap();

    Iterator i = group.getStructure().iterator();
    while (i.hasNext())
    {
        Object obj = i.next();
        if (obj instanceof Template)
        {
            Template t = (Template)obj;

            Iterator i2 = t.getTemplateData().getTemplate().getPortsV().iterator();
            while (i2.hasNext())
            {
                VDBPort port = (VDBPort)i2.next();

                String target = port.getTarget();

/*                LinkProperties lp = new LinkProperties(port);
                Object rec = Group.getRoot().findObject(lp.getRecord(), true);
                if (rec!=null) System.out.println("bingo:"+rec);
                if (rec !=null && rec instanceof Record) {
                    lp.setRecord(namer.getResolvedName(((Record)rec).getRecordData()));
                    target=lp.getFull();
                }*/

                // new macros
                // !!! this is done twice - optimize with buffering
                 Map newSubstitutions = prepareSubstitutions(t.getTemplateData(), substitutions, null);

                target = VDBTemplateInstance.applyProperties(target, newSubstitutions);

                // if target is a contains a port definition it might be defined in lower levels
                // try to resolve it

                if (target.matches(".*\\$\\([a-zA-Z0-9_:-]+\\.[a-zA-Z0-9_:-]+\\).*")) {
                    //  System.out.println("Recursive: "+target);
                    //    we have sequence od '(' .. '.' .. ')'

                    Map lowerLevelPorts = preparePorts(t.getTemplateData().getTemplate().getGroup(), newSubstitutions, namer);
                    /*
                        System.out.println("Ports at lower level:");
                        Iterator i3 = lowerLevelPorts.keySet().iterator();
                        while (i3.hasNext())
                        {
                            Object key = i3.next();
                            System.out.println("\t"+key+"="+lowerLevelPorts.get(key));
                        }
                    */
                    target = VDBTemplateInstance.applyPorts(target, lowerLevelPorts);
                }

                //if (target.indexOf('$')>=0)
            //System.out.println(port.getPortDefinition(t.getTemplateData().getName())+"="+target+"<map>:"+newSubstitutions+"<map>:"+substitutions);
                map.put(port.getPortDefinition(t.getTemplateData().getName()), target);
            }

        }
    }

    return map;
}

/**
 * @see com.cosylab.vdct.graphics.objects.SaveInterface#writeVDCTData(DataOutputStream, String)
 */
public void writeVDCTData(DataOutputStream file, NamingContext renamer, boolean export)
    throws IOException
{
    // No-op (done by writeObjects() method).
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
    return null;
}

/**
 * @param linkableMacros
 * @param macros
 * @param deep
 */
public void generateMacros(HashMap macros) {
    Object obj;
    Enumeration e = subObjectsV.elements();
    while (e.hasMoreElements())
    {
        obj = e.nextElement();
        if (obj instanceof TemplateEPICSMacro)
            LinkManagerObject.checkIfMacroCandidate(((Field)obj).getFieldData(), macros);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.LinkManagerObject.PopupMenuHandler
 */
private Template.PopupMenuHandler createPopupmenuHandler() {
    return new PopupMenuHandler();
}

/**
 * @param link
 * @param isRight
 */
public void fieldSideChange(EPICSLink link, boolean isRight)
{
    if (!link.isVisible())
        return;

    if (isRight)
    {
        leftFields--; rightFields++;
    }
    else
    {
        leftFields++; rightFields--;
    }

    if (initialized)
    {
        unconditionalValidation();
        com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    }
}

/**
 * @param field
 * @param oldValue
 * @param newValue
 */
public void fieldVisibilityChange(VDBFieldData fieldData, boolean newVisible)
{

    EPICSLink link = (EPICSLink)getSubObject(fieldData.getName());
    if (newVisible)
    {
        if (link.isRight())
            rightFields++;
        else
            leftFields++;
    }
    else
    {
        if (link.isRight())
            rightFields--;
        else
            leftFields--;
    }

    // manageLinks will be called later
    if (initialized)
    {
        if (link instanceof TemplateEPICSMacro)
            ((TemplateEPICSMacro)link).visilibityChanged(newVisible);
        else if (link instanceof TemplateEPICSPort)
            ((TemplateEPICSPort)link).visilibityChanged(newVisible);

        unconditionalValidation();
        com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    }

}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:54:43)
 * @return boolean
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public boolean isFirstField(Field field) {
    // find first field on the same side and compare

    EPICSLink ef = (EPICSLink)field;

    Enumeration e = subObjectsV.elements();
    Object obj;
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof EPICSLink)
        {
            EPICSLink tel = (EPICSLink)obj;
            // same side
            if (tel.isVisible() && tel.isRight()==ef.isRight())
                if (tel==ef)
                    return true;
                else
                    return false;
        }
    }

    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:53:47)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public boolean isLastField(Field field) {
    EPICSLink ef = (EPICSLink)field;

    for (int i= subObjectsV.size()-1; i>=0; i--)
        if (subObjectsV.elementAt(i) instanceof EPICSLink)
        {
            EPICSLink tel = (EPICSLink)subObjectsV.elementAt(i);
            // same side
            if (tel.isVisible() && tel.isRight()==ef.isRight())
                if (tel==ef)
                    return true;
                else
                    return false;
        }
    return false;

}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public void moveFieldDown(Field field) {
    // move visual field
    EPICSLink ef = (EPICSLink)field;

    Vector fields = getSubObjectsV();
    int pos = fields.indexOf(ef);

    pos++;
    while (pos<fields.size())
    {
        EPICSLink elo;
        Object obj = fields.elementAt(pos);
        if ((obj instanceof EPICSLink) &&
            (elo=((EPICSLink)obj)).isRight() == ef.isRight() &&
            elo.isVisible())
            break;
        else
            pos++;
    }

    if (pos<fields.size()) {
        fields.removeElement(ef);
        fields.insertElementAt(ef, pos);
        revalidateFieldsPosition();
    }
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.MoveFieldDownAction(ef));
    //InspectorManager.getInstance().updateObject(this);
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public void moveFieldUp(Field field) {
    // move visual field
    EPICSLink ef = (EPICSLink)field;

    Vector fields = getSubObjectsV();
    int pos = fields.indexOf(ef);
    pos--;
    while (pos>=0)
    {
        EPICSLink elo;
        Object obj = fields.elementAt(pos);
        if ((obj instanceof EPICSLink) &&
            (elo=((EPICSLink)obj)).isRight() == ef.isRight() &&
            elo.isVisible())
            break;
        else
            pos--;
    }

    if (pos>=0) {
        fields.removeElement(ef);
        fields.insertElementAt(ef, pos);
        revalidateFieldsPosition();
    }

    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.MoveFieldUpAction(ef));
    //InspectorManager.getInstance().updateObject(this);
}

/**
 * @see com.cosylab.vdct.graphics.objects.Morphable#getType()
 */
public String getType() {
    return templateData.getTemplate().getDescription();
}
/**
 * @see com.cosylab.vdct.graphics.objects.Morphable#morph(java.lang.String)
 */
public boolean morph(String newType) {

    // desc -> type
    String type = null;
    Enumeration templates = VDBData.getTemplates().keys();
    while (templates.hasMoreElements())
    {
        String key = templates.nextElement().toString();
        VDBTemplate t = (VDBTemplate)VDBData.getTemplates().get(key);
        if (newType.equals(t.getDescription()))
        {
            type = key; break;
        }
    }

    if (type == null)
        return false;

    //    copies VDBData
    VDBTemplateInstance templateInstance = VDBData.morphVDBTemplateInstance(templateData, type, getName());

    if (templateInstance==null) {
        Console.getInstance().println("o) Interal error: failed to morph template "+getName()+" ("+getType()+")!");
        return false;
    }

    setTemplateInstance(templateInstance);

    return true;
}

/**
 * @param templateInstance
 */
public void setTemplateInstance(VDBTemplateInstance templateInstance)
{
        this.templateData = templateInstance;

        validate();

        // update inspector
        InspectorManager.getInstance().updateObject(this);
}


/**
 * @see com.cosylab.vdct.graphics.objects.Morphable#getTargets()
 */
public Object[] getTargets() {

    Stack tis = DrawingSurface.getInstance().getTemplateStack();

    Enumeration templates = VDBData.getTemplates().keys();
    ArrayList al = new ArrayList(VDBData.getTemplates().size());
    while (templates.hasMoreElements())
    {
        String key = templates.nextElement().toString();
        VDBTemplate t = (VDBTemplate)VDBData.getTemplates().get(key);
        if (t != this.getTemplateData().getTemplate() &&
            !tis.contains(t))    // do not allow cyclic...
            al.add(t.getDescription());
    }

    Object[] desc = new Object[al.size()];
    al.toArray(desc);
    return desc;
}

}
