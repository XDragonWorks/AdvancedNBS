package fr.iiztp.anbs.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.iiztp.anbs.data.PlayerData;

public class WithoutMusicEndEvent extends Event
{
	private static HandlerList handlers = new HandlerList();
	
	private Player player;
	private PlayerData playerData;
	private Set<ProtectedRegion> regions;
	
	public WithoutMusicEndEvent(Player player, PlayerData playerData, Set<ProtectedRegion> regions)
	{
		this.player = player;
		this.playerData = playerData;
		this.regions = regions;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public PlayerData getPlayerData() {
		return playerData;
	}
	
	public Set<ProtectedRegion> getRegions() {
		return regions;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
