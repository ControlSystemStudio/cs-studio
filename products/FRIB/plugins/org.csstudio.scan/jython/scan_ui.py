"""
Scan UI Tools

Helpers for scan server UI.

This code depends on eclipse packages and
can only be used within CSS BOY scripts.

@author: Kay Kasemir
"""

import re
from org.eclipse.ui import PlatformUI
from org.csstudio.opibuilder.scriptUtil import PVUtil
from org.csstudio.scan.ui import SimulationDisplay

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

def getWidgetPVBool(display, widget):
    """Fetch value of a widget's PV
       @param display BOY Display
       @param widget Widget name
       @return Value of that PV as boolean
    """
    return getWidgetPVLong(display, widget) != 0

def getWidgetPVString(display, widget):
    """Fetch value of a widget's PV
       @param display BOY Display
       @param widget Widget name
       @return Value of that PV as string
    """
    pv = display.getWidget(widget).getPV()
    return str(PVUtil.getString(pv))

def setWidgetPV(display, widget, value):
    """Set value of a widget's PV
       @param display BOY Display
       @param widget Widget name
       @param value Value of that PV as string
    """
    pv = display.getWidget(widget).getPV()
    pv.setValue(value)

def incrementScan(name):
    """When called with a scan name like 'XY Scan',
       return the next name 'XY Scan1'.
       When called with name that ends in number
       like 'XY Scan1', return 'XY Scan2'
    """
    pieces = re.match("([^0-9]*)([0-9]+)\\Z", name)
    if pieces:
        name = pieces.group(1)
        number = int(pieces.group(2)) + 1
    else:
        # Leave name as received, cannot parse
        number = 1
    return name + str(number)

def __showView__(view_id, secondary=0):
    """ Display Eclipse view
        @param view_id: View ID
        @param secondary: Optional ID for secondary view 
        @return View
    """
    workbench = PlatformUI.getWorkbench()
    window = workbench.getActiveWorkbenchWindow()
    page = window.getActivePage()
    if secondary > 0:
        return page.showView(view_id, secondary, page.VIEW_ACTIVATE)
    else:
        return page.showView(view_id)

def showScans():
    """ Display Scan Monitor"""
    __showView__("org.csstudio.scan.ui.scanmonitor")

def showSimulation(simu):
    """ Display Scan Simulation"""
    SimulationDisplay.show(simu)

_scan_plots = 0

def showPlot(*args):
    """ Display Scan Plot, updating the 'main' plot view if already open
    
        May be called with list of scan_name, scan_id, xdevice, ydevice, ydevice, ...
        
        Scan name and ID will set plot to a specific scan.
        X and Y devices will select the data to display.
    """
    view = __showView__("org.csstudio.scan.ui.plot.view")
    if len(args) >= 2:
        # Configure to display scan
        view.selectScan(args[0], args[1])
    if len(args) >= 4:
        # Configure to display devices
        view.selectDevices(args[2:])
        

def showIndividualPlot(*args):
    """ Display Scan Plot, using a new plot view
    
        May be called with list of scan_name, scan_id, xdevice, ydevice, ydevice, ...
        
        Scan name and ID will set plot to a specific scan.
        X and Y devices will select the data to display.
    """
    global _scan_plots
    _scan_plots += 1
    secondary = "scan_ui" + str(_scan_plots)
    view = __showView__("org.csstudio.scan.ui.plot.view", secondary)
    if len(args) >= 2:
        # Configure to display scan
        view.selectScan(args[0], args[1])
    if len(args) >= 4:
        # Configure to display devices
        view.selectDevices(args[2:])
