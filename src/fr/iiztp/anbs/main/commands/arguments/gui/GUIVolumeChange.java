package fr.iiztp.anbs.main.commands.arguments.gui;

import org.bukkit.command.CommandSender;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.data.PlayerData;

/**
 * Class for Volume change handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class GUIVolumeChange
{
	public static boolean execute(CommandSender sender, String[] args, PlayerData data)
	{
		RadioSongPlayer rsp = data.getRsp();
		
		int pm = Integer.parseInt(args[3]);
		if(args[2].toLowerCase().equals("p"))
		{
			if(data.getVolume() + pm <= 100)
			{
				data.setVolume(data.getVolume()+pm);
				rsp.setVolume((byte)(rsp.getVolume()+pm));
			}
			return true;
		}
		
		if(args[2].toLowerCase().equals("m"))
		{
			if(data.getVolume() - pm >= 0)
			{
				data.setVolume(data.getVolume()-pm);
				rsp.setVolume((byte)(rsp.getVolume()-pm));
			}
			return true;
		}
		
		return false;
	}
}
