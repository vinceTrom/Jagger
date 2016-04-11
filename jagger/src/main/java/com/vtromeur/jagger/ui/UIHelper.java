package com.vtromeur.jagger.ui;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.vtromeur.jagger.R;
import com.vtromeur.jagger.UICustomization;

import static com.vtromeur.jagger.UICustomization.UNDEFINED_COLOR;

/**
 * Created by Vince on 30/03/16.
 */
public class UIHelper {

    public static void applyMessageViewCustomization(View pMessV, UICustomization pCustomization, boolean pIsMessageReceived){
        Resources res = pMessV.getResources();

        NinePatchDrawable bubble = (NinePatchDrawable) res.getDrawable(pIsMessageReceived ? R.drawable.chat_bubble_left : R.drawable.chat_bubble_right);
        int backColor = getMessageBackgroundColor(res, pCustomization, pIsMessageReceived);
        if(Build.VERSION.SDK_INT >= 21) {
            bubble.setTint(backColor);
        }else{
            bubble.setColorFilter(backColor, PorterDuff.Mode.MULTIPLY);
        }
        pMessV.setBackground(bubble);

        ((TextView)pMessV.findViewById(R.id.message)).setTextColor(getMessageTextColor(res, pCustomization, pIsMessageReceived));
    }

    private static int getMessageBackgroundColor(Resources res, UICustomization pCustomization, boolean pIsMessageReceived){
        int bubbleColor;
        if(pIsMessageReceived){
            if(pCustomization.mChatterMessageBackgroundColor != UNDEFINED_COLOR){
                bubbleColor = pCustomization.mChatterMessageBackgroundColor;
            }else if(pCustomization.mChatterMessageBackgroundColorResource != UNDEFINED_COLOR){
                bubbleColor = res.getColor(pCustomization.mChatterMessageBackgroundColorResource);
            }else{
                bubbleColor = res.getColor(UICustomization.CHATTER_MESSAGE_DEFAULT_BACKGROUND_COLOR_RES);
            }
        }else{
            if(pCustomization.mUserMessageBackgroundColor != UNDEFINED_COLOR){
                bubbleColor = pCustomization.mUserMessageBackgroundColor;
            }else if(pCustomization.mUserMessageBackgroundColorResource != UNDEFINED_COLOR){
                bubbleColor = res.getColor(pCustomization.mUserMessageBackgroundColorResource);
            }else{
                bubbleColor = res.getColor(UICustomization.USER_MESSAGE_DEFAULT_BACKGROUND_COLOR_RES);
            }
        }
        return bubbleColor;
    }

    private static int getMessageTextColor(Resources res, UICustomization pCustomization, boolean pIsMessageReceived){
        int textColor;
        if(pIsMessageReceived){
            if(pCustomization.mChatterMessageTextColor != UNDEFINED_COLOR){
                textColor = pCustomization.mChatterMessageTextColor;
            }else if(pCustomization.mChatterMessageTextColorResource != UNDEFINED_COLOR){
                textColor = res.getColor(pCustomization.mChatterMessageTextColorResource);
            }else{
                textColor = res.getColor(UICustomization.CHATTER_MESSAGE_DEFAULT_TEXT_COLOR_RES);
            }
        }else{
            if(pCustomization.mUserMessageTextColor != UNDEFINED_COLOR){
                textColor = pCustomization.mUserMessageTextColor;
            }else if(pCustomization.mUserMessageTextColorResource != UNDEFINED_COLOR){
                textColor = res.getColor(pCustomization.mUserMessageTextColorResource);
            }else{
                textColor = res.getColor(UICustomization.USER_MESSAGE_DEFAULT_TEXT_COLOR_RES);
            }
        }
        return textColor;
    }

    public static int getBackgroundColor(Resources res, UICustomization pCustomization){
        if(pCustomization.mBackgroundColor != UNDEFINED_COLOR){
            return  pCustomization.mBackgroundColor;
        }else if(pCustomization.mBackgroundColorResource != UNDEFINED_COLOR){
            return  res.getColor(pCustomization.mBackgroundColorResource);
        }else{
            return res.getColor(UICustomization.DEFAULT_BACKGROUND_COLOR_RES);
        }
    }
}
