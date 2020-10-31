package cc.i9mc.newxbedwars.game;

import cc.i9mc.gameutils.utils.ItemBuilderUtil;
import cc.i9mc.gameutils.utils.TitleUtil;
import cc.i9mc.gameutils.utils.board.Board;
import cc.i9mc.k8sgameack.events.ACKGameLoadingEvent;
import cc.i9mc.k8sgameack.events.ACKGameStartEvent;
import cc.i9mc.newxbedwars.NewXBedwars;
import cc.i9mc.newxbedwars.databse.Database;
import cc.i9mc.newxbedwars.databse.PlayerData;
import cc.i9mc.newxbedwars.databse.map.MapData;
import cc.i9mc.newxbedwars.events.BedwarsGameStartEvent;
import cc.i9mc.newxbedwars.game.event.EventManager;
import cc.i9mc.newxbedwars.scoreboards.GameBoard;
import cc.i9mc.newxbedwars.scoreboards.LobbyBoard;
import cc.i9mc.newxbedwars.shop.ItemShopManager;
import cc.i9mc.newxbedwars.specials.SpecialItem;
import cc.i9mc.newxbedwars.types.ArmorType;
import cc.i9mc.newxbedwars.types.ToolType;
import cc.i9mc.newxbedwars.utils.Util;
import cc.i9mc.nick.Nick;
import cc.i9mc.pluginchannel.BukkitChannel;
import cc.i9mc.pluginchannel.bukkit.PBukkitChannelTask;
import cc.i9mc.rejoin.events.RejoinGameDeathEvent;
import cc.i9mc.rejoin.events.RejoinPlayerJoinEvent;
import com.nametagedit.plugin.NametagEdit;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

@Data
public class Game {
    private NewXBedwars main;
    private EventManager eventManager;
    private SpectatorManger spectatorManger;
    private MapData mapData;
    private GameState gameState;
    private boolean forceStart;

    private Location waitingLocation;

    private List<Location> blocks;
    private GameLobbyCountdown gameLobbyCountdown = null;
    private List<Team> teams;

    private HashMap<UUID, ArmorType> armor;
    private HashMap<UUID, ToolType> pickAxe;
    private HashMap<UUID, ToolType> axe;
    private List<UUID> shears;

    private HashMap<ArmorStand, String> armorSande;
    private HashMap<ArmorStand, String> armorStand;

    private List<SpecialItem> specialItems;

    private HashMap<Player, List<Player>> party;
    private List<String> allowReJoin;
    private HashMap<Player, Player> playerDamages;

    public Game(NewXBedwars main, Location waitingLocation, MapData mapData) {
        this.main = main;
        this.mapData = mapData;
        this.forceStart = false;
        this.waitingLocation = waitingLocation;
        this.blocks = mapData.loadMap();
        this.teams = new ArrayList<>();

        this.armor = new HashMap<>();
        this.pickAxe = new HashMap<>();
        this.axe = new HashMap<>();
        this.shears = new ArrayList<>();

        this.armorSande = new HashMap<>();
        this.armorStand = new HashMap<>();

        this.specialItems = new ArrayList<>();

        this.playerDamages = new HashMap<>();
        this.allowReJoin = new ArrayList<>();
        this.party = new HashMap<>();

        for (int i = 0; i < mapData.getBaseLocations().size(); i++) {
            teams.add(new Team(TeamColor.values()[i], mapData.getBaseLocations().get(i)));
        }

        ItemShopManager.init(this);
        this.eventManager = new EventManager(this);
        this.spectatorManger = new SpectatorManger(this);
        this.gameState = GameState.WAITING;
        Bukkit.getPluginManager().callEvent(new ACKGameLoadingEvent(getMaxPlayers()));
    }

    public void clearPlayer(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.setExhaustion(0.0f);
        player.setMaxHealth(20.0D);
        player.setHealth(20.0f);
        player.setFireTicks(0);

        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(new ItemStack[4]);
        inv.setContents(new ItemStack[]{});
        player.getActivePotionEffects().forEach((potionEffect -> player.removePotionEffect(potionEffect.getType())));
    }

    public void addPlayer(Player player) {
        if (gameState == GameState.RUNNING) {
            main.getBoardManager().getBoardMap().put(player.getUniqueId(), new Board(player, "SB", Collections.singletonList("Test")));
            GameBoard.show(player);
            GameBoard.updateBoard();

            if (allowReJoin.contains(player.getName()) && !spectatorManger.isSpectator(player)) {
                Team team = getTeam(player);
                if (team != null && !team.isBedDestroy()) {
                    Util.setPlayerTeamTab();
                    Bukkit.getPluginManager().callEvent(new PlayerRespawnEvent(player, mapData.getReSpawnLocation(), false));
                    broadcastMessage("§7" + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "§a重连上线");
                    return;
                }

                if (team != null) team.removePlayer(player);
                Bukkit.getPluginManager().callEvent(new RejoinGameDeathEvent(player));
            }

            getPlayers().forEach((player1 -> player1.hidePlayer(player)));
            player.teleport(mapData.getReSpawnLocation());
            if (!spectatorManger.isSpectator(player)) spectatorManger.addSpectator(player);
            toSpectator(player);
            return;
        }

        if (gameState == GameState.WAITING) {
            getPlayers().forEach((player1 -> {
                player1.hidePlayer(player);
                player1.showPlayer(player);

                player.hidePlayer(player);
                player.showPlayer(player);
            }));
        }

        player.spigot().respawn();
        player.setGameMode(GameMode.ADVENTURE);
        player.getEnderChest().clear();
        clearPlayer(player);

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> PBukkitChannelTask.createTask()
                .channel(BukkitChannel.getInst().getBukkitChannel())
                .sender(player)
                .command("BungeeParty", "data", player.getName())
                .result((result) -> {
                    List<String> results = Arrays.asList(result);
                    if (Bukkit.getPlayerExact(results.get(1)) == null) return;
                    if (party.containsKey(Bukkit.getPlayerExact(results.get(1)))) return;

                    String teamName = results.get(0);
                    LinkedList<Player> data = new LinkedList<>();

                    for (int i = 1; i < results.size(); i++) {
                        if (Bukkit.getPlayerExact(results.get(i)) == null) return;
                        data.add(Bukkit.getPlayerExact(results.get(i)));
                    }

                    if (gameState == GameState.RUNNING) return;
                    if (data.size() == 1) return;

                    party.put(Bukkit.getPlayerExact(result[1]), data);
                    broadcastMessage("§a队长§e" + result[1] + "§a带着他的队伍§e" + teamName + "§a加入了");

                    Bukkit.getScheduler().runTask(main, () -> {
                        for (Player player1 : data) {
                            if (getTeam(player1) != null) {
                                getTeam(player1).removePlayer(player1);
                            }
                        }

                        Team team = getLowestTeam();
                        if (data.size() <= (mapData.getTeamMaxPlayers() - team.getPlayers().size())) {
                            for (Player player1 : data) team.addPlayer(player1);
                        }
                    });
                }).run());

        TitleUtil.sendTitle(player, 0, 30, 5, "§e§l超级起床战争", mapData.getAuthor().equals("unknown") ? "§b游戏地址: PLAY.MCYC.WIN" : "§b建筑师: " + mapData.getAuthor() + " 游戏地址: PLAY.MCYC.WIN");
        broadcastMessage("§7" + ChatColor.translateAlternateColorCodes('&', main.getChat().getPlayerPrefix(player)).replace("[VIP]", "") + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "§e加入游戏!");
        NametagEdit.getApi().setPrefix(player, main.getChat().getPlayerPrefix(player).replace("[VIP]", ""));
        if (Nick.get().getCache().get(player.getName()) != null) {
            NametagEdit.getApi().setSuffix(player, "" + Nick.get().getCache().get(player.getName()) + "");
        }

        player.teleport(getWaitingLocation());

        main.getBoardManager().getBoardMap().put(player.getUniqueId(), new Board(player, "SB", Collections.singletonList("Test")));
        LobbyBoard.show(player);
        LobbyBoard.updateBoard();

        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.PAPER).setDisplayName("§a资源类型选择§7(右键选择)").getItem());
        player.getInventory().setItem(8, new ItemBuilderUtil().setType(Material.SLIME_BALL).setDisplayName("§c离开游戏§7(右键离开)").getItem());

        if (isStartable()) {
            if (gameState == GameState.WAITING && getGameLobbyCountdown() == null) {
                GameLobbyCountdown lobbyCountdown = new GameLobbyCountdown(this);
                lobbyCountdown.runTaskTimer(main, 20L, 20L);
                setGameLobbyCountdown(lobbyCountdown);
            }
        }
    }

    public void removePlayers(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> Database.savePlayerData(main.getCacher().get(player.getName())));
        if (gameState == GameState.WAITING) {
            broadcastMessage("§7" + Nick.get().getCache().getOrDefault(player.getName(), player.getName()) + "§e离开游戏");
        }

        if (gameState == GameState.RUNNING && spectatorManger.isSpectator(player)) {
            return;
        }

        Team team = getTeam(player);
        if (team == null) {
            return;
        }

        if (team.getPlayers().isEmpty()) {
            if (!team.isBedDestroy()) {
                team.setBedDestroy(true);
            }

            if (!team.isDie()) {
                team.setDie(true);
            }
        }

        if (team.isBedDestroy() || team.isDie()) {
            Bukkit.getPluginManager().callEvent(new RejoinGameDeathEvent(player));
            allowReJoin.remove(player.getName());
        }
    }

    public ArrayList<Player> getGamePlayers() {
        ArrayList<Player> players = new ArrayList<>();
        teams.forEach(team -> players.addAll(team.getPlayers()));
        return players;
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public int getMaxPlayers() {
        return mapData.getBaseLocations().size() * mapData.getTeamMaxPlayers();
    }

    boolean hasEnoughPlayers() {
        return getPlayers().size() >= mapData.getMinPlayers();
    }

    public Team getLowestTeam() {
        Team lowest = null;
        for (Team team : teams) {
            if (lowest == null) {
                lowest = team;
                continue;
            }

            if (team.getPlayers().size() < lowest.getPlayers().size()) {
                lowest = team;
            }
        }

        return lowest;
    }


    public void moveFreePlayersToTeam() {
        for (Player player : getPlayers()) {
            if (getTeam(player) != null) {
                continue;
            }

            Team lowest = getLowestTeam();
            lowest.addPlayer(player);
        }
    }

    public void teleportPlayersToTeamSpawn() {
        for (Team team : this.teams) {
            for (Player player : team.getPlayers()) {
                player.setVelocity(new Vector(0, 0, 0));
                player.setFallDistance(0.0F);
                player.teleport(team.getSpawn());
            }
        }
    }

    public Player findTargetPlayer(Player player) {
        Player foundPlayer = null;
        double distance = Double.MAX_VALUE;

        Team team = getTeam(player);

        ArrayList<Player> possibleTargets = new ArrayList<>(getAlivePlayers());
        possibleTargets.removeAll(team.getAlivePlayers());

        for (Player player1 : possibleTargets) {
            if (player.getWorld() != player1.getWorld()) {
                continue;
            }

            double dist = player.getLocation().distance(player1.getLocation());
            if (dist < distance) {
                foundPlayer = player1;
                distance = dist;
            }
        }

        return foundPlayer;
    }

    public void giveInventory() {
        getPlayers().forEach(this::giveInventory);
    }

    public void giveInventory(Player player) {
        Team team = getTeam(player);
        player.getInventory().setHelmet(new ItemBuilderUtil().setType(Material.LEATHER_HELMET).setColor(team.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
        player.getInventory().setChestplate(new ItemBuilderUtil().setType(Material.LEATHER_CHESTPLATE).setColor(team.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());

        giveInventory(player, 1, false);
        giveInventory(player, 5, false);
        giveInventory(player, 2, false);
        giveInventory(player, 3, false);
        giveInventory(player, 4, false);
    }

    public void giveInventory(Player player, int level, boolean remove) {
        Team team = getTeam(player);
        if (level == 1) {
            if (!armor.containsKey(player.getUniqueId())) {
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.LEATHER_LEGGINGS).setColor(team.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.LEATHER_BOOTS).setColor(team.getColor()).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
            } else if (armor.get(player.getUniqueId()) == ArmorType.CHAINMAIL) {
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.CHAINMAIL_LEGGINGS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.CHAINMAIL_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
            } else if (armor.get(player.getUniqueId()) == ArmorType.IRON) {
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.IRON_LEGGINGS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.IRON_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
            } else if (armor.get(player.getUniqueId()) == ArmorType.DIAMOND) {
                player.getInventory().setLeggings(new ItemBuilderUtil().setType(Material.DIAMOND_LEGGINGS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
                player.getInventory().setBoots(new ItemBuilderUtil().setType(Material.DIAMOND_BOOTS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
            }

            if (getTeam(player).getReinforcedArmor() > 0) {
                for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
                    ItemStack itemStack1 = player.getInventory().getArmorContents()[i];
                    itemStack1.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getTeam(player).getReinforcedArmor());
                }
            }
        }
        if (level == 2) {
            if (pickAxe.containsKey(player.getUniqueId())) {
                switch (pickAxe.get(player.getUniqueId())) {
                    case STONE:
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    case IRON:
                        if (remove) player.getInventory().remove(Material.WOOD_PICKAXE);
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.STONE_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    case DIAMOND:
                        if (remove) player.getInventory().remove(Material.STONE_PICKAXE);
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.IRON_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    case MAX:
                        if (remove) player.getInventory().remove(Material.IRON_PICKAXE);
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.DIAMOND_PICKAXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    default:
                        break;
                }
            }
        } else if (level == 3) {
            if (axe.containsKey(player.getUniqueId())) {
                switch (axe.get(player.getUniqueId())) {
                    case STONE:
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    case IRON:
                        if (remove) player.getInventory().remove(Material.WOOD_AXE);
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.STONE_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    case DIAMOND:
                        if (remove) player.getInventory().remove(Material.STONE_AXE);
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.IRON_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    case MAX:
                        if (remove) player.getInventory().remove(Material.IRON_AXE);
                        player.getInventory().addItem(new ItemBuilderUtil().setType(Material.DIAMOND_AXE).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addEnchant(Enchantment.DIG_SPEED, 1).getItem());
                        break;
                    default:
                        break;
                }
            }
        } else if (level == 4) {
            if (shears.contains(player.getUniqueId()))
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.SHEARS).setUnbreakable(true, true).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).getItem());
        } else if (level == 5) {
            if (team.isSharpenedSwords()) {
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true, true).getItem());
            } else {
                player.getInventory().addItem(new ItemBuilderUtil().setType(Material.WOOD_SWORD).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true, true).getItem());
            }
        }

        player.updateInventory();
    }

    public Team getTeam(String name) {
        for (Team team : teams) {
            if (team.isInTeam(name)) {
                return team;
            }
        }
        return null;
    }


    public Team getTeam(Player player) {
        for (Team team : teams) {
            if (team.isInTeam(player)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeam(TeamColor teamColor) {
        for (Team team : teams) {
            if (team.getTeamColor() == teamColor) {
                return team;
            }
        }
        return null;
    }

    public void addSpecialItem(SpecialItem specialItem) {
        this.specialItems.add(specialItem);
    }

    public void removeSpecialItem(SpecialItem specialItem) {
        this.specialItems.remove(specialItem);
    }

    public void toSpectator(Player player) {
        player.spigot().setCollidesWithEntities(false);
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.setAllowFlight(true);
        Util.setFlying(player);
        player.teleport(mapData.getReSpawnLocation());

        ItemStack itemStack = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§l传送器§7(右键打开)");
        itemStack.setItemMeta(itemMeta);
        player.getInventory().setItem(0, itemStack);

        itemStack = new ItemStack(Material.REDSTONE_COMPARATOR);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c§l旁观者设置§7(右键打开)");
        itemStack.setItemMeta(itemMeta);
        player.getInventory().setItem(4, itemStack);

        itemStack = new ItemStack(Material.PAPER);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§b§l快速加入§7(右键加入)");
        itemStack.setItemMeta(itemMeta);
        player.getInventory().setItem(7, itemStack);

        itemStack = new ItemStack(Material.SLIME_BALL);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c§l离开游戏§7(右键离开)");
        itemStack.setItemMeta(itemMeta);
        player.getInventory().setItem(8, itemStack);
    }

    public void broadcastTitle(Integer fadeIn, Integer stay, Integer fadeOut, String title, String subTitle) {
        getPlayers().forEach(player -> TitleUtil.sendTitle(player, fadeIn, stay, fadeOut, title, subTitle));
    }

    public void broadcastTeamTitle(Team team, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subTitle) {
        team.getPlayers().forEach(player -> TitleUtil.sendTitle(player, fadeIn, stay, fadeOut, title, subTitle));
    }

    public void broadcastTeamMessage(Team team, String... texts) {
        team.getPlayers().forEach(player -> Arrays.asList(texts).forEach(player::sendMessage));
    }

    public void broadcastTeamSound(Team team, Sound sound, float v, float v1) {
        team.getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, v, v1));
    }

    public void broadcastSpectatorMessage(String... texts) {
        spectatorManger.getSpectators().forEach(name -> Arrays.asList(texts).forEach(message -> {
            Player player = Bukkit.getPlayerExact(name);
            if (player != null) {
                player.sendMessage(texts);
            }
        }));
    }

    public void broadcastMessage(String... texts) {
        getPlayers().forEach(player -> Arrays.asList(texts).forEach(player::sendMessage));
    }

    public void broadcastSound(Sound sound, float v, float v1) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, v, v1));
    }

    public Player getPlayerDamager(Player player) {
        return this.playerDamages.get(player);
    }

    public void setPlayerDamager(Player p, Player damager) {
        this.playerDamages.remove(p);
        this.playerDamages.put(p, damager);
    }

    public boolean isStartable() {
        return (this.hasEnoughPlayers() && this.hasEnoughTeams());
    }

    public boolean hasEnoughTeams() {
        int teamsWithPlayers = 0;
        for (Team team : teams) {
            if (team.getPlayers().size() > 0) {
                teamsWithPlayers++;
            }
        }

        List<Player> freePlayers = getPlayers();
        freePlayers.removeAll(getGamePlayers());

        return (teamsWithPlayers > 1 || (teamsWithPlayers == 1 && freePlayers.size() >= 1) || (teamsWithPlayers == 0 && freePlayers.size() >= 2));
    }

    public String getFormattedTime(int time) {
        String minStr;
        String secStr;
        int min = (int) Math.floor(time / 60);
        int sec = time % 60;
        minStr = min < 10 ? "0" + min : String.valueOf(min);
        secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
        return minStr + ":" + secStr;
    }


    public void start() {
        gameState = GameState.RUNNING;
        Bukkit.getPluginManager().callEvent(new ACKGameStartEvent());

        moveFreePlayersToTeam();
        eventManager.start();
        spectatorManger.start();

        getPlayers().forEach(player -> {
            Bukkit.getPluginManager().callEvent(new RejoinPlayerJoinEvent(player));
            getAllowReJoin().add(player.getName());

            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.setExp(0);
            player.setLevel(0);
            clearPlayer(player);
        });

        teleportPlayersToTeamSpawn();

        getTeams().forEach(team -> {
            if (team.getPlayers().isEmpty()) {
                team.setBedDestroy(true);
                team.setDie(true);
            }
        });

        giveInventory();
        Bukkit.getPluginManager().callEvent(new BedwarsGameStartEvent());
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();
        this.teams.forEach(team -> players.addAll(team.getAlivePlayers()));
        return players;
    }

    public List<Team> getAliveTeams() {
        List<Team> teams = new ArrayList<>();

        this.teams.forEach(team -> {
            List<OfflinePlayer> players = new ArrayList<>(team.getAlivePlayers());

            if (!players.isEmpty()) {
                teams.add(team);
            }
        });
        return teams;
    }

    public boolean isOver() {
        return getAliveTeams().isEmpty() || getAliveTeams().size() <= 1;
    }

    public Team getWinner() {
        List<Team> teams = getAliveTeams();
        return teams.size() == 1 ? teams.get(0) : null;
    }
}
