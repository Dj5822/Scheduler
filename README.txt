Uses Java version 11.
To run the project, do the following:

1) Open the terminal.
2) Type in the following command:
> java -jar Scheduler.jar INPUT.dot P

NOTE: 
INPUT.dot is the name of the dot file.
P is the number of processors.

INPUT.dot can be replaced with the path to the
INPUT.dot file. For instance, in Windows, you can do:
> java -jar Scheduler.jar INPUT.dot P
OR if the input files are in a folder e.g. examples:
> java -jar Scheduler.jar examples/INPUT.dot P

Optional: 
'-o OUTPUT' can be used to set the name and output path of
the output file (default is INPUT-output.dot).

'-v' can be used to visualise the search progress.

'-p N' can be used to execute N cores in parallel
(default is sequential).


3) Press enter.
