package com.cobelpvp.practice.match.postmatchinv;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.match.MatchTeam;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public final class PostMatchInvLang {

    private static final String WINNER = ChatColor.YELLOW + "" + ChatColor.GRAY;
    private static final String LOSER = ChatColor.YELLOW + "" + ChatColor.GRAY;
    private static final String PARTICIPANTS = ChatColor.GREEN + "Participants:";

    private static final TextComponent COMMA_COMPONENT = new TextComponent(", ");

    static {
        COMMA_COMPONENT.setColor(ChatColor.YELLOW);
    }

    static Object[] gen1v1PlayerInvs(UUID winner, UUID loser) {
        return new Object[] {
                new TextComponent[] {
                        new TextComponent(ChatColor.GOLD + "Inventories (click to view): "),
                        clickToViewLine(winner),
                        new TextComponent(ChatColor.YELLOW + " , "),
                        clickToViewLine(loser)
                }
        };
    }

    static Object[] genSpectatorInvs(MatchTeam winner, MatchTeam loser) {
        return new Object[] {
                new TextComponent(ChatColor.GOLD + "Inventories (click to view): "),
                WINNER,
                clickToViewLine(winner.getAllMembers()),
                LOSER,
                clickToViewLine(loser.getAllMembers()),
        };
    }

    static Object[] genTeamInvs(MatchTeam viewer, MatchTeam winner, MatchTeam loser) {
        return new Object[]{
                new TextComponent(ChatColor.GOLD + "Inventories (click to view): "),
                WINNER + (viewer == winner ? " (Your team)" : " (Enemy team)"),
                clickToViewLine(winner.getAllMembers()),
                LOSER + (viewer == loser ? " (Your team)" : " (Enemy team)"),
                clickToViewLine(loser.getAllMembers()),
        };
    }

    static Object[] genGenericInvs(Collection<MatchTeam> teams) {
        Set<UUID> members = teams.stream()
                .flatMap(t -> t.getAllMembers().stream())
                .collect(Collectors.toSet());

        return new Object[]{
                PARTICIPANTS,
                clickToViewLine(members),
        };
    }

    private static TextComponent clickToViewLine(UUID member) {
        String memberName = UniqueIDCache.name(member);
        TextComponent component = new TextComponent();
        component.setText(memberName);
        component.setColor(ChatColor.YELLOW);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Click to view inventory of " + ChatColor.GREEN + memberName).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/matchinventory " + memberName));

        return component;
    }

    private static TextComponent[] clickToViewLine(Set<UUID> members) {
        List<TextComponent> components = new ArrayList<>();

        for (UUID member : members) {
            components.add(clickToViewLine(member));
            components.add(COMMA_COMPONENT);
        }
        components.remove(components.size() - 1);
        return components.toArray(new TextComponent[components.size()]);
    }

}
