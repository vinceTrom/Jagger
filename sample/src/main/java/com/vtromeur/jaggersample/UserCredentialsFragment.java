package com.vtromeur.jaggersample;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Vince on 24/03/16.
 */
public class UserCredentialsFragment extends Fragment{

    private final String CREDENTIALS_SHARED_PREFS = "CREDENTIALS_SHARED_PREFS";
    private final String USER_ID_SHARED_PREFS = "USER_ID_SHARED_PREFS";
    private final String USER_PASSWORD_SHARED_PREFS = "USER_PASSWORD_SHARED_PREFS";
    private final String CHATTER_ID_SHARED_PREFS = "CHATTER_ID_SHARED_PREFS";


    private EditText mUserIDField;
    private EditText mUserPasswordField;
    private EditText mChatterIDField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.user_credentials, container, false);
        initViews(vg);
        initFieldsWithSavedCredentials();
        return vg;
    }

    private void initViews(ViewGroup pVg){
        mUserIDField = (EditText) pVg.findViewById(R.id.userIdField);
        mUserPasswordField = (EditText) pVg.findViewById(R.id.userPasswordField);
        mChatterIDField = (EditText) pVg.findViewById(R.id.chatterIdField);

        pVg.findViewById(R.id.connectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areAllFieldFilled()) {
                    Toast.makeText(getActivity(), R.string.fields_are_not_filled, Toast.LENGTH_SHORT);
                    return;
                }
                String userId = mUserIDField.getText().toString();
                String userPassword = mUserPasswordField.getText().toString();
                String chatterId = mChatterIDField.getText().toString();

                saveCredentialsInSharedPrefs(userId, userPassword, chatterId);
                sendConnectionInfoToActivity(userId, userPassword, chatterId);
            }
        });
    }

    private void sendConnectionInfoToActivity(String userId, String userPassword, String chatterId){
        ((MainActivity)getActivity()).launchChatFragment(userId, userPassword, chatterId);
    }

    private boolean areAllFieldFilled(){
        return mUserIDField.getText().length() > 0 && mUserPasswordField.getText().length() > 0 && mChatterIDField.getText().length() > 0;
    }

    private void initFieldsWithSavedCredentials(){
        SharedPreferences prefs = getSharedPrefs();
        mUserIDField.setText(prefs.getString(USER_ID_SHARED_PREFS, ""));
        mUserPasswordField.setText(prefs.getString(USER_PASSWORD_SHARED_PREFS, ""));
        mChatterIDField.setText(prefs.getString(CHATTER_ID_SHARED_PREFS, ""));

    }

    private void saveCredentialsInSharedPrefs(String pUserId, String pUserPassword, String pChatterId){
        getSharedPrefs().edit().putString(USER_ID_SHARED_PREFS, pUserId)
                .putString(USER_PASSWORD_SHARED_PREFS, pUserPassword)
                .putString(CHATTER_ID_SHARED_PREFS, pChatterId)
                .commit();
    }

    private SharedPreferences getSharedPrefs(){
        return getActivity().getSharedPreferences(CREDENTIALS_SHARED_PREFS, Context.MODE_PRIVATE);
    }
}
