/**
 * Handles accessing, saving, updating, and presentation of player settings.
 *
 * This includes the /settings command, a settings menu, persistence, etc.
 * Clients using the settings API should only concern themselves with {@link com.cobelpvp.practice.lobby.setting.event.SettingUpdateEvent},
 * {@link com.cobelpvp.practice.lobby.setting.SettingHandler#getSetting(java.util.UUID, Setting)} and
 * {@link com.cobelpvp.practice.lobby.setting.SettingHandler#updateSetting(org.bukkit.entity.Player, Setting, boolean)},
 */
package com.cobelpvp.practice.lobby.setting;

