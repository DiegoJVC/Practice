package com.cobelpvp.practice.lobby.scoreboard;

import java.util.function.BiConsumer;
import com.cobelpvp.practice.Practice;
import com.cobelpvp.practice.match.MatchHandler;
import com.cobelpvp.practice.lobby.setting.Setting;
import com.cobelpvp.practice.lobby.setting.SettingHandler;
import com.cobelpvp.atheneum.scoreboard.ScoreGetter;
import com.cobelpvp.atheneum.util.LinkedList;
import org.bukkit.entity.Player;

final class MultiplexingScoreGetter implements ScoreGetter {

    private final BiConsumer<Player, LinkedList<String>> matchScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> LobbyScoreGetter;

    MultiplexingScoreGetter(
            BiConsumer<Player, LinkedList<String>> matchScoreGetter,
            BiConsumer<Player, LinkedList<String>> LobbyScoreGetter
    ) {
        this.matchScoreGetter = matchScoreGetter;
        this.LobbyScoreGetter = LobbyScoreGetter;
    }

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        if (Practice.getInstance() == null) return;
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                matchScoreGetter.accept(player, scores);
            } else {
                LobbyScoreGetter.accept(player, scores);
            }
        }

        if (!scores.isEmpty()) {
            scores.addFirst("&a&7&m--------------------");
            scores.add("&f&7&m--------------------");
        }
    }

}