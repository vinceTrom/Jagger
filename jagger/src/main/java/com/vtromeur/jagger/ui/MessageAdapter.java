package com.vtromeur.jagger.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

/**
 * Created by Vince on 11/04/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<XMPPMessage> mMessages;
    private UICustomization mUICustomization;

    public MessageAdapter(List<XMPPMessage> pMessages, UICustomization pUICustomization){
        mMessages = pMessages;
        mUICustomization = pUICustomization;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        XMPPMessage message = mMessages.get(position);

        boolean isMessageReceived = message.isReceived();

        TextView messV = holder.mMessageView;
        TextView timeV = holder.mTimeView;
        View picV = holder.mPicView;

        Resources res = messV.getResources();


        FrameLayout.LayoutParams picflp = (FrameLayout.LayoutParams) picV.getLayoutParams();
        picflp.gravity = Gravity.BOTTOM | (isMessageReceived ? Gravity.LEFT : Gravity.RIGHT);


        messV.setMaxWidth((int) (Utils.getScreenWidth() * 0.8f));

        messV.setText(message.getMessage());
        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) messV.getLayoutParams();
        flp.gravity = isMessageReceived ? Gravity.LEFT : Gravity.RIGHT;
        int bottomMarginForMess = res.getDimensionPixelOffset(R.dimen.chat_space_for_picandtime);
        flp.setMargins(flp.leftMargin, flp.topMargin, flp.rightMargin, bottomMarginForMess);


        timeV.setMaxWidth((int) (Utils.getScreenWidth() * 0.7f));
        timeV.setText(XMPPMessage.getDateString(message.getDate()));
        int margin = res.getDimensionPixelSize(R.dimen.chat_time_horizontal_margin);
        FrameLayout.LayoutParams flp2 = (FrameLayout.LayoutParams) timeV.getLayoutParams();
        flp2.gravity =  (isMessageReceived ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM;
        int bottomMarginForTimeStamp = res.getDimensionPixelOffset(R.dimen.chat_timestampbottom_for_picandtime);
        flp2.setMargins(isMessageReceived ? margin : 0, flp2.topMargin, isMessageReceived ? 0 : margin, bottomMarginForTimeStamp);

        UIHelper.applyMessageViewCustomization(messV, mUICustomization, isMessageReceived);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mMessageView;
        public TextView mTimeView;
        public View mPicView;

        public ViewHolder(ViewGroup vg) {
            super(vg);
            mMessageView = (TextView) vg.findViewById(R.id.message);
            mTimeView = (TextView) vg.findViewById(R.id.timeStamp);
            mPicView = vg.findViewById(R.id.pic);
        }
    }
}
