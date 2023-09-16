package com.cobelpvp.practice.lobby.scoreboard;

import com.cobelpvp.atheneum.scoreboard.ScoreboardConfiguration;
import com.cobelpvp.atheneum.scoreboard.TitleGetter;

public final class PracticeScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
    ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(TitleGetter.forStaticString("&6&lCobelPvP"));
        configuration.setScoreGetter(new MultiplexingScoreGetter(
                new MatchScoreGetter(),
                new LobbyScoreGetter()
        ));

        return configuration;
    }
}
