package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DeviceBlueprintItem extends Item {

    public DeviceBlueprintItem() {
        super(new Item.Properties()
                .tab(ItemGroup.TAB_MISC)
                .rarity(Rarity.RARE)
                .stacksTo(1)
                .setNoRepair()
        );
    }

    public enum Type {
        SPAWNER(DWBlocks::spawnerBlocks),
        FLOOR_TRAP(DWBlocks::floorTrapBlocks),
        TOWER_DISPENSER(DWBlocks::towerDispenserBlocks);

        Type(Supplier<Block[]> deviceBlocks) {
            devices = deviceBlocks;
        }
        Supplier<Block[]> devices;


        @Nullable
        public static Block getRandomDevice(Random random) {
            Type type = values()[random.nextInt(values().length)];
            Block[] blocks = type.devices.get();

            return blocks[random.nextInt(blocks.length)];
        }
    }

    @Override
    public void fillItemCategory(ItemGroup itemGroup, NonNullList<ItemStack> list) {
        if (getItemCategory() == null) return;

        for (Type type : Type.values()) {
            for (Block block : type.devices.get()) {
                list.add(blueprint(block));
            }
        }
    }

    public static ItemStack randomBlueprint(Random random) {
        Block block = Type.getRandomDevice(random);

        if (block == null)
            throw new IllegalStateException("Attempted to create random DeadlyWorld device blueprint from a device type with no associated blocks.");

        return blueprint(block);
    }

    public static ItemStack randomBlueprint(Type type, Random random) {
        Block[] blocks = type.devices.get();

        if (blocks.length <= 0)
            throw new IllegalStateException("Attempted to create DeadlyWorld device blueprint from a device type with no associated blocks. Type: " + type);

        return blueprint(blocks[random.nextInt(blocks.length)]);
    }

    private static ItemStack blueprint(Block block) {
        ItemStack stack = new ItemStack(DWItems.DEVICE_BLUEPRINT.get());
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT blueprintData = new CompoundNBT();

        blueprintData.putString("DeviceName", block.getRegistryName().toString());
        tag.put("BlueprintData", blueprintData);
        stack.setTag(tag);
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> textComponents, ITooltipFlag flag) {
        CompoundNBT tag = itemStack.getTag();

        if (tag == null || !tag.contains("BlueprintData", tag.getId()))
            return;

        CompoundNBT blueprintData = tag.getCompound("BlueprintData");

        if (blueprintData.contains("DeviceName", Constants.NBT.TAG_STRING)) {
            Block block = null;
            ResourceLocation rl = ResourceLocation.tryParse(blueprintData.getString("DeviceName"));

            if (rl != null) {
                if (ForgeRegistries.BLOCKS.containsKey(rl))
                    block = ForgeRegistries.BLOCKS.getValue(rl);
            }
            if (block != null) {
                textComponents.add(new StringTextComponent(""));
                textComponents.add(new TranslationTextComponent(References.Language.BLUEPRINT_DEVICE_NAME, block.getName().getString()));
                return;
            }
        }
        textComponents.add(new StringTextComponent(""));
        textComponents.add(new TranslationTextComponent(References.Language.BLUEPRINT_NO_INS));
    }
}
