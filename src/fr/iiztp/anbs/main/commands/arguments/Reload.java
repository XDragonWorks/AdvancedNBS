package fr.iiztp.anbs.main.commands.arguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import fr.iiztp.anbs.data.Radio;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.anbs.utils.WorldGuardUtils;
import fr.iiztp.mlib.YamlReader;
import fr.iiztp.mlib.datastructure.CompositeKeyList;

import net.md_5.bungee.api.ChatColor;

/**
 * Class for reload handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class Reload
{
	public static boolean execute(CommandSender sender, Command command, String label, String[] args)
	{
		AdvancedNBS plugin = AdvancedNBS.getInstance();
		
		plugin.onDisable();
		
		plugin.getDataFolder().mkdir();
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		new File(plugin.getDataFolder()+ "/combat/").mkdir();
		new File(plugin.getDataFolder()+ "/radios/").mkdir();
		new File(plugin.getDataFolder(), "data").mkdir();
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
		
		Plugin WG = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(WG != null && WG.isEnabled())
		{
			plugin.setWorldGuardTask(WorldGuardUtils.loadWorldGuard());
		}
		else
			plugin.sendDebug("WorldGuard support is disabled !");
		
		YamlReader data = AdvancedNBS.getInstance().getData();
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
			for(Radio radio : plugin.getRadios())
			{
				radio.setAssociatedSigns(radiosWithSigns.computeIfAbsent(radio.getName(), __ -> new CompositeKeyList<>()));
			}
		}
		
		if(fr.iiztp.mlib.utils.Utils.needsUpdate(plugin, plugin.getResourceId()))
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[AdvancedNBS] An update is available !");
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[AdvancedNBS] https://www.spigotmc.org/resources/advancednbs.81195/");
		}
		
		plugin.setCheckModeTask(Utils.checkModes());
		sender.sendMessage(ChatColor.GREEN + "AdvancedNBS reload complete !");
		return true;
	}
}
