package com.qrfood.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;
import com.qrfood.R;
import com.qrfood.utility.CommonMethods;
import com.qrfood.utility.Constants;

public class MainActivity extends AppCompatActivity {

    Lock lock;
    CommonMethods commonMethods;
    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            // Login Success response
            commonMethods.setSharedPreferences(Constants.userTokenId, credentials.getIdToken(), Constants.aString);
            commonMethods.setSharedPreferences(Constants.loginFlagId, true, Constants.aBoolean);

        }

        @Override
        public void onCanceled() {
            // Login Cancelled response
            commonMethods.setSharedPreferences(Constants.loginFlagId, false, Constants.aBoolean);
        }

        @Override
        public void onError(LockException error) {
            // Login Error response
            Log.e("MA", error.getLocalizedMessage());
            commonMethods.setSharedPreferences(Constants.loginFlagId, false, Constants.aBoolean);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonMethods = new CommonMethods(this);

        Auth0 auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        lock = Lock.newBuilder(auth0, callback)
                .useBrowser(true)
                .loginAfterSignUp(true)
                // Add parameters to the Lock Builder
                .build(this);

        if (!commonMethods.isLoggedIn()) {
            startActivity(lock.newIntent(this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lock.onDestroy(this);
        lock = null;
    }
}
