package com.flippey.mydemos.permissionUtil;

import java.io.Serializable;

/**
 * Created by flippey on 2018/9/28 11:45.
 */
public class PermissionItem implements Serializable{
    public String permissionName;
    public String permission;

    public PermissionItem(String permission, String permissionName) {
        this.permissionName = permissionName;
        this.permission = permission;
    }

    public PermissionItem(String permission) {
        this.permission = permission;
    }
}
