package fr.iiztp.anbs.main.listeners;

import org.bukkit.entity.Player;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import fr.iiztp.anbs.data.PlayerData;
import fr.iiztp.anbs.data.Radio;
import fr.iiztp.anbs.main.AdvancedNBS;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * Class for PlaceHolder event handling (Listeners)
 * @author iiztp
 * @version 1.0.1
 */
public class SomeExpansion extends PlaceholderExpansion
{
    private AdvancedNBS plugin;

    public SomeExpansion(AdvancedNBS plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "anbs";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null)
            return "";

        PlayerData data = plugin.getAudioPlayers().getKey2(player, () -> new PlayerData(player));
        
        if(data == null)
        	return null;
        
        if (identifier.equals("player_song_volume"))
            return data.getVolume() + "";

          RadioSongPlayer rsp = data.getRsp();

          if (identifier.equals("player_is_song_playing"))
              return rsp.isPlaying() ? "playing" : "stopped";

          if (identifier.equals("player_song_current_ticks"))
              return rsp.getTick() + ""; 
          
          if(identifier.equals("player_mute"))
        	  return data.isMute() ? "muted" : "unmuted";

          Song song = rsp.getSong();

          if (identifier.equals("player_song_max_ticks"))
              return song.getLength() + ""; 

          if (identifier.equals("player_song_author"))
              return song.getAuthor(); 
       
          if (identifier.equals("player_song_title"))
              return song.getTitle();

          if (identifier.equals("player_song_description"))
              return song.getDescription(); 

          if (identifier.equals("player_song_filename"))
              return song.getPath().getName().replace(".nbs", "").replace("", " "); 

          if (identifier.startsWith("player_radio_"))
          {
            String id = identifier.split("player_radio_")[0];
            if(data.getRadio() != null)
            {
                Radio radio = data.getRadio();
                if (id.equals("name"))
                    return radio.getName(); 
                if (id.equals("listeners"))
                    return radio.nbListeners() + ""; 
            }
            return "";
          }
          return null;
    }
}