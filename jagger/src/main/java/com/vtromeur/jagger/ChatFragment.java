package com.vtromeur.jagger;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vtromeur.jagger.xmpp.XMPPMessage;
import com.vtromeur.jagger.xmpp.XMPPServerConfig;
import com.vtromeur.jagger.xmpp.XMPPService;
import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPOnMessageReceivedListener;

import java.util.ArrayList;


/**
 * Created by Vincent Tromeur on 03/12/15.
 */


public class ChatFragment extends Fragment {

    private static final String USERNAME_KEY = "USERNAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String CHATTERNAME_KEY = "CHATTERNAME_KEY";


    private XMPPService mXmppService;

    private ArrayList<XMPPMessage> mMessages = new ArrayList<>();

    private String mUserName;
    private String mPassword;
    private String mChatterName;


    private ViewGroup mVg;
    private EditText mEditText;
    private View mSendBtn;
    private ScrollView mScrollView;
    private LinearLayout mMessageListView;

    private Handler mHandler = new Handler();

    /**
     * Return an instance of ChatFragment, initialized with the two users ids
     * @param pUserName your chat username
     * @param pPassword your chat password
     * @param pChatterName the other user username
     * @return a usable instance of ChatFragment
     */
    public static ChatFragment getInstance(String pUserName, String pPassword, String pChatterName) {
        ChatFragment chatFrag = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME_KEY, pUserName);
        bundle.putString(PASSWORD_KEY, pPassword);
        bundle.putString(CHATTERNAME_KEY, pChatterName);
        chatFrag.setArguments(bundle);
        return chatFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        //ChatSessions.getInstance().addChatSession(mUser, System.currentTimeMillis());
        Utils.initScreenWidth(container.getContext());

        mUserName = getArguments().getString(USERNAME_KEY);
        mPassword = getArguments().getString(PASSWORD_KEY);
        mChatterName = getArguments().getString(CHATTERNAME_KEY);

        mVg = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(mVg);
        initViewsListeners(mVg);

        initXMPPService();

        //UnreadMessageManager.getInstance().removeHasUnreadMessage(mUser.getId());

        return mVg;
    }

    private void removeLoaderView() {
        //mVg.removeView(mVg.findViewById(R.id.loaderView));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mXmppService != null)
            mXmppService.setMessageReceiver(null);
        if (getActivity() != null) {
            hideSoftKeyboard();
        }
    }


    private void initXMPPService() {
        XMPPServerConfig serverConfig = new XMPPServerConfig("securejabber.me", "5222", "securejabber.me");
        serverConfig.setSASLAuthenticationEnabled(true);

        mXmppService = XMPPService.getInstance();

        mXmppService.init(getActivity(), serverConfig);

        mXmppService.connectAndLogin(mUserName, mPassword, new ConnectionStateListener() {

            @Override
            public void loggingFailed() {
                Toast.makeText(getActivity(), "logging Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionFailed() {
                removeLoaderView();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "connection Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectedAndLogged() {
                removeLoaderView();
                Toast.makeText(getActivity(), "connected And Logged", Toast.LENGTH_SHORT).show();
            }
        });
        mXmppService.setMessageReceiver(new XMPPOnMessageReceivedListener() {

            @Override
            public void messageReceived(XMPPMessage message) {
                Toast.makeText(getActivity(), "messageReceived:" + message.getMessage(), Toast.LENGTH_SHORT).show();

                mMessages.add(message);
                addMessageToList(message);
                scrollDown();
            }
        });
        /*
        Log.d("", "Debut Chgt. histo de chat");
        XMPPMessagesCache.getInstance().getCachedMessages(mUser.getId(), new XMPPMessagesCache.GetCachedMessagesCallback() {
            @Override
            public void messagesRetrieved(ArrayList<XMPPMessage> messages) {
                Log.d("", "Fin Chgt. histo de chat");

                if(messages != null){
                    mMessages = messages;
                    addMessagesToList(mMessages);
                    //if(mXmppService.isConnected()){
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            removeLoaderView();
                        }
                    }, 200);
                    //}
                }
            }
        });
        */

    }

    private void initViews(ViewGroup vg) {
        mSendBtn = vg.findViewById(R.id.sendbtn);
        mEditText = (EditText) vg.findViewById(R.id.editText);
        mScrollView = (ScrollView) vg.findViewById(R.id.scrollView);
        mMessageListView = (LinearLayout) vg.findViewById(R.id.messagelistview);

    }


    private void initViewsListeners(ViewGroup vg) {
        mSendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message = mEditText.getText().toString();
                sendMessage(message);
            }
        });
        mEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                scrollDown();
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mSendBtn.setActivated(true);
                } else {
                    mSendBtn.setActivated(false);
                }
            }
        });

    }

    private void sendMessage(final String message) {
        if (message.length() == 0)
            return;

        mVg.findViewById(R.id.sendloading).setVisibility(View.VISIBLE);
        mVg.findViewById(R.id.sendbtn).setVisibility(View.GONE);


        mXmppService.sendMessage(mChatterName, message, new MessageSendingListener() {

            @Override
            public void messageSent(XMPPMessage pMessage) {
                Toast.makeText(getActivity(), "Message envoyé", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    mVg.findViewById(R.id.sendloading).setVisibility(View.GONE);
                    mVg.findViewById(R.id.sendbtn).setVisibility(View.VISIBLE);

                    mEditText.setText("");
                    mMessages.add(pMessage);
                    addMessageToList(pMessage);
                    scrollDown();

                }
            }

            @Override
            public void messageNotSent(XMPPMessage pMessage) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Message non envoyé", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addMessagesToList(ArrayList<XMPPMessage> messages) {
        if (messages == null)
            return;
        for (int i = 0; i < messages.size(); i++) {
            addMessageToList(messages.get(i));
        }
        scrollDown();
    }

    private void addMessageToList(XMPPMessage message) {

        boolean receive = message.getReceiverId().contains("vtrom");

        Resources res = mScrollView.getResources();

        ViewGroup vg = (ViewGroup) LayoutInflater.from(mMessageListView.getContext()).inflate(R.layout.chat_message, mMessageListView, false);
        TextView messV = (TextView) vg.findViewById(R.id.message);
        TextView timeV = (TextView) vg.findViewById(R.id.timeStamp);
        final View picV = vg.findViewById(R.id.pic);

        FrameLayout.LayoutParams picflp = (FrameLayout.LayoutParams) picV.getLayoutParams();
        picflp.gravity = Gravity.BOTTOM | (receive ? Gravity.LEFT : Gravity.RIGHT);


        messV.setMaxWidth((int) (Utils.getScreenWidth() * 0.8f));
        messV.setBackgroundResource(receive ? R.drawable.chat_bubble_left : R.drawable.chat_bubble_right);
        messV.setText(message.getMessage());
        messV.setTextColor(receive ? Color.WHITE : Color.BLACK);
        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) messV.getLayoutParams();
        flp.gravity = receive ? Gravity.LEFT : Gravity.RIGHT;
        int bottomMarginForMess = res.getDimensionPixelOffset(R.dimen.chat_space_for_picandtime);
        flp.setMargins(flp.leftMargin, flp.topMargin, flp.rightMargin, bottomMarginForMess);


        timeV.setMaxWidth((int) (Utils.getScreenWidth() * 0.7f));
        timeV.setText(message.getDateString());
        int margin = res.getDimensionPixelSize(R.dimen.chat_time_horizontal_margin);
        FrameLayout.LayoutParams flp2 = (FrameLayout.LayoutParams) timeV.getLayoutParams();
        flp2.gravity = flp2.gravity | (receive ? Gravity.LEFT : Gravity.RIGHT);
        int bottomMarginForTimeStamp = res.getDimensionPixelOffset(R.dimen.chat_timestampbottom_for_picandtime);
        flp2.setMargins(receive ? margin : 0, flp2.topMargin, receive ? 0 : margin, bottomMarginForTimeStamp);

 /*
        String url = mUser.getPicUrl();

        Datas.loadImage(mMessageListView.getContext(), url, new DataCallback<Bitmap>() {
            @Override
            public void dataLoaded(Bitmap img) {
                if(img != null)
                    picV.setImage(img);
            }
        });
        */
        mMessageListView.addView(vg);

    }

    private void scrollDown() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 200);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

}
