package fathertoast.deadlyworld.common.block.infested;

import fathertoast.deadlyworld.common.core.DeadlyWorld;

public enum NameStyle {
    VANILLA( "vanilla" ),
    SUSPICIOUS( "sus" ),
    IDENTITY( "identity" );
    
    private static final String LANG_KEY = "block." + DeadlyWorld.MOD_ID + ".infested_block.";
    
    private final String code;
    
    NameStyle( String code ) { this.code = code; }
    
    /**
     * @return The key for the .lang file entry for this name style. It is expected to point to
     * a translation with one argument, where the host block's translation will be inserted.
     */
    public String getLangKey() { return LANG_KEY + code; }
}