package com.cobelpvp.practice.lobby.setting;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Setting {

    SHOW_SCOREBOARD(
        ChatColor.LIGHT_PURPLE + "Match Scoreboard",
        ImmutableList.of(
            ChatColor.BLUE + "Toggles side scoreboard in-match"
        ),
        Material.ITEM_FRAME,
        ChatColor.YELLOW + "Show match scoreboard",
        ChatColor.YELLOW + "Hide match scoreboard",
        true,
        null
    ),
    SHOW_SPECTATOR_JOIN_MESSAGES(
        ChatColor.BLUE + "Spectator Join Messages",
        ImmutableList.of(
            ChatColor.BLUE + "Enable this to display messages as spectators join."
        ),
        Material.BONE,
        ChatColor.YELLOW + "Show spectator join messages",
        ChatColor.YELLOW + "Hide spectator join messages",
        true,
        null
    ),
    VIEW_OTHER_SPECTATORS(
        ChatColor.GREEN + "Other Spectators",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you can see spectators",
            ChatColor.BLUE + "in the same match as you.",
            "",
            ChatColor.BLUE + "Disable to only see alive players in match."
        ),
        Material.GLASS_BOTTLE,
        ChatColor.YELLOW + "Show other spectators",
        ChatColor.YELLOW + "Hide other spectators",
        true,
        null
    ),
    ALLOW_SPECTATORS(
            ChatColor.DARK_GREEN + "Allow Spectators",
            ImmutableList.of(
                    ChatColor.BLUE + "If enabled, players can spectate your",
                    ChatColor.BLUE + "matches with /spectate.",
                    "",
                    ChatColor.BLUE + "Disable to disallow match spectators."
            ),
            Material.REDSTONE_TORCH_ON,
            ChatColor.YELLOW + "Let players spectate your matches",
            ChatColor.YELLOW + "Don't let players spectate your matches",
            true,
            null
    ),
    RECEIVE_DUELS(
        ChatColor.GREEN + "Duel Invites",
        ImmutableList.of(
            ChatColor.BLUE + "If enabled, you will be able to receive",
            ChatColor.BLUE + "duels from other players or parties.",
           "",
            ChatColor.BLUE + "Disable to not receive, but still send duels."
        ),
        Material.FIRE,
        ChatColor.YELLOW + "Allow duel invites",
        ChatColor.YELLOW + "Disallow duel invites",
        true,
        "practice.toggleduels"
    ),
    SEE_TOURNAMENT_JOIN_MESSAGE(
            ChatColor.DARK_PURPLE + "Tournament Join Messages",
            ImmutableList.of(
                ChatColor.BLUE + "If enabled, you will see messages",
                ChatColor.BLUE + "when people join the tournament",
                "",
                ChatColor.BLUE + "Disable to only see your own party join messages."
            ),
            Material.IRON_DOOR,
            ChatColor.YELLOW + "Tournament join messages are shown",
            ChatColor.YELLOW + "Tournament join messages are hidden",
            true,
            null
    ),
    SEE_TOURNAMENT_ELIMINATION_MESSAGES(
            ChatColor.DARK_PURPLE + "Tournament Elimination Messages",
            ImmutableList.of(
                ChatColor.BLUE + "If enabled, you will see messages when",
                ChatColor.BLUE + "people are eliminated the tournament",
                "",
                ChatColor.BLUE + "Disable to only see your own party elimination messages."
            ),
            Material.SKULL_ITEM,
            ChatColor.YELLOW + "Tournament elimination messages are shown",
            ChatColor.YELLOW + "Tournament elimination messages are hidden",
            true,
            null
    );

    /**
     * Friendly (colored) display name for this setting
     */
    @Getter private final String name;

    /**
     * Friendly (colored) description for this setting
     */
    @Getter private final List<String> description;

    /**
     * Material to be used when rendering an icon for this setting
     * @see SettingButton
     */
    @Getter private final Material icon;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     * @see SettingButton
     */
    @Getter private final String enabledText;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     * @see SettingButton
     */
    @Getter private final String disabledText;

    /**
     * Default value for this setting, will be used for players who haven't
     * updated the setting and if a player's settings fail to load.
     */
    private final boolean defaultValue;

    /**
     * The permission required to be able to see and update this setting,
     * null means no permission is required to update/see.
     */
    private final String permission;

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean canUpdate(Player player) {
        return permission == null || player.hasPermission(permission);
    }

}