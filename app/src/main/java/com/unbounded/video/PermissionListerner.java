package com.unbounded.video;

import java.util.List;

/**
 * 权限执行回调接口
 */

public interface PermissionListerner {
    void onGranted();
    void onDenid(List<String> denidPermission);
}
