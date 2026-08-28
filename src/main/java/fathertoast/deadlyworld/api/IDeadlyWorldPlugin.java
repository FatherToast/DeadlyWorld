package fathertoast.deadlyworld.api;

import net.minecraft.resources.ResourceLocation;

/**
 * This interface can be implemented into a class to make it a valid Deadly World mod plugin.
 * <br><br>
 * <strong>Note: your plugin class must also be annotated with @{@link DeadlyWorldPlugin}!</strong>
 */
public interface IDeadlyWorldPlugin {
    
    /** Called by Deadly World after {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}. */
    void onLoad( IDeadlyWorldApi apiInstance );
    
    /** @return A ResourceLocation representing the ID of this plugin. */
    ResourceLocation getId();
}
