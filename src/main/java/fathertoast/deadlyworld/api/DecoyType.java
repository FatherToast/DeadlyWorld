package fathertoast.deadlyworld.api;

import java.util.function.Supplier;

/**
 * Represents a decoy type used by floor traps.
 * A decoy type can be registered in Deadly World's {@link DWRegistries#DECOY_TYPE_REGISTRY decoy registry} and then
 * mapped to a {@link fathertoast.deadlyworld.api.client.IDecoyRenderer decoy renderer} by calling
 * {@link fathertoast.deadlyworld.api.client.IClientRegisterHelper#registerDecoyRenderer(DecoyType, Supplier)}.
 */
public final class DecoyType { }
