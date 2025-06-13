package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.FishingPrank;
import fathertoast.deadlyworld.common.config.field.WeightedEntityList;
import fathertoast.deadlyworld.common.config.field.WeightedEntityListField;
import fathertoast.deadlyworld.common.config.field.WeightedRegEntryList;
import fathertoast.deadlyworld.common.config.field.WeightedRegEntryListField;
import fathertoast.deadlyworld.common.core.registry.DWFishingPranks;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

public class FishingPrankConfig extends AbstractConfigFile {

    public final General GENERAL;
    public final SingleTnt SINGLE_TNT;
    public final Mob MOB;

    /** Builds the config spec that should be used for this config. */
    FishingPrankConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options fishing pranks."
        );

        GENERAL = new General( this );

        SPEC.titledComment( "Fishing Prank Properties",
                "Below you can configure the properties of the fishing pranks added by Deadly World.",
                "Other mods registering their own pranks are responsible for creating their own configs." );

        SINGLE_TNT = new SingleTnt( this, DWFishingPranks.SINGLE_TNT );
        MOB = new Mob( this, DWFishingPranks.MOB );
    }


    public static class General extends AbstractConfigCategory<FishingPrankConfig> {

        public final DoubleField prankChance;

        public final WeightedRegEntryListField<FishingPrank> prankList;


        General( FishingPrankConfig parent ) {
            super( parent, "general",
                    "General settings related to traps and pranks that may trigger when reeling in your catch." );

            prankChance = SPEC.define( new DoubleField("prank_chance", 0.1, DoubleField.Range.PERCENT,
                    "The chance for a \"prank\" to trigger when the player is fishing and right-clicks to reel in their catch",
                    "Setting this to 0.0 effectively disabled fishing pranks." ) );

            SPEC.newLine();

            prankList = SPEC.define( new WeightedRegEntryListField<>( "prank_list", makeDefaultPrankList(),
                    "Weighted list of fishing pranks to pick from when pranking a player that is fishing.",
                    "Note that some pranks have custom checks to see if they can be executed under the current circumstances.",
                    "If the prank cannot be executed, nothing happens.") );

            SPEC.newLine();
        }

        private WeightedRegEntryList<FishingPrank> makeDefaultPrankList() {
            return new WeightedRegEntryList<>( DWRegistries.FISHING_PRANKS_REGISTRY,
                    new RegistryValueEntry<>( DWFishingPranks.SINGLE_TNT.getId(), 100 ),
                    new RegistryValueEntry<>( DWFishingPranks.MOB.getId(), 150 )
            );
        }
    }


    private static class FishingPrankCategory extends AbstractConfigCategory<FishingPrankConfig> {

        public FishingPrankCategory( FishingPrankConfig parent, RegistryObject<FishingPrank> regObj ) {
            super(parent, regObj.getId().getPath(),
                    "Properties for the " + regObj.getId().toString() + " fishing prank." );
        }
    }


    public static class SingleTnt extends FishingPrankCategory {

        public final IntField fuse;
        public final BooleanField yeet;


        SingleTnt( FishingPrankConfig parent, RegistryObject<FishingPrank> regObj ) {
            super( parent, regObj );

            fuse = SPEC.define( new IntField("fuse", 60, IntField.Range.POSITIVE,
                    "The tick duration of the fuse of the TNT spawned by this prank." ) );
            yeet = SPEC.define( new BooleanField( "yeet", false,
                    "If enabled, the TNT spawned from this prank will deal MASSIVE knockback instead of damaging blocks and entities. " ) );

            SPEC.newLine();
        }
    }


    public static class Mob extends FishingPrankCategory {

        public final WeightedEntityListField mobList;


        Mob( FishingPrankConfig parent, RegistryObject<FishingPrank> regObj ) {
            super( parent, regObj );

            mobList = SPEC.define( new WeightedEntityListField("mob_list", makeDefaultMobs(),
                    "A weighted list of the different mobs that this prank can spawn." ) );

            SPEC.newLine();
        }

        private WeightedEntityList makeDefaultMobs() {
            return new WeightedEntityList(
                    new EntityEntry( EntityType.CREEPER, 100 ),
                    new EntityEntry( EntityType.DROWNED, 200 ),
                    new EntityEntry( EntityType.PUFFERFISH, 100 ),
                    new EntityEntry( EntityType.PIG, 5 ),
                    new EntityEntry( EntityType.GUARDIAN, 50 ),
                    new EntityEntry( EntityType.ELDER_GUARDIAN, 1 ),
                    new EntityEntry( EntityType.MINECART, 40 )
            );
        }
    }
}
