package originals;

import java.awt.*;
import java.applet.*;

import org.csstudio.utility.eliza.ElizaParse;

/**
 * Eliza in Java. Adapted from a BASIC program I found floating on the net.
 * Eliza was originally written by Joseph Weizenbaum. This version is an
 * adaption of the program as it appeared in the memorable magazine Create
 * Computing around 1981. <br>
 * Jesper Juul - jj@pobox.com. Copenhagen, February 24th, 1999.
 */

public class ElizaApplet extends Applet
{
    private static final long serialVersionUID = 1L;

    int w, h;

    TextField type;

    List list;

    ElizaParse eliza = new ElizaParse();

    public void init()
    {
        w = size().width;
        h = size().height;
        setBackground(Color.black);
        setForeground(Color.green);
        setLayout(new BorderLayout());
        type = new TextField();
        type.setBackground(Color.black);
        type.setForeground(Color.green);
        add(type, "South");

        list = new List();
        list.setBackground(Color.black);
        list.setForeground(Color.green);
        add(list, "Center");
        addText(eliza.getIntroMsg());
        doprint();
    }

    public boolean handleEvent(Event evt)
    {
        if (evt.target == type)
        {
            if (evt.id == Event.ACTION_EVENT)
            {
                handleLine(type.getText());
                type.setText("");
                return true;
            }
        }
        return false;
    }

    void handleLine(String s)
    {
        if (s == null) return;
        s = s.trim();
        if (s.length() < 1) return;
        addText(" >" + s);
        eliza.handleLine(s);
        doprint();
    }

    void doprint()
    {
        while (eliza.msg.size() > 0)
        {
            addText((String) eliza.msg.elementAt(0));
            eliza.msg.removeElementAt(0);
        }
    }

    void addText(String s[])
    {
        for (int i = 0; i < s.length; i++)
        {
            addText(s[i]);
        }
    }

    void addText(String s)
    {
        int width = list.getFontMetrics(list.getFont()).stringWidth(s);
        if (width > w - 25)
        {
            double part = (double) width / (double) (w - 27);
            int n = (int) (s.length() / part);
            list.addItem(s.substring(0, n));
            addText(s.substring(n));
            return;
        }
        list.addItem(s);
        list.makeVisible(list.getItemCount() - 1);
    }

}
