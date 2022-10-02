package fr.iiztp.anbs.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.anbs.utils.Utils;
import fr.iiztp.mlib.YamlReader;
import fr.iiztp.mlib.datastructure.CompositeKeyList;

/**
 * Class representing the radios of the plugin
 * @author iiztp
 * @version 1.0.1
 */
public class Radio
{
	private String name;
	private RadioSongPlayer rsp;
	private List<Player> listeners = new ArrayList<>();
	private CompositeKeyList<String, Sign> associatedSigns = new CompositeKeyList<>();
	
	  public static void loadRadios()
	  {
		  YamlReader reader = AdvancedNBS.getInstance().getReader();
		  for(String radio : reader.getStringList("radios"))
		  {
			  List<Song> songs = Utils.getPlaylistFromFolder(new File(AdvancedNBS.getInstance().getDataFolder(), "/radios/" + radio));
			  
			  if(!songs.isEmpty())
			  {
				  Playlist playlist = new Playlist(songs.toArray(new Song[songs.size()]));
				  AdvancedNBS.getInstance().getRadios().add(new Radio(radio, playlist));
			  }
		  }
	}
	  
	private Radio(String name, Playlist list)
	{
		this.name = name;
		this.rsp = new RadioSongPlayer(list);
		this.rsp.setRepeatMode(RepeatMode.ALL);
		this.rsp.setPlaying(true);
		this.rsp.setAutoDestroy(false);
	}
	
	public String getName() {
		return name;
	}
	
	public void setAssociatedSigns(CompositeKeyList<String, Sign> signs)
	{
		associatedSigns = signs;
	}
	
	public void addSign(String key, Sign sign)
	{
		associatedSigns.add(key, sign);;
	}
	
	public void removeSign(String key)
	{
		associatedSigns.removeKey1(key);;
	}
	
	public void removeSign(Sign sign)
	{
		associatedSigns.removeKey2(sign);
	}
	
	public boolean containsSign(Sign sign)
	{
		return associatedSigns.containsKey2(sign);
	}
	
	public CompositeKeyList<String, Sign> getAssociatedSigns() {
		return associatedSigns;
	}
	
	public List<Player> getListeners() {
		return listeners;
	}
	
	public RadioSongPlayer getRsp() {
		return rsp;
	}
	
	public int nbListeners()
	{
		return listeners.size();
	}
	
	public void syncRsp(Player player)
	{
		CompositeKeyList<Player, PlayerData> data = AdvancedNBS.getInstance().getAudioPlayers();
		PlayerData pd = data.getKey2(player, () -> new PlayerData(player));
		pd.reloadRsp(this);
		listeners.add(player);
		pd.setLoadedRegion(null);
	}
	
	public void asyncRsp(Player player)
	{
		CompositeKeyList<Player, PlayerData> data = AdvancedNBS.getInstance().getAudioPlayers();
		PlayerData pd = data.getKey2(player, () -> new PlayerData(player));
		listeners.remove(player);
		pd.stopRsp();
		pd.setMode(Mode.REGION);
		pd.setRadio(null);
	}
}
