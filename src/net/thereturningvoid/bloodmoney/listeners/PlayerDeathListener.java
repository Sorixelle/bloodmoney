package net.thereturningvoid.bloodmoney.listeners;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import net.thereturningvoid.bloodmoney.BloodMoney;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

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
    public void onKillPlayer(PlayerDeathEvent e) throws InvalidConfigurationException {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player pKilled = e.getEntity();
            if (pKilledBy.getName().equals(pKilled.getName())) return;
            if (pKilled.getKiller() != null && getRanks() != null) {
                Player pKilledBy = pKilled.getKiller();
                if (plugin.getConfig().getBoolean("bloodmoney.sendMessageToDeadPlayer") && !plugin.getConfig().getBoolean("bloodmoney.loseMoneyOnDeath")) {
                    String dMessage = plugin.getConfig().getString("bloodmoney.deathMessage").replace("%p", pKilledBy.getName());
                    pKilled.sendMessage(BloodMoney.CHAT_PREFIX + dMessage);
                }
                for (Map.Entry rank : getRanks().entrySet()) {
                    if (!(pKilledBy instanceof Player)) {
                        String rankName = (String) rank.getKey();
                        if (perm.has(pKilledBy, "bloodmoney.rank." + rank.getKey())) {
                            EconomyResponse creditPlayer = econ.depositPlayer(pKilledBy, plugin.getConfig().getDouble("bloodmoney.ranks." + rankName + ".gain"));
                            if (creditPlayer.transactionSuccess()) {
                                String kMessage = plugin.getConfig().getString("bloodmoney.killMessage").replace("%m", econ.format(creditPlayer.amount)).replace("%p", pKilled.getName());
                                pKilledBy.sendMessage(BloodMoney.CHAT_PREFIX + kMessage);
                            } else {
                                pKilledBy.sendMessage(BloodMoney.CHAT_PREFIX + "Something went wrong and the transaction was unsuccessful.");
                            }
                        }
                    }
                    if (plugin.getConfig().getBoolean("bloodmoney.loseMoneyOnDeath")) {
                        if (perm.has(pKilled, "bloodmoney.rank." + rank.getKey())) {
                            EconomyResponse takeFromPlayer = econ.withdrawPlayer(pKilled, plugin.getConfig().getDouble("bloodmoney.ranks." + rankName + ".loss"));
                            if (takeFromPlayer.transactionSuccess()) {
                                String kMessage2 = plugin.getConfig().getString("bloodmoney.deathMessage").replace("%p", pKilledBy.getName()).replace("%m", econ.format(takeFromPlayer.amount));
                                pKilled.sendMessage(BloodMoney.CHAT_PREFIX + kMessage2);
                            }
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
