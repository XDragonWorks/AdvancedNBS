package fr.iiztp.anbs.main.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.mlib.YamlReader;

/**
 * Class for song event handling (Listeners)
 * @author iiztp
 * @version 1.0.1
 */
public class NoteBlockAPI implements Listener
{
	@EventHandler
	public void onNextSongStart(SongNextEvent event)
	{
		if(event.getSongPlayer().getPlayerUUIDs().isEmpty())
			return;
		AdvancedNBS plugin = AdvancedNBS.getInstance();
		YamlReader langReader = plugin.getLang();
		YamlReader reader = plugin.getReader();
		if(langReader.getBoolean("active"))
		{
			Player player = Bukkit.getPlayer(event.getSongPlayer().getPlayerUUIDs().iterator().next());
			if(player == null)
				return;
			PlayerData data = plugin.getAudioPlayers().getKey2(player, () -> new PlayerData(player));
			if(data != null)
			{
				data.getRsp().setPlaying(false);
				data.setSecondsWithoutMusic(reader.getInt("mode.withoutMusic.afterSong"));
				new BukkitRunnable()
				{
					public void run()
					{
						data.getRsp().setPlaying(true);
					}
				}.runTaskLater(AdvancedNBS.getInstance(), reader.getInt("mode.withoutMusic.afterSong")*20);
				player.sendMessage(data.toCompletedString(langReader.getString("player.music.next")));	
			}
		}
	}
}
