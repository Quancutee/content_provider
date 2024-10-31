package com.example.content_provider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 2;
    Button btnshowallcontact, btnaccesscalllog, btnaccessmediastore, btnshowmessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnshowallcontact = findViewById(R.id.btnshowallcontact);
        btnaccesscalllog = findViewById(R.id.btnaccesscalllog);
        btnaccessmediastore = findViewById(R.id.btnmediastore);
        btnshowmessages = findViewById(R.id.btnshowmessages);

        btnshowallcontact.setOnClickListener(this);
        btnaccesscalllog.setOnClickListener(this);
        btnaccessmediastore.setOnClickListener(this);
        btnshowmessages.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnshowallcontact) {
            Intent intent = new Intent(this, ShowAllContactActivity.class);
            startActivity(intent);
        } else if (v == btnaccesscalllog) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                accessTheCallLog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            }
        } else if (v == btnaccessmediastore) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                accessMediaStore();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        } else if (v == btnshowmessages) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                displayMessages();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 3);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            accessTheCallLog();
        } else if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            accessMediaStore();
        } else if (requestCode == 3 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayMessages();
        } else {
            Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
        }
    }

    public void accessTheCallLog() {
        String[] projection = {CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.DURATION};
        Cursor c = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, CallLog.Calls.DURATION + "<?",
                new String[]{"30"}, CallLog.Calls.DATE + " ASC");
        StringBuilder s = new StringBuilder();

        if (c != null) {
            while (c.moveToNext()) {
                s.append("Ngày: ").append(c.getString(0))
                        .append("\nSĐT: ").append(c.getString(1))
                        .append("\nThời Gian: ").append(c.getString(2)).append(" Giây\n\n");
            }
            c.close();
        }

        new AlertDialog.Builder(this)
                .setTitle("Lịch sử cuộc gọi gần đây")
                .setMessage(s.toString().isEmpty() ? "Không có cuộc gọi nào" : s.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    public void accessMediaStore() {
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.MIME_TYPE};
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        StringBuilder s = new StringBuilder();

        if (c != null) {
            while (c.moveToNext()) {
                s.append("Tên file: ").append(c.getString(0))
                        .append("\nNgày thêm: ").append(c.getString(1))
                        .append("\nLoại file: ").append(c.getString(2)).append("\n\n");
            }
            c.close();
        }

        new AlertDialog.Builder(this)
                .setTitle("Danh sách Media")
                .setMessage(s.toString().isEmpty() ? "Không có file media nào" : s.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    public void displayMessages() {
        Cursor cursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, null);
        StringBuilder messages = new StringBuilder();

        if (cursor != null) {
            int addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
            while (cursor.moveToNext()) {
                String address = cursor.getString(addressIndex);
                String body = cursor.getString(bodyIndex);
                messages.append("Từ: ").append(address).append("\nNội dung: ").append(body).append("\n\n");
            }
            cursor.close();
        }

        new AlertDialog.Builder(this)
                .setTitle("Tin nhắn SMS")
                .setMessage(messages.toString().isEmpty() ? "Không có tin nhắn nào" : messages.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }
}
