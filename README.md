# DVD-Companion, a companion for the game "Dave the Diver"

>  _I'm in the process of learning Spring autonomously. This project is an idea I got while playing Dave the Diver recently,
and it fits perfectly with what I want to practice._

In Dave The Diver, you can only track the fish you caught when you are out of the water with the "Marinca" app.
This can be a problem when you are diving and wondering "Did I already catch that fish?" or "do I already have 3 stars for this one?".

DVD-Companion plans to address that problem: you will be able to track and save your progress at any time.

### The app will show you:
- The fish you discovered
- The stars you got for each fish
- Which fish are yet to be caught
- How much roe (up to 2) you still need for breeding


The display will be categorised by depth band, just like the "Marinca" app in-game. Filters will be added to help you find what you need more quickly.

### Planned tech stack
- __Database, migration and ORM:__ MySQL, Flyway and Hibernate
- __Back-end (Java 25):__ Spring Boot, Spring Data Jpa
- __Local testing:__ JUnit 5
- __CI:__ GitHub Actions
- __Front-end:__ React
- __Containerisation and deployment:__ Docker and Railway


### Design decisions
Each decision for the project is being recorded and justified in the file [DESIGN.md](DESIGN.md).

#### Copyrights

To respect Mintrocket's intellectual property, no assets from the game are used. Each graphic asset is my own creation and each of its components
(e.g. the font) is royalty-free.

#### Development approach
Commit activity on this project comes in waves: it starts with design ideas and sketches, before any code. Quiet periods usually mean
I am researching the topics for the next steps I want to take — or working on my own skills — before continuing the project.
