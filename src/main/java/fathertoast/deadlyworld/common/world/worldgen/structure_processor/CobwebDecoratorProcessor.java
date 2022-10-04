package fathertoast.deadlyworld.common.world.worldgen.structure_processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nullable;
import java.util.Random;

public class CobwebDecoratorProcessor extends StructureProcessor {

    public static final Codec<CobwebDecoratorProcessor> CODEC = Codec.FLOAT.fieldOf("webbiness")
            .xmap(CobwebDecoratorProcessor::new, (processor) -> processor.webbiness).codec();

    private final float webbiness;


    public CobwebDecoratorProcessor(float webbiness) {
        this.webbiness = webbiness;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return DWStructureProcessors.COOL_PROCESSOR;
    }

    @Override
    @Nullable
    public Template.BlockInfo process(IWorldReader world, BlockPos structurePos, BlockPos pos, Template.BlockInfo blockInfo, Template.BlockInfo blockToProcess, PlacementSettings placementSettings, @Nullable Template template) {
        Random random = placementSettings.getRandom(blockToProcess.pos);
        BlockState state = blockToProcess.state;
        BlockPos blockPos = blockToProcess.pos;

        if (state.isAir(world, blockToProcess.pos)) {
            BlockPos randomNeighborPos = blockToProcess.pos.relative(Direction.getRandom(random));
            BlockState randomNeighborState = world.getBlockState(randomNeighborPos);

            if (!randomNeighborState.isAir(world, randomNeighborPos) && !randomNeighborState.getCollisionShape(world, randomNeighborPos).isEmpty()) {
                if (webbiness < random.nextFloat()) {
                    return new Template.BlockInfo(blockPos, Blocks.COBWEB.defaultBlockState(), blockToProcess.nbt);
                }
            }
        }
        return blockToProcess;
    }
}
