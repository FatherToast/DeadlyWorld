package fathertoast.deadlyworld.common.block.spike_trap;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpikeTrapBlock extends Block {

    public static BooleanProperty ACTIVE = BooleanProperty.create( "active" );

    private static final VoxelShape SHAPE = Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D );

    private final SpikeTrapType type;


    public SpikeTrapBlock( SpikeTrapType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.SPAWNER ) ) );
        this.type = type;
        registerDefaultState( stateDefinition.any().setValue( ACTIVE, false ) );
    }

    public SpikeTrapType getSpikeTrapType() {
        return type;
    }

    @Override
    @SuppressWarnings( "Hello, I would like to suppress this deprecation warning please" )
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings( "deprecation" )
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        if ( !entity.isSteppingCarefully() && entity instanceof LivingEntity livingEntity ) {
            float damage = type.getConfig( level ).damage.getFloat();
            livingEntity.hurt( DWDamageTypes.of( level, DWDamageTypes.SPIKE_TRAP ), damage );
        }
    }

    @Override
    public void stepOn( Level level, BlockPos pos, BlockState state, Entity entity ) {
        if ( !state.getValue( ACTIVE ) && !entity.isSteppingCarefully() && entity instanceof LivingEntity livingEntity ) {
            float damage = type.getConfig( level ).damage.getFloat();
            livingEntity.hurt( DWDamageTypes.of( level, DWDamageTypes.SPIKE_TRAP ), damage );
        }
        super.stepOn( level, pos, state, entity );
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        super.createBlockStateDefinition( builder.add( ACTIVE ) );
    }
}
