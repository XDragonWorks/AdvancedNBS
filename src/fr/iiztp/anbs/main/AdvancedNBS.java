package fr.iiztp.anbs.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.data.Radio;
import fr.iiztp.anbs.main.commands.ANBSHandler;
import fr.iiztp.anbs.main.listeners.CrackShot;
import fr.iiztp.anbs.main.listeners.Entity;
import fr.iiztp.anbs.main.listeners.MyListener;
import fr.iiztp.anbs.main.listeners.NoteBlockAPI;
import fr.iiztp.anbs.main.listeners.QuestCreator;
import fr.iiztp.anbs.main.listeners.Server;
import fr.iiztp.anbs.main.listeners.Sign;
import fr.iiztp.anbs.main.listeners.SomeExpansion;
import fr.iiztp.anbs.main.listeners.WorldGuard;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.anbs.utils.WorldGuardUtils;
import fr.iiztp.mlib.AbstractPlugin;
import fr.iiztp.mlib.datastructure.CompositeKeyList;
import fr.iiztp.mlib.utils.MySQL;
import fr.iiztp.mlib.utils.Query;
import fr.iiztp.mlib.YamlReader;

/**
 * Main class
 * @author iiztp
 * @version 1.0.3
 */
public class AdvancedNBS extends AbstractPlugin
{
	private static AdvancedNBS plugin;
	public static AdvancedNBS getInstance() {
		return plugin;
	}

	private CompositeKeyList<Player, PlayerData> audioPlayers = new CompositeKeyList<>();
	private List<Radio> radios = new ArrayList<>();
	private BukkitTask checkModeTask;
	private BukkitTask worldGuardTask;
	private MySQL database;
	
	public void onEnable()
	{
		resourceId = 81195;
		plugin = this;
		plugin.getDataFolder().mkdir();
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		plugin.getCommand("anbs").setExecutor((CommandExecutor)new ANBSHandler());
		checkForUpdates();
	    
		new File(plugin.getDataFolder()+ "/combat/").mkdir();
		new File(plugin.getDataFolder()+ "/radios/").mkdir();
		YamlReader config = plugin.getReader();
		if(!config.getBoolean("database.enable"))
			new File(plugin.getDataFolder()+"/playerdata/").mkdir();
		else
		{
			String host = config.getString("database.host");
			String name = config.getString("database.name");
			String url = "jdbc:mysql://" + host + "/" + name + "?allowMultiQueries=true";
			this.sendDebug(url);
			database = new MySQL(url, config.getString("database.user"), config.getString("database.password"));
			try {
				database.connect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Query query = new Query("CREATE TABLE IF NOT EXISTS ADVANCEDNBS(player_uid VARCHAR(255) NOT NULL PRIMARY KEY, volume SMALLINT, mute BOOLEAN)");
			database.performQuery(query);
		}
		
		Radio.loadRadios();
		
		try
		{
			File file = new File(plugin.getDataFolder(), "lang.yml");
			file.createNewFile();
			Files.copy(plugin.getResource("lang.yml"), file.toPath());
			new File(plugin.getDataFolder(), "data").createNewFile();
		}
		catch(FileAlreadyExistsException e)
		{
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		PluginManager plMan = Bukkit.getPluginManager();
		plMan.registerEvents((Listener)new Entity(), (Plugin)this);
		plMan.registerEvents((Listener)new NoteBlockAPI(), (Plugin)this);
		plMan.registerEvents((Listener)new Server(), (Plugin)this);
		plMan.registerEvents((Listener)new Sign(), (Plugin)this);
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(!plugin.getAudioPlayers().containsKey1(player))
				plugin.getAudioPlayers().add(player, new PlayerData(player));
		}
		
		YamlReader data = plugin.getData();
		Set<String> signs = data.getSections("signs");
		if(signs != null)
		{
			Map<String, CompositeKeyList<String, org.bukkit.block.Sign>> radiosWithSigns = new HashMap<>();
			for(String key : signs)
			{
				ConfigurationSection section = data.getConfigurationSection("signs." + key);
				org.bukkit.block.Sign sign = (org.bukkit.block.Sign)(Bukkit.getWorld(section.getString("world"))
						.getBlockAt(Integer.parseInt(section.getString("x")),
								Integer.parseInt(section.getString("y")),
								Integer.parseInt(section.getString("z"))).getState());
				radiosWithSigns.computeIfAbsent(section.getString("name"), __ -> new CompositeKeyList<>()).add(key, sign);
			}
			for(Radio radio : radios)
			{
				radio.setAssociatedSigns(radiosWithSigns.computeIfAbsent(radio.getName(), __ -> new CompositeKeyList<>()));
			}
		}
		
		Plugin MM = plMan.getPlugin("MythicMobs");
		if(MM != null && MM.isEnabled())
			plugin.sendDebug("MythicMobs " + MM.getDescription().getVersion() + " support is enabled !");
		else
			plugin.sendDebug("MythicMobs support is disabled !");
		
		Plugin WG = plMan.getPlugin("WorldGuard");
		if(WG != null && WG.isEnabled())
		{
			plMan.registerEvents((Listener)new WorldGuard(), (Plugin)this);
			plMan.registerEvents((Listener)new MyListener(), (Plugin)this);
			worldGuardTask = WorldGuardUtils.loadWorldGuard();
			plugin.sendDebug("WorldGuard " + WG.getDescription().getVersion() + " support is enabled !");
		}
		else
			plugin.sendDebug("WorldGuard support is disabled !");
		
		Plugin PAPI = plMan.getPlugin("PlaceholderAPI");
		
		if(PAPI != null && PAPI.isEnabled())
		{
			(new SomeExpansion(this)).register();
			plugin.sendDebug("PlaceHolder " + PAPI.getDescription().getVersion() + " support is enabled !");
		}
		else
			plugin.sendDebug("PlaceHolder support is disabled !");
		
		Plugin QC = plMan.getPlugin("QuestCreator");
		
		if(QC != null && QC.isEnabled())
		{
			plMan.registerEvents((Listener)new QuestCreator(), (Plugin)this);
			plugin.sendDebug("QuestCreator " + QC.getDescription().getVersion() + " support is enabled !");
		}
		else
			plugin.sendDebug("QuestCreator support is disabled !");
		
		Plugin CS = plMan.getPlugin("CrackShot");
		
		if(CS != null && CS.isEnabled())
		{
			plMan.registerEvents((Listener)new CrackShot(), (Plugin)this);
			plugin.sendDebug("CrackShot " + CS.getDescription().getVersion() + " support is enabled !");
		}
		else
			plugin.sendDebug("CrackShot support is disabled !");
		
		checkModeTask = Utils.checkModes();
	}
	
	public void onDisable()
	{
		if(checkModeTask != null)
			checkModeTask.cancel();
		if(worldGuardTask != null)
			worldGuardTask.cancel();
		radios.clear();
		for(PlayerData pd : audioPlayers.getKeys2())
		{
			pd.getRsp().destroy();
		}
		audioPlayers.clear();
	}
	
	public void setWorldGuardTask(BukkitTask worldGuardTask) {
		this.worldGuardTask = worldGuardTask;
	}
	
	public void setCheckModeTask(BukkitTask checkModeTask) {
		this.checkModeTask = checkModeTask;
	}
	
	public CompositeKeyList<Player, PlayerData> getAudioPlayers() {
		return audioPlayers;
	}
	
	public List<Radio> getRadios() {
		return radios;
	}
	
	public MySQL getDatabase() {
		return database;
	}
}
