package jp.techacademy.bryan.thogerson.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    Timer mTimer;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Handler mHandler = new Handler();

    Button startStopButton;
    Button forwardButton;
    Button backButton;

    int fieldIndex;
    Long id;
    Uri imageUri;

    ContentResolver resolver;

    Cursor cursor;
    Boolean isRunning = false;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = (Button) findViewById(R.id.button_startstop);
        forwardButton = (Button) findViewById(R.id.button_forward);
        backButton = (Button) findViewById(R.id.button_back);

        imageView = (ImageView) findViewById(R.id.imageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Read External Storageの許可が必要", Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning == false){
                    if (mTimer == null) {
                        startStopButton.setText("停止");
                        isRunning = true;
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(cursor.moveToNext()){
                                            fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                            id = cursor.getLong(fieldIndex);
                                            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                            imageView.setImageURI(imageUri);
                                        }else{
                                            cursor.moveToFirst();
                                            fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                            id = cursor.getLong(fieldIndex);
                                            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                            imageView.setImageURI(imageUri);
                                        }
                                    }
                                });
                            }
                        }, 2000, 2000);
                    }
                } else{
                    isRunning = false;
                    startStopButton.setText("再生");
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                }

            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning == false){
                    if(cursor.moveToNext()){
                        fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        imageView.setImageURI(imageUri);
                    }else{
                        cursor.moveToFirst();
                        fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        imageView.setImageURI(imageUri);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning == false){
                    if(cursor.moveToPrevious()){
                        fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        imageView.setImageURI(imageUri);
                    }else{
                        cursor.moveToLast();
                        fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        imageView.setImageURI(imageUri);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else{
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        resolver = getContentResolver();
        cursor  = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
        if (cursor.moveToFirst()) {
            fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            id = cursor.getLong(fieldIndex);
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            imageView.setImageURI(imageUri);
        }
        else{
            finish();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cursor != null){
            cursor.close();
        }
    }
}
