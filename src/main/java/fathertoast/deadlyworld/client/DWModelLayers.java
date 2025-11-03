package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class DWModelLayers {
    
    
    public static final ModelLayerLocation CHEST_MIMIC = create( "chest_mimic" );
    public static final ModelLayerLocation MINI_CHEST_MIMIC = create( "mini_chest_mimic" );
    public static final ModelLayerLocation JUKEBOX_MIMIC = create( "jukebox_mimic" );
    public static final ModelLayerLocation SPAWNER_MIMIC = create( "spawner_mimic" );
    public static final ModelLayerLocation MINI_SPAWNER_MIMIC = create( "mini_spawner_mimic" );
    public static final ModelLayerLocation MINI_CHEST = create( "mini_chest" );
    
    public static final ModelLayerLocation DEADLY_TRAP_OVERLAY = create( "deadly_trap", "overlay" );
    
    
    private static ModelLayerLocation create( String path ) {
        return create( path, "main" );
    }
    
    private static ModelLayerLocation create( String path, String layerName ) {
        return new ModelLayerLocation( DeadlyWorld.rl( path ), layerName );
    }
}