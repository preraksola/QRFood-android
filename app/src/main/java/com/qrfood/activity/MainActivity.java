package com.qrfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.qrfood.R;
import com.qrfood.utility.CommonMethods;
import com.qrfood.utility.Constants;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity {

    CommonMethods commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonMethods = new CommonMethods(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(AuthUI.GOOGLE_PROVIDER, AuthUI.EMAIL_PROVIDER)
                            .setIsSmartLockEnabled(false)
                            .build(), RC_SIGN_IN
            );
        } else {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                commonMethods.setSharedPreferences(Constants.userTokenId, FirebaseAuth.getInstance().getCurrentUser().getUid(), Constants.aString);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Unable to sign in. Pleas try again", Toast.LENGTH_LONG).show();
                Log.i("MA", "unable to sign in");

            }
        }
    }
}
