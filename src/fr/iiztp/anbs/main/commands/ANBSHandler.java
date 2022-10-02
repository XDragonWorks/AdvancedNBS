package fr.iiztp.anbs.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.main.commands.arguments.Reload;
import fr.iiztp.anbs.main.commands.arguments.gui.GUIVolumeChange;
import fr.iiztp.anbs.main.commands.arguments.gui.Mute;
import fr.iiztp.anbs.main.commands.arguments.radio.Join;
import fr.iiztp.anbs.main.commands.arguments.radio.Leave;
import fr.iiztp.anbs.main.commands.arguments.song.Play;
import fr.iiztp.anbs.main.commands.arguments.song.Stop;
import fr.iiztp.anbs.main.commands.arguments.song.Volume;
import fr.iiztp.mlib.YamlReader;
import fr.iiztp.mlib.datastructure.CompositeKeyList;
import net.md_5.bungee.api.ChatColor;

/**
 * Class for command handling
 * @author iiztp
 * @version 1.0.3
 */
public class ANBSHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		CompositeKeyList<Player, PlayerData> audioPlayers = AdvancedNBS.getInstance().getAudioPlayers();
		if(args == null || args.length < 1)
			return false;
		
		if(args[0].toLowerCase().equals("reload") && sender.hasPermission("anbs.reload"))
			return Reload.execute(sender, command, label, args);
		
		if(args.length < 2)
			return false;
		
		if(args[0].toLowerCase().equals("gui"))
		{
			Player player;
			if(args[1].toLowerCase().equals("volume") && sender.hasPermission("anbs.gui.volume"))
			{
				player = Bukkit.getPlayer(args[4]);
				return GUIVolumeChange.execute(sender, args, audioPlayers.getKey2(player, () -> new PlayerData(player)));
			}
			if(args[1].toLowerCase().equals("mute") && sender.hasPermission("anbs.gui.mute"))
			{
				player = Bukkit.getPlayer(args[2]);
				return Mute.execute(sender, args, audioPlayers.getKey2(player, () -> new PlayerData(player)));
			}
			if(args[1].toLowerCase().equals("join") && sender.hasPermission("anbs.gui.join"))
			{
				player = Bukkit.getPlayer(args[3]);
				return Join.execute(Bukkit.getPlayer(args[3]), args, audioPlayers.getKey2(player, () -> new PlayerData(player)));
			}
			if(args[1].toLowerCase().equals("leave") && sender.hasPermission("anbs.gui.leave"))
			{
				player = Bukkit.getPlayer(args[2]);
				return Leave.execute(Bukkit.getPlayer(args[2]), args, audioPlayers.getKey2(player, () -> new PlayerData(player)));	
			}
		}
		
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			PlayerData data = audioPlayers.getKey2(player, () -> new PlayerData(player));
			if(args[0].toLowerCase().equals("radio"))
			{
				if(args.length >= 3 && args[1].toLowerCase().equals("join"))
					if(sender.hasPermission("anbs.radio.join") || sender.hasPermission("anbs.radio.join." + args[2]))
						return Join.execute(player, args, data);
				
				if(args[1].toLowerCase().equals("leave") && sender.hasPermission("anbs.radio.leave"))
					return Leave.execute(player, args, data);
			}
			
			if(args[0].toLowerCase().equals("song"))
			{
				if(data.getRsp().getPlaylist().getSongList().isEmpty())
				{
					YamlReader langReader = AdvancedNBS.getInstance().getLang();
					if(langReader.getBoolean("active"))
						player.sendMessage(data.toCompletedString(langReader.getString("player.music.noPlaylist")));
					return true;
				}
				
				if(args[1].toLowerCase().equals("play") && sender.hasPermission("anbs.song.play"))
					return Play.execute(player, args, data);
				
				if(args[1].toLowerCase().equals("stop") && sender.hasPermission("anbs.song.stop"))
					return Stop.execute(player, args, data);
				
				if(args[1].toLowerCase().equals("volume") && sender.hasPermission("anbs.song.volume"))
					return Volume.execute(player, args, data);
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "You must be a player to control the music !");
		}
		
		return false;
	}

}
