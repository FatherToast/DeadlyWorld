package fathertoast.deadlyworld.common.tile.spawner;

import com.google.common.collect.ImmutableMap;
import fathertoast.deadlyworld.common.block.MiniSpawnerBlock;
import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.OnClient;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Map;

public class MiniSpawnerTileEntity extends DeadlySpawnerTileEntity {

    private static final Map<Direction, Vector3d> effectOffsets = new ImmutableMap.Builder<Direction, Vector3d>()
            .put(Direction.UP, new Vector3d(0.0D, 0.15D, 0.0D))
            .put(Direction.DOWN, new Vector3d(0.0D, 0.60, 0.0D))
            .put(Direction.NORTH, new Vector3d(0.0D, 0.4D, 0.25D))
            .put(Direction.WEST, new Vector3d(0.25D, 0.4D, 0.0D))
            .put(Direction.EAST, new Vector3d(-0.25D, 0.4D, 0.0D))
            .put(Direction.SOUTH, new Vector3d(0.0D, 0.4D, -0.25D))
            .build();

    private Direction facing = Direction.NORTH;


    public MiniSpawnerTileEntity() {
        super(DWTileEntities.MINI_SPAWNER.get());
    }

    // Initializing the tile entity here
    // when it is safe to do so.
    @Override
    public void onLoad() {
        super.onLoad();

        BlockState state = this.getBlockState();

        if (state.getBlock() instanceof MiniSpawnerBlock) {
            this.facing = this.getBlockState().getValue(BlockStateProperties.FACING);
        }
    }

    @Override
    protected void effectTick() {
        if( activated ) {
            final World world = level;
            final double xPos = worldPosition.getX() + world.random.nextDouble();
            final double yPos = worldPosition.getY() + world.random.nextDouble();
            final double zPos = worldPosition.getZ() + world.random.nextDouble();
            world.addParticle( ParticleTypes.SMOKE, xPos, yPos, zPos, 0.0, 0.0, 0.0 );
            world.addParticle( ParticleTypes.FLAME, xPos, yPos, zPos, 0.0, 0.0, 0.0 );

            if( spawnDelay > 0 ) {
                spawnDelay--;
            }
            prevMobRotation = mobRotation;
            mobRotation = (mobRotation + 1000.0F / (spawnDelay + 200.0F)) % 360.0;
        }
    }

    @OnClient
    public Direction getFacing() {
        return this.facing;
    }

    @OnClient
    public Vector3d getEntityRenderOffset() {
        return effectOffsets.get(getFacing());
    }
}