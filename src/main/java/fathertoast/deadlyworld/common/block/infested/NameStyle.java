package fathertoast.deadlyworld.common.block.infested;

public enum NameStyle {
    VANILLA( "vanilla" ),
    SUSPICIOUS( "sus" ),
    IDENTITY( "identity" );
    
    private final String code;
    
    NameStyle( String code ) { this.code = code; }
    
    public String getCode() { return code; }
}