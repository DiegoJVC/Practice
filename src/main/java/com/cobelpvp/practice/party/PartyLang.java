package com.cobelpvp.practice.party;

import com.cobelpvp.practice.util.uuid.UniqueIDCache;
import com.cobelpvp.practice.Practice;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PartyLang {

    private static final TextComponent INVITE_PREFIX = new TextComponent("");

    private static final TextComponent INVITED_YOU_TO_JOIN = new TextComponent (" has invited you to join their party. ");

    private static final TextComponent ACCEPT_BUTTON = new TextComponent("[Click to Accept]");
    private static final TextComponent INFO_BUTTON = new TextComponent("[Info]");

    static {
        INVITE_PREFIX.setColor(ChatColor.DARK_GREEN);
        INVITE_PREFIX.setBold(true);
        INVITED_YOU_TO_JOIN.setColor(ChatColor.YELLOW);
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT; // readability
        BaseComponent[] acceptTooltip = new ComponentBuilder("Click to join party").color(ChatColor.GREEN).create();
        ACCEPT_BUTTON.setColor(ChatColor.YELLOW);
        ACCEPT_BUTTON.setHoverEvent(new HoverEvent(showText, acceptTooltip));
        INFO_BUTTON.setColor(ChatColor.BLUE);
    }

    public static TextComponent inviteAcceptPrompt(Party party) {
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        String partyLeader = UniqueIDCache.name(party.getLeader());

        TextComponent acceptButton = new TextComponent(ACCEPT_BUTTON);
        TextComponent infoButton = new TextComponent(INFO_BUTTON);
        acceptButton.setClickEvent(new ClickEvent(runCommand, "/p join " + partyLeader));
        infoButton.setHoverEvent(hoverablePreviewTooltip(party));
        infoButton.setClickEvent(new ClickEvent(runCommand, "/p info " + partyLeader));
        TextComponent builder = new TextComponent("");
        builder.addExtra(INVITE_PREFIX);
        builder.addExtra(hoverablePartyName(party));
        builder.addExtra(INVITED_YOU_TO_JOIN);
        builder.addExtra(acceptButton);
        builder.addExtra(new TextComponent(" "));
        builder.addExtra(infoButton);
        return builder;
    }

    public static TextComponent hoverablePartyName(Party party) {
        TextComponent previewComponent = new TextComponent();
        String leaderName = UniqueIDCache.name(party.getLeader());

        if (party.getMembers().size() > 1) {
            HoverEvent hoverEvent = hoverablePreviewTooltip(party);

            previewComponent.setText("[" + leaderName + "'s Party]");
            previewComponent.setHoverEvent(hoverEvent);
        } else {
            previewComponent.setText(leaderName);
        }

        previewComponent.setColor(ChatColor.DARK_GREEN);
        return previewComponent;
    }

    public static HoverEvent hoverablePreviewTooltip(Party party) {
        ComponentBuilder builder = new ComponentBuilder(ChatColor.YELLOW + "Members" + "(").color(ChatColor.GOLD);
        String size = "" + party.getMembers().size();
        builder.append(size).color(ChatColor.GOLD);
        builder.append("):").color(ChatColor.GOLD);

        for (String member : getMemberPreviewNames(party)) {
            builder.append("\n");
            builder.append(member);
        }

        HoverEvent.Action action = HoverEvent.Action.SHOW_TEXT;
        return new HoverEvent(action, builder.create());
    }

    private static List<String> getMemberPreviewNames(Party party) {
        List<UUID> members = new ArrayList<>(party.getMembers());
        int partySize = members.size();
        List<String> displayNames = new ArrayList<>();

        for (int i = 0; i < Math.min(partySize, 6); i++) {
            UUID member = members.remove(0);
            String suffix = party.isLeader(member) ? "*" : "";
            displayNames.add(ChatColor.GREEN + UniqueIDCache.name(member) + suffix);
        }

        if (!members.isEmpty()) {
            displayNames.add(ChatColor.YELLOW + "+ " + ChatColor.GOLD + members.size() + ChatColor.YELLOW + " more");
        }
        return displayNames;
    }

}