package net.thereturningvoid.bloodmoney.listeners;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import net.thereturningvoid.bloodmoney.BloodMoney;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;
import java.util.regex.Matcher;

public class PlayerDeathListener implements Listener {

    private BloodMoney plugin;
    private Economy econ;
    private Permission perm;

    public PlayerDeathListener(BloodMoney instance) {
        plugin = instance;
        econ = instance.economy;
        perm = instance.permission;
    }

    @EventHandler
    public void onKillPlayer(PlayerDeathEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player pKilled = e.getEntity();
            Player pKilledBy = pKilled.getKiller();
            if (getRanks() != null) {
                if (plugin.getConfig().getBoolean("bloodmoney.sendMessageToDeadPlayer")) {
                    String dMessage = plugin.getConfig().getString("bloodmoney.deathMessage").replace("%p", pKilledBy.getName());
                    pKilled.sendMessage(BloodMoney.CHAT_PREFIX + dMessage);
                }
                for (Map.Entry rank : getRanks().entrySet()) {
                    String rankName = (String) rank.getKey();
                    if (perm.has(pKilledBy, "bloodmoney.rank." + rank.getKey())) {
                        EconomyResponse creditPlayer = econ.depositPlayer(pKilledBy, plugin.getConfig().getDouble("bloodmoney.ranks." + rankName));
                        if (creditPlayer.transactionSuccess()) {
                            String kMessage = plugin.getConfig().getString("bloodmoney.killMessage").replace("%m", econ.format(creditPlayer.amount)).replace("%p", Matcher.quoteReplacement(pKilled.getName()));
                            pKilledBy.sendMessage(BloodMoney.CHAT_PREFIX + kMessage);
                        }
                    }
                }
            }
        }
    }

    private Map<String, Object> getRanks() {
        if (plugin.getConfig().isConfigurationSection("bloodmoney.ranks")) {
            return plugin.getConfig().getConfigurationSection("bloodmoney.ranks").getValues(false);
        } else return null;
    }

}
