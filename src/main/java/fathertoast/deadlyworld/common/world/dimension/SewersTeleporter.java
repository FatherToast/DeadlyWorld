package fathertoast.deadlyworld.common.world.dimension;

import fathertoast.deadlyworld.common.core.registry.DWStructures;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.test.StructureHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.server.command.ForgeCommand;

import javax.annotation.Nullable;
import java.util.function.Function;

public class SewersTeleporter implements ITeleporter {

    public SewersTeleporter() {

    }

    @Nullable
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        if (entity.level.dimension().equals(DWDimensions.SEWERS_WORLD)) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                BlockPos pos = player.getRespawnPosition();

                if (pos == null)
                    pos = destWorld.getSharedSpawnPos();

                return new PortalInfo(new Vector3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D), Vector3d.ZERO, 90.0F, 0.0F);
            }
            BlockPos pos = destWorld.getSharedSpawnPos();
            return new PortalInfo(new Vector3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D), Vector3d.ZERO, entity.yRot, entity.xRot);
        }
        else if (entity.level.dimension().equals(World.OVERWORLD)) {
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                BlockPos playerPos = player.blockPosition();

                //attemptChunkLoading(destWorld, playerPos, 4);


                return new PortalInfo(new Vector3d(playerPos.getX(), playerPos.getY(), playerPos.getZ()), player.getDeltaMovement(), 90.0F, 0.0F);

                //return new PortalInfo(new Vector3d(centerPos.getX() + 0.5D, centerPos.getY(), centerPos.getZ() + 0.5D), player.getDeltaMovement(), 90.0F, 0.0F);
            }
            // Don't bother teleporting non-players
            else {
                return null;
            }
        }
        else return null;
    }

    /*
    private static boolean attemptChunkLoading(ServerWorld world, BlockPos center, int chunkRange) {
        chunkRange *= 16;

        if (!world.hasChunksAt(center.offset(-chunkRange, -chunkRange, -chunkRange), center.offset(chunkRange, chunkRange, chunkRange))) {
            world.getChunk()
        }
    }

     */


    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }

    @Override
    public boolean playTeleportSound(ServerPlayerEntity player, ServerWorld sourceWorld, ServerWorld destWorld) {
        return false;
    }

    @Override
    public boolean isVanilla() {
        return false;
    }
}
