# Test cases

## Test 1: Simple test case

- Input file: [](./src/main/resources/test1.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Single passenger goes from floor 2 to 4

**Expected behavior**: 
 - Elevator 1 picks the passenger at floor 2 and drop him at floor 4
 - Elevator 2 and 3, 4 are idle

- Result: PASSED

## Test 2: Simple command but needs to change direction

- Input file: [](./src/main/resources/test2.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Single passenger goes down from floor 5 to 2

- Result: PASSED

**Expected behavior**: 
 - Elevator 1 goes to floor 5, picks the passenger, goes down and drops him at floor 2
 - Elevator 2 and 3, 4 are idle

### Result: PASSED

## Test 3: Simple request from elevator's current floor

- Input file: [](./src/main/resources/test3.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Single passenger goes up from floor 1 to 4

**Expected behavior**: 
 - Elevator 1 goes to floor 1, picks the passenger, goes up to floor 4. Elevator 1 is now idle
 - Elevator 2 and 3, 4 are idle

### Result: PASSED

## Test 4: Dispatching multiple elevators for 2 passengers (Sequencially)

- Input file: [](./src/main/resources/test4.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Two passengers. Passenger A wants to go from floor 1 up to 9 and Passenger B wants to go from floor 3 down to 1

**Expected behavior**: 
 - Elevator 1 goes to floor 1, picks Passenger A, goes up to floor 9 and drops off Passenger A. Elevator 1 is now idle
 - Elevator 2 is dispatched and goes from floor 1 to 3 and picks up Passenger B who is going back down to floor 1
 - Elevator 2 drops off Passenger B on floor 1. Elevator 2 is now idle
 - Elevator 3, 4 are idle

### Result: PASSED

## Test 5: Dispatching multiple elevators for 2 passengers (Concurrently)

- Input file: [](./src/main/resources/test5.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Two passengers. Passenger A wants to go from floor 7 up to 13 and Passenger B wants to go from floor 3 up to 5

**Expected behavior**: 
 - Elevator 1 dispatches from floor 1 to floor 7 to go pick up Passenger A
 - A new request is made on floor 3 to pick up Passenger B going up in the same direction as Elevator 1, but Elevator 1 just passed floor 3 so it ignores Passenger B
 - Elevator 2 is dispatched and goes from floor 1 to 3 and picks up Passenger B who just missed Elevator 1
 - Elevator 1 picks up Passenger A on floor 7 and proceeds to floor 13
 - Elevator 2 drops off Passenger B on floor 5. Elevator 2 is now idle
 - Elevator 1 drops off Passenger A on floor 13. Elevator 1 is now idle
 - Elevator 3, 4 are idle

### Result: PASSED

## Test 6: Dispatching multiple elevators for 3 passengers (Concurrently)

- Input file: [](./src/main/resources/events-3.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Three passengers. Passenger A wants to go from floor 2 up to 7, Passenger B wants to go from floor 4 down to 3
and Passenger C wants to go from floor 5 down to floor 2

**Expected behavior**: 
 - Elevator 1 dispatches from floor 1 to floor 2 to go pick up Passenger A
 - A new request is made on floor 4 to pick up Passenger B going down to floor 3
 - Elevator 2 is dispatched and goes from floor 1 to 4 and picks up Passenger B who is going in the opposite direction as Elevator 1
 - Elevator 1 drops off Passenger A on floor 7. Elevator i is now idle
 - A new request is made on floor 5 down to floor 2 by Passenger C
 - Elevator 1 (which is now idle), becomes active again and goes down to accept the new request to pick up passenger C on floor 5
 - Elevator 2 drops off Passenger B on floor 3. Elevator 2 is now idle
 - Elevator 1 drops off Passenger C on floor 3. Elevator 1 is now idle
 - Elevator 3, 4 are idle

### Result: PASSED

## Test 7: Dispatching all 4 elevators for 4 passengers (Concurrently)

- Input file: [](./src/main/resources/test9.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Four passengers. Passenger A wants to go from floor 1 up to 20, Passenger B wants to go from floor 1 up to 10, Passenger C wants to go from floor 1 down to floor 15 and Passenger D wants to go from floor 1 up to floor 5

**Expected behavior**: 
 - Elevator 1 dispatches from floor 1 to go pick up Passenger A and starts going to its destination floor 20
 - Shortly after, a new request is made on floor 1 to pick up Passenger B also going up but they just missed Elevator 1
 - Elevator 2 is dispatched from floor 1 to go pick up Passenger B and starts going to its destination floor 10
 - Shortly after, another new request is made on floor 1 to pick up Passenger C also going up but they just missed Elevator 1 and Elevator 2
 - Elevator 3 is dispatched from floor 1 to go pick up Passenger C and starts going to its destination floor 15
 - One last request is made on floor 1 to pick up Passenger D also going up but they just missed Elevator 1, Elevator 2 and Elevator 3
 - Elevator 4 is dispatched from floor 1 to go pick up Passenger D and starts going to its destination floor 5
 - All Elevators are actively going up
 - Elevator 2 drops off Passenger B on floor 10. Elevator 2 is now idle
 - Elevator 1 drops off Passenger A on floor 20. Elevator 1 is now idle
 - Elevator 4 drops off Passenger D on floor 5. Elevator 3 is now idle
 - Elevator 3 drops off Passenger C on floor 15. Elevator 4 is now idle
 - All elevators are idle
 
### Result: PASSED

## Test 8: Extreme Test Case for 5 Passengers (concurrently)

- Input file: [](./src/main/resources/extreme.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Five passengers. Passenger A wants to go from floor 2 up to 4, Passenger B wants to go from floor 5 down to 2, Passenger C wants to go from floor 7 down to floor 1, Passenger D wants to go from floor 3 up to floor 4 and Passenger E wants to go from floor 2 up to floor 5

**Expected behavior**: 
 - Elevator 1 dispatches from floor 1 to go pick up Passenger A on floor 2
 - Elevator 1 arrives on floor 4 to drop off Passenger A. Elevator 1 is now idle
 - Elevator 1 (which is now idle on floor 4), becomes active again and goes up to accept the new request to pick up Passenger C on floor 7 because it is the closest elevator to floor 7
 - Elevator 2 is dispatched and arrives on floor 3 to pick up Passenger D
 - Elevator 1 stops on floor 5 and picks up Passenger B on its way down with Passenger C from floor 7
 - Elevator 3 is dispatched and arrives on floor 2 to pick up Passenger E
 - Elevator 2 drops off Passenger D on floor 4. Elevator 2 is now idle
 - Elevator 1 arrives on floor 2 to drop off Passenger B and still has Passenger C who is going down to floor 1
 - Elevator 3 drops off Passenger E on floor 5. Elevator 3 is now idle
 - Elevator 1 drops off Passenger C on floor 1. Elevator 1 is now idle
 - Elevator 4 is idle
 
### Result: PASSED

## Test 9: Extreme Test Case for Multiple Passengers with Error Handling(concurrently)

- Input file: [](./src/main/resources/extreme with error.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** Passenger A wants to go from floor 2 up to 4, Passenger B wants to go from floor 5 down to 2, Passenger C wants to go from floor 7 down to floor 1, Error occurs with elevator 1, Passenger D wants to go from floor 3 up to floor 4 and Passenger E wants to go from floor 2 up to floor 5

**Expected behavior**: 
 - Elevator 1 dispatches from floor 1 to go pick up Passenger A on floor 2
 - Elevator 1 arrives on floor 4 to drop off Passenger A. Elevator 1 is now idle
 - Elevator 1 (which is now idle on floor 4), becomes active again and goes up to accept the new request to pick up Passenger C on floor 7 because it is the closest elevator to floor 7
 - Elevator 1 arrives on floor 7 and load passenger but it's unable to close the door
 - The scheduler disables elevator 1 until it is fixed
 - The scheduler commands elevator 3 to pick up passenger A on floor 7
 - Elevator 3 successfully picks the passenger and begins heading down to floor 1. Elevator 3 is now idle
 - Elevator 1 got fixed after door stuck for 20 seconds
 - Elevator 2 is dispatched and arrives on floor 3 to pick up Passenger D
 - Elevator 2 drops off Passenger D on floor 4. Elevator 2 is now idle
 - Elevator 1 (which is now fixed) arrives on floor 2 to drop off the final passenger on floor 5. Elevator 1 is now idle
 
### Result: PASSED

## Test 10: Door stuck error

- Input file: [](./src/main/resources/test10.txt)

- Number of elevators: 4

- Number of floor: 22

- Position of elevators: All at original (floor 1)

**Description:** One passenger wants to go from floor 2 to floor 4 but elevator door stuck at floor 2

**Expected behavior**: 
 - Elevator 1 dispatches from floor 1 to go pick up Passenger A on floor 2
 - Elevator 1 arrives on floor 2 and load passenger but it's unable to close the door
 - The scheduler disables elevator 1 until it is fixed
 - The scheduler commands elevator 2 to pick up passenger A on floor 2
 - The elevator 2 successfully picks the passenger and drop him at floor 4
 - Elevator 1 got fixed after door stuck for 20 seconds
 - Both Elevator 1 and elevator 2 go to idle state
 
### Result: PASSED
