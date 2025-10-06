# ParkourRace

A lightweight and feature-rich parkour plugin for Minecraft servers. Create challenging parkour courses with an intuitive setup system and compete for the best times!

## Features

- **Pressure Plate System**: Simple course navigation using pressure plates
  - Gold plates for start points
  - Stone plates for checkpoints
  - Iron plates for finish lines
- **Timer System**: Accurate time tracking for each run
- **Checkpoint System**: Save progress and retry from checkpoints
- **Personal Bests**: Track and beat your best times
- **Leaderboards**: See the top 10 fastest players per course
- **Inventory Protection**: Your inventory is saved and restored when entering/leaving courses
- **Fall Damage Protection**: No fall damage while in parkour
- **Flight Protection**: Flying is disabled during parkour and auto-stops your run if enabled
- **Title Notifications**: Configurable titles for course events
- **PlaceholderAPI Support**: Display stats in other plugins
- **Easy Setup**: In-game course creation with step-by-step guide

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/parkour join <course>` | Join a parkour course | `parkourrace.join` |
| `/parkour leave` | Leave the current course | `parkourrace.leave` |
| `/parkour retry` | Retry from the last checkpoint | `parkourrace.retry` |
| `/parkour cancel` | Cancel course setup | `parkourrace.cancel` |
| `/parkour best <course> [player]` | View best time for a course | `parkourrace.best` |
| `/parkour list` | List all parkour courses | `parkourrace.list` |
| `/parkour create <name>` | Create a new parkour course | `parkourrace.admin` |
| `/parkour done` | Complete course setup | `parkourrace.admin` |
| `/parkour delete <course>` | Delete a parkour course | `parkourrace.admin` |

## Permissions

- `parkourrace.join` - Join parkour courses
- `parkourrace.leave` - Leave parkour courses
- `parkourrace.retry` - Retry from checkpoints
- `parkourrace.best` - View personal bests
- `parkourrace.list` - List all courses
- `parkourrace.admin` - Create and manage courses

## Installation

1. Download the latest release
2. Place the `.jar` file in your server's `plugins` folder
3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support
4. Restart your server
5. Configure the plugin in `config.yml` and `messages.yml`

## How to Create a Course

1. Run `/parkour create <name>` to start setup mode
2. Place a **Gold Pressure Plate** where you want the start point
3. Place **Stone Pressure Plates** for checkpoints (optional)
4. Place an **Iron Pressure Plate** for the finish line
5. Run `/parkour done` to complete the setup
6. Players can now join with `/parkour join <name>`

## Configuration

### config.yml
```yaml
# Enable or disable titles
titles:
  enabled: true
  start:
    enabled: true
  checkpoint:
    enabled: true
  finish:
    enabled: true
  personal-best:
    enabled: true

# Course data storage
data:
  save-interval: 300 # Auto-save interval in seconds
```

### messages.yml
Fully customizable messages with color code support (`&`) and placeholders:
- `{course}` - Course name
- `{time}` - Formatted time
- `{player}` - Player name
- `{rank}` - Leaderboard rank

## PlaceholderAPI Placeholders

| Placeholder | Description |
|-------------|-------------|
| `%parkourrace_course%` | Current course name |
| `%parkourrace_time%` | Current run time |
| `%parkourrace_best_<course>%` | Personal best for a course |
| `%parkourrace_rank_<course>%` | Your rank on a course |

## Requirements

- **Minecraft Version**: 1.20.4+
- **Java Version**: 17+
- **Server Software**: Paper, Spigot, or compatible forks

## Support & Links

- **Issues**: Report bugs or request features on GitHub
- **Author**: Dutchwilco

## License

This plugin is provided as-is for use on Minecraft servers.

---
