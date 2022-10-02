package fr.iiztp.anbs.main.commands.arguments.song;

import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.mlib.YamlReader;

/**
 * Class for Song stop handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class Stop {
	public static boolean execute(Player player, String[] args, PlayerData data)
	{
		RadioSongPlayer rsp = data.getRsp();
		YamlReader langReader = AdvancedNBS.getInstance().getLang();;
		boolean active = langReader.getBoolean("active");
		
		if(rsp.isPlaying())
		{
			rsp.setPlaying(false);
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.music.stop")));
		}
		else
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.music.stopped")));
		
		return true;
	}
}
