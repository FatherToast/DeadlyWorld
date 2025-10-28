package fathertoast.deadlyworld.common.block.pitfall;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.util.TrapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings( "deprecation" )
public class PitfallTrapBlock extends Block {

    private static final VoxelShape SHAPE = Block.box( 1, 0, 1, 15, 1, 15 );
    private static final AABB TOUCH_SHAPE = Block.box( 0, 0, 0, 16, 2, 16 ).toAabbs().get( 0 );

    private final PitfallTrapType type;

    public PitfallTrapBlock( PitfallTrapType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.STONE_PRESSURE_PLATE ) ) );
        this.type = type;
    }

    public PitfallTrapType getPitfallTrapType() {
        return type;
    }

    @Override
    public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape( BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos ) {
        if ( !canSurvive( state, level, pos ) )
            return Blocks.AIR.defaultBlockState();

        return super.updateShape( state, direction, neighborState, level, pos, neighborPos );
    }

    @Override
    public boolean canSurvive( BlockState state, LevelReader level, BlockPos pos ) {
        BlockPos belowPos = pos.below();
        return level.getBlockState( belowPos ).isFaceSturdy( level, belowPos, Direction.UP );
    }

    @Override
    public void entityInside( BlockState state, Level level, BlockPos pos, Entity entity ) {
        if ( level instanceof ServerLevel serverLevel ) {

            if (getEntitiesInBox(level, TOUCH_SHAPE.move(pos), Player.class) > 0) {
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS);
                makeCoverFall( level, pos );
                TrapHelper.spawnPoofCloud( serverLevel, pos );
            }
        }
    }

    /** Scans the floor in a circle around the trap and turns the blocks into falling block entities. */
    private void makeCoverFall( Level level, BlockPos pos ) {
        final int radius = type.getConfig( level ).radius.get();
        BlockPos.MutableBlockPos scanPos = pos.below().mutable();
        final int centerX = scanPos.getX();
        final int centerZ = scanPos.getZ();
        final int radSqr = radius * radius;

        for ( int x = centerX - radius; x <= centerX + radius; x++ ) {
            for ( int z = centerZ - radius; z <= centerZ + radius; z++ ) {
                int dx = x - centerX;
                int dz = z - centerZ;

                if ( dx * dx + dz * dz <= radSqr ) {
                    scanPos.set( x, scanPos.getY(), z );

                    BlockState state = level.getBlockState( scanPos );

                    if ( !state.is( BlockTags.FEATURES_CANNOT_REPLACE ) ) {
                        FallingBlockEntity.fall( level, scanPos, state );
                    }
                }
            }
        }
    }

    /**
     * @return The count of entities within the given AABB that are not stepping carefully
     *         or don't ignore block triggers. Entity class-hierarchy sensitive.
     */
    protected static int getEntitiesInBox( Level level, AABB boundingBox, Class<? extends Entity> entityClass ) {
        return level.getEntitiesOfClass(
                entityClass,
                boundingBox,
                EntitySelector.NO_SPECTATORS.and( (entity) -> !entity.isIgnoringBlockTriggers() && !entity.isSteppingCarefully() )
        ).size();
    }
}
