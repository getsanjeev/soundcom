package org.scorelab.soundcom.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.scorelab.soundcom.MainActivity;
import org.scorelab.soundcom.R;


public class requestPermissions extends AppCompatActivity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Button goMainActivity;
    private TextView textView;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permissions);
        textView = (TextView)findViewById(R.id.text_permissions_note);
        goMainActivity = (Button)findViewById(R.id.go_to_main_activity);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        requestPermissionFromUser();
        goMainActivity.setText(R.string.allow_permission);
        textView.setText(R.string.permission_statement);
        goMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionFromUser();
            }
        });
    }


    public void requestPermissionFromUser(){
        if(ActivityCompat.checkSelfPermission(requestPermissions.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requestPermissions.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(requestPermissions.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(requestPermissions.this,permissionsRequired[1])){

                // Tell user about why we need permissions
                AlertDialog.Builder builder = new AlertDialog.Builder(requestPermissions.this);
                builder.setTitle(R.string.need_multiple_permission);
                builder.setMessage(R.string.permission_statement);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(requestPermissions.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

            else if (permissionStatus.getBoolean(permissionsRequired[0],false)) {
                // Previously if the user has cancelled the permission request with 'Dont Ask Again',
                // Redirect to Settings simply
                AlertDialog.Builder builder = new AlertDialog.Builder(requestPermissions.this);
                builder.setTitle(R.string.need_multiple_permission);
                builder.setMessage(R.string.permission_statement);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

            else {
                //just request the permission
                ActivityCompat.requestPermissions(requestPermissions.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }

            textView.setText(R.string.permissions_note_2);
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0],true);
            editor.commit();
        }

        else {
            // user granted the permissions needed
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            }

            else if(ActivityCompat.shouldShowRequestPermissionRationale(requestPermissions.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(requestPermissions.this,permissionsRequired[1])){
                AlertDialog.Builder builder = new AlertDialog.Builder(requestPermissions.this);
                builder.setTitle(R.string.need_multiple_permission);
                builder.setMessage(R.string.permission_statement);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(requestPermissions.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

            else {
                textView.setText(R.string.permissions_note_2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_PERMISSION_SETTING) {

            if (ActivityCompat.checkSelfPermission(requestPermissions.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requestPermissions.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED) {
            //Got Permission
                proceedAfterPermission();
            }

            else {
                Toast.makeText(this, "Permissions not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void proceedAfterPermission() {
        // Go to our MainActivity
        startActivity(new Intent(requestPermissions.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(requestPermissions.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requestPermissions.this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }
}