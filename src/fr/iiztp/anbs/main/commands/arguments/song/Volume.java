package fr.iiztp.anbs.main.commands.arguments.song;

import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.mlib.YamlReader;
import net.md_5.bungee.api.ChatColor;

/**
 * Class for volume change handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class Volume
{
	public static boolean execute(Player player, String[] args, PlayerData data)
	{
		RadioSongPlayer rsp = data.getRsp();
		YamlReader langReader = AdvancedNBS.getInstance().getLang();
		boolean active = langReader.getBoolean("active");
		
		if(args.length >= 2)
		{
			int volume = 0;
			try
			{
				volume = Integer.parseInt(args[2]);
				if(volume < 0 || volume > 100)
					throw new NumberFormatException();
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				player.sendMessage(ChatColor.RED + "You must enter a valid number between 0 and 100 !");
				return true;
			}
			
			data.setVolume(volume);
			rsp.setVolume((byte)volume);
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.music.volumeChange")));
			return true;
		}
		
		if(langReader.getBoolean("active"))
			player.sendMessage(langReader.getString("player.music.volumeError"));
		return true;
	}
}
