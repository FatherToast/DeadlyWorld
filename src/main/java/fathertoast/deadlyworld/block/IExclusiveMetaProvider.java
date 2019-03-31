package fathertoast.deadlyworld.block;

import net.minecraft.util.IStringSerializable;

public
interface IExclusiveMetaProvider extends IStringSerializable
{
	int getMetadata( );
}
