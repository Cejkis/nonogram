# Nonogram evolutionary solver

Finds a solution if the input isn't too large. However, note that this isn't a completely finished project - code style needs an improvement, run parameter are hardcoded, some parts of optimization are not necessary and served as a experiment.

Individuals are not represented as 2D grid of booleans, but as gap sizes in rows - that way the solution is always correct looking from left side and fitness is only computed as error when looking from the top.

Written in 2015 as a part of EOA course.
