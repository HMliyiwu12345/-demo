package com.example.fragmentdialog.fragmentdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fragmentdialog.MainActivity;
import com.example.fragmentdialog.R;
import com.example.fragmentdialog.util.ScreenUtil;
import com.example.fragmentdialog.util.SpUtil;
import com.example.fragmentdialog.util.WYLog;

public class MainDialog extends BaseDialog implements View.OnClickListener {
    private int ib_custom,tv_custom,ib_email,tv_email;



    public static MainDialog newInstance(Context ctx, String tittle) {
        MainDialog fragment = new MainDialog();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.layout_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //游客登录
        ib_custom = getResources().getIdentifier("ib_custom", "id", getActivity().getPackageName());
        tv_custom=getResources().getIdentifier("tv_cusLogin", "id", getActivity().getPackageName());
        ImageButton imageButton=view.findViewById(ib_custom);
        imageButton.setOnClickListener(this);
        TextView tv_cusLogin=view.findViewById(tv_custom);
        tv_cusLogin.setOnClickListener(this);

        //邮箱登录
        ib_email = getResources().getIdentifier("ib_mail", "id", getActivity().getPackageName());
        tv_email=getResources().getIdentifier("tv_email", "id", getActivity().getPackageName());
        ImageButton ib_mail=view.findViewById(ib_email);
        ib_mail.setOnClickListener(this);
        TextView tv_mail=view.findViewById(tv_email);
        tv_mail.setOnClickListener(this);





        super.onViewCreated(view, savedInstanceState);
    }



    @Override
    public void onClick(View view) {
        if(view.getId()==ib_custom||view.getId()==tv_custom){
            SpUtil.putString(getActivity(),"account","customLogin");
            WaitDialog.newInstance().show(getActivity().getSupportFragmentManager(),"WAIT");
            getDialog().cancel();
        }
        if(view.getId()==ib_email||view.getId()==tv_email){
            LoginDialog.newInstance().show(getActivity().getSupportFragmentManager(),"LOGIN");
            getDialog().cancel();
        }

    }
}
