package fr.iiztp.anbs.main.commands.arguments.radio;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.data.Radio;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.mlib.YamlReader;
import net.md_5.bungee.api.ChatColor;

/**
 * Class for Radio leave handling (Command)
 * @author iiztp
 * @version 1.0.1
 */
public class Leave {
	public static boolean execute(Player player, String[] args, PlayerData data)
	{
		YamlReader langReader = AdvancedNBS.getInstance().getLang();
		boolean active = langReader.getBoolean("active");
		
		if(!data.getMode().equals(Mode.RADIO))
		{
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.radio.noRadio")));
		}
		else
		{
			if(active)
				player.sendMessage(data.toCompletedString(langReader.getString("player.radio.leave")));
			Radio radio = data.getRadio();
			Set<Sign> signs = new HashSet<>(radio.getAssociatedSigns().getKeys2());
			radio.asyncRsp(player);
			if(!signs.isEmpty())
			{
				YamlReader reader = AdvancedNBS.getInstance().getReader();
				for(org.bukkit.block.Sign s : signs)
				{
					for(int i = 0; i < 4; i++)
					{
						s.setLine(i, ChatColor.translateAlternateColorCodes('&', reader.getStringList("signPattern.radio.join").get(i)
								.replace("%rl", radio.nbListeners()+"")
								.replace("%r", radio.getName())));
						s.update(true);
					}
				}
			}
		}
		
		return true;
	}
}
