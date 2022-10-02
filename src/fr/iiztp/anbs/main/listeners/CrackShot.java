package fr.iiztp.anbs.main.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import fr.iiztp.anbs.main.AdvancedNBS;

/**
 * Class for crackShot event handling (Listeners)
 * @author iiztp
 * @version 1.0.2
 */
public class CrackShot implements Listener {
	@EventHandler
	public void onWeaponDamageEntity(WeaponDamageEntityEvent event)
	{
		if(AdvancedNBS.getInstance().getReader().getBoolean("debug"))
			AdvancedNBS.getInstance().sendDebug("Crackshot Entity damaged event triggered...");
		new Entity().onEntityDamageByEntity(new EntityDamageByEntityEvent((Player)((Projectile)event.getDamager()).getShooter(), event.getVictim(), DamageCause.ENTITY_ATTACK, event.getDamage()));
	}
}
