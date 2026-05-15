package fathertoast.deadlyworld.api.client;

import fathertoast.deadlyworld.api.DecoyType;

import java.util.function.Supplier;

public interface IClientRegisterHelper {
    
    void registerDecoyRenderer( DecoyType type, Supplier<IDecoyRenderer> decoyRenderer );
}
