package fr.iiztp.anbs.main.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.model.Song;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.events.WithoutMusicEndEvent;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.mlib.YamlReader;

public class MyListener implements Listener
{
	@EventHandler
	public void onWithoutMusicEnd(WithoutMusicEndEvent event)
	{
		AdvancedNBS plugin = AdvancedNBS.getInstance();
		YamlReader reader = plugin.getReader();
		if(reader.getBoolean("debug"))
			plugin.sendDebug("WithoutMusicEndEvent triggered ...");
		Set<ProtectedRegion> regions = event.getRegions();
		PlayerData data = event.getPlayerData();
		if(data.getMode() != Mode.REGION)
			return;
		File playlistFile = null;
		List<Song> tmpSongs = null;
		List<Song> songs = null;

		ProtectedRegion selectedRegion = null;
		
		for(ProtectedRegion region : regions)
		{
			if(selectedRegion == null || selectedRegion.getPriority() <= region.getPriority())
			{
				selectedRegion = region;
				String regionId = selectedRegion.getId();
				String worldName = event.getPlayer().getWorld().getName();
				if(reader.getBoolean("worldguard.use_region_folders"))
					playlistFile = new File(AdvancedNBS.getInstance().getDataFolder() + "/worlds/" + worldName + "/" + regionId);
				else
					for(String playlist : reader.getSections("worldguard.playlists"))
						if(reader.getStringList("worldguard.playlists." + playlist).contains(worldName + "." + regionId))
							playlistFile = new File(AdvancedNBS.getInstance().getDataFolder() + "/playlists/" + playlist);
				if(playlistFile != null)
				{
					tmpSongs = Utils.getPlaylistFromFolder(playlistFile);
					if(tmpSongs == null || tmpSongs.isEmpty())
						selectedRegion = null;
					else
						songs = new ArrayList<>(tmpSongs);
				}
			}
		}
		
		if(songs != null && !songs.isEmpty())
		{
			data.setLoadedRegion(selectedRegion);
			data.reloadRsp(songs);
		}
	}
}
