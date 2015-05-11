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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS draw"AS IS" AND
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
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.*;
import javax.swing.*;
import com.cosylab.vdct.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.events.*;
import com.cosylab.vdct.undo.DescriptionChangeAction;
import com.cosylab.vdct.util.*;

/**
 * @author ssah, msekoran
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class TextBox extends VisibleObject implements BorderObject, Movable, Selectable, Popupable, Descriptable, Flexible,
    Clipboardable
{

class PopupMenuHandler implements ActionListener
{

public void actionPerformed(ActionEvent event)
{
    String action = event.getActionCommand();

    if(action.equals(changeTextString))
        showChangeTextDialog();

    else if(action.equals(changeFontString))
    {
        FontSelector demoFont = new FontSelector();

        demoFont.setFont(getFont());
        demoFont.getDisplayPanel().setDisplayText(getDescription());
        demoFont.getDisplayPanel().setForeground(getColor());
        demoFont.getDisplayPanel().setBackground(Constants.BACKGROUND_COLOR);

        JOptionPane optionPane = new JOptionPane(demoFont, JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION);

        optionPane.setPreferredSize(new Dimension(480, 172));

        JDialog dialog = optionPane.createDialog(VisualDCT.getInstance(),
            "Select a Font, Font Style, and Font Size...");
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();

        if((selectedValue != null) && (selectedValue instanceof Integer)
            && (((Integer)selectedValue).intValue() == JOptionPane.OK_OPTION))
        {
            setFont(demoFont.getFont());
        }

        CommandManager.getInstance().execute("RepaintWorkspace");

    }
    else if(action.equals(colorString))
    {
        Color newColor = ColorChooser.getColor(colorTitleString, getColor());

        if(newColor != null)
        {
            setColor(newColor);

            currentColor = newColor;
        }

        CommandManager.getInstance().execute("RepaintWorkspace");
    }
    else if(action.equals(borderString))
    {
        border = !border;
        currentBorder = border;

        CommandManager.getInstance().execute("RepaintWorkspace");
    }
}

}

private String hashId;
private String name;
private Vertex startVertex;
private Vertex endVertex;
private boolean border;

private static final String colorString = "Color...";
private static final String colorTitleString = "Text Color";
private static final String borderString = "Border";
private static final String changeFontString = "Change Font...";
private static final String changeTextString = "Change Text...";

private static Color currentColor = Constants.LINE_COLOR;
private static boolean currentBorder = false;

private static final String nullString = "";
private static final String htmlString = "<html>";
protected String description = null;
protected JLabel label = null;
protected boolean htmlMode = false;

protected Hashtable map;
protected AttributedString attText = null;
protected AttributedCharacterIterator paragraph = null;
protected int paragraphStart = 0;
protected int paragraphEnd = 0;
protected LineBreakMeasurer lineMeasurer = null;
protected int[] breaks = null;
protected FontRenderContext frc = null;

protected double fontScale = 0.0;
protected Font rfont = null;



public void showChangeTextDialog()
{
    JTextArea textArea = new JTextArea(getDescription());

    textArea.setFont(getFont());

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION);

    optionPane.setPreferredSize(new Dimension(480, 256));

    JDialog dialog = optionPane.createDialog(VisualDCT.getInstance(), "Text Box Content");
    dialog.setResizable(true);
    dialog.setVisible(true);

    Object selectedValue = optionPane.getValue();

    if((selectedValue != null) && (selectedValue instanceof Integer)
        && (((Integer)selectedValue).intValue() == JOptionPane.OK_OPTION))
    {
        setDescription(textArea.getText());
    }

    CommandManager.getInstance().execute("RepaintWorkspace");
}

private String getAvailableHashId()
{
    int grLineNumber = 0;
    String testHashId = "TB" + String.valueOf(grLineNumber);

    while(getParent().containsObject(testHashId))
    {
        grLineNumber++;
        testHashId = "TB" + String.valueOf(grLineNumber);
    }

    return testHashId;
}

public TextBox(String parName, Group parentGroup, int posX, int posY, int posX2, int posY2)
{
    super(parentGroup);

    startVertex = new Vertex(this, posX, posY);
    endVertex = new Vertex(this, posX2, posY2);

    border = currentBorder;

    revalidatePosition();

    if(parName == null)
    {
        hashId = getAvailableHashId();

        if(parentGroup.getAbsoluteName().length() > 0)
            name = parentGroup.getAbsoluteName() + Constants.GROUP_SEPARATOR + hashId;
        else
            name = hashId;
    }
    else
        name = parName;

    label = new javax.swing.JLabel() {
            public void paint(Graphics g) {
                Shape clip = g.getClip();

                Rectangle clipRect = g.getClipBounds();
                Rectangle newClipRect = new Rectangle(this.getX(), this.getY(), getWidth(), getHeight());
                if (clipRect!= null) newClipRect = newClipRect.intersection(clipRect);
                if (!newClipRect.isEmpty()) {
                    newClipRect.setLocation(-this.getX(), -this.getY());

                    g.translate(this.getX(),this.getY());
                    g.setClip(newClipRect);
                    super.paint(g);
                    g.translate(-this.getX(),-this.getY());
                    g.setClip(clip);
                }
            }
        };

    setColor(currentColor);

    label.setVerticalAlignment(JLabel.TOP);

    setDescription(nullString);
    setFont(label.getFont());

    // initialize plain text helpers
    map = new Hashtable();
    attText = null;
    paragraph = null;;
    paragraphStart = 0;
    paragraphEnd = 0;
    frc = new FontRenderContext(null, false, false);


}


protected void drawMultiLineText(Graphics g, float drawPosX, float drawPosY, float formatWidth)
{
    if (formatWidth<4 || lineMeasurer==null)
        return;

    // TBD: no tabs are supported

    Graphics2D graphics2D = (Graphics2D)g;

    lineMeasurer.setPosition(paragraphStart);

    int breaksPos = 0;

    // Get lines from lineMeasurer until the entire
    // paragraph has been displayed.
    while (lineMeasurer.getPosition() < paragraphEnd) {

        // Retrieve next layout.
        TextLayout layout = lineMeasurer.nextLayout(formatWidth, breaks[breaksPos], false);

        // Move y-coordinate by the ascent of the layout.
        drawPosY += layout.getAscent();

        // Draw the TextLayout at (drawPosX, drawPosY).
        layout.draw(graphics2D, drawPosX, drawPosY);

        // Move y-coordinate in preparation for next layout.
        drawPosY += layout.getDescent() + layout.getLeading();

        // if EOL found, make a new line (assuming that descent and leading are all the same)
        if (lineMeasurer.getPosition()>=breaks[breaksPos])
        {
                breaksPos++;
                drawPosY += layout.getDescent() + layout.getLeading();
        }

    }

 }

public void setColor(java.awt.Color color)
{
    super.setColor(color);
    label.setForeground(getColor());
}

public void setFont(Font parFont)
{
    super.setFont(parFont);
    updateTextFont(true, getRscale());
}

public void accept(Visitor visitor)
{
    visitor.visitGroup();
}

public boolean checkMove(int dx, int dy)
{
    //ViewState view = ViewState.getInstance();

    if(startVertex.checkMove(dx, dy) && endVertex.checkMove(dx, dy))
        return true;

    return false;
}

public Flexible copyToGroup(String group)
{
    String newName;
    if(group.equals(nullString))
        newName = Group.substractObjectName(getName());
    else
        newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

// object with new name already exists, add suffix ///!!!
    while(Group.getRoot().findObject(newName, true) != null)
        newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);

    TextBox grTextBox = new TextBox(newName, null,
                        startVertex.getX(), startVertex.getY(),
                        endVertex.getX(), endVertex.getY());
    grTextBox.setDescription(description);
    grTextBox.setColor(getColor());
    Group.getRoot().addSubObject(newName, grTextBox, true);

    //ViewState view = ViewState.getInstance();
    //grTextBox.move(20 - view.getRx(), 20 - view.getRy());

    unconditionalValidation();
    return grTextBox;
}

public void destroy()
{
    super.destroy();
    if(getParent() != null)
        getParent().removeObject(Group.substractObjectName(name));

    if(!startVertex.isDestroyed())
        startVertex.destroy();

    if(!endVertex.isDestroyed())
        endVertex.destroy();
}

protected void draw(Graphics g, boolean hilited)
{
    ViewState view = ViewState.getInstance();

    int posX = getRx() - view.getRx();
    int posY = getRy() - view.getRy();
    int rwidth = getRwidth();
    int rheight = getRheight();

    if(!((posX > view.getViewWidth()) || (posY > view.getViewHeight())
        || ((posX + rwidth) < 0) || ((posY + rheight) < 0)))
    {

        double Rscale = view.getScale();
        boolean zoom = (Rscale < 1.0) && view.isZoomOnHilited() && view.isHilitedObject(this);
        if (zoom) {
            rwidth /= Rscale;
            rheight /= Rscale;
            posX -= (rwidth - getRwidth())/2;
            posY -= (rheight - getRheight())/2;
            if (view.getRx() < 0)
                posX = posX < 0 ? 2 : posX;
            if (view.getRy() < 0)
                posY = posY <= 0 ? 2 : posY;
            Rscale = 1.0;
            g.setColor(Constants.BACKGROUND_COLOR);
            g.fillRect(posX, posY, rwidth, rheight);
            updateTextFont(true, Rscale);
        }

        if(view.isSelected(this))
        {
            g.setColor(Constants.HILITE_COLOR);
            g.drawRect(posX - 2, posY - 2, rwidth + 4, rheight + 4);
        }

        //int size = (int)(getFont().getSize()*getRscale());
        //Font f = FontMetricsBuffer.getInstance().getFont(getFont().getFamily(), size, getFont().getStyle());

        if (htmlMode)
        {
            Color tmp = label.getForeground();
            Color c = tmp;
            if (c.equals(Constants.BACKGROUND_COLOR))
                if (c.equals(Color.black))
                    c=Color.white;
                else
                    c=Color.black;
            label.setForeground(c);
            label.setBounds(posX + 2, posY + 2, rwidth - 4, rheight - 4);
            label.paint(g);
            label.setForeground(tmp);
        }
        else
        {
            Shape clip = g.getClip();

            Rectangle clipRect = g.getClipBounds();
            Rectangle newClipRect = new Rectangle(posX, posY, rwidth, rheight);
            if (clipRect != null) newClipRect = newClipRect.intersection(clipRect);

            if (!newClipRect.isEmpty()) {
                g.setClip(newClipRect);

                g.setColor(getVisibleColor());
                drawMultiLineText(g, posX+2, posY+2, rwidth-4);

                g.setClip(clip);
            }
        }

        if(border || hilited)
            drawDashedBorder(g, hilited, view, posX, posY, rwidth, rheight);

    }

}

public void drawDashedBorder(Graphics g, boolean hilited)
{
    ViewState view = ViewState.getInstance();

    int posX = getRx() - view.getRx();
    int posY = getRy() - view.getRy();
    int rwidth = getRwidth();
    int rheight = getRheight();

    drawDashedBorder(g, hilited, view, posX, posY, rwidth, rheight);
}

private void drawDashedBorder(Graphics g, boolean hilited,
    ViewState view,
    int posX,
    int posY,
    int rwidth,
    int rheight)
{
// draw dashed border
    if (hilited)
        g.setColor(Constants.SELECTION_COLOR);
    else
    {
        g.setColor(getVisibleColor());
    }

    //double scale = view.getScale();

    int posX2 = posX + rwidth;
    int posY2 = posY + rheight;

    if(((posX != posX2) || (posY != posY2)))
    {
        int curX = posX;
        while(curX <= posX2)
        {
            int curX2 = curX + Constants.DASHED_LINE_DENSITY;

            if (curX2 > posX2)
                curX2 = posX2;

            g.drawLine(curX, posY, curX2, posY);
            g.drawLine(curX, posY2, curX2, posY2);

            curX += 2 * Constants.DASHED_LINE_DENSITY;
        }

        int curY = posY;
        while(curY <= posY2)
        {
            int curY2 = curY + Constants.DASHED_LINE_DENSITY;

            if(curY2 > posY2)
                curY2 = posY2;

            g.drawLine(posX, curY, posX, curY2);
            g.drawLine(posX2, curY, posX2, curY2);

            curY += 2 * Constants.DASHED_LINE_DENSITY;
        }

    }
}

public static boolean getCurrentBorder()
{
    return currentBorder;
}

public Vertex getEndVertex()
{
    return endVertex;
}

public String getFlexibleName()
{
    return name;
}

public String getHashID()
{
    return hashId;
}

public Vector getItems()
{
    Vector items = new Vector();
    ActionListener al = new PopupMenuHandler();

    JMenuItem changeTextItem = new JMenuItem(changeTextString);
    changeTextItem.addActionListener(al);
    items.addElement(changeTextItem);

    JMenuItem changeFontItem = new JMenuItem(changeFontString);
    changeFontItem.addActionListener(al);
    items.addElement(changeFontItem);

    JMenuItem colorItem = new JMenuItem(colorString);
    colorItem.addActionListener(al);
    items.addElement(colorItem);

    JCheckBoxMenuItem borderItem = new JCheckBoxMenuItem(borderString);
    borderItem.setSelected(border);
    borderItem.addActionListener(al);
    items.addElement(borderItem);

    return items;
}

public String getName()
{
    return name;
}

public Vertex getStartVertex()
{
    return startVertex;
}

public boolean move(int dx, int dy)
{
    if(checkMove(dx, dy))
    {
        startVertex.move(dx, dy);
        endVertex.move(dx, dy);

        revalidatePosition();

        return true;
    }

    return false;
}

public boolean moveToGroup(String group)
{
    String currentParent = Group.substractParentName(getName());
    if(group.equals(currentParent))
        return false;

    //String oldName = getName();
    String newName;
    if (group.equals(nullString))
        newName = Group.substractObjectName(getName());
    else
        newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

    // object with new name already exists, add suffix // !!!
    Object obj;
    boolean renameNeeded = false;
    while ((obj=Group.getRoot().findObject(newName, true))!=null)
    {
        if (obj==this)    // it's me :) already moved, fix data
        {
            name = newName;
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

    name = newName;
    unconditionalValidation();

    return true;
}

public boolean rename(String newName)
{
    String newObjName = Group.substractObjectName(newName);
    String oldObjName = Group.substractObjectName(getName());

    if(!oldObjName.equals(newObjName))
    {
        getParent().removeObject(oldObjName);
        name = StringUtils.replaceEnding(getName(), oldObjName, newObjName);
        getParent().addSubObject(newObjName, this);
    }

   // move if needed
    moveToGroup(Group.substractParentName(newName));

    return true;
}

public void revalidatePosition()
{
    startVertex.revalidatePosition();
    endVertex.revalidatePosition();

    int posX = startVertex.getX();
    int posY = startVertex.getY();

    int posX2 = endVertex.getX();
    int posY2 = endVertex.getY();

    setX(Math.min(posX, posX2));
    setY(Math.min(posY, posY2));
    setWidth(Math.abs(posX2 - posX));
    setHeight(Math.abs(posY2 - posY));

    double rscale = getRscale();
    setRwidth((int)(getWidth() * rscale));
    setRheight((int)(getHeight() * rscale));

    setRx((int)(getX() * rscale));
    setRy((int)(getY() * rscale));
}

public void setBorder(boolean parBorder)
{
    border = parBorder;
}

protected void validate()
{
    startVertex.validate();
    endVertex.validate();

    revalidatePosition();

    double rscale = getRscale();

    setRwidth((int)(getWidth() * rscale));
    setRheight((int)(getHeight() * rscale));

    updateTextFont(false, getRscale());
}

/**
 * @see com.cosylab.vdct.graphics.objects.Descriptable#getDescription()
 */
public String getDescription()
{
    return description;
}

/**
 * @see com.cosylab.vdct.graphics.objects.Descriptable#setDescription(String)
 */
public void setDescription(String description)
{
    if (this.description!=null && !this.description.equals(nullString) && !this.description.equals(description))
        com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
                new DescriptionChangeAction(this, this.description, description));

    this.description = description;
    updateText();
}

private void updateTextFont(boolean force, double Rscale) {
    if ((force || Rscale!=fontScale) && getFont()!=null)
    {
        fontScale = Rscale;
        int size = (int)(getFont().getSize()*fontScale);
        if (rfont!=null && size==rfont.getSize())
            return;
        rfont = FontMetricsBuffer.getInstance().getFont(getFont().getFamily(), size, getFont().getStyle());
        updateText();
    }
}


/**
 */
private void updateText()
{

    if (description.startsWith(htmlString))
    {
        if (rfont!=null && rfont!=label.getFont())
            label.setFont(rfont);

        htmlMode = true;
        label.setText(description);
    }
    else
    {
        htmlMode = false;


        if (description.length()>1)
        {
            // let attText be an AttributedCharacterIterator containing at least one character, otherwise null
            if (rfont!=null) map.put(TextAttribute.FONT, rfont);

            attText = new AttributedString(description, map);

            paragraph = attText.getIterator();
            paragraphStart = paragraph.getBeginIndex();
            paragraphEnd = paragraph.getEndIndex();

            int i = 1;
            char c = paragraph.first();        // skip first char
            for (paragraph.next(); c != CharacterIterator.DONE; c = paragraph.next())
                 if (c == '\n')
                     i++;
            breaks = new int[i];

            i = 0;
            c = paragraph.first();                // skip first char
            for (paragraph.next(); c != CharacterIterator.DONE; c = paragraph.next())
                 if (c == '\n')
                     breaks[i++]=paragraph.getIndex();
            breaks[i] = paragraphEnd;

            lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        }
        else
        {
            lineMeasurer = null;
            paragraph = null;
            attText = null;
            breaks = null;
        }
    }
}

/**
 * Returned value inicates change
 * Creation date: (21.12.2000 22:21:12)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param x int
 * @param y int
 */
public VisibleObject hiliteComponentsCheck(int x, int y) {

    if (startVertex.intersects(x, y)!=null)
        return startVertex;
    if (endVertex.intersects(x, y)!=null)
        return endVertex;
    return null;
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
 * Returns the border.
 * @return boolean
 */
public boolean isBorder()
{
    return border;
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getX()
 */
public int getX() {
    return Math.min(startVertex.getX(), endVertex.getX());
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getY()
 */
public int getY() {
    return Math.min(startVertex.getY(), endVertex.getY());
}

public void snapToGrid() {
    startVertex.snapToGrid();
    endVertex.snapToGrid();
}

}
