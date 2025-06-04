package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MiniSpawnerMimic extends SpawnerMimic {

    public MiniSpawnerMimic( EntityType<? extends PathfinderMob> entityType, Level level ) {
        super( entityType, level );
        // Is smol, can't take big steps like its older sibling
        setMaxUpStep( 0.5F );
        setSpawner( new ProgressiveDelaySpawner( SpawnerType.MINI, this ) );
    }

    @Override
    public void spawnEffectParticle( ProgressiveDelaySpawner spawner, Level level, BlockPos pos ) {
        if( (level.getGameTime() & 0b11) != 0 ) return; // Only spawn every 4th tick

        RandomSource random = level.getRandom();
        double x = (double) pos.getX() + 0.25 + random.nextDouble() / 2.0;
        double y = (double) pos.getY() + 0.3 + random.nextDouble() / 2.0;
        double z = (double) pos.getZ() + 0.25 + random.nextDouble() / 2.0;
        level.addParticle( ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0 );
        level.addParticle( ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0 );
    }
}
