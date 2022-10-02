package fr.iiztp.anbs.main.listeners;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.guillaumevdn.questcreator.api.QuestStartEvent;
import com.guillaumevdn.questcreator.api.QuestStopEvent;
import com.guillaumevdn.questcreator.lib.quest.AbstractQuest;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.mlib.YamlReader;

/**
 * Class for QuestCreator event handling (Listeners)
 * @author iiztp
 * @version 1.0.2
 */
public class QuestCreator implements Listener
{	
	@EventHandler
	public void onQuestStart(QuestStartEvent event)
	{
		launchMusicFromQuest(event.getQuest(), "QuestCreator.playlists.start");
	}
	
	@EventHandler
	public void onQuestComplete(QuestStopEvent event)
	{
		launchMusicFromQuest(event.getQuest(), "QuestCreator.playlists.complete");
	}
	
	public void launchMusicFromQuest(AbstractQuest quest, String fromFolder)
	{
		YamlReader reader = AdvancedNBS.getInstance().getReader();
		for(String section : reader.getSections(fromFolder))
		{
			if(reader.getStringList(fromFolder + "." + section).contains(quest.getModelId()))
			{
				for(Player player : quest.getOnlinePlayers())
				{
					PlayerData data = AdvancedNBS.getInstance().getAudioPlayers().getKey2(player, () -> new PlayerData(player));
					data.reloadRsp(Utils.getPlaylistFromFolder(new File(AdvancedNBS.getInstance().getDataFolder() + "/playlists/" + section)));
				}
				break;
			}
		}
	}
}
