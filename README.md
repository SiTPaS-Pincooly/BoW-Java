# ⚔️ Battle of Wizards

> A multiplayer Java board game where 2–4 wizards race across a magical 10×10 board, casting spells and outsmarting opponents to reach cell 100 first.

---

## 📖 What is Battle of Wizards?

Battle of Wizards is a turn-based board game for 2–4 players (human or AI) built with Java Swing. Players take turns rolling a dice and moving their wizard piece across a 100-cell snake-path board, landing on special tiles and casting spells to gain an edge over their rivals.

Each turn has two phases: a **spell phase**, where a player can cast Heal, Fireball, or Shield, and a **dice phase**, where they roll and move. The first wizard to reach or pass cell 100 wins.

The game supports mixed human/AI sessions — you can play solo against up to three AI opponents, or play with friends in a hot-seat format.

> 📸 **[ADD SCREENSHOT: Main game board with players on it and the stat panels visible on both sides]**

---

## 🧙 Origin Story

Battle of Wizards started as a Python terminal game built by Quân for fun — a simple dice race with no UI, no spells, just movement and a finish line.

When our group (Group 16) was assigned a Java OOP project for COMP1020, we decided to take that prototype and rebuild it properly: a full graphical interface, a clean 3-layer architecture, a spell system, AI opponents, and special board tiles. What began as a few dozen lines of Python grew into a complete, polished Java application across five team members.

The rewrite wasn't just a translation — almost every mechanic was redesigned. The bounce logic was dropped in favor of a clean win condition, the board became randomized each match, and the spell system was built to be infinitely extensible without touching existing code.

---

## 🎮 Gameplay Mechanics

### The Board
The game is played on a 10×10 snake-path board (cells 1–100). Odd rows run left-to-right, even rows run right-to-left. The board is randomized at the start of each match with special tiles placed using a probability algorithm.

### Turn Structure
Every turn follows two phases in order:
1. **Spell Phase** — Cast a spell or skip
2. **Dice Phase** — Roll (1–6) and move

### Spells
| Spell | Mana Cost | Effect |
|---|---|---|
| 🔥 Fireball | 3 mana | Deal 1 HP damage to a chosen opponent |
| 💚 Heal | 4 mana | Restore 1 HP to yourself |
| 🛡️ Shield | 2 mana | Block the next hit you receive |

Players gain 1–3 mana randomly after every dice roll, plus bonus mana from Mana Fountain tiles.

### Special Tiles
| Tile | Effect |
|---|---|
| 🏁 Checkpoint | Fully restores HP; sets your revival point |
| 💧 Mana Fountain | Fully restores mana |
| 🟢 Green Portal | Teleports you to another random green portal |
| 🔵 Blue Portal | Shifts your position ±10 cells |

### Revive System
If a player's HP drops to 0 (from Fireball), they are sent back to their last Checkpoint with full HP restored.

### Winning
The first player to land on or pass cell 100 wins. No bounce — once you cross the finish line, the game ends.

### AI Opponent
AI players make decisions automatically using a priority-based decision tree:
1. Shield if HP is critically low
2. Heal if HP is at 1 or below half
3. Fireball the weakest or most-advanced opponent
4. Proactive shield if mana is high
5. Skip otherwise

> 📸 **[ADD SCREENSHOT: Spell menu open with the three spell options visible]**
> 📸 **[ADD SCREENSHOT: A Checkpoint or Portal tile being landed on, with the log showing the effect]**

---

## 🛠️ Technologies Used

- **Java 17+** — Core language
- **Java Swing** — Full GUI: board rendering, panels, dialogs, animations
- **OOP Principles** — Interfaces (`Spell`, `GameLogPanel`, `SpellMenu`), abstract classes (`Tile`, `Player`), inheritance throughout
- **MVC-style Architecture** — Model (pure logic), Controller (turn management), View (Swing UI)
- **javax.swing.Timer** — AI turn delays and dice face display timing
- **ImageIO / BufferedImage** — Asset loading, scaling, and programmatic dice face generation

---

## 🧱 Challenges

**Fullscreen + dialogs**
Getting the game to run in fullscreen while keeping spell menus and pause overlays functional was harder than expected. Exclusive fullscreen mode caused dialogs to collapse the window entirely. We switched to borderless windowed fullscreen using `Toolkit.getDefaultToolkit().getScreenSize()`, which solved it cleanly.

**Dynamic sizing**
The game needed to look correct on any screen resolution. Every size — cell size, font size, panel widths, bar heights — is calculated at runtime from the screen dimensions in `UIConstants`. This took careful coordination so nothing overlapped or clipped.

**AI and human turn isolation**
Early on, AI and human turn logic was tangled together, causing race conditions where button clicks during an AI turn would corrupt the game state. We added a guard in `handleAction()` that ignores all input during AI turns, and separated the two flows completely.

**Two-pass board rendering**
When multiple players share a cell, naive single-pass drawing would randomly stack pieces. We implemented a two-pass render: all non-current players are drawn first, then the current player is always drawn on top with a glow ring — so the active player is always visible.

**Portal placement**
Portals need to be placed in pairs (green portals teleport between each other). A naive random placement could result in too few portals or unbalanced boards. We solved this with a two-pass placement algorithm that guarantees a minimum portal count before the game starts.

---

## 📚 What We Learned

**Interfaces make everything easier to extend.**
The `Spell` interface meant that adding HealSpell, FireballSpell, and ShieldSpell never required touching the Player or Controller. Adding a new spell in the future is a single new class plus one line in SpellRegistry.

**Separate your concerns early.**
Keeping the model completely free of Swing code meant we could work on game logic and UI simultaneously without conflicts. Any time a view component needed game data, it read from the read-only `GameState` — never wrote to it.

**Swing has quirks that documentation doesn't warn you about.**
`repaint()` is asynchronous — using it after teleport caused stale frames. Switching to `paintImmediately()` in `BoardRenderer` fixed it. These kinds of platform-specific details only surface when you actually build something.

**Team coordination on shared files is hard.**
`GameController.java` and `GamePanel.java` were touched by almost everyone. We learned quickly to agree on method signatures and interfaces before anyone wrote implementations.

---

## 📸 Screenshots & GIFs

> 📸 **[ADD SCREENSHOT: SetupScreen showing the 4-column player config table]**

> 📸 **[ADD SCREENSHOT: Full game board mid-game with all 4 players active and the log panel populated]**

> 📸 **[ADD GIF: A full turn — spell phase, roll, dice face appearing on the board, player piece moving]**

> 📸 **[ADD SCREENSHOT: The pause menu overlay (ESC) over the game board]**

> 📸 **[ADD SCREENSHOT: A player being revived after HP drops to 0]**

---

## 🚀 Installation Instructions

### Requirements
- Java 17 or higher installed ([download here](https://adoptium.net))
- No external dependencies — everything uses the Java standard library

### Run from JAR
```bash
# Download the latest release JAR
java -jar BattleOfWizards.jar
```

### Build from Source
```bash
# Clone the repository
git clone https://github.com/your-username/BattleOfWizards.git
cd BattleOfWizards

# Compile
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# Run
java -cp bin Main
```

> ⚠️ Make sure the `assets/` folder is in the same directory as where you run the command, or the game will fall back to placeholder graphics.

### IntelliJ IDEA
1. Open the project folder in IntelliJ
2. Mark `src/` as the Sources Root
3. Run `Main.java`

---

## 🔮 Future Improvements

- **Win screen** — A proper animated banner when a player reaches cell 100, instead of a dialog box
- **More spells** — Freeze (skip opponent's dice phase), Swap (exchange positions with another player), Curse (reverse movement for one turn)
- **More tile types** — Damage tiles that deal HP on landing, Mana Drain tiles
- **Sound effects** — Dice roll sounds, spell cast sounds, portal whoosh
- **Online multiplayer** — Replace hot-seat with networked play over sockets
- **Spell targeting UI** — A visual board overlay for selecting Fireball targets instead of a dropdown
- **Save & load** — Persist game state so sessions can be resumed
- **Difficulty levels** — Smarter AI with lookahead instead of a fixed decision tree

---

## 👥 Team

| Name | Role |
|---|---|
| Nguyễn Trọng Nam Anh | Lead Dev · GUI (`controller/`, `view/`, `Main.java`) |
| Nguyễn Hà My | Core Dev · Player model & spell system |
| Lê Anh Quân | Core Dev · Tiles, board, `GameState` |
| Phạm Thảo Linh | Core Dev · `GameController` turn flow |
| Lê Hải Ngọc | Artist + Dev · Assets & `AssetLoader` |

---

*COMP1020 — Group 16*
