package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.common.block.infested.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Contains client-only helper methods for auto-generating infested blocks at runtime.
 */
public class InfestedBlockClientHelper {
    /** The logger used for infested block auto-generation. */
    public static final Logger LOG = LogManager.getLogger( DeadlyWorld.MOD_ID + "/infested_client" );
    
    public static void injectModels( Map<ResourceLocation, BakedModel> models ) {
        if( DWBlocks.getInfestedBlocks().isEmpty() ) return;
        
        LOG.warn( "Ignore all 'Exception loading blockstate definition' and 'Unable to load model' warnings above for Deadly World infested blocks!" );
        for( RegistryObject<DeadlyInfestedBlock> regObj : DWBlocks.getInfestedBlocks() ) {
            DeadlyInfestedBlock infestedBlock = regObj.get();
            // Copy block state models
            for( BlockState state : infestedBlock.getStateDefinition().getPossibleStates() ) {
                mirrorModel( models, BlockModelShaper.stateToModelLocation( state ),
                        BlockModelShaper.stateToModelLocation( infestedBlock.toHost( state ) ) );
            }
            // Copy inventory model
            mirrorModel( models, new ModelResourceLocation( regObj.getId(), "inventory" ),
                    new ModelResourceLocation( infestedBlock.getHostBlockLocation(), "inventory" ) );
        }
    }
    
    private static void mirrorModel( Map<ResourceLocation, BakedModel> models, ResourceLocation infestedKey, ResourceLocation hostKey ) {
        if( models.containsKey( infestedKey ) ) {
            BakedModel hostModel = models.get( hostKey );
            if( hostModel != null ) {
                models.put( infestedKey, hostModel );
            }
            else {
                LOG.warn( "Failed to populate model for '{}': Could not find model for host block '{}'",
                        infestedKey, hostKey );
            }
        }
    }
}