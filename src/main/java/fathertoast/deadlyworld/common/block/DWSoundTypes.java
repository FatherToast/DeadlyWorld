package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.util.References;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.util.ForgeSoundType;

public final class DWSoundTypes {
    
    public static final ForgeSoundType MINI_WOOD = new ForgeSoundType( 1.0F, 1.0F + References.MINI_PITCH_SHIFT,
            () -> SoundEvents.WOOD_BREAK,
            () -> SoundEvents.WOOD_STEP,
            () -> SoundEvents.WOOD_PLACE,
            () -> SoundEvents.WOOD_HIT,
            () -> SoundEvents.WOOD_FALL
    );
    
    public static final ForgeSoundType MINI_METAL = new ForgeSoundType( 1.0F, 1.0F + References.MINI_PITCH_SHIFT,
            () -> SoundEvents.METAL_BREAK,
            () -> SoundEvents.METAL_STEP,
            () -> SoundEvents.METAL_PLACE,
            () -> SoundEvents.METAL_HIT,
            () -> SoundEvents.METAL_FALL
    );
    
    // Utility class
    private DWSoundTypes() { }
}
