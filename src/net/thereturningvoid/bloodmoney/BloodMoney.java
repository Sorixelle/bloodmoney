package net.thereturningvoid.bloodmoney;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thereturningvoid.bloodmoney.listeners.PlayerDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

public class BloodMoney extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    public static final ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    public static final String VERSION = "v0.0.1-SNAPSHOT";
    public static final String CHAT_PREFIX = ChatColor.BOLD + "" + ChatColor.GOLD + "[" + ChatColor.DARK_RED + "Blood" + ChatColor.RED + "Money" + ChatColor.GOLD + "]" + ChatColor.RESET + " ";

    public Permission permission;
    public Economy economy;
    public BloodMoney instance = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if (!registerEconomy()) {
            log.info(ChatColor.stripColor(CHAT_PREFIX) + "Vault not installed on this server! Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerPermissions();
        registerListeners();
        instance = this;
        log.info(ChatColor.stripColor(CHAT_PREFIX) + "Enabled BloodMoney " + VERSION);
    }

    @Override
    public void onDisable() {
        instance = null;
        log.info(ChatColor.stripColor(CHAT_PREFIX) + "Disabled BloodMoney " + VERSION);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bloodtest")) {
            sender.sendMessage(CHAT_PREFIX + "BloodMoney " + VERSION + " is good to go!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("bmreloadconfig")) {
            this.reloadConfig();
            sender.sendMessage(CHAT_PREFIX + "Config reloaded!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("cfgtest")) {
            if (getConfig().isConfigurationSection("test2")) {
                Map<String, Object> values = getConfig().getConfigurationSection("test2").getValues(true);
                for (Map.Entry<String, Object> value : values.entrySet()) {
                    sender.sendMessage(CHAT_PREFIX + "" + value.getKey() + " : " + value.getValue());
                }
            }
        }
        return false;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    private boolean registerPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        permission = permissionProvider.getProvider();
        return permission != null;
    }

    private boolean registerEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) return false;
        economy = economyProvider.getProvider();
        return economy != null;
    }

}
