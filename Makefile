JFLAGS = -g
JC = javac
SRCDIR = src
BINDIR = bin
DATADIR = data

# Use find command to locate all .java files inside the MonteCarloMini folder
CLASSES := $(shell find $(SRCDIR)/clubSimulation -name "*.java")

# Compile the classes inside the MonteCarloMini folder
default: classes

classes:
	$(JC) $(JFLAGS) -cp $(BINDIR) $(CLASSES) -d $(BINDIR)

clean:
	$(RM) $(BINDIR)/MonteCarloMini/*.class

run:
	java -cp $(BINDIR) clubSimulation.ClubSimulation $(ARGS)

