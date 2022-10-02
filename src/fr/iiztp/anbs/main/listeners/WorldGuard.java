package fr.iiztp.anbs.main.listeners;

import java.io.File;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.model.Song;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.events.RegionPlayerEnterEvent;
import fr.iiztp.anbs.events.RegionPlayerLeaveEvent;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.mlib.YamlReader;

/**
 * Class for WorldGuard event handling (Listeners)
 * @author iiztp
 * @version 1.0.1
 */
public class WorldGuard implements Listener
{
	@EventHandler
	public void onRegionPlayerEnter(RegionPlayerEnterEvent event)
	{
		PlayerData data = AdvancedNBS.getInstance().getAudioPlayers().getKey2(event.getPlayer(), () -> new PlayerData(event.getPlayer()));
		YamlReader reader = AdvancedNBS.getInstance().getReader();
		String worldName = event.getPlayer().getWorld().getName();
		ProtectedRegion regionToLoad = event.getRegion();
		String regionId = regionToLoad.getId();
		if(reader.getBoolean("debug"))
			AdvancedNBS.getInstance().sendDebug(event.getPlayer().getName() + " entered the region " + regionId + ", priority : " + event.getRegion().getPriority());
		
		if(data.getMode().equals(Mode.REGION) && !data.isInNoMusic())
		{
			if(reader.getStringList("worldguard.no_music").contains(event.getPlayer().getWorld().getName() + "." + regionId))
			{
				data.stopRsp();
				data.setInNoMusic(true);
				return;
			}
			if((data.getLoadedRegion() == null || regionToLoad.getPriority() >= data.getLoadedRegion().getPriority()))
			{
				File playlistFile = null;
				if(reader.getBoolean("worldguard.use_region_folders"))
					playlistFile = new File(AdvancedNBS.getInstance().getDataFolder() + "/worlds/" + worldName + "/" + regionId);
				else
					for(String playlist : reader.getSections("worldguard.playlists"))
						if(reader.getStringList("worldguard.playlists." + playlist).contains(worldName + "." + regionId))
							playlistFile = new File(AdvancedNBS.getInstance().getDataFolder() + "/playlists/" + playlist);

				if(playlistFile != null)
				{
					List<Song> songs = Utils.getPlaylistFromFolder(playlistFile);
					if(!songs.isEmpty())
					{
						data.setLoadedRegion(regionToLoad);
						data.reloadRsp(songs);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onRegionPlayerLeave(RegionPlayerLeaveEvent event)
	{
		PlayerData data = AdvancedNBS.getInstance().getAudioPlayers().getKey2(event.getPlayer(),() -> new PlayerData(event.getPlayer()));
		YamlReader reader = AdvancedNBS.getInstance().getReader();
		String regionId = event.getRegion().getId();
		if(reader.getBoolean("debug"))
			AdvancedNBS.getInstance().sendDebug(event.getPlayer().getName() + " left the region " + event.getRegion().getId() + ", priority : " + event.getRegion().getPriority());
		if(!event.getRegion().equals(data.getLoadedRegion()))
			return;
		if(reader.getStringList("worldguard.no_music").contains(event.getPlayer().getWorld().getName() + "." + regionId))
			data.setInNoMusic(false);
		if(!data.getMode().hasPriorityOver(Mode.REGION))
			data.stopRsp();
		if(data.getLoadedRegion().equals(event.getRegion()))
			data.setLoadedRegion(null);
		if(data.getSecondsWithoutMusic() < 0)
			data.setSecondsWithoutMusic(reader.getInt("mode.withoutMusic.afterRegionLeave"));
	}
}
