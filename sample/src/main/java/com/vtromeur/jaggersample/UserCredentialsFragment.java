package com.vtromeur.jaggersample;


import android.app.Fragment;
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

    private EditText mUserIDField;
    private EditText mUserPasswordField;
    private EditText mChatterIDField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.user_credentials, container, false);
        initViews(vg);
        return vg;
    }

    private void initViews(ViewGroup pVg){
        mUserIDField = (EditText) pVg.findViewById(R.id.userIdField);
        mUserPasswordField = (EditText) pVg.findViewById(R.id.userPasswordField);
        mChatterIDField = (EditText) pVg.findViewById(R.id.chatterIdField);

        pVg.findViewById(R.id.connectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConnectionInfoToActivity();
            }
        });
    }

    private void sendConnectionInfoToActivity(){
        if(!areAllFieldFilled()){
            Toast.makeText(getActivity(), R.string.fields_are_not_filled, Toast.LENGTH_SHORT);
            return;
        }
        String userId = mUserIDField.getText().toString();
        String userPassword = mUserPasswordField.getText().toString();
        String chatterId = mChatterIDField.getText().toString();

        ((MainActivity)getActivity()).launchChatFragment(userId, userPassword, chatterId);
    }

    private boolean areAllFieldFilled(){
        return mUserIDField.getText().length() > 0 && mUserPasswordField.getText().length() > 0 && mChatterIDField.getText().length() > 0;
    }
}
