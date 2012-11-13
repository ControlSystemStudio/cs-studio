"""
Demo for different types
@author: Kay Kasemir
"""

from scan_client import *

# Create scan sequence
seq = CommandSequence(
[
  ScriptCommand('HandleTypes')
])

# Submit scan
scan.submit("Types", seq);

