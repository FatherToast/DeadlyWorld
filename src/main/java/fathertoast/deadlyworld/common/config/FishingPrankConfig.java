package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.IFishingPrank;
import fathertoast.deadlyworld.common.config.field.WeightedEntityList;
import fathertoast.deadlyworld.common.config.field.WeightedEntityListField;
import fathertoast.deadlyworld.common.config.field.WeightedRegEntryList;
import fathertoast.deadlyworld.common.config.field.WeightedRegEntryListField;
import fathertoast.deadlyworld.common.core.registry.DWFishingPranks;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

public class FishingPrankConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final SingleTnt SINGLE_TNT;
    public final SingleTnt YEET_TNT;
    public final Mob MOB;
    public final Snag SNAG;
    
    /** Builds the config spec that should be used for this config. */
    FishingPrankConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for fishing pranks."
        );
        
        GENERAL = new General( this );
        
        SPEC.newLine( 2 );
        SPEC.titledComment( ChatFormatting.AQUA + "Fishing Prank Properties",
                "Below you can configure the properties of the fishing pranks added by Deadly World.",
                "Other mods registering their own pranks are responsible for creating their own configs." );
        
        SINGLE_TNT = new SingleTnt( this, DWFishingPranks.SINGLE_TNT, 80 );
        YEET_TNT = new SingleTnt( this, DWFishingPranks.YEET_TNT, 40 );
        MOB = new Mob( this, DWFishingPranks.MOB );
        SNAG = new Snag( this, DWFishingPranks.SNAG );
    }
    
    
    public static class General extends AbstractConfigCategory<FishingPrankConfig> {
        
        public final DoubleField prankChance;
        
        public final WeightedRegEntryListField<IFishingPrank> prankList;
        
        
        General( FishingPrankConfig parent ) {
            super( parent, "general",
                    "General settings related to traps and pranks that may trigger when reeling " +
                            "in your catch." );
            
            prankChance = SPEC.define( new DoubleField( "prank_chance", 0.1, DoubleField.Range.PERCENT,
                    "The chance for a \"prank\" to trigger when the player is fishing and right-clicks " +
                            "to reel in their catch.",
                    "Setting this to 0.0 effectively disables fishing pranks." ) );
            
            SPEC.newLine();
            
            prankList = SPEC.define( new WeightedRegEntryListField<>( "prank_list", makeDefaultPrankList(),
                    "Weighted list of fishing pranks to pick from when pranking a player that is fishing.",
                    "Note that some pranks have custom checks to see if they can be executed under the current " +
                            "circumstances. If the prank cannot be executed, nothing happens." ) );
        }
        
        private WeightedRegEntryList<IFishingPrank> makeDefaultPrankList() {
            return new WeightedRegEntryList<>( DWRegistries.FISHING_PRANKS_REGISTRY,
                    new RegistryValueEntry<>( DWFishingPranks.SINGLE_TNT.getId(), 50 ),
                    new RegistryValueEntry<>( DWFishingPranks.YEET_TNT.getId(), 50 ),
                    new RegistryValueEntry<>( DWFishingPranks.MOB.getId(), 200 ),
                    new RegistryValueEntry<>( DWFishingPranks.SNAG.getId(), 100 )
            );
        }
    }
    
    
    private static class FishingPrankCategory extends AbstractConfigCategory<FishingPrankConfig> {
        
        public FishingPrankCategory( FishingPrankConfig parent, RegistryObject<IFishingPrank> regObj ) {
            super( parent, regObj.getId().getPath(),
                    "Properties for the '" + regObj.getId() + "' fishing prank." );
        }
    }
    
    public static class SingleTnt extends FishingPrankCategory {
        
        public final IntField fuse;
        
        SingleTnt( FishingPrankConfig parent, RegistryObject<IFishingPrank> regObj, int fuseTime ) {
            super( parent, regObj );
            
            fuse = SPEC.define( new IntField( "fuse", fuseTime, IntField.Range.POSITIVE,
                    "The tick duration of the fuse for TNT spawned by this prank." ) );
        }
    }
    
    public static class Mob extends FishingPrankCategory {
        
        public final WeightedEntityListField mobList;
        
        Mob( FishingPrankConfig parent, RegistryObject<IFishingPrank> regObj ) {
            super( parent, regObj );
            
            mobList = SPEC.define( new WeightedEntityListField( "mob_list", makeDefaultMobs(),
                    "A weighted list of the different mobs that this prank can spawn." ) );
        }
        
        private WeightedEntityList makeDefaultMobs() {
            return new WeightedEntityList(
                    new EntityEntry( EntityType.CREEPER, 100 ),
                    new EntityEntry( EntityType.DROWNED, 300 ),
                    new EntityEntry( EntityType.SKELETON, 100 ),
                    new EntityEntry( EntityType.PUFFERFISH, 100 ),
                    new EntityEntry( EntityType.PIG, 5 ),
                    new EntityEntry( EntityType.GUARDIAN, 50 ),
                    new EntityEntry( EntityType.ELDER_GUARDIAN, 1 ),
                    new EntityEntry( EntityType.MINECART, 40 )
            );
        }
    }
    
    public static class Snag extends FishingPrankCategory {
        
        public final DoubleField speed;
        
        Snag( FishingPrankConfig parent, RegistryObject<IFishingPrank> regObj ) {
            super( parent, regObj );
            
            speed = SPEC.define( new DoubleField( "speed", 2.0, DoubleField.Range.ANY,
                    "How fast the player is pulled toward the fish hook when this prank triggers." ) );
        }
    }
}