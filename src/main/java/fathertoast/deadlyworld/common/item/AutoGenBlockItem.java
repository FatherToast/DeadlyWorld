package fathertoast.deadlyworld.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

/** Block item used for auto-gen blocks. */
public class AutoGenBlockItem extends BlockItem {

    private final String tooltipKey;

    public AutoGenBlockItem( String blockKey, Block block, Properties properties ) {
        super( block, properties );
        tooltipKey = translationKey( blockKey );
    }

    @Override
    public void appendHoverText( ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag ) {
        // Add a translatable component to show the auto-gen block type
        components.add( Component.translatable( tooltipKey ).withStyle( ChatFormatting.GRAY ) );
    }

    public static String translationKey( String blockKey ) {
        return "block_auto_gen.block_key." + blockKey +".tooltip";
    }
}
