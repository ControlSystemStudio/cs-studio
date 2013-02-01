from org.csstudio.opibuilder.scriptUtil import PVUtil
from org.csstudio.opibuilder.scriptUtil import ColorFontUtil


table = widget.getTable()

#Fill PV Name only once
if widget.getVar("firstTime") == None:
    widget.setVar("firstTime", True)
    i=0
    for pv in pvs:
        # By default pv.getName() gives name with 'epics://' prefix. Rip it off before showing
        table.setCellText(i, 0, pv.getName()[8::])
        if not pv.isConnected():
            table.setCellText(i, 1, "Disconnected")
        i+=1
    # Based on value of macro SHOW_PLC_IOC, enable visibility of PLCIOCDetailsTable 
    if widget.getPropertyValue("name") == 'PLCIOCDetailsTable':
        if display.getMacroValue("SHOW_PLC_IOC") == "true":
            widget.setPropertyValue("visible", "true")
            display.getWidget("PLCIOCDetailsLabel").setPropertyValue("visible", "true")
    

#find index of the trigger PV
i=0
while triggerPV != pvs[i]:
    i+=1

table.setCellText(i, 1, PVUtil.getString(triggerPV))
table.setCellText(i, 2, PVUtil.getStatus(triggerPV))
table.setCellText(i, 3, PVUtil.getSeverityString(triggerPV))

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
    
table.setCellBackground(i, 3, color)