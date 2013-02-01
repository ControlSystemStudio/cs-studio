from org.csstudio.opibuilder.scriptUtil import PVUtil
from org.csstudio.opibuilder.scriptUtil import ColorFontUtil
#from org.csstudio.opibuilder.scriptUtil import ConsoleUtil

table = widget.getTable()
func = display.getPropertyValue("name")

i = 0
row = 0
col = 3
#ConsoleUtil.writeInfo("Trigger PV : " + triggerPV.getName());
while triggerPV != pvs[i]:
    if col == 5:
        if pvs[i+1].getName() == "epics://" + func + ":" + table.getCellText(row, 0) + "PLC-IOCHLTS":
            col = col+1
        else:
            col = 3
            row = row+1
    else:
        col += 1
        if col > 6:
           row += 1
           col = 3
    i += 1

table.setCellText(row, col, PVUtil.getString(triggerPV))
    
s = PVUtil.getSeverity(triggerPV)
    
color = ColorFontUtil.WHITE
if s == 0:
    color = ColorFontUtil.GREEN
elif s == 1:
    color = ColorFontUtil.RED
elif s == 2:
    color = ColorFontUtil.YELLOW
elif s == 3:
    color = ColorFontUtil.PINK
    
table.setCellBackground(row, col, color)