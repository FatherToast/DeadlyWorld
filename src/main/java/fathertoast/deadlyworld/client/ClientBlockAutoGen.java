package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.BlockAutoGen;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.IAutoGenBlock;
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
 * Contains client-only helper methods for auto-generating blocks at runtime.
 * See also {@link BlockAutoGen}.
 */
public class ClientBlockAutoGen {
    /** The logger used for block auto-generation. */
    public static final Logger LOG = LogManager.getLogger( DeadlyWorld.MOD_ID + "/block_autogen_client" );
    
    /**
     * Called by the modify baking result event. Loops through all auto-generated blocks and links
     * each origin block state model to its respective auto-gen block state(s).
     */
    public static void injectModels( Map<ResourceLocation, BakedModel> models ) {
        if( DWBlocks.getAutoGenBlocks().isEmpty() ) return;
        
        LOG.warn( "Ignore all 'Exception loading blockstate definition' and 'Unable to load model' warnings above for Deadly World blocks!" );
        for( RegistryObject<? extends IAutoGenBlock> regObj : DWBlocks.getAutoGenBlocks() ) {
            IAutoGenBlock autoGenBlock = regObj.get();
            // Copy block state models
            for( BlockState state : autoGenBlock.getBlockStateDefinition().getPossibleStates() ) {
                mirrorModel( models, BlockModelShaper.stateToModelLocation( state ),
                        BlockModelShaper.stateToModelLocation( autoGenBlock.toOrigin( state ) ) );
            }
            // Copy inventory model
            mirrorModel( models, new ModelResourceLocation( regObj.getId(), "inventory" ),
                    new ModelResourceLocation( autoGenBlock.getOriginBlockLocation(), "inventory" ) );
        }
    }
    
    /** Points the auto-gen resource location to its respective origin resource location's model. */
    private static void mirrorModel( Map<ResourceLocation, BakedModel> models, ResourceLocation autoGenLoc, ResourceLocation originLoc ) {
        if( models.containsKey( autoGenLoc ) ) {
            BakedModel hostModel = models.get( originLoc );
            if( hostModel != null ) {
                models.put( autoGenLoc, hostModel );
            }
            else {
                LOG.warn( "Failed to populate model for auto-gen block state '{}': Could not find model for origin block state '{}'",
                        autoGenLoc, originLoc );
            }
        }
    }
}