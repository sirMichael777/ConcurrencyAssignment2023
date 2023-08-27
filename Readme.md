Club Simulation Program - Readme Guide

This GUIDELINE offers insights into the utilization of the Makefile provided to compile and execute the Club Simulation program. The Makefile has been created to facilitate and streamline the compilation of the Java source code and the execution of the simulation.

Pre-requisites

Before leveraging the Makefile, ensure the availability of the following elements:

Java Development Kit (JDK) installed on your computer.
A basic understanding of command-line usage.
Directory Architecture

Adhere to the following directory architecture:

```
project-root/
│
├── src/
│   └── clubSimulation/
│       └── (Java source files)
│
├── bin/
│   └── (Compiled .class files will be placed here)
│
├── git.txt
├── Makefile
└── README.md
```

Leveraging the Makefile

Launch a terminal and navigate to the root of your project directory.

To compile the Java source code, simply input the following command:

```
make
```
This command will identify all .java files within the src/clubSimulation folder, compiling them into .class files and placing them in the bin/clubSimulation folder.

To execute the simulation, utilize the following command:

```
make run ARGS="arg1 arg2 arg3 arg4"
```
Replace arg1, arg2, arg3, and arg4 with the relevant arguments (with arguments being noClubgoers, gridX, gridY and max respectively) intended to be passed to the program. If no arguments are required, just run:

```
make run
```
To purge the compiled .class files, utilize the following command:

```
make clean
```
This command will delete all .class files from the bin/clubSimulation folder.

Primer

By utilizing the offered Makefile, you can smoothly compile and execute the Club Simulation program using a handful of streamlined commands. This ensures a clean project structure while setting up and running the simulation efficiently.

For further details or troubleshooting, refer to the project's documentation or liaise with your development team.