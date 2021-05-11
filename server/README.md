# Server
This is the backend server for the Uno game.

## Available Commands
### PlayCard
Used when a player plays a card from their hand.
Example:
```java
  Command command = new Command("PlayCard", "green_5");
  commandOfficer.send(command);
```
### DrawCard
Used when a player draws a card from the draw pile to their hand.
Example:
```java
  Command command = new Command("DrawCard", "green_5");
  commandOfficer.send(command);
```
