# DESIGN

# DVD Co app: Decision synthesis

Reference document gathering the design choices, to guide the Spring Boot implementation.

## 1. Vision

The app aims to be a support for the player, used while the game is on pause. Therefore, players want to update their progression with only a few clicks 
(they are playing, after all; let’s keep it simple for them!). Since there are a lot of fish in the game, and almost all of them have a star rating system, 
they already have a lot of data to sift through: the app must never drown them in information that they do not need to save their progress. 

Every design decision has been made according to the vision of an ergonomic, intuitive and minimalist app.

On a more personal note, this project is a means to practice Spring on an "enterprise-grade" level: its modelling, development, testing, containerisation and deployment 
are aiming to be as close to enterprise standards as possible. 

## 2. Data schema (ERD v3)

### a) Reference tables (fixed game data)

Note: All the **id** in reference tables are hardcorded, because those are fixed data.

***fish***

- `fish_id` INT, PK
- `fish_name` VARCHAR(45)
- `fish_rank` INT: A difficulty number provided by the game, no Enum needed. Can be sorted.
- `active_time` VARCHAR(10) NOT NULL: `@Enumerated(EnumType.STRING)`, values `'DAY'` / `'NIGHT'` / `'BOTH'` (some fish appear both day and night) .  Can be sorted.
    
    constraint **`chk_fish_active_time`**: `CHECK (active_time IN ('DAY', 'NIGHT', 'BOTH'))` (a single place to update if the vocabulary grows),  `NOT NULL` : 
a CHECK alone would let NULL through.
    
- `always_three_stars` TINYINT(1) : matches the game rule (e.g. shrimps are always 3 stars).
- `zone_id` INT, FK = zone
- `dlc_id` INT, FK = dlc : even if data entry reveals that no current DLC fish has stars, the column deliberately stays to accommodate possible future 
star-bearing DLC fish. Can be sorted.

***zone***

- `zone_id` INT, PK
- `zone_name` VARCHAR(45)
- `zone_depth_min` / `zone_depth_max` INT:
    
    Both columns are nullable to accomodate zones like the DREDGE ones, which have no explicit depth . NULL automatically excludes a zone from depth-based
tabs (see the sketch and the DREDGE tab).
    
    The constraint is named **`chk_zone_depths`**  plural, because the rule covers the pair of limits.
    
    Tabs are displayed by depth band: one band can contain several zones (e.g. the limestone cave in the middle of the medium depths). A tab filters the fish
whose zone overlaps the band.
    
    `zone_depth_min` anchors the interval: it is always present when depth is defined. `zone_depth_max` remains optional (e.g. used for the Hydrothermal Vents zone, 
which is 540m +).
    
    The constraint accepts three states:
    
    - Both columns absent : depth undefined (DREDGE zones)
    - Min present, max absent : open interval, e.g. "540m and more"
    - Both present with `zone_depth_min < zone_depth_max`, closed interval. The inequality is strict : every zone has thickness.
    
    It rejects one state: max without min, which would leave the interval without its low anchor.
    
    Two consequences follow:
    
    - Tab filter: an absent max means **+∞**; plan for a half-open predicate (an OR).
    - Display: derive "540m and more" when max is absent.
- `dlc_id` INT, FK = dlc, nullable

***dlc***

- `dlc_id` INT, PK
- `dlc_name` VARCHAR
- No release date (YAGNI: useless to the player, varies by country)

***user***

- `user_id` INT, PK
- `user_login` VARCHAR(20) (no email, deliberately)
- `user_password_hash` VARCHAR(72) — BCrypt (60 chars + margin for algorithm migration, e.g. Argon2)

***no_star_fish*:** fish without the star mechanic (e.g. Dredge Dlc content). Any future starless fish will belong here, wherever it comes from.

- `nsf_id` INT, PK
- `nsf_name` VARCHAR(45)
- `nsf_rank` INT: informational, like `fish_rank`. Can be sorted
- `zone_id` INT, FK = zone
- **No `dlc_id` nor `active_time`**: (for now, only DREDGE content, active at night)
- No stars, no eggs: binary tracking mechanic

***v1 scope* - product decision**: 

- Bosses and photographable animals are excluded from the tracker (memorable by nature, single capture, no stars nor eggs). Data are kept dormant in the data 
spreadsheet. If a v2 “collector mode” happens: reintroduce `is_boss`, nullable `best_nb_stars` and the associated rules via a dedicated Flyway migration.
- “In the Jungle” DLC (spin-off):  out of v1 scope. It is almost a separate game; if ever integrated, a clear main-game / spin-off separation is required.

### b) Progression tables (user data)

***catch*:** auto-incremented PK `catch_id` + `UNIQUE (user_id, fish_id)` ****constraint.

- `fish_id` INT NOT NULL, FK = fish
- `user_id` INT NOT NULL, FK = user

A composite key would have required boilerplate code on the Java side, JPA accepting it only through extra machinery. The uniqueness of the initial pair remains 
guaranteed by the constraint. Only the bearer of identity changes, not the rule.

- `best_nb_stars` INT NOT NULL, constraint “1 to 3” (absence of a row = never caught, a catch always gives ≥ 1 star; the presence of the row means “caught”).
- Semantics: current best value (no history), updated via *setter*; the player may lower it to correct an entry error, the UI is asking for confirmation first.

***roe*:**  auto-incremented PK `roe_id` + `UNIQUE (user_id, fish_id)`, symmetric to catch.

- `fish_id, user_id` INT NOT NULL, FK
- `nb_roe` INT, constraint “1 to 2”
- Game rule: 2 eggs of the same fish = breeding possible at the farm
- Absence of a row = no eggs (no distinction between “never had” / “sold”)

***no_star_catch*:** auto-incremented PK `nscatch_id` + `UNIQUE (user_id`, `nsf_id`), FK to `user` and `no_star_fish`.

- `nsf_id, user_id` INT NOT NULL, FK
- **No data column:** the presence of the row = caught
- Idempotent insert (`INSERT IGNORE` / `ON DUPLICATE KEY`), no max-upsert

### c) Notes on the model

- `fish.dlc_id` AND `zone.dlc_id`: two distinct facts (a DLC fish can live in a base-game zone), not a redundancy.
- Constant-column criterion: a constant column is removed by default (type information is not stored).
- `dlc_id` nullable = base-game content (no fake “DLC 0”)
- Catching (`catch`) and breeding (`roe`) are two independent progression axes. In addition, the binary checklist of starless fish (`no_star_catch`) is a parallel 
structure deliberately kept separate: the different mechanic (no stars) would have forced a cascade of nulls and conditional rules into `catch`.

## 3. Java /JPA decisions

- **Each world is spelled in its native language:** Java PK is `id`, SQL column is `zone_id`; the link is made at every change of layer: `@Column(name = "zone_id")` 
on each `id`, `@JoinColumn(name = "dlc_id")` on each relationship (since JPA's defaults assume bare `id` keys, the names are explicitly given). The other JPA Entity 
fields are without prefix (`name`, not `zoneName`).
- **No `setId()`**: it is an intentional guardrail; identity is born at INSERT and never changes.
- **Constructors follow entity creation:** entities created by the application (`User`, `FishCatch`, `Roe`) take required-field constructors plus a `protected` 
empty constructor (for Hibernate); entities born only from the database (`Fish`, `Zone`, `Dlc`) need no explicit one.

---

## 4. Business rules to implement in the Service layer

1. Refuse a catch under 3 stars for a fish with `always_three_stars = true`
2. Upsert on catch: record the submitted  `best_nb_stars` . Decreases are allowed to correct entry errors, the UI asks for confirmation before applying one. 
3. The “fish not yet at 3 stars” list is derived (never stored): fish without a catch row or with `best_nb_stars < 3` ( refinement: uncaptured `always_three_stars` 
fish are part of it, and behave there in binary fashion like `no_star_fish` - at this level only).

## 5. Security

- **Spring Security:** added at the authentication step, not at initialisation; add the dependency + `SecurityConfig` together at the right moment, 
a dependency addition remaining trivial at any time.
- Passwords hashed with BCrypt (`BCryptPasswordEncoder`)
- The salt is generated automatically and embedded in the hash: a single column is enough.
- **SQL:**
    - `catch` (the SQL table) is reserved in Java → entity `FishCatch` with `@Table(name = "catch")`.
    - files named with the Flyway convention: `install.sql` (run manually, as root: CREATE DATABASE + USER + GRANT) and 
  `db/migration/V1__create_schema.sql`, `db/migration/V2__seed_reference_data.sql`
- Repo structure:
    - Spring flat at the root (pom.xml, src/, README, .gitignore, .env gitignored) + `diagrams/` (ERD)
    - Package:  `io.github.mariageclementine.dvdco`
- Secrets (DB credentials…) via environment variables, never committed (`.env` + `application.properties`); 
`spring.config.import=optional:file:.env[.properties]`: the `[.properties]` hint is **mandatory**; 
without an extension, Spring does not know which parser to use (*“File extension is not known to any PropertySourceLoader”*).
    
    `optional:` so it does not crash in prod, where Railway provides the variables. 
    
- **Dedicated MySQL account for the application**: DML (`SELECT/INSERT/UPDATE/DELETE`) + DDL limited to `dvd_co.*` so 
Flyway can migrate at startup; credentials via `spring.datasource.username=${DB_USERNAME}` etc.

## 6. Assets (copyright)

- **No Mintrocket/Nexon assets**:  the project uses the developer's own creations everywhere, dev environment included.
- **Homemade images** with a theme close to DVD's, but original: an atmosphere, a genre, a palette family that evoke the game without copying it.
- **Royalty-free fonts** used in the images. 

## 7. REST API - sketch V1

| Need | Possible endpoint |
| --- | --- |
| Tabs = **depth bands** (navigation; one band can contain several zones) | tabs defined on the front side; `GET /api/zones` remains for display info |
| Content of a tab (depth band), fish **merged with the user’s progression** | `GET /api/fish?depthMin=...&depthMax=...` |
| Record or improve a catch | `POST /api/catches` |
| Record or update eggs | `POST /api/roe` |
| Starless fish caught (presence = info) | `POST /api/no-star-catches` (name to refine) |
| Completion / stats | `GET /api/users/{id}/progression` |

### **a) Design method**

Hand-drawn site mockups before the final endpoint choice: the API will be derived from the screen’s concrete needs.

### **b) V1 sketch decisions** (post-login page)

Table sorting, the “hide 3 stars / hide 2 roe”, a checkbox and the active-tab state live on the **front end**, for two reasons: 
the data is tiny (a few dozen fish per zone - client-side sorting and hiding are instant, zero round-trip), 
and the front already holds all the facts (the table displays the progression).

The clicked tab calls the same parameterized `GET`, by depth band (cf. `zone_depth_min`/`zone_depth_max`; one band can merge several zones).
*My Account* is a page/route, not a data tab.

### **c) Write UX (“+” and “↑” gestures)**

- “+” means “new fish/egg”; “↑” means “improve my progression”. Each gesture targets a single endpoint: 
the backend performs the upsert ( setter for catch and roe) and does not care whether the row is new or improved. "New" versus "improved" is only an ergonomic display choice.
- Starless fish get “+” only, with no value (presence = caught).
- **registration form**: select the fish, add 1 star or 1 egg, submit. Planned refinement: pre-fill the fish when the click comes from a row.
- Disable “↑” at the ceiling (3 stars / 2 eggs) : never offer a gesture the CHECK would refuse.
- **Lowering a value asks first**: if the submitted stars are *lower* than the current ones, the front shows a confirmation dialogue ("are you sure you want to lower the 
star count?"). The backend cannot tell an error correction from a wrong weaker new catch; the dialogue delegates that call to the player. 
 
  `roe` gets no dialogue: lowering it is normal gameplay, not a correction.
  
  Same logic for `always_three_stars` in the form: the stars are locked to 3 as soon as the fish is selected. The fields are  automatically filled and blocked, 
with a visible hint (“always 3 stars”) rather than silently disabled. The player types nothing, no input error is possible. 

  The API receives absolute values, so the form can evolve (1/2/3 choice) without any back-end change.
- **v1 precisions (sketch)**
    - The table is **non-interactive**: no per-row buttons;
    - The form lists **the fish of the active tab’s zone**: the front reuses the list already loaded for the table (no extra endpoints).
    - Sorting uses **arrows on each column**, front-side, with semantics per type: text A→Z, `Rank`/stars/eggs numerically; each click toggles asc/desc. 
  Never-caught fish sort at the bottom. `Active Time`: alphabetical or logical grouping (DAY/NIGHT/BOTH), to settle.
    - **v1 filters: a checkbox for “Fish missing stars”** : the tracker exists to show what’s missing to obtain 100 % progression. 
  Front-end filter on the enriched `GET` (0 stars = absence of a row in `catch`).
  
    **COMPLETION semantics**: uncaptured `always_three_stars` fish stay in the list; they are missing for the 100 %. At this level *only*, 
  they behave like `no_star_fish` (binary: 0 = missing, 3 = done). The filter predicate is therefore uniform (“not yet 3 stars”), with no special case: 
the always-3 fishes behave naturally.

### **d) "DREDGE" dlc tab**

Zones without depth will be displayed on a dedicated “DREDGE” tab, all together, with **one table per zone**.

Per-zone grouping happens on the front side (tiny data; the list already carries its zone, no extra endpoints).

If a second depthless event appears, the tab generalises into “events” (front label, trivial to rename).

### e) Others

- Use of **Spring Data JPA Specifications** (`JpaSpecificationExecutor`) rather than derived Repository methods multiplying;
no SQL views in v1 (frozen, parameterless — unsuited to dynamic filters, logic invisible in the repo).
- Enum in the API: expose `DAY`/`NIGHT`/`BOTH` in plain letters (consistent with STRING storage)
- `activeTime` filter semantics: filtering on `DAY` must include `DAY` **and** `BOTH` fish (same for `NIGHT`). Otherwise, “always active” 
fish would disappear from the results;

## 8. Estimated build order

1. **Local development**: Spring Boot + MySQL installed manually (simple and sufficient); **Java 25** (current LTS, supported by the latest Spring Boot 
generation; up-to-date stack for the portfolio).
2. **Database population**: **2 separate scripts**: an *installation* script run once by hand as root (`CREATE DATABASE IF NOT EXISTS` + `CREATE USER` + `GRANT`), 
and the *schema* scripts versioned in the repo, handwritten; scripts are rendered idempotent (`CREATE TABLE IF NOT EXISTS`).
3. **First tests**: JUnit 5, run locally via `mvn test`
4. **CI** - GitHub Actions (`.github/workflows/ci.yml`): every push automatically triggers the tests (`mvn -B verify`).
5. **Flyway migrations**: adopted from project creation: the scripts are born directly in `src/main/resources/db/migration/` with the Flyway 
convention (`V1__create_schema.sql`, `V2__seed_reference_data.sql`); applied automatically at startup on any environment (local, CI, Railway).
6. **Containerisation**: `Dockerfile` (app image) + `docker-compose.yml` will be done after the app runs.
7. **Deployment**: Railway/Render (containers); in the enterprise, deployment is only triggered if the CI is green (CI/CD).

>
> *Document drafted with the assistance of Mistral AI for the structure; all design decisions and their rationale are the author's own*
>