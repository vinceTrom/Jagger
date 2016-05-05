package com.vtromeur.jagger.di;

import android.content.Context;

/**
 * Created by Vince on 08/04/16.
 */
public class DaggerSingleton {

    private static DaggerSingleton sDaggerSingleton;

    private MyDaggerComponent myDaggerComponent;

    private DaggerSingleton(Context pContext){
        myDaggerComponent = DaggerMyDaggerComponent.builder().daggerXMPPModule(new DaggerXMPPModule(pContext)).build();
    }

    public static void initInstance(Context pContext) {
        if (sDaggerSingleton == null) {
            sDaggerSingleton = new DaggerSingleton(pContext);
        }
    }

    public static DaggerSingleton getInstance(){
        return sDaggerSingleton;
    }

    public MyDaggerComponent daggerComponent(){
        return myDaggerComponent;
    }
}
