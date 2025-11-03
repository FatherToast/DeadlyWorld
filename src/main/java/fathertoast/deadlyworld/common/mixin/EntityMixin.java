package fathertoast.deadlyworld.common.mixin;

import fathertoast.deadlyworld.common.util.mixin_hooks.CommonMixinHooks;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Entity.class )
public abstract class EntityMixin extends CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, IForgeEntity {
    
    @Shadow
    protected Object2DoubleMap<FluidType> forgeFluidTypeHeight;
    @Shadow
    protected boolean firstTick;
    
    public EntityMixin( EntityType<?> entityType, Level level ) {
        super( Entity.class );
    }
    
    protected EntityMixin( Class<Entity> baseClass ) {
        super( baseClass );
    }
    
    protected EntityMixin( Class<Entity> baseClass, boolean isLazy ) {
        super( baseClass, isLazy );
    }
    
    /**
     * Yes, in order for our custom lava fluid to "count" as lava in many cases (without sacrificing the custom type),
     * we have to do this mixin. Very strange that fluid tag checks aren't being used instead.
     */
    @Inject(
            method = "isInLava",
            at = @At( "HEAD" ),
            cancellable = true
    )
    public void onIsInLava( CallbackInfoReturnable<Boolean> cir ) {
        CommonMixinHooks.onIsInLava( firstTick, forgeFluidTypeHeight, cir );
    }
}
