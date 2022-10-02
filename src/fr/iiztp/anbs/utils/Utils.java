package fr.iiztp.anbs.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.events.WithoutMusicEndEvent;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.mlib.YamlReader;
import fr.iiztp.mlib.datastructure.CompositeKey;

/**
 * Class for radio and playerdata handling
 * @author iiztp
 * @version 1.0.3
 */
public class Utils
{
	public static BukkitTask checkModes()
	{
		return new BukkitRunnable()
		{
			public void run()
			{
				YamlReader reader = AdvancedNBS.getInstance().getReader();
				for(CompositeKey<Player, PlayerData> ck : AdvancedNBS.getInstance().getAudioPlayers())
				{	
					PlayerData data = ck.getKey2();
					Player player = ck.getKey1();
					
					if(data.getMode().equals(Mode.COMBAT))
					{
						String deactivation = "mode.combat.deactivation.";
						List<Entity> entities = player.getNearbyEntities(reader.getInt(deactivation + "x"), reader.getInt(deactivation + "y"), reader.getInt(deactivation + "z"));
						if(entities.isEmpty())
						{
							data.setMode(Mode.REGION);
							data.stopRsp();
							data.setSecondsWithoutMusic(reader.getInt("mode.withoutMusic.afterCombat"));
						}
					}
					
					if(reader.getBoolean("debug"))
						AdvancedNBS.getInstance().sendDebug("Player " + player.getName() + ", mode : " + data.getMode() + ", SecondsWithoutMusicLeft : " + data.getSecondsWithoutMusic() + ", loadedRegion : " + data.getLoadedRegion());
					if(data.getSecondsWithoutMusic() >= 0)
					{
						data.setSecondsWithoutMusic(data.getSecondsWithoutMusic()-1);
						Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
						if(data.getSecondsWithoutMusic() <= -1 && wg != null && wg.isEnabled())
							Bukkit.getPluginManager().callEvent((Event)new WithoutMusicEndEvent(player, data, WorldGuardUtils.getPlayerLocation().get(player)));
					}
				}
			}
		}.runTaskTimer(AdvancedNBS.getInstance(), 20, 20);
	}
	
	public static List<Song> getPlaylistFromFolder(File folder)
	{
		if(AdvancedNBS.getInstance().getReader().getBoolean("debug"))
			AdvancedNBS.getInstance().sendDebug("Load playlist from folder : " + folder.getAbsolutePath());
		
		List<Song> songs = new ArrayList<>();
		byte b;
		int i;
		File[] arrayOfFiles;
		for (i = (arrayOfFiles = folder.listFiles()).length, b = 0; b < i; b++)
		{
			File element = arrayOfFiles[b];
		    songs.add(NBSDecoder.parse(element));
		}
		Collections.shuffle(songs);
		return songs;
	}
}
