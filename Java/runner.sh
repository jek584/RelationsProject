#!/bin/bash 
javac WellsRelationSolver.java
echo "$1 test results:" >> ../wellsoutput.txt
(command time -f "Elapsed time: \t%E\nPercentage of CPU: \t%P\nNumber of swaps: \t%w\nMax Memory(KB): \t%M\n" java -Xmx"$3"g WellsRelationSolver input.txt)&>> ../wellsoutput.txt;
echo "\n" >> ../wellsoutput.txt;
rm *.class;

