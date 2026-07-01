# Maze Game

Simple logic maze game built for Android where players must traverse the maze to collect all the goals.

# How to Setup
***Note:*** Make sure Android Studio is installed!

**Minimum SDK Version:** API 25 (Nougat: Android 7.1.1)  
**Target SDK Version:** API 36 (Baklava: Android 16.0)

### 1. Clone the Repository
```
git clone https://github.com/Tyelk/Maze_Game.git
```

### 2. Open the Project in Android Studio
- Open Android Studio and select Open/Open Existing Project.
- Navigate to and select the cloned project.

### 3. Build & Run
- Click run (`Shift + F10`)

# How to Play

- Tap on a space to move to its position!
- Must reach all the goals to win!
- Can only move to a space with a matching shape or colour!
- Can NOT move diagonally!
- Can NOT move backwards!
- Can NOT move over blank spaces!

# Demo Video

<video src="https://github.com/user-attachments/assets/1d32d194-4ca8-42f7-835e-6f5db17ff42a" controls width="100%"></video>

# File Structure
```
├── app/
│   ├── src/
│   │   ├── androidTest/                             # UI tests
│   │   └── main/
│   │       ├── assets/
│   │       │   ├── levels/                          # Level data (custom levels can be added)
│   │       │   └── level_builder_legend.txt         # Level creation guide
│   │       ├── java/
│   │       │   └── nz/
│   │       │       └── ac/
│   │       │           └── ara/
│   │       │               └── hc/
│   │       │                   └── logicmaze/
│   │       │                       ├── data/        # Common data, level translator, and repository
│   │       │                       ├── model/       # Game model (level and movement logic)
│   │       │                       ├── ui/          # UI screens
│   │       │                       └── viewmodel/   # Individual screen ViewModels
│   │       ├── res/
│   │       ├── AndroidManifest.xml
│   │       └── ic_launcher-playstore.png
│   ├── .gitignore
│   ├── build.gradle
│   └── proguard-rules.pro
├── gradle/
├── .gitignore
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```

# Level Creation

Additional levels can be created by adding them into the levels folder!  
Each level is made up of multiple spaces, and each space is comprised of a COLOUR | SHAPE | STATE.

***NOTE:*** Levels must include only ONE player starting position and AT LEAST one goal.
## Colours
| Value | Colour | 
| :---: | :---: | 
| B | Blue |
| R | Red | 
| Y | Yellow | 
| G | Green | 
| P | Purple |

## Shapes
| Value | Shape | 
| :---: | :---: | 
| D | Diamond |
| C | Cross | 
| S | Star | 
| F | Flower | 
| L | Lightning | 

## State
| Value | State | 
| :---: | :---: | 
| 0 | Nothing |
| 1 | Goal | 
| < | Player start LEFT | 
| > | Player start RIGHT | 
| ^ | Player start UP |
| v | Player start DOWN | 

## Blank
| Value | Space | 
| :---: | :---: | 
| ___ | Blank space |

## Examples
| Level Data | In-Game Layout | 
| :---: | :---: | 
| RD0,RC1<br>BD>,BC0 | <img width="363" height="361" alt="Screenshot_5" src="https://github.com/user-attachments/assets/e932c149-3a97-4411-9c9c-8f972587438b" /> |
| RF0,RS1,GS0<br>BF0,___,GL0<br>BS0,RS^,BS0 | <img width="363" height="357" alt="Screenshot_2" src="https://github.com/user-attachments/assets/2f51f6dc-ca71-429b-a0c2-cc31dd364443" /> | 
| RF0,RS0,GS0<br>BF0,RF1,GF0<br>BS0,RSv,BS0<br>GL0,RF0,YF1 | <img width="363" height="484" alt="Screenshot_4" src="https://github.com/user-attachments/assets/19e4b285-98ef-4900-b23d-885cfd30f60e" /> |

