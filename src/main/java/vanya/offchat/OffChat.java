package vanya.offchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import org.bukkit.entity.Entity;

public final class OffChat extends JavaPlugin implements Listener {
    private ChatBuffer buffer;
    ChatBubbles bubbles;
    public static Scoreboard scoreboard;
    public static Team team;
    HashMap<String, String> playerOnCN = new HashMap<>(); // map  to check name command


    // enabler
    public void onEnable()
    {
        // chat bubbles
        saveDefaultConfig();
        bubbles = new ChatBubbles(this);
        buffer = new ChatBuffer(this);
        getServer().getPluginManager().registerEvents(this, this);

        // no name tag
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        team = scoreboard.registerNewTeam("hide_nametag");
        team.setNameTagVisibility(NameTagVisibility.NEVER);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        // add in no name tag team
        Player player = e.getPlayer();
        team.addEntry(player.getName());
        player.setScoreboard(scoreboard);
        playerOnCN.put(player.getName(), "off");

        if (getConfig().getString("displayNames").equals("false"))
        {

            player.setPlayerListName("Игрок");

        }
    }

    // chat event listener, highest priority so it would be run at last
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        if (!e.isCancelled())
        {
            buffer.receiveChat(e.getPlayer(), e.getMessage());

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity clickedEntity = e.getRightClicked();

        if (clickedEntity instanceof Player) {

            if (playerOnCN.get(player.getName()).equals("on")) {
                player.sendActionBar(clickedEntity.getName());
            }

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {

            // role play commands to chat bubbles

            case "me":
                if (!(sender instanceof Player)) return false;
                if (args.length < 1) return false;
                buffer.receiveChat((Player)sender,  ChatColor.ITALIC + "*" + String.join(" ", args) + "*");
                return true;

            case "b":
                if (!(sender instanceof Player)) return false;
                if (args.length < 1) return false;
                buffer.receiveChat((Player)sender,  ChatColor.GRAY + "((" + String.join(" ", args) + "))");
                return true;

            case "try":
                if (!(sender instanceof Player)) return false;
                if (args.length < 1) return false;
                double random = Math.random();
                if (random >= 0.5) {
                    buffer.receiveChat((Player)sender,  ChatColor.ITALIC + "*" + String.join(" ", args) + "* " + ChatColor.GREEN + "(удачно)");
                } else {
                    buffer.receiveChat((Player)sender,  ChatColor.ITALIC + "*" + String.join(" ", args) + "* " + ChatColor.RED + "(неудачно)");
                }
                return true;

            case "roll":
                if (!(sender instanceof Player)) return false;
                if (args.length != 0) return false;
                int random2 = (int) (Math.random() * 11);
                buffer.receiveChat((Player)sender, ChatColor.ITALIC + "*Выбил \u00A7l " + random2 + ChatColor.WHITE + ChatColor.ITALIC + "из \u00A7l10\u00A7f*");
                return true;


            // command to check real name

            case "checkname":
                if (!(sender instanceof Player)) return false;
                if (args.length != 0) return false;
                if (playerOnCN.get(sender.getName()).equals("on")) { // command off
                    playerOnCN.put(sender.getName(), "off");
                    sender.sendMessage("Вы выключи показ имени на пкм");
                    return true;

                } else { // command on
                    playerOnCN.put(sender.getName(), "on");
                    sender.sendMessage("Вы включили показ имени на пкм");
                    return true;
                }
        }


        return super.onCommand(sender, command, label, args);

    }

}
