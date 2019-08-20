package com.zerogame.fragmentdialog.fragmentdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zerogame.fragmentdialog.util.ScreenUtil;


public class BaseDialog extends DialogFragment {


    public void resizeDialogFragment() {
        Dialog dialog = getDialog();
        boolean screenOriatationPortrait = isScreenOriatationPortrait(getActivity());
        if (screenOriatationPortrait) {
            if (null != dialog) {
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
                lp.height = (25 * ScreenUtil.getScreenHeight(getContext()) / 45);//获取屏幕的宽度，定义自己的宽度
                lp.width = (8 * ScreenUtil.getScreenWidth(getContext()) / 9);
                lp.gravity = Gravity.CENTER;
                if (window != null) {
                    window.setLayout(lp.width, lp.height);
                    window.setAttributes(lp);
                }
            }
        } else {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.height = (25 * ScreenUtil.getScreenHeight(getContext()) / 30);//获取屏幕的宽度，定义自己的宽度
            lp.width = (5* ScreenUtil.getScreenWidth(getContext()) / 9);
            lp.gravity = Gravity.CENTER;
            if (window != null) {
                window.setLayout(lp.width, lp.height);
                window.setAttributes(lp);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        resizeDialogFragment();
        super.onResume();
    }


    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }


}
