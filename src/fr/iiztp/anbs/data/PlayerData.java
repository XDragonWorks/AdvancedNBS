package fr.iiztp.anbs.data;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.Layer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.main.AdvancedNBS;
import fr.iiztp.anbs.utils.Mode;
import fr.iiztp.mlib.YamlReader;
import fr.iiztp.mlib.utils.MySQL;
import fr.iiztp.mlib.utils.Query;
import net.md_5.bungee.api.ChatColor;

/**
 * Class representing the player data of the plugin
 * @author iiztp
 * @version 1.0.3
 */
public class PlayerData
{
	private Player player;
	private RadioSongPlayer rsp = new RadioSongPlayer(new Song(0.F, new HashMap<Integer, Layer>(), (short)0, (short)0, "", "", "", "", new File(""), 0, false));
	private Mode mode = Mode.REGION;
	private boolean mute = false;
	private int volume = 100;
	private Radio radio = null;
	private ProtectedRegion loadedRegion = null;
	private int secondsWithoutMusic = -1;
	private FileConfiguration fc = null;
	private boolean isInNoMusic = false;
	
	public PlayerData(Player player)
	{
		this.player = player;
		fc = YamlConfiguration.loadConfiguration( new File(AdvancedNBS.getInstance().getDataFolder(), "/playerdata/" + player.getUniqueId().toString() + ".json"));
		YamlReader reader = AdvancedNBS.getInstance().getReader();
		int volume = reader.getInt("defaultVolume");
		this.volume = (volume > 100 || volume < 0) ? 100 : volume;
		if(!reader.getBoolean("database.enable"))
		{
			if(fc.contains("volume"))
				this.volume = fc.getInt("volume");
			if(fc.contains("mute"))
				this.mute = fc.getBoolean("mute");
		}
		else
		{
			MySQL db = AdvancedNBS.getInstance().getDatabase();
			ResultSet set = db.performGetQuery(new Query("SELECT volume,mute FROM ADVANCEDNBS WHERE player_uid = ?", player.getUniqueId().toString()));
			try {
				if(set.next())
				{
					this.volume = set.getInt("volume");
					this.mute = set.getBoolean("mute");
				}
			} catch (SQLException e) {
				db.performQuery(new Query("INSERT INTO ADVANCEDNBS (player_uid, volume, mute) VALUES (?, " + volume + ", " + mute + ")", player.getUniqueId().toString()));
			}
		}
	}
	
	public RadioSongPlayer getRsp() {
		return rsp;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public Radio getRadio() {
		return radio;
	}
	
	public int getSecondsWithoutMusic() {
		return secondsWithoutMusic;
	}

	public boolean isMute() {
		return mute;
	}
	
	public ProtectedRegion getLoadedRegion() {
		return loadedRegion;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public void setVolume(int volume)
	{
		this.volume = volume;
		updateData();
	}
	
	public void setRadio(Radio radio) {
		this.radio = radio;
	}
	
	public void setSecondsWithoutMusic(int secondsWithoutMusic) {
		this.secondsWithoutMusic = secondsWithoutMusic;
	}
	
	public void setMute(boolean mute) {
		this.mute = mute;
		updateData();
	}
	
	public void setLoadedRegion(ProtectedRegion loadedRegion) {
		this.loadedRegion = loadedRegion;
	}
	
	public void reloadRsp(Playlist list)
	{
		if(mute || (secondsWithoutMusic >= 0 && !mode.equals(Mode.COMBAT) || mode.equals(Mode.RADIO)))
			return;
		secondsWithoutMusic = -1;
		rsp.destroy();
		rsp = new RadioSongPlayer(list);
		rsp.addPlayer(player);
		rsp.setAutoDestroy(false);
		rsp.setRandom(AdvancedNBS.getInstance().getReader().getBoolean("isRandom"));
		rsp.setRepeatMode(RepeatMode.ALL);
		rsp.setVolume((byte)volume);
		rsp.setPlaying(true);
	}
	
	public void reloadRsp(Radio radio)
	{
		this.radio = radio;
		secondsWithoutMusic = -1;
		mute = false;
		mode = Mode.RADIO;
		RadioSongPlayer radioPlayer = radio.getRsp();
		rsp.destroy();
		rsp = new RadioSongPlayer(radioPlayer.getPlaylist());
		rsp.addPlayer(player);
		rsp.playSong(radioPlayer.getPlayedSongIndex());
		rsp.setTick(radioPlayer.getTick());
		rsp.setAutoDestroy(false);
		rsp.setRepeatMode(RepeatMode.ALL);
		rsp.setVolume((byte)volume);
		rsp.setPlaying(true);
	}
	
	public void reloadRsp(List<Song> songs)
	{
		if(songs.isEmpty())
			return;
		reloadRsp(new Playlist(songs.toArray(new Song[songs.size()])));
	}
	
	public void stopRsp()
	{
		if(mode.equals(Mode.RADIO))
			radio.asyncRsp(player);
		rsp.setPlaying(false);
	}
	
	public String toCompletedString(String n)
	{
		if(this.rsp != null)
		{
			Song song = rsp.getSong();
			n = n.replace("%a", song.getAuthor())
			.replace("%t", song.getTitle())
			.replace("%d", song.getDescription())
			.replace("%fn", song.getPath().getName().replace(".nbs", "").replace("_", " "));
		}
		
		if(radio != null)
		{
			n = n.replace("%rl", radio.getListeners().size()+"")
					.replace("%r", radio.getName());
		}
		
		return ChatColor.translateAlternateColorCodes('&', n.replace("%v", String.valueOf(rsp.getVolume())));
	}
	
	public void updateData()
	{
		AdvancedNBS anbs = AdvancedNBS.getInstance();
		YamlReader config = AdvancedNBS.getInstance().getReader();
		if(!config.getBoolean("database.enable"))
		{
			File file = new File(anbs.getDataFolder(), "/playerdata/" + player.getUniqueId().toString() + ".json");
			try
			{
				if(!file.exists())
					file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			fc.set("volume", this.volume);
			fc.set("mute", this.mute);
			try {
				fc.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			String uid = this.player.getUniqueId().toString();
			MySQL db = AdvancedNBS.getInstance().getDatabase();
			db.performQuery(new Query("INSERT INTO ADVANCEDNBS (player_uid, volume, mute) VALUES (?, " + volume + ", " + mute + ") "
					+ "ON DUPLICATE KEY UPDATE volume = VALUES(volume), mute = VALUES(mute)", uid));
		}
	}

	public boolean isInNoMusic() {
		return isInNoMusic;
	}

	public void setInNoMusic(boolean isInNoMusic) {
		this.isInNoMusic = isInNoMusic;
	}
}
