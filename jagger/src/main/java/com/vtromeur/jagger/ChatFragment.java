package com.vtromeur.jagger;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.vtromeur.jagger.db.DatabaseHelper;
import com.vtromeur.jagger.di.DaggerSingleton;
import com.vtromeur.jagger.ui.CustomRecyclerViewLayoutManager;
import com.vtromeur.jagger.ui.MessageAdapter;
import com.vtromeur.jagger.ui.UIHelper;
import com.vtromeur.jagger.xmpp.XMPPMessage;
import com.vtromeur.jagger.xmpp.XMPPServerConfig;
import com.vtromeur.jagger.xmpp.XMPPService;
import com.vtromeur.jagger.xmpp.listeners.ConnectionStateListener;
import com.vtromeur.jagger.xmpp.listeners.MessageSendingListener;
import com.vtromeur.jagger.xmpp.listeners.XMPPOnMessageReceivedListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * The ChatFragment is the UI element which allow you to chat with another user
 *<br>
 * Call the {@link #getInstance(XMPPServerConfig, String, String, String) getInstance} method to initialize one
 * and add it in your fullscreen view.
 *<br>
 * Created by Vincent Tromeur on 03/12/15.
 */


public class ChatFragment extends Fragment {

    private static final String USERNAME_KEY = "USERNAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String CHATTERNAME_KEY = "CHATTERNAME_KEY";
    private static final String SERVER_CONFIG_KEY = "SERVER_CONFIG_KEY";


    private UICustomization mUICustomization = new UICustomization();

    @Inject
    public XMPPService mXmppService;

    private List<XMPPMessage> mMessages = new ArrayList<>();

    private String mUserName;
    private String mPassword;
    private String mChatterName;

    private XMPPServerConfig mServerConfig;


    private ViewGroup mVg;
    private EditText mEditText;
    private View mSendBtn;
    private RecyclerView mMessageRecyclerView;

    private Handler mHandler = new Handler();

    public ChatFragment(){
        DaggerSingleton.getInstance().daggerComponent().inject(this);
    }

    /**
     * Return an instance of ChatFragment, initialized with the two users ids
     * @param pUserName your chat username
     * @param pPassword your chat password
     * @param pChatterName the other user username
     */
    public static ChatFragment getInstance(XMPPServerConfig pServerConfig, String pUserName, String pPassword, String pChatterName) {
        ChatFragment chatFrag = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME_KEY, pUserName);
        bundle.putString(PASSWORD_KEY, pPassword);
        bundle.putString(CHATTERNAME_KEY, pChatterName);
        bundle.putSerializable(SERVER_CONFIG_KEY, pServerConfig);
        chatFrag.setArguments(bundle);
        return chatFrag;
    }

    public void setUICustomization(UICustomization pUICustomization){
        if(pUICustomization != null) {
            mUICustomization = pUICustomization;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        Utils.initScreenWidth(container.getContext());

        DatabaseHelper.init(getActivity());

        mUserName = getArguments().getString(USERNAME_KEY);
        mPassword = getArguments().getString(PASSWORD_KEY);
        mChatterName = getArguments().getString(CHATTERNAME_KEY);

        mServerConfig = (XMPPServerConfig) getArguments().getSerializable(SERVER_CONFIG_KEY);
        mVg = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(mVg);
        initViewsListeners();
        applyViewCustomization(mVg);

        initXMPPService();

        return mVg;
    }

    private void applyViewCustomization(View pView){
        int color = UIHelper.getBackgroundColor(pView.getResources(), mUICustomization);
        pView.setBackgroundColor(color);
    }

    private void removeLoaderView() {
        //mVg.removeView(mVg.findViewById(R.id.loaderView));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mXmppService != null)
            mXmppService.setMessageReceiver(mChatterName, null);
        if (getActivity() != null) {
            hideSoftKeyboard();
        }
    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        mXmppService.disconnect();
    }

    private void initXMPPService() {
        mServerConfig.setSASLAuthenticationEnabled(true);

        mXmppService.init(mServerConfig);

        mXmppService.connect(mUserName, mPassword, new ConnectionStateListener() {

            @Override
            public void loggingFailed() {
                Toast.makeText(getActivity(), R.string.logging_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionFailed() {
                removeLoaderView();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectedAndLogged() {
                removeLoaderView();
                Toast.makeText(getActivity(), R.string.connection_and_logged, Toast.LENGTH_SHORT).show();
            }
        });
        mXmppService.setMessageReceiver(mChatterName, new XMPPOnMessageReceivedListener() {

            @Override
            public void messageReceived(final XMPPMessage message) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.message_received) + message.getMessage(), Toast.LENGTH_SHORT).show();

                        mMessages.add(message);
                        scrollDown(true);
                    }
                });

            }
        });
        mMessages = MessageDbHelper.getLastMessages(mUserName, mChatterName);
        addMessageListToScrollView(mMessages);
        scrollDown(false);
    }



    private void initViews(ViewGroup vg) {
        mSendBtn = vg.findViewById(R.id.sendbtn);
        mEditText = (EditText) vg.findViewById(R.id.editText);
        mMessageRecyclerView = (RecyclerView) vg.findViewById(R.id.messagerecyclerview);

        LinearLayoutManager layoutManager = new CustomRecyclerViewLayoutManager(vg.getContext());
        mMessageRecyclerView.setLayoutManager(layoutManager);
    }


    private void initViewsListeners() {
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
                scrollDown(true);
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
                Toast.makeText(getActivity(), R.string.message_sent, Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    mVg.findViewById(R.id.sendloading).setVisibility(View.GONE);
                    mVg.findViewById(R.id.sendbtn).setVisibility(View.VISIBLE);

                    mEditText.setText("");
                    mMessages.add(pMessage);

                    scrollDown(true);
                }
            }

            @Override
            public void messageNotSent(XMPPMessage pMessage) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.message_not_sent, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addMessageListToScrollView(List<XMPPMessage> messages) {
        if (messages == null)
            return;
        mMessageRecyclerView.setAdapter(new MessageAdapter(mMessages, mUICustomization));
    }


    private void scrollDown(final boolean pSmoothly) {
        mMessageRecyclerView.getAdapter().notifyItemInserted(mMessageRecyclerView.getAdapter().getItemCount());
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(pSmoothly) {
                    mMessageRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
                }else{
                    mMessageRecyclerView.scrollToPosition(mMessages.size() - 1);
                }
            }
        }, pSmoothly? 200 : 0);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

}
