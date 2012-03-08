"""
Schedule scan with parameters from BOY script

@author: Kay Kasemir
"""

from scan_client import *
from scan_ui import *

x0 = getWidgetPVDouble(display, "x0")
x1 = getWidgetPVDouble(display, "x1")
dx = getWidgetPVDouble(display, "dx")

y0 = getWidgetPVDouble(display, "y0")
y1 = getWidgetPVDouble(display, "y1")
dy = getWidgetPVDouble(display, "dy")

name = getWidgetPVString(display, "name")

if getWidgetPVLong(display, "updown") > 0:
    toggle = -1
else:
    toggle = 1

#from org.eclipse.jface.dialogs import MessageDialog
#MessageDialog.openWarning(
#        None, "Type", "Type is " + delay.__class__.__name__)       

# Set commands mostly added to create delay for demo
# because scan is otherwise too fast to see
id = scan(name,
     ('xpos', min(x0, x1), max(x0, x1), max(0.1, abs(dx))),
     ('ypos', min(y0, y1), max(y0, y1), toggle * max(0.1, abs(dy))),
     SetCommand('setpoint', 1.0, 'readback'),
     SetCommand('setpoint', 2.0, 'readback'),
     'readback')

showScans()
showPlot(name, id, 'xpos', 'ypos')




