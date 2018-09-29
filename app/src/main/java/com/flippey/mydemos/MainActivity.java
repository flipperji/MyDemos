package com.flippey.mydemos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.flippey.mydemos.permissionUtil.PermissionDemoActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initClick();
    }

    private void initClick() {
        findViewById(R.id.tv_permisssion).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_permisssion:
                startActivity(new Intent(MainActivity.this, PermissionDemoActivity.class));
                break;
        }
    }
}
