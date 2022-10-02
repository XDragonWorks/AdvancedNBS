package fr.iiztp.anbs.main.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;

/**
 * Class for Server event handling (Listeners)
 * @author iiztp
 * @version 1.0.2
 */
public class Server implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(!AdvancedNBS.getInstance().getAudioPlayers().containsKey1(event.getPlayer()))
			AdvancedNBS.getInstance().getAudioPlayers().add(event.getPlayer(), new PlayerData(event.getPlayer()));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		PlayerData data = AdvancedNBS.getInstance().getAudioPlayers().getKey2(event.getPlayer(), () -> new PlayerData(event.getPlayer()));
		data.stopRsp();
		if(data.getMode().equals(Mode.RADIO))
			data.getRadio().getListeners().remove(event.getPlayer());
		data.getRsp().destroy();
		AdvancedNBS.getInstance().getAudioPlayers().removeKey2(data);
	}
}
