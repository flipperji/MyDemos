package com.flippey.mydemos.permissionUtil

import java.io.Serializable

/**
 * Created by flippey on 2018/9/28 14:04.
 */
interface PermissionCallback : Serializable {
    //申请弹窗关闭
    fun onClose()

    //已授权
    fun onSuccess()

    //拒绝
    fun onDeny(permission: String, position: Int)

    //允许授权
    fun onGuarantee(permission: String, position: Int)




}