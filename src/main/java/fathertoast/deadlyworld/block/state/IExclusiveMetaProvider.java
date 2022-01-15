package fathertoast.deadlyworld.block.state;

import net.minecraft.util.IStringSerializable;

public
interface IExclusiveMetaProvider extends IStringSerializable
{
	int getMetadata( );
}
