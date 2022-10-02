package fr.iiztp.anbs.main.commands.arguments.radio;

import org.bukkit.entity.Player;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.data.Radio;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.mlib.YamlReader;
import net.md_5.bungee.api.ChatColor;

/**
 * Class for Radio join handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class Join
{
	public static boolean execute(Player player, String[] args, PlayerData data)
	{
		Radio radio = null;
		for(Radio currentRadio : AdvancedNBS.getInstance().getRadios())
		{
			if(currentRadio.getName().equals(args[2]))
			{
				radio = currentRadio;
				break;
			}
		}
		
		YamlReader langReader = AdvancedNBS.getInstance().getLang();
		boolean active = langReader.getBoolean("active");
		
		if(radio == null)
		{
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.radio.notExist")));
			return true;
		}
		
		if(data.getMode().equals(Mode.RADIO))
		{
			Radio current = data.getRadio();
			if(current.getName().equals(args[2]))
			{
				if(active)
					player.sendMessage(data.toCompletedString(langReader.getString("player.radio.joined")));
				return true;
			}
			current.asyncRsp(player);
		}
		
		radio.syncRsp(player);
		PlayerData playerData = AdvancedNBS.getInstance().getAudioPlayers().getKey2(player, () -> new PlayerData(player));
		YamlReader reader = AdvancedNBS.getInstance().getReader();;
		if(playerData == null)
		{
			playerData = new PlayerData(player);
		}
		for(org.bukkit.block.Sign s : radio.getAssociatedSigns().getKeys2())
		{
			for(int i = 0; i < 4; i++)
			{
				s.setLine(i, ChatColor.translateAlternateColorCodes('&', playerData.toCompletedString(reader.getStringList("signPattern.radio.join").get(i))));
				s.update(true);
			}
		}
		if(active)
			player.sendMessage(data.toCompletedString(langReader.getString("player.radio.join")));
		return true;
	}
}
