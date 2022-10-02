package fr.iiztp.anbs.main.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.data.Radio;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.mlib.YamlReader;
import fr.iiztp.mlib.utils.DataConfigWritable;
import net.md_5.bungee.api.ChatColor;

/**
 * Class of the sign listeners
 * @author iiztp
 * @version 1.0.2
 */
public class Sign implements Listener
{
	@EventHandler
	public void onSignClick(PlayerInteractEvent event)
	{
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		AdvancedNBS anbs = AdvancedNBS.getInstance();
		if(block.getType().toString().contains("SIGN"))
		{
			YamlReader reader = anbs.getReader();
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
			if(player.hasPermission("anbs.sign.join"))
			{
				Radio radio = null;
				for(Radio r : anbs.getRadios())
				{
					if(r.getAssociatedSigns().containsKey2(sign))
					{
						radio = r;
						break;
					}
				}
				if(radio != null)
				{
					Bukkit.dispatchCommand((CommandSender)player, "anbs radio join " + radio.getName());
					return;
				}
			}
			for(int i = 0; i < 4; i++)
			{
				if(!sign.getLine(i).equals(ChatColor.translateAlternateColorCodes('&', reader.getStringList("signPattern.radio.leave").get(i))))
					break;
				if(i == 3 && player.hasPermission("anbs.sign.leave"))
				{
					Bukkit.dispatchCommand((CommandSender)player, "anbs radio leave");
				}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		AdvancedNBS plugin = AdvancedNBS.getInstance();
		PlayerData playerData = plugin.getAudioPlayers().getKey2(player, () -> new PlayerData(player));
		YamlReader langReader = plugin.getLang();
		if(!player.hasPermission("anbs.sign.create"))
		{
			if(langReader.getBoolean("active"))
				player.sendMessage(playerData.toCompletedString(langReader.getString("player.radio.notExist")));
			return;
		}
		YamlReader reader = plugin.getReader();
		YamlReader data = plugin.getData();
		String[] lines = event.getLines();
		
		if(lines[0].equalsIgnoreCase("[ANBS]"))
		{
			if(lines[1].equalsIgnoreCase("join radio"))
			{
				Radio radio = null;
				for(Radio r : plugin.getRadios())
				{
					if(r.getName().equals(lines[2]))
					{
						radio = r;
						break;
					}
				}
				if(radio != null)
				{
					for(int i = 0; i < 4; i++)
						event.setLine(i, ChatColor.translateAlternateColorCodes('&', reader.getStringList("signPattern.radio.join").get(i).replace("%rl", radio.getListeners().size() + "").replace("%r", radio.getName())));
					int index = data.sizeOfConfigSection("signs");
					String genericPath = "signs." + index + ".";
					Block block = event.getBlock();
					data.set(new DataConfigWritable(genericPath + "name", radio.getName()),
							new DataConfigWritable(genericPath + "world", block.getWorld().getName()),
							new DataConfigWritable(genericPath + "x", block.getX()+""),
							new DataConfigWritable(genericPath + "y", block.getY()+""),
							new DataConfigWritable(genericPath + "z", block.getZ()+""));
					org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
					radio.addSign(index+"", sign);
				}
				else
				{
					event.setCancelled(true);
		            if(langReader.getBoolean("active"))
		                player.sendMessage(playerData.toCompletedString(langReader.getString("player.radio.notExist")));
				}
			}
			else
				if(lines[1].equalsIgnoreCase("leave radio"))
					for(int i = 0; i < 4; i++)
						event.setLine(i, ChatColor.translateAlternateColorCodes('&', reader.getStringList("signPattern.radio.leave").get(i)));
		}
	}
	
	@EventHandler
	public void onSignDestroyed(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		YamlReader data = AdvancedNBS.getInstance().getData();
		AdvancedNBS plugin = AdvancedNBS.getInstance();
		if(block.getType().toString().contains("SIGN"))
		{
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
			for(Radio radio : plugin.getRadios())
			{
				if(radio.containsSign(sign))
				{
					String uid = radio.getAssociatedSigns().getKey1(sign);
					radio.removeSign(sign);
					data.set(new DataConfigWritable("signs." + uid));
					return;
				}
			}
		}
	}
}
