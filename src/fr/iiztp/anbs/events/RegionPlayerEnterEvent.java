package fr.iiztp.anbs.events;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Class of the Worldguard Events (Enter)
 * @author iiztp
 * @version 0.0.1
 */
public class RegionPlayerEnterEvent extends GenericRegionPlayerEvent
{
	public RegionPlayerEnterEvent(Player player, ProtectedRegion region) {
		super(player, region);
	}
}
