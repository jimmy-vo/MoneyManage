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

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.JimmyVo.MoneyManage.BaseActivity.BaseActivity;
import com.JimmyVo.MoneyManage.BaseActivity.DriveBaseActivity;
import com.JimmyVo.MoneyManage.DataHandler.DataHandler.GDrive;
import com.JimmyVo.MoneyManage.Utility.Message;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * An activity that lets you open/create a Drive text file and modify it.
 */
public class DriveBackupActivity extends DriveBaseActivity {

    private DriveId mCurrentDriveId;
    private Metadata mMetadata;
    private DriveContents mDriveContents;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        currentCode = RCODE_GDRIVEEXPORT;
        currentLabel = INTENT_GDRIVEEXPORT;
        super.onCreate(savedInstanceState);

        //check signin status
        if(isSignedIn()) {
            saveFile(GDrive.exportFile());
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
            saveFile(GDrive.exportFile());
        }else{
            Message.showDebug(this, "DriveBackupActivity: catch this rediculuos bug");
            closeActivity(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CREATOR:
                if (resultCode == RESULT_OK) {

//                    removeFile(mDriveResourceClient,GDrive.getExportFileName());
                    mCurrentDriveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                }else{
                    Message.showDebug(this,"Cancel pressed");
                }
                closeActivity(false);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void closeActivity(boolean ifChanged){
        if (ifChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }


    /** Create a new file and backup it to Drive. */
    private void saveFile(final ArrayList<String> input) {
        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {

                                return createFileIntentSender(task.getResult(), input);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {                           }
                        });

    }

    public  Task<Void> createFileIntentSender(DriveContents driveContents, ArrayList<String> input) throws IOException {
        // Get an output stream for the contents.
        OutputStream outputStream = driveContents.getOutputStream();
        // Write the bitmap summary from it.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        for (String item : input){
            out.writeBytes(item );
        }

        try {
            outputStream.write(baos.toByteArray());
        } catch (IOException e) {

        }

        // Create the initial metadata - MIME type and title.
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(GDrive.getExportFileName())
                .setMimeType(MIME_TYPE_TEXT)
                .setStarred(true)
                .build();


        // Set up options to configure and display the create file activity.
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(changeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                            @Override
                            public Void then(@NonNull Task<IntentSender> task) throws Exception {


                                startIntentSenderForResult(
                                        task.getResult(),
                                        REQUEST_CODE_CREATOR,
                                        null,
                                        0,
                                        0,
                                        0);
                                return null;
                            }
                        });
    }

    private void removeFile(final DriveResourceClient client, final String sFilename){

        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, sFilename)).build();

        Task<MetadataBuffer> queryTask = client.query(query);

        queryTask.addOnSuccessListener( this,
                new OnSuccessListener< MetadataBuffer >()
                {
                    @Override
                    public void onSuccess( MetadataBuffer metadataBuffer )
                    {
                        for( Metadata m : metadataBuffer )
                        {
                            DriveResource driveResource = m.getDriveId().asDriveResource();

                            client.delete( driveResource );
                        }

                    }
                } )
                .addOnFailureListener( this, new OnFailureListener()
                {
                    @Override
                    public void onFailure( @NonNull Exception e )
                    {
                        Message.showAlways(getApplicationContext(), "Old file not found");
                    }
                } );
    }
}
