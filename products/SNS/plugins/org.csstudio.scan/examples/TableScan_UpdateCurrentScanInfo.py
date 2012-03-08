"""
Show current executing scan info.

@author:Xihui Chen
"""

from scan_client import *
from scan_ui import *
from java.lang import Thread, Runnable
from org.csstudio.scan.server import ScanState, ScanInfo
from org.csstudio.opibuilder.scriptUtil import ConsoleUtil, PVUtil, ColorFontUtil
from org.eclipse.swt.widgets import Display

table = display.getWidget("ScanTable1").getTable()

client = ScanClient()

client.checkServer()

progressBar= display.getWidget("ScanProgress")
scanNameLabel = display.getWidget("ScanName")
commandLabel = display.getWidget("ScanCommand")
display.setVar("LatestPointScanID", -1)
table = display.getWidget("ScanTable1").getTable()

class SetRowColor(Runnable):
    
    def __init__(self, row, color):
        self.row=row
        self.color=color
    def run(self):
        table.setRowBackground(self.row, self.color)



class UpdateScanInfo(Runnable):
    def run(self):
        while display.isActive():
            scanInfos = client.server.getScanInfos()
            find = False
            for scanInfo in scanInfos:
                if scanInfo.getState().isActive():
                    scanNameLabel.setPropertyValue("text", scanInfo.getName())
                    commandLabel.setPropertyValue("text", scanInfo.getCurrentCommand())
                    progressBar.setPropertyValue("pv_value", scanInfo.getPercentage()/100.0)
                    #Mark scanned points as green 
                    if scanInfo.getId() == long(display.getVar("LatestPointScanID")):
                        for i in range(table.getRowCount()):
                            xpos=float(table.getCellText(i, 1))
                            ypos=float(table.getCellText(i, 2))
                            if (xpos == PVUtil.getDouble(pvs[1]) and ypos==PVUtil.getDouble(pvs[2]) 
                                and scanInfo.getPercentage() >= i*100.0/table.getRowCount()): #To make sure the matched position is set from this scan                              
                                Display.getDefault().asyncExec(SetRowColor(i, ColorFontUtil.GREEN))                            
                   
                    find=True
                    break
            if not find:
                scanNameLabel.setPropertyValue("text", "None")
                commandLabel.setPropertyValue("text", "")
                progressBar.setPropertyValue("pv_value", 0)
                 
            Thread.sleep(100)

thread=Thread(UpdateScanInfo())
thread.start()        
    
