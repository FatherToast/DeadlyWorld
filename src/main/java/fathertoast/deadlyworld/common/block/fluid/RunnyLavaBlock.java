package fathertoast.deadlyworld.common.block.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

public class RunnyLavaBlock extends LiquidBlock {
    
    public RunnyLavaBlock( Supplier<? extends FlowingFluid> fluidSupplier ) {
        super( fluidSupplier, BlockBehaviour.Properties.of()
                .mapColor( MapColor.FIRE )
                .replaceable()
                .noCollission()
                .randomTicks()
                .strength( 100.0F )
                .lightLevel( ( state ) -> 15 )
                .pushReaction( PushReaction.DESTROY )
                .noLootTable()
                .liquid()
                .sound( SoundType.EMPTY )
        );
    }
}
