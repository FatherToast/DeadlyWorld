package fathertoast.deadlyworld.common.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileEntityHelper {

    /**
     * Looks for the closest Tile Entity from the
     * BlockPos of origin.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends TileEntity> T getNearestTileEntity(Class<? extends T> teClass, World world, BlockPos origin) {
        double shortestDist = -1.0D;
        TileEntity tileEntity = null;

        for (TileEntity te : world.pendingBlockEntities) {
            double distanceSqr = blockPosDistToSqr(te.getBlockPos(), origin.getX(), origin.getY(), origin.getZ());

            if (shortestDist == -1.0D || distanceSqr < shortestDist && te.getClass().isAssignableFrom(teClass)) {
                shortestDist = distanceSqr;
                tileEntity = te;
            }
        }
        return tileEntity == null ? null : (T) tileEntity;
    }

    public static double blockPosDistToSqr(BlockPos pos, double x, double y, double z) {
        double xPos = pos.getX() - x;
        double yPos = pos.getY() - y;
        double zPos = pos.getZ() - z;
        return xPos * xPos + yPos * yPos + zPos * zPos;
    }
}
