package com.flippey.mydemos.permissionUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.flippey.mydemos.Constants;
import com.flippey.mydemos.R;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by flippey on 2018/9/28 15:37.
 */
public class PermissionApplyActivity extends AppCompatActivity {
    private static PermissionCallback mPermissionCallback;
    public static int PERMISSION_TYPE_SINGLE = 1;
    public static int PERMISSION_TYPE_GROUP = 2;
    private static final int REQUEST_CODE_SINGLE = 1;
    private static final int REQUEST_CODE_GROUP = 2;
    public static final int RE_REQUEST_CODE_SINGLE = 3;
    private static final int REQUEST_SETTING = 110;

    private int mPermissionType;
    private ArrayList<PermissionItem> mPermissions;
    private String mAppName;
    /**
     * 重新申请权限数组的索引
     */
    private int mRePermissionIndex;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        mAppName = String.valueOf(getApplicationInfo().loadLabel(getPackageManager()));
        if (mPermissionType == PERMISSION_TYPE_SINGLE) {
            //单个权限申请
            if (mPermissions == null || mPermissions.size() == 0) {
                return;
            }
            ActivityCompat.requestPermissions(PermissionApplyActivity.this,
                    new String[]{mPermissions.get(0).permission}, REQUEST_CODE_SINGLE);
        } else {
            String[] permissionStrings = new String[mPermissions.size()];
            for (int i = 0; i < mPermissions.size(); i++) {
                permissionStrings[i] = mPermissions.get(i).permission;
            }
            ActivityCompat.requestPermissions(PermissionApplyActivity.this,
                   permissionStrings, REQUEST_CODE_GROUP);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        mPermissionType = intent.getIntExtra(Constants.PERMISSION_TYPE, 0);
        mPermissions = (ArrayList<PermissionItem>) intent.getSerializableExtra(Constants.PERMISSIONS);
    }

    public static void setPermissionCallback(PermissionCallback permissionCallback) {
        mPermissionCallback = permissionCallback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_SINGLE:
                String permission = getPermissionItem(permissions[0]).permission;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onGuarantee(permission, 0);
                    finish();
                } else {
                    //onDeny(permission,0);
                    reRequestPermission(permission);
                }
                break;
            case REQUEST_CODE_GROUP:
                for (int i = 0; i < grantResults.length; i++) {
                    //权限允许后，删除需要检查的权限
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        PermissionItem item = getPermissionItem(permissions[i]);
                        mPermissions.remove(item);
                        onGuarantee(permissions[i], i);
                    } else {
                        //权限拒绝
                        onDeny(permissions[i], i);
                    }
                }
                if (mPermissions.size() > 0) {
                    //用户拒绝了某个或多个权限，重新申请
                    reRequestPermission(mPermissions.get(mRePermissionIndex).permission);
                } else {
                    onFinish();
                }
                break;
            case RE_REQUEST_CODE_SINGLE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //重新申请后再次拒绝
                    try {
                        String name = getPermissionItem(permissions[0]).permissionName;
                        String title = String.format(getString(R.string.permission_title), name);
                        String msg = String.format(getString(R.string.permission_denied_with_naac), mAppName, name, mAppName);
                        showAlertDialog(title, msg, getString(R.string.permission_reject), getString(R.string.permission_go_to_setting), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivityForResult(intent, REQUEST_SETTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    onClose();
                                }
                            }
                        });
                        onDeny(permissions[0], 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onClose();
                    }
                } else {
                    onGuarantee(permissions[0], 0);
                    if (mRePermissionIndex < mPermissions.size() - 1) {
                        //继续申请下一个被拒绝的权限
                        reRequestPermission(mPermissions.get(++mRePermissionIndex).permission);
                    } else {
                        //全部允许了
                        onFinish();
                    }
                    onFinish();
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    private void onFinish() {
        if (mPermissionCallback != null) {
            mPermissionCallback.onSuccess();
        }
        finish();
    }

    private void onClose() {
        if (mPermissionCallback != null) {
            mPermissionCallback.onClose();
        }
        finish();
    }

    private void onDeny(String permission, int position) {
        if (mPermissionCallback != null) {
            mPermissionCallback.onDeny(permission, position);
        }
    }

    private void onGuarantee(String permission, int position) {
        if (mPermissionCallback != null) {
            mPermissionCallback.onGuarantee(permission, position);
        }
    }

    private PermissionItem getPermissionItem(String permission) {
        for (PermissionItem permissionItem : mPermissions) {
            if (permissionItem.permission.equals(permission)) {
                return permissionItem;
            }
        }
        return null;
    }

    private void reRequestPermission(final String permission) {
        String permissionName = getPermissionItem(permission).permissionName;
        String alertTitle = String.format(getString(R.string.permission_title), permissionName);
        String msg = String.format(getString(R.string.permission_denied), permissionName, mAppName);
        showAlertDialog(alertTitle, msg, getString(R.string.permission_cancel), getString(R.string.permission_ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(PermissionApplyActivity.this, new String[]{permission}, RE_REQUEST_CODE_SINGLE);

            }
        });
    }

    private void showAlertDialog(String title, String msg, String cancelTxt, String PosTxt, DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(cancelTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClose();
                    }
                })
                .setPositiveButton(PosTxt, onClickListener).create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) {
            checkPermission();
            if (mPermissions.size() > 0) {
                mRePermissionIndex = 0;
                reRequestPermission(mPermissions.get(mRePermissionIndex).permission);
            } else {
                onFinish();
            }
        }

    }


    private void checkPermission() {
        ListIterator<PermissionItem> iterator = mPermissions.listIterator();
        while (iterator.hasNext()) {
            int checkPermission = ContextCompat.checkSelfPermission(getApplicationContext(), iterator.next().permission);
            if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                iterator.remove();
            }
        }
    }

}
