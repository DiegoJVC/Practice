package com.cobelpvp.practice.match.postmatchinv.menu;

import com.cobelpvp.atheneum.util.TimeUtils;
import library.cobelpvp.menu.Button;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;
import java.util.stream.Collectors;

final class PostMatchPotionEffectsButton extends Button {

    private final List<PotionEffect> effects;

    PostMatchPotionEffectsButton(List<PotionEffect> effects) {
        this.effects = ImmutableList.copyOf(effects);
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Potion Effects";
    }

    @Override
    public List<String> getDescription(Player player) {
        if (!effects.isEmpty()) {
            return effects.stream()
                .map(effect ->
                    ChatColor.BLUE +
                    formatEffectType(effect.getType()) +
                    " " +
                    (effect.getAmplifier() + 1) +
                    ChatColor.GRAY +
                    " - " +
                    TimeUtils.formatIntoMMSS(effect.getDuration() / 20)
                )
                .collect(Collectors.toList());
        } else {
            return ImmutableList.of(
            "",
            ChatColor.GRAY + "No potion effects."
            );
        }
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    private String formatEffectType(PotionEffectType type) {
        switch (type.getName().toLowerCase()) {
            case "fire_resistance": return "Fire Resistance";
            case "increase_damage": return "Strength";
            case "damage_resistance": return "Resistance";
            default: return StringUtils.capitalize(type.getName().toLowerCase());
        }
    }

}