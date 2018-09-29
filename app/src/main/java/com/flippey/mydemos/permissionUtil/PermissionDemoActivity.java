package com.flippey.mydemos.permissionUtil;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.flippey.mydemos.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PermissionDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        findViewById(R.id.permission_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singlePermission();
            }
        });

        findViewById(R.id.permission_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupPermission();
            }
        });
    }
    //单个权限申请
    private void singlePermission() {
        PermissionManager.create(PermissionDemoActivity.this)
                .checkSinglePermission(Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void onClose() {
                        Toast.makeText(PermissionDemoActivity.this, "弹窗关闭", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onDeny(@NotNull String permission, int position) {
                        Toast.makeText(PermissionDemoActivity.this, "拒绝", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onGuarantee(@NotNull String permission, int position) {
                        Toast.makeText(PermissionDemoActivity.this, "相机权限允许", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivity(intent);
                    }
                });


    }
    //多组权限申请
    private void groupPermission() {
        ArrayList<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "文件读写"));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "文件读写"));
        PermissionManager.create(PermissionDemoActivity.this)
                .permission(permissionItems)
                .checkGroupPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(PermissionDemoActivity.this, "文件读写权限申请成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                    }
                });
    }
}
