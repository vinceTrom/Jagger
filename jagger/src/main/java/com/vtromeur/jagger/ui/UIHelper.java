package com.vtromeur.jagger.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vtromeur.jagger.R;
import com.vtromeur.jagger.UICustomization;
import com.vtromeur.jagger.Utils;
import com.vtromeur.jagger.xmpp.XMPPMessage;

import static com.vtromeur.jagger.UICustomization.UNDEFINED_COLOR;

/**
 * Created by Vince on 30/03/16.
 */
public class UIHelper {

    public static ViewGroup buildMessageView(Context pCtx, XMPPMessage pMessage, ViewGroup pViewContainer, UICustomization pCustomization){

        boolean isMessageReceived = pMessage.isReceived();

        Resources res = pCtx.getResources();

        ViewGroup vg = (ViewGroup) LayoutInflater.from(pCtx).inflate(R.layout.chat_message, pViewContainer, false);
        TextView messV = (TextView) vg.findViewById(R.id.message);
        TextView timeV = (TextView) vg.findViewById(R.id.timeStamp);
        final View picV = vg.findViewById(R.id.pic);

        FrameLayout.LayoutParams picflp = (FrameLayout.LayoutParams) picV.getLayoutParams();
        picflp.gravity = Gravity.BOTTOM | (isMessageReceived ? Gravity.LEFT : Gravity.RIGHT);


        messV.setMaxWidth((int) (Utils.getScreenWidth() * 0.8f));

        messV.setText(pMessage.getMessage());
        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) messV.getLayoutParams();
        flp.gravity = isMessageReceived ? Gravity.LEFT : Gravity.RIGHT;
        int bottomMarginForMess = res.getDimensionPixelOffset(R.dimen.chat_space_for_picandtime);
        flp.setMargins(flp.leftMargin, flp.topMargin, flp.rightMargin, bottomMarginForMess);


        timeV.setMaxWidth((int) (Utils.getScreenWidth() * 0.7f));
        timeV.setText(XMPPMessage.getDateString(pMessage.getDate()));
        int margin = res.getDimensionPixelSize(R.dimen.chat_time_horizontal_margin);
        FrameLayout.LayoutParams flp2 = (FrameLayout.LayoutParams) timeV.getLayoutParams();
        flp2.gravity = flp2.gravity | (isMessageReceived ? Gravity.LEFT : Gravity.RIGHT);
        int bottomMarginForTimeStamp = res.getDimensionPixelOffset(R.dimen.chat_timestampbottom_for_picandtime);
        flp2.setMargins(isMessageReceived ? margin : 0, flp2.topMargin, isMessageReceived ? 0 : margin, bottomMarginForTimeStamp);

        applyMessageViewCustomization(messV, pCustomization, isMessageReceived);

        return vg;
    }

    private static void applyMessageViewCustomization(View pMessV, UICustomization pCustomization, boolean pIsMessageReceived){
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
}
