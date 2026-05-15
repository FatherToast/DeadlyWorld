package fathertoast.deadlyworld.client.renderer.block.bewlr;

import fathertoast.deadlyworld.client.ClientRegister;
import fathertoast.deadlyworld.common.block.entity.MiniChestBlockEntity;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This is a helper class for easily referencing and instantiating BEWLRs (Block Entity Without Level Renderer)
 * for items that require them.
 */
public class BEWLRHolders {
    
    /** A list containing all holders for lookup. */
    public static final List<Holder> HOLDERS = new ArrayList<>();
    /** Currently only exists to satisfy BEWLR constructor. */
    public static final EntityModelSet MODEL_SET = new EntityModelSet();
    
    
    //
    // HOLDERS
    //
    public static final Holder MINI_CHEST = new Holder( ( renderDispatcher, modelSet ) ->
            new SimpleBEWLR( renderDispatcher, modelSet, () -> new MiniChestBlockEntity( BlockPos.ZERO, DWBlocks.MINI_CHEST.get().defaultBlockState() ) ) );
    
    
    /**
     * This is a holder that contains a factory to later create a BEWLR instance at the appropriate time.
     */
    public static class Holder {
        
        private final BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory;
        private BlockEntityWithoutLevelRenderer instance;
        
        public Holder( BiFunction<BlockEntityRenderDispatcher, EntityModelSet, BlockEntityWithoutLevelRenderer> factory ) {
            this.factory = factory;
            HOLDERS.add( this );
        }
        
        /**
         * Creates this holder's BEWLR instance from its factory.
         * <br><br>
         * Called from {@link ClientRegister#registerBlockEntityRenderers()}
         */
        @SuppressWarnings( "JavadocReference" )
        public void populate( BlockEntityRenderDispatcher renderDispatcher ) {
            instance = factory.apply( renderDispatcher, MODEL_SET );
        }
        
        /** @return This holder's BEWLR instance, or null if it hasn't been created yet. */
        public BlockEntityWithoutLevelRenderer getInstance() {
            if( instance == null ) {
                throw new IllegalStateException( "Attempted to access a BlockEntityWithoutLevelRenderer instance that had not been created." );
            }
            return instance;
        }
    }
}
