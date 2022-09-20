package fathertoast.deadlyworld.common.util;

import com.google.common.collect.ImmutableMap;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public final class NBTHelper {

    @Nullable
    public static BlockState readBlockState(CompoundNBT compoundNBT) {
        if (!compoundNBT.contains("Name", Constants.NBT.TAG_STRING)) {
            return null;
        }
        else {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compoundNBT.getString("Name")));

            if (block == null)
                return null;

            BlockState blockstate = block.defaultBlockState();

            if (compoundNBT.contains("Properties", Constants.NBT.TAG_COMPOUND)) {
                CompoundNBT propertiesTag = compoundNBT.getCompound("Properties");
                StateContainer<Block, BlockState> stateContainer = block.getStateDefinition();

                for(String propertyId : propertiesTag.getAllKeys()) {
                    Property<?> property = stateContainer.getProperty(propertyId);

                    if (property != null) {
                        blockstate = setValueHelper(blockstate, property, propertyId, propertiesTag, compoundNBT);
                    }
                }
            }
            return blockstate;
        }
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper
            (S blockState, Property<T> property, String propertyId, CompoundNBT propertiesTag, CompoundNBT compoundNBT) {

        Optional<T> optional = property.getValue(propertiesTag.getString(propertyId));

        if (optional.isPresent()) {
            return blockState.setValue(property, optional.get());
        }
        else {
            DeadlyWorld.LOG.warn("Unable to read property: {} with value: {} for blockstate: {}", propertyId, propertiesTag.getString(propertyId), compoundNBT.toString());
            return blockState;
        }
    }

    public static CompoundNBT writeBlockState(BlockState blockState) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putString("Name", ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).toString());
        ImmutableMap<Property<?>, Comparable<?>> properties = blockState.getValues();

        if (!properties.isEmpty()) {
            CompoundNBT propertiesTag = new CompoundNBT();

            for(Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
                Property<?> property = entry.getKey();
                propertiesTag.putString(property.getName(), getName(property, entry.getValue()));
            }
            compoundNBT.put("Properties", propertiesTag);
        }
        return compoundNBT;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getName(Property<T> property, Comparable<?> comparable) {
        return property.getName((T)comparable);
    }

    // Utility class, not instantiable
    private NBTHelper() {}
}
