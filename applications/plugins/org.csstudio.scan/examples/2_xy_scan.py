"""
Schedule scan with parameters from BOY script

@author: Kay Kasemir
"""

from scan_client import *
from scan_ui import *

# Fetch parameters from display
x0 = getWidgetPVDouble(display, "x0")
x1 = getWidgetPVDouble(display, "x1")
dx = getWidgetPVDouble(display, "dx")

y0 = getWidgetPVDouble(display, "y0")
y1 = getWidgetPVDouble(display, "y1")
dy = getWidgetPVDouble(display, "dy")

simu = getWidgetPVBool(display, "simu")
neutrons = getWidgetPVDouble(display, "neutrons")

name = getWidgetPVString(display, "name")

if getWidgetPVLong(display, "updown") > 0:
    toggle = -1
else:
    toggle = 1

#from org.eclipse.jface.dialogs import MessageDialog
#MessageDialog.openWarning(
#        None, "Type", "Type is " + delay.__class__.__name__)       

# Create scan sequence
seq = CommandSequence(
[
  LoopCommand('xpos', min(x0, x1), max(x0, x1), max(0.1, abs(dx)),
    LoopCommand('ypos', min(y0, y1), max(y0, y1), toggle * max(0.1, abs(dy)),
    [
       WaitCommand('neutrons', Comparison.INCREASE_BY, neutrons),
       LogCommand([ 'xpos', 'ypos', 'readback' ])
    ]
    )
  )
]
)

if simu:
    simu = scan.simulate(seq)
    showSimulation(simu)
else:
    # Submit scan
    id = scan.submit(name, seq);

    # Open scan monitor and plot, configured for this scan
    showScans()
    showPlot(name, id, 'xpos', 'ypos')

    # Update name of the scan
    setWidgetPV(display, "name", incrementScan(name))
