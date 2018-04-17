# Database System
[![Build Status](https://travis-ci.org/rjmcf/DatabaseSystem.svg?branch=master)](https://travis-ci.org/rjmcf/DatabaseSystem)

An exercise in development, I have tried to emulate a database system in Java.

The Table and Record classes can represent any standard relational table and record respectively.
A Table can be saved to and loaded from a file using the TableFileReadWriter class, and can be pretty printed to the console using the TablePrinter class.
A Database acts as a collection of Tables, and can be used to save and load all of them from file.

A text-based interface is partially implemented, allowing a user to add Tables and Records and have them saved to file.

A makefile is provided to simply compile and run parts of the project, and the `make help` target provides information on the given targets.
