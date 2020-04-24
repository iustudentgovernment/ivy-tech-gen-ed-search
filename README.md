# Ivy Tech Course Parser

## Generating the list
Running CourseCheckApplicationKt with the following arguments:
 - path to course-catalog.txt
 - path to selenium chrome webdriver executable
 
Will output a file called "gen-ed-equivalencies.txt" with the following format, each line corresponding to an Ivy Tech course

IVY|(.+)->IU|(.+)->GE|(.+)

Where:
- group 1 corresponds to the Ivy Tech course name
- group 2 corresponds to the IU course name (OR None)
- group 3 corresponds to the IUB Gen Ed requirements the course fulfills, comma separated

Examples:

IVY|ANTH 154->IU|ANTH-E 105->GE|SH

IVY|ANTH 254->IU|None->GE|None

The first example has a corresponding IU course AND counts for SH credit, while the second does not.

## Generating a readable CSV

Run GenEdCSVCreatorKt with no arguments. The output will be a CSV called "ivy-tech-gen-ed-transfer.csv"