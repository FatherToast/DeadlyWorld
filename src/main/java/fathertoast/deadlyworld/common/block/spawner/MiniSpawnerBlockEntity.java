package fathertoast.deadlyworld.common.block.spawner;

import com.google.common.collect.ImmutableMap;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

/**
 * Modified copy-paste of {@link DeadlySpawnerBlockEntity}.
 */
public class MiniSpawnerBlockEntity extends DeadlySpawnerBlockEntity {
    
    private static final Map<Direction, Vec3> EFFECT_OFFSETS = new ImmutableMap.Builder<Direction, Vec3>()
            .put( Direction.UP, new Vec3( 0.0, 0.15, 0.0 ) )
            .put( Direction.DOWN, new Vec3( 0.0, 0.65, 0.0 ) )
            .put( Direction.NORTH, new Vec3( 0.0, 0.40, 0.25 ) )
            .put( Direction.WEST, new Vec3( 0.25, 0.40, 0.0 ) )
            .put( Direction.EAST, new Vec3( -0.25, 0.40, 0.0 ) )
            .put( Direction.SOUTH, new Vec3( 0.0, 0.40, -0.25 ) )
            .build();
    
    private Direction facing = Direction.NORTH;
    
    public MiniSpawnerBlockEntity( BlockPos pos, BlockState state ) {
        super( DWBlockEntities.MINI_SPAWNER.get(), pos, state );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        
        BlockState state = getBlockState();
        if( state.getBlock() instanceof MiniSpawnerBlock ) {
            facing = getBlockState().getValue( BlockStateProperties.FACING );
        }
    }
    
    @Override
    public void spawnEffectParticle( ProgressiveDelaySpawner spawner, Level level, BlockPos pos ) {
        if( (level.getGameTime() & 0b11) != 0 ) return; // Only spawn every 4th tick
        
        RandomSource random = level.getRandom();
        Vec3 offset = getEntityRenderOffset();
        double x = (double) pos.getX() + 0.25 + offset.x + random.nextDouble() / 2.0;
        double y = (double) pos.getY() - 0.15 + offset.y + random.nextDouble() / 2.0;
        double z = (double) pos.getZ() + 0.25 + offset.z + random.nextDouble() / 2.0;
        level.addParticle( ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0 );
        level.addParticle( ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0 );
    }
    
    public Direction getFacing() { return facing; }
    
    @Override
    public float getEntityRenderScale() { return 0.265625F; } // Half default size
    
    @Override
    public Vec3 getEntityRenderOffset() { return EFFECT_OFFSETS.get( getFacing() ); }
}