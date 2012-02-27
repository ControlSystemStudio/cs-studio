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
for count in (100, 1000, 10000):
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

print "-> Should see 10000+ commands/sec"
print
print



# Set PV without readback
print "'Set' command without readback"
print
print "Write to PV, don't wait for feedback"
print
count = 1000
seq = CommandSequence()
for x in range(count):
    seq.add(SetCommand('xpos', x, "", 0, 0, 0))
# Without any readback, scan context can be closed
# while writes to PV are not flushed out
seq.set('xpos', count)

client.submit("Set %d times (no rb)" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(1.0)
ms = info.getRuntimeMillisecs()

print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
print "%f commands/second" % (1000.0*count/ms)
print "-> Should see about 3000 'set' commands/sec when not using readback"
print "(but unclear how many updates were actually received by IOC)"
print
print



# Set PV with readback
print "'Set' command with readback"
print
print "Write to PV, wait for monitor that shows readback matches"
print
count = 100
seq = CommandSequence()
for x in range(count):
    seq.set('xpos', x);
client.submit("Set %d times (with readback)" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(1.0)
ms = info.getRuntimeMillisecs()

print "Time for %d commands: %s, " % (count, info.getRuntimeText()),
print "%f commands/second" % (1000.0*count/ms)
print "-> Should see about 10 'set' commands/sec when using readback"
print
print



# Loop PV with readback
print "'Loop' command"
print
print "Write to PV, wait for monitor that shows readback matches"
print
count = 100
seq = CommandSequence()
seq.loop('xpos', 1, count, 1, DelayCommand(0));
client.submit("Loop %d times" % count, seq)

while True:
    info = client.getScanInfo()
    if info.getState().isDone():
        break;
    time.sleep(1.0)
ms = info.getRuntimeMillisecs()

print "Time for %d loop iteration: %s, " % (count, info.getRuntimeText()),
print "%f iterations/second" % (1000.0*count/ms)
print "-> Should see about 10 'loop' iterations/sec when using readback"
print
print