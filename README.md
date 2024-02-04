
# ClansManager

ClansManager is plugin for minecraft java.
This plugin allow you to create clans with bank also clan chat and clan chest.
ClansManager also have admin commands to easy manager player clans.

# Player Command

- /clan create <clanName>
- /clan set <prefix, owner, tp>
- /clan tp 
- /clan list 
- /clan back <deposit, balance, withdraw>
- /clan chat <message>
- /clan lock
- /clan invite <player>
- /clan accept <clanName>
- /clan kick <player>
- /clan kickall
- /clan remove
- /clan chest
- /clan leave
- /clan battle 
- /clan battle <accept,deny>


# Admin Command

- /clan admin is master command.
- /clan admin tp <clanName>
- /clan admin remove <clanName>
- /clan admin kick <clanName> <member>
- /clan admin setOwner <clanName> <player>
- /clan admin balance <clanName>
- /clan admin take <clanName> <amount>
- /clan admin give <clanName> <amount>
- /clan admin clear <clanName>
- /clan admin spyChat <enable. disable> <clanName>
- /clan admin chest <clanName>
- /clan reload

# Placeholders
- %clan_prefix%
- %clan_prefix_clear%
- %clan_name%
- %clan_owner%
- %clan_members%
- %clan_members_count%
- %clan_kills%
- %clan_death%
- %clan_balance%

- used in clan list
- %clans_prefix_<id>%
- %clans_prefix_clear_<id>%
- %clans_name_<id>%
- %clans_owner_<id>%
- %clans_members_<id>%
- %clans_members_count_<id>%
- %clans_kills_<id>%
- %clans_death_<id>%
- %clans_balance_<id>%

- used for top holograms,etc
- %clantop_kill<place>%
- %clantop_death<place>%
- %clantop_balance<place>%
- %clantop_battle_<place>%

## example

- %clantop_kill_1% 
- ouput will be {clan} &6&l{amount} &aKills by default from config.yml section clan-top-kills-preset


# Permission

- clansmanager.reload (Default: op)
- clansmanager.clan (Default: op)
- clansmanager.set (Default: op)
- clansmanager.bank (Default: op)
- clansmanager.remove (Default: op)
- clansmanager.teleport (Default: op)
- clansmanager.lock (Default: op)
- clansmanager.kickall (Default: op)
- clansmanager.kick (Default: op)
- clansmanager.create (Default: op)
- clansmanager.list (Default: op)
- clansmanager.leave (Default: op)
- clansmanager.accept (Default: op)
- clansmanager.chest (Default: op)
- clansmanager.chat (Default: op)
- clansmanager.battle (Default: op)
- clansmanager.baccept (Default: op)
- clansmanager.admin (Default: op)

## SoftDepends
 - [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245)
 - [LuckPerms](https://luckperms.net)
 - [Vault](https://www.spigotmc.org/resources/vault.34315)

