package toast.deadlyWorld;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;

public abstract class AttributeHelper {
    /// Applies a modifier with the given properties.
    public static void modify(EntityLivingBase entity, IAttribute attribute, String name, double modifier, int operation) {
        entity.getEntityAttribute(attribute).applyModifier(new AttributeModifier(name, modifier, operation));
    }

    /// Shifts the base value.
    public static void shift(EntityLivingBase entity, IAttribute attribute, String name, double modifier) {
        AttributeHelper.modify(entity, attribute, name, modifier, 0);
    }

    /// Multiplier (from 0) on the shifted base value.
    public static void baseMult(EntityLivingBase entity, IAttribute attribute, String name, double modifier) {
        AttributeHelper.modify(entity, attribute, name, modifier, 1);
    }

    /// Multiplier (from 0) applied multiplicatively.
    public static void mult(EntityLivingBase entity, IAttribute attribute, String name, double modifier) {
        AttributeHelper.modify(entity, attribute, name, modifier, 2);
    }

    /// Applies a shift modifier to force the base to equal the given value. (Not counting other modifiers.) Generally, min or max should be called instead.
    public static void set(EntityLivingBase entity, IAttribute attribute, String name, double base) {
        AttributeHelper.shift(entity, attribute, name, base - entity.getEntityAttribute(attribute).getBaseValue());
    }

    /// Applies a shift modifier to decrease the base to at most the given value. (Not counting other modifiers.)
    public static void min(EntityLivingBase entity, IAttribute attribute, String name, double base) {
        if (base < entity.getEntityAttribute(attribute).getBaseValue()) {
            AttributeHelper.set(entity, attribute, name, base);
        }
    }

    /// Applies a shift modifier to increase the base to at least the given value. (Not counting other modifiers.)
    public static void max(EntityLivingBase entity, IAttribute attribute, String name, double base) {
        if (base > entity.getEntityAttribute(attribute).getBaseValue()) {
            AttributeHelper.set(entity, attribute, name, base);
        }
    }
}