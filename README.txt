To run the project, do the following:

1) Open the terminal.
2) Type in the following command:
java -jar scheduler.jar INPUT.dot P

NOTE: 
INPUT is the name of the dot file.
P is the number of processors.

INPUT.dot can be replaced with the path to the
INPUT.dot file. For instance, in Windows, you can do:
java -jar scheduler.jar examples/INPUT.dot P

Optional: '-o OUTPUT' can be used to set the name and output path of
the output file. 


3) Press enter. If "output file generated"
message shows up, then INPUT-output.dot
file should appear in the same directory as the
input file.