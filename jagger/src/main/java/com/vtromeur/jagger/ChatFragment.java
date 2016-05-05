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

import com.vtromeur.jagger.ui.CustomRecyclerViewLayoutManager;
import com.vtromeur.jagger.ui.MessageAdapter;
import com.vtromeur.jagger.ui.UIHelper;
import com.vtromeur.jagger.xmpp.XMPPMessage;
import com.vtromeur.jagger.xmpp.XMPPServerConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * The ChatFragment is the UI element which allow you to chat with another user
 *<br>
 * Call the {@link #getInstance(XMPPServerConfig, String, String, String) getInstance} method to initialize one
 * and add it in your fullscreen view.
 *<br>
 * Created by Vincent Tromeur on 03/12/15.
 */


public class ChatFragment extends Fragment {

    private ChatPresenter mPresenter;

    private UICustomization mUICustomization = new UICustomization();

    private List<XMPPMessage> mMessages = new ArrayList<>();

    private ViewGroup mVg;
    private EditText mEditText;
    private View mSendBtn;
    private RecyclerView mMessageRecyclerView;

    private Handler mHandler = new Handler();

    public ChatFragment(){
        mPresenter = new ChatPresenter(this);
    }

    private void initPresenter(XMPPServerConfig pServerConfig, String pUserName, String pPassword, String pChatterName){
        mPresenter.initChatParams(pServerConfig, pUserName, pPassword, pChatterName);
    }

    /**
     * Return an instance of ChatFragment, initialized with the two users ids
     * @param pUserName your chat username
     * @param pPassword your chat password
     * @param pChatterName the other user username
     */
    public static ChatFragment getInstance(XMPPServerConfig pServerConfig, String pUserName, String pPassword, String pChatterName) {
        ChatFragment chatFrag = new ChatFragment();
        chatFrag.initPresenter(pServerConfig, pUserName, pPassword, pChatterName);
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

        mVg = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(mVg);
        initViewsListeners();
        applyViewCustomization(mVg);

        mPresenter.viewCreated();

        return mVg;
    }

    private void applyViewCustomization(View pView){
        int color = UIHelper.getBackgroundColor(pView.getResources(), mUICustomization);
        pView.setBackgroundColor(color);
    }

    public void removeLoaderView() {
        //mVg.removeView(mVg.findViewById(R.id.loaderView));
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.viewStopped();
        if (getActivity() != null) {
            hideSoftKeyboard();
        }
    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        mPresenter.viewDestroyed();
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
                mPresenter.sendMessage(message);
            }
        });
        mEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                scrollDownMessageListView(true);
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




    public void setMessageList(List<XMPPMessage> messages) {
        mMessages = messages;
        mMessageRecyclerView.setAdapter(new MessageAdapter(mMessages, mUICustomization));
    }

    public void addMessage(XMPPMessage pMessage){
        mMessages.add(pMessage);
    }

    public void showToastMessage(String pMessage){
        if(getActivity() != null) {
            Toast.makeText(getActivity(), pMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void showSendButton(){
        mVg.findViewById(R.id.sendbtn).setVisibility(View.VISIBLE);
    }

    public void hideSendButton(){
        mVg.findViewById(R.id.sendbtn).setVisibility(View.GONE);
    }

    public void showMessageSendingLoader(){
        mVg.findViewById(R.id.sendloading).setVisibility(View.VISIBLE);
    }

    public void hideMessageSendingLoader(){
        mVg.findViewById(R.id.sendloading).setVisibility(View.GONE);
    }

    public void emptyMessageEdit(){
        mEditText.setText("");
    }


    public void scrollDownMessageListView(final boolean pSmoothly) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMessageRecyclerView.getAdapter().notifyItemInserted(mMessageRecyclerView.getAdapter().getItemCount());
            }
        });
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
