package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.world.dimension.DWDimensions;
import fathertoast.deadlyworld.common.world.dimension.SewersTeleporter;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.ILootFunctionConsumer;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class SewerExitBlock extends Block {

    public SewerExitBlock() {
        super(AbstractBlock.Properties.of(Material.STONE)
                .strength(-1.0F, 3600000.0F)
                .noDrops()
                .sound(SoundType.STONE)
                .randomTicks()
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (world instanceof ServerWorld && !player.isPassenger() && !player.isVehicle() && player.canChangeDimensions()
                && player.isAlive() && world.dimension().equals(DWDimensions.SEWERS_WORLD)) {

            ServerWorld overworld = ((ServerWorld) world).getServer().overworld();

            player.changeDimension(overworld, new SewersTeleporter());
            return ActionResultType.SUCCESS;
        }
        else {
            return ActionResultType.PASS;
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(2) == 0) {
            if (!world.getBlockState(pos.below()).isCollisionShapeFullBlock(world, pos.below())) {
                world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + random.nextDouble(), pos.getY(), pos.getZ() + random.nextDouble(), 0.01F, 0.01F, 0.01F);
            }
        }
    }
}
