package com.flippey.mydemos.permissionUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.flippey.mydemos.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by flippey on 2018/9/28 11:41.
 */
public class PermissionManager {

    private Context mContext;
    private ArrayList<PermissionItem> mPermissionItems;
    private PermissionCallback mCallback;
    private int mPermissionType;
    public PermissionManager(Context context) {
        mContext = context;
    }

    public static PermissionManager create(Context context) {
        return new PermissionManager(context);
    }


    public PermissionManager permission(ArrayList<PermissionItem> permissionItems) {
        mPermissionItems = permissionItems;
        return this;
    }

    @SuppressLint("ObsoleteSdkInt")
    public void checkGroupPermission(PermissionCallback callback) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }
        Iterator<PermissionItem> iterator = mPermissionItems.iterator();
        while (iterator.hasNext()) {
            if (checkPermission(mContext, iterator.next().permission)) {
                iterator.remove();
            }
        }
        mCallback = callback;
        mPermissionType = PermissionApplyActivity.PERMISSION_TYPE_GROUP;
        if (mPermissionItems.size() > 0) {
            //8.0权限组申请
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ArrayList<String> nameList = new ArrayList<>();
                for (int i = 0; i < mPermissionItems.size(); i++) {
                    String permission = mPermissionItems.get(i).permission;
                    String permissionArrName = Permission.permissionArrMap.get(permission);
                    if (!TextUtils.isEmpty(permissionArrName)) {
                        nameList.add(permissionArrName);
                    }
                }
                //去掉重复权限组
                List<String> mList = removeRpetitionr(nameList);
                mPermissionItems.clear();
                for (int i = 0; i < mList.size(); i++) {
                    String str = mList.get(i);
                    String permissionName = Permission.permissionNameMap.get(str);
                    String[] arr = Permission.permissionMap.get(str);
                    for (int x = 0; x < arr.length; x++) {
                        mPermissionItems.add(new PermissionItem(arr[x], permissionName));
                    }
                }
            }
            if (mPermissionItems.size() > 0) {
                startActivity();
            } else {
                callback.onClose();
            }
        } else {
            if (callback != null) {
                callback.onSuccess();
            }
        }
    }

    public void checkSinglePermission(String permission, PermissionCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkPermission(mContext, permission)) {
            if (callback != null) {
                callback.onGuarantee(permission, 0);
            }
            return;
        }
        mCallback = callback;
        mPermissionType = PermissionApplyActivity.PERMISSION_TYPE_SINGLE;
        mPermissionItems = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String permissionArrName = Permission.permissionArrMap.get(permission);

            String permissionName = Permission.permissionNameMap.get(permissionArrName);
            String[] arr = Permission.permissionMap.get(permissionArrName);
            for (int x = 0; x < arr.length; x++) {
                mPermissionItems.add(new PermissionItem(arr[x], permissionName));
            }
        } else {
            mPermissionItems.add(new PermissionItem(permission));
        }

        startActivity();
    }
    private void startActivity() {
        PermissionApplyActivity.setPermissionCallback(mCallback);
        Intent intent = new Intent(mContext, PermissionApplyActivity.class);
        intent.putExtra(Constants.PERMISSION_TYPE, mPermissionType);
        intent.putExtra(Constants.PERMISSIONS, (Serializable) mPermissionItems);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    public  boolean checkPermission(Context context, String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(context, permission);
        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private List<String> removeRpetitionr(List<String> arrNameList) {
        Set set = new HashSet();
        List<String> newList = new ArrayList();
        set.addAll(arrNameList);
        newList.addAll(set);
        return newList;
    }
}
