package com.vtromeur.jagger.di;

/**
 * Created by Vince on 08/04/16.
 */
public class DaggerSingleton {

    private static DaggerSingleton sDaggerSingleton;

    private MyDaggerComponent myDaggerComponent;

    private DaggerSingleton(){
        myDaggerComponent = DaggerMyDaggerComponent.builder().build();
    }

    public static DaggerSingleton getInstance(){
        if(sDaggerSingleton == null){
            sDaggerSingleton = new DaggerSingleton();
        }
        return sDaggerSingleton;
    }

    public MyDaggerComponent daggerComponent(){
        return myDaggerComponent;
    }
}
