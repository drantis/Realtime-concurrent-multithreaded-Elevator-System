 SYSC 3303 - Iteration 4 Plan 

## Main tasks

- [X] Refactor the way timestamp currently works to mimic realtime behaviors better
- [ ] Creation of new error event type and new error handlers
- [ ] Changes for Floor subsystem with the present of error (see below)
- [ ] Changes for Elevator subsystem with the present of error (see below)
- [ ] Changes for Scheduler subsystem with the present of error (see below)
- [ ] Dealing with Door keep opening error 
- [ ] Dealing with Elevator stuck between floor error
- [ ] (Nice to have) Allow errors to be fixed
- [ ] New test cases with error injections
- [ ] New timing diagram + changes to other diagrams
- [ ] ... Maybe other problems...

## Refactoring timing code (Tri)

I will try to change the way the current timing system works. We need a way for the system to track
realtime.

Will try to finish this as soon as possible (probably some big changes)


## New error event types
