package com.cenco.lib.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.List;

/**
 * Created by Administrator on 2018/3/26.
 */

public class PermissionManager {

    public static final int REQUEST_LOCATION_PERMISSION = 0x0001;

    private Rationale mRationale;
    private PermissionSetting mSetting;
    private Context context;

    public PermissionManager(Context context) {
        this.context = context;
        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(context);
    }

    public PermissionManager(Context context,int requestCode) {
        this.context = context;
        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(context,requestCode);
    }

    public void requestPermission(final PermissionCallback callback, String... permissions) {
        AndPermission.with(context)
                .permission(permissions)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (callback!=null){
                            callback.onGrant();
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            mSetting.showSetting(permissions);
                        }
                        if (callback!=null){
                            callback.onDeny();
                        }
                    }
                })
                .start();
    }

    public interface PermissionCallback{
        void onGrant();
        void onDeny();
    }

    public final class DefaultRationale implements Rationale {

        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames));

            AlertDialog.newBuilder(context)
                    .setCancelable(false)
                    .setTitle(R.string.title_dialog)
                    .setMessage(message)
                    .setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.cancel();
                        }
                    })
                    .show();
        }
    }

    public final class PermissionSetting {

        private final Context mContext;

        private int requestCode;

        public PermissionSetting(Context context) {
            this.mContext = context;
        }

        public PermissionSetting(Context mContext, int requestCode) {
            this.mContext = mContext;
            this.requestCode = requestCode;
        }

        public void showSetting(final List<String> permissions) {
            List<String> permissionNames = Permission.transformText(mContext, permissions);
            String message = mContext.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));


            if (mContext instanceof Activity) {

                Activity activity = (Activity) mContext;
                final SettingService settingService = AndPermission.permissionSetting(activity);
                AlertDialog.newBuilder(mContext)
                        .setCancelable(false)
                        .setTitle(R.string.title_dialog)
                        .setMessage(message)
                        .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settingService.execute(requestCode);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settingService.cancel();
                            }
                        })
                        .show();
            }
        }
    }

}



