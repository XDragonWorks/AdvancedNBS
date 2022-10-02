package fr.iiztp.anbs.main.commands.arguments.gui;

import org.bukkit.command.CommandSender;

import fr.iiztp.anbs.data.PlayerData;

/**
 * Class for Mute change handling (Command)
 * @author iiztp
 * @version 1.0.3
 */
public class Mute
{
	public static boolean execute(CommandSender sender, String[] args, PlayerData data)
	{
		data.setMute(!data.isMute());
		if(data.isMute())
			data.stopRsp();
		else
			data.getRsp().setPlaying(true);
		return true;
	}
}
