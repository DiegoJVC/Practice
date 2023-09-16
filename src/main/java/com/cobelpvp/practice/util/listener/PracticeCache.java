package com.cobelpvp.practice.util.listener;

import com.cobelpvp.practice.Practice;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class PracticeCache implements Runnable {

    private int onlineCount = 0;
    private int fightsCount = 0;
    private int queuesCount = 0;

    @Override
    public void run() {
        onlineCount = Bukkit.getOnlinePlayers().size();
        fightsCount = Practice.getInstance().getMatchHandler().countPlayersPlayingInProgressMatches();
        queuesCount = Practice.getInstance().getQueueHandler().getQueuedCount();
    }

}
