package fr.iiztp.anbs.main.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.mlib.YamlReader;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

/**
 * Class for entity event handling (Listeners)
 * @author iiztp
 * @version 1.0.1
 */
public class Entity implements Listener
{
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		AdvancedNBS plugin = AdvancedNBS.getInstance();
		YamlReader reader = plugin.getReader();
		if(reader.getBoolean("debug"))
			plugin.sendDebug("EntityDamageByEntityEvent triggered ...");
		Player player;
		List<Song> songs;
		org.bukkit.entity.Entity entity = null;
		
		if(!(event.getEntity() instanceof Player))
		{
			if(!(event.getDamager() instanceof Player))
				return;
			player = (Player)event.getDamager();
			entity = (org.bukkit.entity.Entity)event.getEntity();
		}
		else
		{
			player = (Player)event.getEntity();
			entity = (org.bukkit.entity.Entity)event.getDamager();
		}
		
		if(!reader.getBoolean("mode.combat.triggers.beinghit"))
			if(event.getEntity().equals(player))
				return;
		
		if(!reader.getBoolean("mode.combat.triggers.hitanentity"))
			if(event.getDamager().equals(player))
				return;
		
		PlayerData data = plugin.getAudioPlayers().getKey2(player, () -> new PlayerData(player));
		Mode mode = data.getMode();
		
		if(mode.hasPriorityOver(Mode.COMBAT) || mode.equals(Mode.COMBAT))
			return;
		
		data.setMode(Mode.COMBAT);
		data.setLoadedRegion(null);
		
		if(!reader.getBoolean("mode.combat.useEntities"))
		{
			songs = new ArrayList<>();
			if(reader.contains("mode.combat.sounds"))
				for(String element : reader.getStringList("mode.combat.sounds"))
				{
					songs.add(NBSDecoder.parse(new File(plugin.getDataFolder() + "/combat/" + element)));
					if(reader.getBoolean("debug"))
						plugin.sendDebug(player.getName() + " loading fight " + element + "...");
				}
		}
		else
		{
			String nameOfEntity = null;
			if(Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
			{
				MythicMobs mm = MythicMobs.inst();
				MobManager mobManager = mm.getMobManager();
				if(mobManager.getAllMythicEntities().contains(entity))
				{
					MythicMob mythicEntity = mobManager.getMythicMobInstance(entity).getType();
					if(reader.getStringList("mode.combat.entities").contains(mythicEntity.getInternalName()))
						nameOfEntity = mythicEntity.getInternalName();
				}
			}
			if(nameOfEntity == null && reader.getStringList("mode.combat.entities").contains(entity.getType().name()))
				nameOfEntity = entity.getType().name();
			
			if(reader.getBoolean("debug"))
				plugin.sendDebug(player.getName() + " hit " + nameOfEntity + "...");
			
			songs = Utils.getPlaylistFromFolder(new File(plugin.getDataFolder() + "/combat/" + nameOfEntity + "/"));
		}
		data.reloadRsp(songs);
		if(data.getSecondsWithoutMusic() < 0)
			data.setSecondsWithoutMusic(reader.getInt("mode.withoutMusic.afterCombat"));
	}
}
