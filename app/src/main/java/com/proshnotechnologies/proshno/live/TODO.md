* Ensure if app crashes in the middle of a live game question round that answer selected survives.
  - Could return an initial game state onConnect

* Flow:
  1. Establish connection
      -> Retrieve gameId
      -> Write to participant list [Ensure idempotence].
      -> Listen for participants, on self.uid
      -> Listen for questions, answers, results where gameId = gameId
  2. On question [cloud]
    -> [mobile] Display Question
    -> [cloud] Start timer
  3. On select -> Write to responses
  4. On expire [cloud]
      -> Delete participant if wrong/no response
  5.