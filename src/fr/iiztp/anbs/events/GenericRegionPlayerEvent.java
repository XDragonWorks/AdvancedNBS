package fr.iiztp.anbs.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Class of the Worldguard Events (Generic)
 * @author iiztp
 * @version 0.0.1
 */
public abstract class GenericRegionPlayerEvent extends Event
{
	private static HandlerList handlers = new HandlerList();
	
	private Player player;
	private ProtectedRegion region;
	
	protected GenericRegionPlayerEvent(Player player, ProtectedRegion region)
	{
		this.player = player;
		this.region = region;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public ProtectedRegion getRegion()
	{
		return region;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
