package com.vtromeur.jagger;

import android.content.Context;

/**
 * Created Vincent Tromeur
 */
public class Utils {

	private static int SCREEN_WIDTH = 0;

	public static void initScreenWidth(Context ctx){
		SCREEN_WIDTH = ctx.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenWidth(){
		return SCREEN_WIDTH;
	}

}
