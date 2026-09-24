# Mystic Maze( Award winning project in UIU CSE project show srping-25 )

Mystic Maze is a team-based 2D multiplayer puzzle-hunting game designed to engage players in completing levels filled with riddles, puzzles, and challenges. It offers teamwork-based gameplay with hints, leaderboards, and special powers.

## Features

### General
- Multiplayer team competition
- Player profile management

### Players
- Join/Host rooms
- Compete in puzzles and riddles
- Use special powers during challenges
- View rankings and leaderboard

### Admin/Moderators
- Manage rooms and players
- Monitor reports and feedback
- Manage puzzle levels

## Technologies Used

### Frontend
- JavaFX (with SceneBuilder)

### Backend
- Java (Socket Programming)
- MySQL (Database)

### Others
- XAMPP (Local Server for MySQL)
- IntelliJ IDEA

## Class Diagram

![Class Diagram](MysticMaze/MysticMaze/target/classes/com/example/mysticmaze/images/c.png)


## System Diagram

![System Diagram](docs/class-diagram.png)


## Setup Instructions

### Prerequisites
- JDK 24 or newer
- MySQL 8+ or MariaDB (e.g. via XAMPP)

### 1. Create the database

```bash
mysql -u root -p -e "CREATE DATABASE mysticmaze"
mysql -u root -p mysticmaze < MysticMaze/MysticMaze/src/main/resources/com/example/mysticmaze/SQL/mysticmaze.sql
```

By default the game connects to `jdbc:mysql://localhost:3306/mysticmaze` as `root` with an empty password (the XAMPP default). To use different settings, set `MYSTICMAZE_DB_URL`, `MYSTICMAZE_DB_USER` and `MYSTICMAZE_DB_PASSWORD` before running.

### 2. Start the game server (for rooms and chat)

```bash
cd MysticMaze/MysticMaze
./mvnw compile
./mvnw exec:java -Dexec.mainClass=com.example.mysticmaze.network.GameServer
```

Or run `network/GameServer.java` from IntelliJ. It listens on port **9999**.

### 3. Launch the game

In a second terminal:

```bash
cd MysticMaze/MysticMaze
./mvnw javafx:run        # Windows: mvnw.cmd javafx:run
```

Or run `Main.java` from IntelliJ. Start one game client per player.

### Conclusion 


Mystic Maze aims to provide an engaging and interactive multiplayer experience where teamwork, problem-solving, and strategy come together in a dynamic 2D puzzle environment. This project showcases the integration of JavaFX, socket programming, and MySQL to create a seamless game experience while maintaining an efficient backend infrastructure.
Through this project, we also explored key aspects of multiplayer networking, real-time data synchronization, and user experience design

