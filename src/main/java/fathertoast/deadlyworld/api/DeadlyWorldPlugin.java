package fathertoast.deadlyworld.api;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for classes that should be treated as plugins
 * for Deadly World. To make a plugin, simply decorate your plugin class with this
 * annotation. Deadly World will then attempt to find it at runtime, during
 * the {@link FMLCommonSetupEvent}.<br>
 * <br>
 * <strong>Note: your plugin class must also implement {@link fathertoast.deadlyworld.api.IDeadlyWorldPlugin}</strong>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface DeadlyWorldPlugin {
}
