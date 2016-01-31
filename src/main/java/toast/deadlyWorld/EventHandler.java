package toast.deadlyWorld;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    /// Useful properties for this class.
    private static final byte ADJUST_BREAK_SPEED;
    static {
        String breakSpeed = Properties.getString(Properties.GENERAL, "modify_break_speed");
        if (breakSpeed.equalsIgnoreCase("true")) {
            ADJUST_BREAK_SPEED = 1;
        }
        else if (breakSpeed.equalsIgnoreCase("instant")) {
            ADJUST_BREAK_SPEED = 2;
        }
        else {
            ADJUST_BREAK_SPEED = 0;
        }
    }

    public EventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }

    /**
     * Called by ChunkProviderGenerate.populate().
     * EventType type = the event type.
     * World world = the world being populated.
     * Random rand = world's random number generator.
     * int chunkX, chunkZ = the chunk coordinates.
     * boolean hasVillageGenerated = true if a village has already generated in the chunk.
     * IChunkProvider chunkProvider = the world's chunk provider.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChunkPopulate(PopulateChunkEvent.Populate event) {
        if (_DeadlyWorld.generation && event.type == PopulateChunkEvent.Populate.EventType.DUNGEON && !event.world.isRemote && event.world.provider.dimensionId == 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    /**
     * Called by EntityPlayer.getCurrentPlayerStrVsBlock().
     * EntityPlayer entityPlayer = the player breaking the block.
     * Block block = the block being broken.
     * int metadata = the block's metadata.
     * float originalSpeed = the original, unmodified block break speed.
     * float newSpeed = the new modified speed to use.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.block == Blocks.monster_egg) {
            if (EventHandler.ADJUST_BREAK_SPEED == 1) {
                Block posingBlock = event.metadata == 1 ? Blocks.cobblestone : event.metadata == 2 ? Blocks.stonebrick : Blocks.stone;
                float hardness = posingBlock.getBlockHardness(event.entityPlayer.worldObj, 0, 0, 0) / event.block.getBlockHardness(event.entityPlayer.worldObj, 0, 0, 0);

                if (ForgeHooks.canHarvestBlock(posingBlock, event.entityPlayer, event.metadata)) {
                    event.newSpeed = event.entityPlayer.getBreakSpeed(posingBlock, false, 0, 0, -1, 0);
                }
                else {
                    event.newSpeed = 0.3F;
                }
                event.newSpeed = ForgeEventFactory.getBreakSpeed(event.entityPlayer, posingBlock, 0, event.newSpeed / hardness, 0, -1, 0);
            }
            else if (EventHandler.ADJUST_BREAK_SPEED == 2) {
                event.newSpeed = Float.POSITIVE_INFINITY;
            }
        }
    }

    /**
     * Called by MinecraftServer.loadAllWorlds().
     * World world = the world being loaded.
     * 
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world != null && !event.world.isRemote && event.world.provider.dimensionId == 0) {
            EventHandler.checkWorld(event.world);
        }
    }

    /// Checks the world to see if this mod should disable its generation.
    public static void checkWorld(World world) {
        boolean canDisable = Properties.getBoolean(Properties.GENERAL, "automatic_disable");
        if (world.getSaveHandler() instanceof SaveHandler) {
            if (world.getTotalWorldTime() == 0) {
                try {
                    File worldDirectory = ((SaveHandler) world.getSaveHandler()).getWorldDirectory();
                    File worldFile = new File(worldDirectory, "deadlyWorld.bin");
                    worldDirectory.mkdirs();
                    worldFile.createNewFile();
                    _DeadlyWorld.generation = true;
                    return;
                }
                catch (Exception ex) {
                    _DeadlyWorld.console("Failed to initialize generation! Disabling world generation...");
                    ex.printStackTrace();
                }
            }
            else {
                try {
                    File worldFile = new File( ((SaveHandler) world.getSaveHandler()).getWorldDirectory(), "deadlyWorld.bin");
                    _DeadlyWorld.generation = !canDisable || worldFile.exists();
                    if (!_DeadlyWorld.generation) {
                        _DeadlyWorld.console("Detected world not created with this mod! Disabling world generation...");
                    }
                    return;
                }
                catch (Exception ex) {
                    _DeadlyWorld.console("Failed generation check! Disabling world generation...");
                    ex.printStackTrace();
                }
            }
        }
        _DeadlyWorld.generation = !canDisable;
    }
}