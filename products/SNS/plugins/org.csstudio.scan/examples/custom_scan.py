"""
Example for custom scan from shell

@author: Kay Kasemir
"""

from scan_client import *

# Connect to server
client = ScanClient()

# Create some scan by adding commands to sequence
seq = CommandSequence()
seq.set('ypos', 5)
for x in range(1, 5):
    seq.set('xpos', x)
    seq.delay(1)
    seq.log('xpos', 'readback')
seq.set('xpos', 1)

# Schedule for execution on server
client.submit("My Scan 1", seq)
client.waitUntilDone()



# Create some scan by assembling the commands
# as a python list
cmds = [
  DelayCommand(2.0),
  LoopCommand('xpos', 1, 5, 0.1,
    [
      SetCommand('setpoint', 1),
      WaitCommand('readback', 1.0, 0.5),
      SetCommand('setpoint', 5),
      WaitCommand('readback', 3.0, 0.5),
      LogCommand([ 'xpos', 'readback' ])
    ]),
]


# Schedule for execution on server
seq = CommandSequence(cmds)
seq.dump()
client.submit("My Scan 2", seq)
client.waitUntilDone()
