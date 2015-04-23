"""
Scan benchmark
@author: Kay Kasemir
"""

from scan_client import *

client = ScanClient()


# Simple 'NOP' commands
print "Sequences with zero-delay commands"
print
print "Idea is to measure the time to execute a simple command"
print
for count in (1000, 50000, 100000):
    cmds = []
    for x in range(count):
        cmds.append(DelayCommand(0))
    seq = CommandSequence(cmds)
    # seq.dump()

    client.submit("%d zero-delays" % count, seq)
    
    while True:
        info = client.getScanInfo()
        if info.getState().isDone():
            break;
        time.sleep(1.0)
    ms = info.getRuntimeMillisecs()
    
    print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
    print "%f commands/second" % (1000.0*count/ms)

print "-> Should see 50000 commands in about 0.6sec, roughly 80000 commands/sec"
print
print



# Set PV without readback
print "'Set' command without readback"
print
print "Write to PV, don't wait for feedback"
print
count = 10000
cmds = []
for x in range(count):
    cmds.append(SetCommand('xpos', x,  False))
# Without any readback, scan context can be closed
# while writes to PV are not flushed out
# So we add one write with readback,
# hoping that this one does not mess too much
# with the statistics
cmds.append(SetCommand('xpos', x))
seq = CommandSequence(cmds)
client.submit("Set %d times (no rb)" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(2.0)
ms = info.getRuntimeMillisecs()

# Tests with up to 1000 set commands verified
# that they reach the IOC by enabling TPRO on
# the affected record and counting the updates.
# A 'camonitor' will not see all updates because
# events are throttled!

print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
print "%f commands/second" % (1000.0*count/ms)
print "-> Should see almost the same as the 'delay 0' commands/sec when not using readback"
print "   for the MemoryDataLog."
print "   The DerbyDataLog reduces it from about 30000/sec to 2000/sec"
print
print



# Set PV with readback
print "'Set' command with readback"
print
print "Write to PV, wait for monitor that shows readback matches"
print
count = 100000
cmds = []
for x in range(count):
    cmds.append(SetCommand('xpos', x))
seq = CommandSequence(cmds)

client.submit("Set %d times (with readback)" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(2.0)
ms = info.getRuntimeMillisecs()

print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
print "%f commands/second" % (1000.0*count/ms)
print "-> Should see about 4200 'set' commands/sec"
print
print




# Loop PV with readback
print "'Loop' command"
print
print "Write to PV, wait for monitor that shows readback matches"
print
count = 50000
cmds = [
    LoopCommand('xpos', 1, count, 1, [])
]
seq = CommandSequence(cmds)
client.submit("Loop %d times" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(2.0)
ms = info.getRuntimeMillisecs()

print "Time for %d loop iteration: %s, " % (count, info.getRuntimeText()),
print "%f iterations/second" % (1000.0*count/ms)
print "-> Should see about 7000 'loop' iterations/sec."
print
print



# Set PV with readback for a "slow" device
print "'Set' command with readback for a 'slow' device"
print
print "Write to PV, with callback, wait for the readback from the slow device to match"
print
count = 20
cmds = []
for x in range(count/2):
    cmds.append(SetCommand('setpoint', 1, True, 'readback', True, 0.1, 0.0))
    cmds.append(SetCommand('setpoint', 2, True, 'readback', True, 0.1, 0.0))
seq = CommandSequence(cmds)
client.submit("Set %d times (with slow readback)" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(2.0)
ms = info.getRuntimeMillisecs()

print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
print "%f commands/second" % (1000.0*count/ms)
print "-> Should see about 1 command/sec because of the device's speed,"
print "   log mechanism does not matter."
print
print



