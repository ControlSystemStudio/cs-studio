"""
Schedule alignment scan with parameters from BOY script

@author: Kay Kasemir
"""

from scan_client import *
from scan_ui import *

# Fetch parameters from display
x0 = getWidgetPVDouble(display, "x0")
x1 = getWidgetPVDouble(display, "x1")
dx = getWidgetPVDouble(display, "dx")
name = getWidgetPVString(display, "name")

# Display stuff in dialog box for debugging:
#from org.eclipse.jface.dialogs import MessageDialog
#MessageDialog.openWarning(
#        None, "Type", "Type is " + delay.__class__.__name__)       

# Create scan
seq = CommandSequence(
[
  # Scan something
  SetCommand('ypos', 3.0),
  LoopCommand('xpos', min(x0, x1), max(x0, x1), max(0.1, abs(dx)),
  [
    # Slow scan down so it's more interesting to watch
    DelayCommand(0.2),
    # Log the scan parameters (xpos) and the signal
    LogCommand([ 'xpos', 'signal' ])
  ]
  ),
  # Invoke another script on server to find peak
  ScriptCommand('FindPeak')
]
)

# Submit scan
id = scan.submit(name, seq);

# Open scan monitor
showScans()
# Open plot for scan (xpos, signal) as well as result of fit
showIndividualPlot(name, id, 'xpos', 'signal', 'fit')

# Update name of the scan
setWidgetPV(display, "name", incrementScan(name))

# This script ends, scan with fit will be performed on server, plot will update as that's happening