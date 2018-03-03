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

package com.JimmyVo.MoneyManage.Activity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.JimmyVo.MoneyManage.BaseActivity.BaseUtility;
import com.JimmyVo.MoneyManage.BaseActivity.DriveBaseActivity;
import com.JimmyVo.MoneyManage.DataHandler.DataHandler;
import com.JimmyVo.MoneyManage.DataHandler.DataHandler.GDrive;
import com.JimmyVo.MoneyManage.DataHandler.StorageApp;
import com.JimmyVo.MoneyManage.Utility.Message;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * An activity that lets you open/create a Drive text file and modify it.
 */
public class DriveImportActivity extends DriveBaseActivity {

    private DriveId mCurrentDriveId;
    private Metadata mMetadata;
    private DriveContents mDriveContents;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        currentCode = RCODE_GDRIVEIMPORT;
        currentLabel = INTENT_GDRIVEIMPORT;
        super.onCreate(savedInstanceState);

        //check signin status
        if(isSignedIn()) {
            openDriveFile();
        }else if (isSigningIn()){
            Message.showDebug(this, "Wait for onSignInSuceed");
        }else {
            Message.showDebug(this, "Sign In fail");
            closeActivity(false);
        }
}

    @Override
    public void onSignInSuceed() {
        if(isSignedIn()) {
            openDriveFile();
        }else {
            Message.showDebug(this, "catch this rediculuos bug at onSignInSuceed");
        }
    }


    protected void closeActivity(boolean ifChanged){
        if (ifChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }

    private void processFile() {
        ArrayList<String> input = new ArrayList<>(0);
        try (InputStream inputStream = mDriveContents.getInputStream()) {
            input = readFromInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (input.size() == 0) {
            Message.showAlways(this, "File is empty!");
            closeActivity(true);
            return ;
        }else if (!GDrive.verifyHeading(input)) {
            final ArrayList<String> data = input;
            new ConfirmDialog("Warning", "Different account config detected. Import may make current data mismatch. Do you still want to import?") {
                @Override
                protected void onAccept() {
                    GDrive.importAccountConfig(data);
                    GDrive.importFile(getApplicationContext(), data, mMetadata.getTitle());
                    closeActivity(true);
                }
            };

        }else {
            GDrive.importFile(getApplicationContext(), input, mMetadata.getTitle());
            closeActivity(true);
        }

    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    mCurrentDriveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    loadCurrentFile();
                }else{
                    Message.showDebug(this,"Cancel pressed");
                    closeActivity(false);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }




    private void loadCurrentFile() {
        Message.showDebug(this, "Retrieving...");

        final DriveFile file = mCurrentDriveId.asDriveFile();

        // Retrieve and store the file metadata and contents.
        mDriveResourceClient.getMetadata(file)
                .continueWithTask(new Continuation<Metadata, Task<DriveContents>>() {
                    @Override
                    public Task<DriveContents> then(@NonNull Task<Metadata> task) {
                        if (task.isSuccessful()) {
                            mMetadata = task.getResult();
                            Message.showDebug(getApplicationContext(), "Metadate loaded!");
                            return mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
                        } else {
                            Message.showAlways(getApplicationContext(), "Metadate unable to initialize!");
                            return Tasks.forException(task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents driveContents) {
                mDriveContents = driveContents;
                processFile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Message.showAlways(getApplicationContext(), "Unable to retrieve file metadata and contents.");
            }
        });
    }


    private void openDriveFile() {
        Message.showDebug(getApplicationContext(), "Open Drive file.");

        if (!isSignedIn()) {
            Message.showDebug(getApplicationContext(), "Failed to open file, user is not signed in.");
            return;
        }

        // Build activity options.
        final OpenFileActivityOptions openFileActivityOptions =
            new OpenFileActivityOptions.Builder()
                //.setMimeType(Collections.singletonList(MIME_TYPE_TEXT))
                .build();

        // Start a OpenFileActivityIntent
        mDriveClient.newOpenFileActivityIntentSender(openFileActivityOptions)
            .addOnSuccessListener(new OnSuccessListener<IntentSender>() {
                @Override
                public void onSuccess(IntentSender intentSender) {
                    try {
                        startIntentSenderForResult(
                            intentSender,
                            REQUEST_CODE_OPENER,
                            /* fillInIntent= */ null,
                            /* flagsMask= */ 0,
                            /* flagsValues= */ 0,
                            /* extraFlags= */ 0);
                    } catch (SendIntentException e) {
                        Message.showDebug(getApplicationContext(), "Unable to send intent.");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Message.showDebug(getApplicationContext(), "Unable to create OpenFileActivityIntent.");
                }
            });
    }
    public static ArrayList<String> readFromInputStream(InputStream is) throws IOException {
        ArrayList<String> output = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            output.add(line);
        }
        return output;
    }
}
