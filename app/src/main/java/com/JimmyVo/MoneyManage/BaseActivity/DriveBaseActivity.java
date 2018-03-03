/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.JimmyVo.MoneyManage.BaseActivity;

import com.JimmyVo.MoneyManage.Utility.Message;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * An abstract activity that handles authorization and connection to the Drive services.
 */
public class DriveBaseActivity extends BaseUtility {
    protected static final String MIME_TYPE_TEXT = "text/plant";
    protected static final String ACCOUNT_NAME_KEY = "account_name";

    protected static final int REQUESTCODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_CREATOR = 1;
    protected static final int REQUEST_CODE_OPENER = 2;

    protected static GoogleSignInClient mGoogleSignInClient;
    protected static DriveClient mDriveClient;
    protected static DriveResourceClient mDriveResourceClient;
    protected static String mAccountName;
    private static boolean isSigningIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSigningIn = false;
        if(!isSignedIn())
        signIn();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCOUNT_NAME_KEY, mAccountName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAccountName = savedInstanceState.getString(ACCOUNT_NAME_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                createDriveClients(GoogleSignIn.getLastSignedInAccount(this));
                Message.showDebug(this, "Signed in successfully.");
                onSignInSuceed();
            } else {
                Message.showAlways(this, String.format("Unable to sign in, result code %d", resultCode));
                //finish if not signin
                finish();
            }

            isSigningIn = false;
        }
    }

    public void onSignInSuceed() {}

    public boolean isSigningIn() {
        return isSigningIn;
    }

    public boolean isSignedIn() {
        isSigningIn = true;
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        Boolean result =
                mGoogleSignInClient != null &&
                        (signInAccount != null &&
                                signInAccount.getGrantedScopes().contains(Drive.SCOPE_FILE));

        if (result){
            createDriveClients(signInAccount);
        }
        return result;
    }

    public void signIn() {
        Message.showDebug(this, "Start sign-in.");
        mGoogleSignInClient = getGoogleSignInClient();
        // Attempt silent sign-in
        mGoogleSignInClient.silentSignIn()
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        createDriveClients(googleSignInAccount);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Silent sign-in failed, display account selection prompt
                startActivityForResult(
                        mGoogleSignInClient.getSignInIntent(), REQUESTCODE_SIGN_IN);
            }
        });
    }


    private GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    private void createDriveClients(GoogleSignInAccount googleSignInAccount) {
        // Build a drive client.
        mDriveClient = Drive.getDriveClient(getApplicationContext(), googleSignInAccount);
        // Build a drive resource client.
        mDriveResourceClient =
            Drive.getDriveResourceClient(getApplicationContext(), googleSignInAccount);
    }


}
