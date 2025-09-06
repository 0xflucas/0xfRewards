[![Codacy Badge](https://app.codacy.com/project/badge/Grade/04304ef22b2947d284565c49e65124da)](https://app.codacy.com/gh/0xflucas/Rewards0001007/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

# 💎 cRewards – Daily Rewards System with NPC, Permissions, Cooldown & MySQL

**cRewards** is a customizable daily rewards plugin for Bukkit/Spigot servers. Players can claim personalized rewards via an interactive menu opened by an NPC (requires Citizens). Supports cooldowns, permissions, and MySQL integration.

---

## 📦 Features

- ✅ Configurable rewards system  
- ✅ Customizable inventory layout  
- ✅ NPC with custom skin and name (using Citizens)  
- ✅ Persistent cooldowns saved in MySQL  
- ✅ Per-reward permissions  
- ✅ Custom commands per reward  
- ✅ Editable messages  
- ✅ Easy to install and configure  

---

## 🛠️ Requirements

- Java 8+  
- Spigot 1.8+ (tested on 1.8.8)  
- Citizens plugin (lightweight dependency for NPCs)  

---

## 📥 Installation

1. Download the `cRewards.jar` plugin.  
2. Place it in your server’s `plugins/` folder.  
3. Install and enable the Citizens plugin.  
4. Restart the server.  
5. The `config.yml` will be generated automatically.  
6. Configure your rewards and MySQL inside `config.yml`.  

---

## ⚙️ Commands

| Command               | Permission       | Description               |
| --------------------- | ---------------- | ------------------------- |
| `/rewards npc`        | `rewards.admin`  | Spawns the rewards NPC    |
| `/rewards reload` or `/rl` | `rewards.admin`  | Reload the `config.yml`    |

> **To remove the NPC:**  
> Double left-click the NPC with a **diamond_sword** (configurable) in hand with the `reward.op` permission.

---

## 📄 Sample `config.yml`

```yaml
mysql:
  use: false
  host: "localhost"
  port: 3306
  database: "crewards_db"
  username: "root"
  password: ""

npc:
  skin: "Maria"
  name: "&6&lREWARDS"

menu:
  title: "Rewards menu"
  rows: 4

messages:
  no_perm: "&cyou dont have permission to collect this"
  in_cooldown: "&4&lOOOPS! &cyou need wait {cooldown} to collect again"

item_to_remove: "DIAMOND_SWORD" # permission 'reward.op' double left click to remove npc

rewards:
  reward1:
    slot: 1
    name: "&aDiamond Daily"
    item: "ENDER_CHEST"
    lore: 
      - "&7You can collect a diamond daily"
      - "&7Every 1 hour you can collect a diamond"
      - " "
    lore_ready: "&bClick to collect"
    lore_in_cooldown: "&cyou need wait {cooldown} to collect this"
    lore_no_permission: "&cyou dont have permission to collect this"
    permission: "rewards.reward1"
    cooldown: 36000 
    message: "&ayou get the diamond!"
    commands:
      - "give {player} diamond 1"
      - "say {player} collect the reward: {reward}"
  reward2:
    slot: 4
    name: "&cItem Diário"
    item: "CACTUS"
    lore: 
      - "&7You can collect an item daily"
      - "&7Every 3 hours you can collect an item"
      - ""
    lore_ready: "&bClick to collect"
    lore_in_cooldown: "&cyou need wait {cooldown} to collect this"
    lore_no_permission: "&cyou dont have permission to collect this"
    permission: "rewards.reward2"
    cooldown: 3600
    message: "&ayou get the item!"
    commands:
      - "give {player} diamond_sword"
      - "say {player} collect the reward: {reward}"
  vip:
    slot: 7
    name: "&aReward VIP++"
    item: "ENDER_PEARL"
    lore: 
      - "&7Reward VIP++"
      - "&7Available every 6 hours"
      - ""
      - "&eAvailable for VIP players"
      - ""
    lore_ready: "&bClick to collect"
    lore_in_cooldown: "&cyou need wait {cooldown} to collect this"
    lore_no_permission: "&cyou dont have permission to collect this"
    permission: "rewards.vip2"
    cooldown: 180
    message: "&ayou get the VIP reward!"
    commands:
      - "give {player} diamond 50"
      - "say {player} collect the reward: {reward}"
```
 ## 🌆 Screenshots

- ![Screenshot 1](https://i.imgur.com/WsZgOfz.png)
- ![Screenshot 2](https://i.imgur.com/Euqn7Xv.png)
- ![Screenshot 3](https://i.imgur.com/AfOQEJ9.png)
- ![Screenshot 30](https://i.imgur.com/KFbGDKc.png)
- ![Screenshot 4](https://i.imgur.com/R0METpK.png)
- ![Screenshot 5](https://i.imgur.com/hBHiPWX.png)
- ![Screenshot 6](https://i.imgur.com/eijYbbd.png)
- ![Screenshot 7](https://i.imgur.com/cV2q0Rh.png)
- ![Screenshot 8](https://i.imgur.com/6qe5Inw.png)

## Download latest release
[![Download Latest Release](https://img.shields.io/github/downloads/lucas0001007/Rewards0001007/latest/total?style=for-the-badge)](https://github.com/lucas0001007/Rewards0001007/releases/latest)


## 🐞 Support & Bugs
If you find issues, please submit them on GitHub or contact me on X: @lucascccccccccc

- 07/07/25 update - now shows on lore if have perm, if is cooldown, if can collect reward

- 06/09/28: Commit d2745ba (2.0.2): "feat: added support for (1.8.x > 1.21.x) versions, refactor all code"
- 06/09/28: Commit 993f6ff (2.0.3) "feat: added support for yml files. not required mysql now"

— all made w ♥ in 🇧🇷
