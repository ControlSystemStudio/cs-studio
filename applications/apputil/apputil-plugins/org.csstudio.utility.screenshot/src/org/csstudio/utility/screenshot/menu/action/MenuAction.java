
package org.csstudio.utility.screenshot.menu.action;

import org.eclipse.jface.action.Action;

public class MenuAction extends Action
{
    private String name = null;

    public MenuAction(String n)
    {
        super();

        name = n;

        setText(name);
    }

    @Override
    public void run()
    {
        System.out.println(name + " Ausgew�hlt...");
    }
}
