package fr.iiztp.anbs.main.commands.arguments.song;

import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.mlib.YamlReader;

/**
 * Class for Song play handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class Play
{
	public static boolean execute(Player player, String[] args, PlayerData data)
	{
		RadioSongPlayer rsp = data.getRsp();
		YamlReader langReader = AdvancedNBS.getInstance().getLang();
		boolean active = langReader.getBoolean("active");
		
		if(!rsp.isPlaying())
		{
			if(!data.getMode().equals(Mode.RADIO))
			{
				rsp.setPlaying(true);
				if(active)
					player.sendMessage(data.toCompletedString(langReader.getString("player.music.play")));
			}
			else
			{
				data.getRadio().syncRsp(player);
				if(active)
					player.sendMessage(data.toCompletedString(langReader.getString("player.music.resume")));
			}
		}
		else
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.music.played")));
		return true;
	}
}
