"""
Scan UI Tools

Helpers for scan server UI.

This code depends on eclipse packages and
can only be used within CSS BOY scripts.

@author: Kay Kasemir
"""

import org.eclipse.ui.PlatformUI
from org.csstudio.opibuilder.scriptUtil import PVUtil


def getWidgetPVDouble(display, widget):
    """Fetch value of a widget's PV
       @param display BOY Display
       @param widget Widget name
       @return Value of that PV as double
    """
    pv = display.getWidget(widget).getPV()
    return PVUtil.getDouble(pv);


def getWidgetPVLong(display, widget):
    """Fetch value of a widget's PV
       @param display BOY Display
       @param widget Widget name
       @return Value of that PV as long
    """
    pv = display.getWidget(widget).getPV()
    return PVUtil.getLong(pv);


def getWidgetPVString(display, widget):
    """Fetch value of a widget's PV
       @param display BOY Display
       @param widget Widget name
       @return Value of that PV as string
    """
    pv = display.getWidget(widget).getPV()
    return str(PVUtil.getString(pv))


def __showView__(view_id):
    """ Display Eclipse view
        @param view_id View ID
        @return View
    """
    workbench = org.eclipse.ui.PlatformUI.getWorkbench()
    window = workbench.getActiveWorkbenchWindow()
    page = window.getActivePage()
    return page.showView(view_id)


def showScans():
    """ Display Scan Monitor"""
    __showView__("org.csstudio.scan.ui.scanmonitor")


def showPlot(*args):
    """ Display Scan Plot for a given scan
        @param name, id:         Optional Scan name and ID to show
        @param xdevice, ydevice: Optional X and Y device to use for plot
    """
    view = __showView__("org.csstudio.scan.ui.plot.view")
    if len(args) >= 2:
        # Configure it to display scan and devices
        view.selectScan(args[0], args[1])
    if len(args) >= 4:
        view.selectDevices(args[2], args[3])

