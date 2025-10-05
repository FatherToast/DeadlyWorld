package fathertoast.deadlyworld.common.item;

import fathertoast.deadlyworld.client.renderer.block.bewlr.BEWLRHolders;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MiniChestBlockItem extends BlockItem {

    public MiniChestBlockItem( Block block ) {
        super( block, new Item.Properties() );
    }

    @Override
    public void initializeClient( Consumer<IClientItemExtensions> consumer ) {
        consumer.accept( new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return BEWLRHolders.MINI_CHEST.getInstance();
            }
        });
    }
}
