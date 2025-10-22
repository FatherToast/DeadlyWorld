package fathertoast.deadlyworld.common.block.misc;

import fathertoast.deadlyworld.common.util.DWDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SpikeTrapBlock extends Block {

    

    public static BooleanProperty ACTIVE = BooleanProperty.create( "active" );

    public SpikeTrapBlock( Properties properties ) {
        super( properties );
        registerDefaultState( stateDefinition.any().setValue( ACTIVE, false ) );
    }

    @Override
    public void stepOn( Level level, BlockPos pos, BlockState state, Entity entity ) {
        if ( !state.getValue( ACTIVE ) && !entity.isSteppingCarefully() && entity instanceof LivingEntity livingEntity ) {
            livingEntity.hurt( DWDamageSources.of( level, DWDamageSources.SPIKE_TRAP ), 4.0F );
        }
        super.stepOn( level, pos, state, entity );
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( ACTIVE ) );
    }
}
