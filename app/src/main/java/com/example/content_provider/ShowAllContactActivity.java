package com.example.content_provider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ShowAllContactActivity extends Activity {

    Button btnback;
    private static final int REQUEST_CODE_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_contact);

        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(v -> finish());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            showAllContacts();
        } else {
            // Yêu cầu quyền truy cập danh bạ
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
    }


    public void showAllContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        ArrayList<String> list = new ArrayList<>();
        Cursor c1 = getContentResolver().query(uri, null, null, null, null);
        if (c1 != null) {
            while (c1.moveToNext()) {
                try {
                    String id = c1.getString(c1.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = c1.getString(c1.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String s = id + " - " + name;
                    list.add(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c1.close();
        }
        ListView lv = findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAllContacts();
            } else {
                Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}