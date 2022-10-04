package fathertoast.deadlyworld.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DeviceBlueprintItem extends Item {

    public DeviceBlueprintItem() {
        super(new Item.Properties()
                .rarity(Rarity.RARE)
                .stacksTo(1)
                .setNoRepair()
        );
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // TODO - Grant deadly device recipes depending on blueprint data
        return super.use(world, player, hand);
    }
}
