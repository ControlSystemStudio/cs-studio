"""
Scan benchmark
@author: Kay Kasemir
"""

from scan_client import *

client = ScanClient()


# Simple 'NOP' commands
print "Sequences with zero-delay commands"
print
print "Idea is to measure the time to execute a simple command,"
print "but really mostly measures the time to connect"
print "to all devices configured for the default device context,"
print "even though this scan doesn't use any devices..."
print
for count in (100, 1000, 10000, 50000):
    seq = CommandSequence()
    for x in range(count):
        seq.delay(0)
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
seq = CommandSequence()
for x in range(count):
    seq.set('xpos', x,  False)
# Without any readback, scan context can be closed
# while writes to PV are not flushed out
# So we add one write with readback,
# hoping that this one does not mess too much
# with the statistics
seq.set('xpos', count)

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
count = 50000
seq = CommandSequence()
for x in range(count):
    seq.set('xpos', x);
client.submit("Set %d times (with readback)" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(2.0)
ms = info.getRuntimeMillisecs()

print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
print "%f commands/second" % (1000.0*count/ms)
print "-> Should see about 4700 'set' commands/sec when using readback with MemoryDataLog,"
print "   1600 commands/sec with DerbyDataLog."
print
print




# Loop PV with readback
print "'Loop' command"
print
print "Write to PV, wait for monitor that shows readback matches"
print
count = 50000
seq = CommandSequence()
seq.loop('xpos', 1, count, 1);
client.submit("Loop %d times" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(2.0)
ms = info.getRuntimeMillisecs()

print "Time for %d loop iteration: %s, " % (count, info.getRuntimeText()),
print "%f iterations/second" % (1000.0*count/ms)
print "-> Should see about 4700 'loop' iterations/sec when using readback with MemoryDataLog,"
print "   1600 commands/sec with DerbyDataLog."
print
print



# Set PV with readback for a "slow" device
print "'Set' command with readback for a 'slow' device"
print
print "Write to PV, wait for the readback from the slow device to match"
print
count = 20
seq = CommandSequence()
for x in range(count/2):
    seq.set('setpoint', 1, 'readback', 0.1, 0.0);
    seq.set('setpoint', 2, 'readback', 0.1, 0.0);
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



