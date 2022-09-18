package fathertoast.deadlyworld.common.tile.spawner;

import com.google.common.collect.ImmutableMap;
import fathertoast.deadlyworld.common.block.MiniSpawnerBlock;
import fathertoast.deadlyworld.common.registry.DWBlocks;
import fathertoast.deadlyworld.common.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.OnClient;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Map;

public class MiniSpawnerTileEntity extends DeadlySpawnerTileEntity {
    
    //TODO Maybe we can make this inherit almost everything from the base deadly spawner TE?
    //  I think the only differences are cosmetic, right? In this class, we can just offset (& shrink?) the particle effects by block state,
    //  and the smaller & offset spinning entity will be handled in the TE renderer.

    // TODO - The plan here is to store coordinate offsets for each direction the
    //        spawner is "facing" to use for particle effects and whatnot.
    private static final Map<Direction, Vector3d> EFFECT_OFFSETS = new ImmutableMap.Builder<Direction, Vector3d>()
            .put(Direction.UP, new Vector3d(0, 0.5D, 0))
            .put(Direction.DOWN, new Vector3d(0, -0.5D, 0))
            .put(Direction.NORTH, new Vector3d(0, 0, 0))
            .put(Direction.WEST, new Vector3d(0, 0, 0))
            .put(Direction.EAST, new Vector3d(0, 0, 0))
            .put(Direction.SOUTH, new Vector3d(0, 0, 0))
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
}