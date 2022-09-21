package fathertoast.deadlyworld.common.tile.floortrap;

import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import fathertoast.deadlyworld.common.util.DWDamageSources;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class StormDrainTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int MAX_TIME_SUCTION_BOX_UPDATE = 50;
    private static final int MAX_DAMAGE_TIMER = 20;

    private int timeSuctionBoxUpdate;
    private int damageTimer = MAX_DAMAGE_TIMER;

    @Nullable
    private AxisAlignedBB suctionBox = null;

    public StormDrainTileEntity() {
        super(DWTileEntities.STORM_DRAIN.get());
    }


    @Override
    public void tick() {
        World world = this.level;

        if (world == null)
            return;

        // Update the suction box bounds
        if (--timeSuctionBoxUpdate <= 0) {
            this.updateSuctionBox();
            this.timeSuctionBoxUpdate = MAX_TIME_SUCTION_BOX_UPDATE;
        }

        if (this.suctionBox != null) {
            List<LivingEntity> nearbyEntities = world.getLoadedEntitiesOfClass(LivingEntity.class, this.suctionBox);

            if (!nearbyEntities.isEmpty()) {
                if (this.damageTimer > 0) {
                    --this.damageTimer;
                }
                else this.damageTimer = 20;

                int count = 0;

                for (LivingEntity livingEntity : nearbyEntities) {
                    // Try not to work with too many entities
                    if (count > 6)
                        break;

                    if (livingEntity.isInWater()) {
                        boolean isPlayer = livingEntity instanceof PlayerEntity;
                        this.pullEntity(livingEntity, isPlayer);

                        if (livingEntity.isColliding(this.getBlockPos().above(), this.getBlockState())) {
                            this.onCollidingTop(livingEntity, isPlayer);
                        }
                    }
                    ++count;
                }
                if (this.level.isClientSide) {
                    this.whirlpoolEffects(world, world.random, this.getBlockPos());
                }
            }
        }
    }

    // TODO - Rework this completely; use a small box to suck
    //        entities to the center, while using custom
    //        bubble column blocks to suck entities down
    //        from far up heights.
    private void updateSuctionBox() {
        final int maxHeight = 8;
        BlockPos tilePos = this.getBlockPos();
        BlockPos lowerCorner = tilePos.above().south(3).east(3).immutable();
        BlockPos upperCorner = tilePos.above(2).north(3).west(3).immutable();

        if (suctionBox != null) {
            boolean canGrow = suctionBox.getYsize() < maxHeight;
            upperCorner = canGrow ? upperCorner.above((int) suctionBox.getYsize()) : upperCorner.above((int) suctionBox.getYsize() - 1);
        }

        for (BlockPos pos : BlockPos.betweenClosed(lowerCorner, upperCorner)) {
            if (!this.level.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
                this.suctionBox = null;
                return;
            }
        }
        this.suctionBox = new AxisAlignedBB(lowerCorner, upperCorner).move(0.5D, 0.0D, 0.5D);
    }

    private void pullEntity(Entity entity, boolean isPlayer) {
        if (isPlayer && ((PlayerEntity) entity).isCreative()) {
            return;
        }
        BlockPos pos = this.getBlockPos();
        double xMotion = (pos.getX() + 0.5D) - entity.getX();
        double yMotion = (pos.getY() + 1.1D) - entity.getY();
        double zMotion = (pos.getZ() + 0.5D) - entity.getZ();
        Vector3d deltaMovement = new Vector3d(xMotion, yMotion, zMotion);

        double lengthSqr = deltaMovement.lengthSqr();

        if (lengthSqr < 64.0D) {
            double scale = 1.0D - Math.sqrt(lengthSqr) / 8.0D;
            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaMovement.normalize().scale(scale * scale * 0.1D)));
            entity.fallDistance = 0.0F;
        }
    }

    private void onCollidingTop(LivingEntity livingEntity, boolean isPlayer) {
        if (this.damageTimer <= 0) {
            livingEntity.hurt(DWDamageSources.VORTEX, 1);
        }
        if (isPlayer) {
            // TODO - sewer dimension teleport stuff
        }
    }

    private void whirlpoolEffects(World world, Random random, BlockPos pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // TODO - Figure out some cool shit
        world.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, x + 0.5D, y + 2.0D, z + 0.5D, 0.0D, 0.0D, 0.0D);
        if (random.nextInt(200) == 0) {
            world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Nullable
    public AxisAlignedBB getSuctionBox() {
        return this.suctionBox;
    }
}
