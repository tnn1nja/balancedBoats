package net.tnn1nja.balancedboats;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {

    public static HashMap<UUID, Double> height = new HashMap<UUID, Double>();

    @Override
    public void onEnable(){
        //Custom Listener
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            public void run() {
                for(Player p: Bukkit.getOnlinePlayers()){
                    if (p.isInsideVehicle()) {
                        if(p.getVehicle() instanceof Boat) {
                            //Boating
                            iceHole(p);
                        }
                    }
                }
            }

        }, 0L, 1L);

        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info("[BalancedBoats] BalancedBoats 1.0 Successfully Loaded!");
    }

    @EventHandler
    public static void onEnter(VehicleEnterEvent e){
        if((e.getVehicle() instanceof Boat) && (e.getEntered() instanceof Player)) {
            //Variables
            Player p = (Player) e.getEntered();
            Boat b = (Boat) e.getVehicle();

            //Falling and Water Fix
            b.setGravity(false);
            if(b.isOnGround()) {
                height.put(p.getUniqueId(), b.getLocation().getY() - 1);
            }else{
                height.put(p.getUniqueId(), b.getLocation().getY());

                Location tpl = b.getLocation();
                tpl.setY(tpl.getY() + 1);
                b.teleport(tpl);
            }


            //Effects
            for (PotionEffect effect : p.getActivePotionEffects())
                p.removePotionEffect(effect.getType());

            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 5));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 5));
        }
    }


    @EventHandler
    public static void onLeave(VehicleExitEvent e){
        if((e.getVehicle() instanceof Boat) && (e.getExited() instanceof Player)){
            //Variables
            Player p = (Player) e.getExited();

            //Falling Boat Fix
            height.remove(p.getUniqueId());
            e.getVehicle().setGravity(true);

            //Effects
            for (PotionEffect effect : p.getActivePotionEffects())
                p.removePotionEffect(effect.getType());
        }
    }

    //Pasting Method
    public static void iceHole(Player p){
        //Constants
        int radius = 4;

        //Air
        Location la = p.getLocation();
        la.setY(height.get(p.getUniqueId())+1);
        for (int z = 0; z < radius+1; z++) {
            la.setY(height.get(p.getUniqueId()) + z);
            for (int i = -radius; i < radius; i++) {
                la.setZ(p.getLocation().getZ() + i);
                for (int j = -radius; j < radius; j++) {
                    la.setX(p.getLocation().getX() + j);

                    //Make Circle
                    if (!((i == radius - 1 || i == -radius) && (j == -radius || j == radius - 1) &&
                            (z <= 1 || z == radius))) {
                        //Set Block
                        Block b = la.getBlock();
                        b.setType(Material.AIR);
                    }
                }
            }
        }

        //Make Ice
        Location li = p.getLocation();
        li.setY(height.get(p.getUniqueId()));

        for (int i = -radius; i < radius; i++) {
            li.setZ(p.getLocation().getZ() + i);
            for (int j = -radius; j < radius; j++) {
                li.setX(p.getLocation().getX() + j);

                //Make Circle
                if (!((i == radius - 1 || i == -radius) && (j == -radius || j == radius - 1))) {
                    //Set Block
                    Block b = li.getBlock();
                    b.setType(Material.BLUE_ICE);
                }
            }
        }

        //Make Boat Fall
        p.getVehicle().setGravity(!(p.getVehicle()).isOnGround());
    }
}
