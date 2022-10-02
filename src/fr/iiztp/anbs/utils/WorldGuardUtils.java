package fr.iiztp.anbs.utils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import fr.iiztp.anbs.events.RegionPlayerEnterEvent;
import fr.iiztp.anbs.events.RegionPlayerLeaveEvent;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.mlib.YamlReader;

/**
 * Class for handling useful functions for WG handling
 * @author iiztp
 * @version 1.0.1
 */
public class WorldGuardUtils
{
	//TODO fix the thing about regions when they concurrent each other
	private static WorldGuard WG = null;
	private static Map<Player, Set<ProtectedRegion>> playerLocation = new HashMap<>();
	
	public static BukkitTask loadWorldGuard()
	{
		YamlReader reader = AdvancedNBS.getInstance().getReader();
		
		WG = WorldGuard.getInstance();
		if(reader.getBoolean("worldguard.use_region_folders"))
		{
			File worlds = new File(AdvancedNBS.getInstance().getDataFolder() + "/worlds/");
			worlds.mkdir();
			
			for(World world : Bukkit.getWorlds())
			{
				File worldFile = new File(worlds + "/" + world.getName() + "/");
				worldFile.mkdir();
				for(String keyOfTheRegion : WG.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegions().keySet())
					new File(worldFile + "/" + keyOfTheRegion + "/").mkdir();
			}
		}
		else
			new File(AdvancedNBS.getInstance().getDataFolder() + "/playlists/").mkdir();
		
		return new BukkitRunnable()
		{
			public void run()
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					Set<ProtectedRegion> oldRegions = playerLocation.computeIfAbsent(player, __ -> new HashSet<>());
					RegionContainer container = WG.getPlatform().getRegionContainer();
					RegionManager manager = container.get(BukkitAdapter.adapt(player.getWorld()));
					Set<ProtectedRegion> newRegions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation())).getRegions();
					
					playerLocation.put(player, newRegions);
					
					
					for(ProtectedRegion region : oldRegions)
					{
						if(!newRegions.contains(region))
						{
							Bukkit.getPluginManager().callEvent((Event)new RegionPlayerLeaveEvent(player, region));
						}
					}
					
					for(ProtectedRegion region : newRegions)
					{
						if(!oldRegions.contains(region))
						{
							Bukkit.getPluginManager().callEvent((Event)new RegionPlayerEnterEvent(player, region));
						}
					}
				}
			}
		}.runTaskTimer(AdvancedNBS.getInstance(), 20, 20);
	}
	
	public static Map<Player, Set<ProtectedRegion>> getPlayerLocation() {
		return playerLocation;
	}
}
