# DVD-Companion, a companion for the game "Dave the Diver"

>  _I'm in the process of learning Spring autonomously. This project is an idea I got while playing Dave the Diver recently,
and it fits perfectly with what I want to practice. The development may be a bit slow as I am still learning at the same time._

In Dave The Diver, you can only track the fish you caught when you are out of the water with the "Marinca" app.
This can be a problem when you are diving and wondering "Did I already catch that fish?" or "do I already have 3 stars for this one?".

DVD-Companion plans to address that problem: you will be able to track and save your progress at any time.

### The app will show you:
- The fish you discovered
- The stars you got for each fish
- What fish are yet to be caught
- How many roe (up to 2) you still need for breeding


The display will be categorised by zone, just like the "Marinca" app in-game. Filters will be added to help you find what you need more quickly.

### Planned tech stack
- On the web
  - A MySQL database
  - Spring for the back-end (Java 25):
    - Spring Boot
    - Spring Data JPA
    - Hibernate as ORM
  - JUnit 5 for the local tests
  - Flyway for the migrations
  - GitHub Actions for CI
  - React for the front-end
  - Docker for containerisation
  - Railway for deployment

#### Copyrights

To respect Mintrocket's intellectual property, no asset from the game are used. Each graphic asset is my own creation and each of its component
(e.g. the font) is royalty-free.
