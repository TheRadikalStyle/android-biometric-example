package com.theradikalsoftware.myapplication;

import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity{
    Button authButton, resetButton;
    TextView statusTXV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        authButton = findViewById(R.id.main_button_auth);
        resetButton = findViewById(R.id.main_button_reset);
        statusTXV = findViewById(R.id.main_textview_text);

        statusTXV.setText(getResources().getString(R.string.text_no_auth));
        authButton.setText(getResources().getString(R.string.text_pressto_auth));

        //Reset buttons when auth is complete
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authButton.setEnabled(true);
                statusTXV.setText(getResources().getString(R.string.text_no_auth));
                resetButton.setVisibility(View.GONE);
            }
        });


        //If version >= android 9 pie, use BiometricPrompt API
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            final Executor newExecutor = Executors.newSingleThreadExecutor();
            final CancellationSignal cancellationSignal = new CancellationSignal();

            final BiometricPrompt.AuthenticationCallback biometricPromtAuth = new BiometricPrompt.AuthenticationCallback(){
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Log.d("AuthCallback:", "Auth Succeded");
                    authButton.setEnabled(false);
                    resetButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Log.d("AuthCallback:", "Auth Failed");
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if(errorCode == BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS){
                        Log.d("Auth error " + errorCode + " ->", String.valueOf(errString));
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    Log.d("AuthCallback:", "Auth Help");
                }
            };

            final BiometricPrompt prompt = new BiometricPrompt.Builder(this)
                    .setTitle(getResources().getString(R.string.authdialog_title))
                    .setDescription(getResources().getString(R.string.authdialog_description))
                    .setNegativeButton(getResources().getString(android.R.string.cancel), newExecutor, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).build();

            authButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prompt.authenticate(cancellationSignal ,newExecutor, biometricPromtAuth);
                }
            });
        }
    }
}
