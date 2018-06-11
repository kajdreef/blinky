#!/bin/bash

# to do mutatino testing first compile the program for the full program
mvn compile -P full
mvn test -P full

# Run the mutation testing framework, use -Dthread=8 to make use of multithreading capabilities.
mvn -P full org.pitest:pitest-maven:mutationCoverage -Dthread=8
