package com.example.fragmentdialog.fragmentdialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fragmentdialog.R;
import com.example.fragmentdialog.util.SpUtil;

public class BoundDialog extends BaseDialog {

    private int user_id,cancle,bound_mail,change_admin;
    private static BoundDialog fragment;
    private String id,login;


    public static BoundDialog newInstance(String id,String login) {
        if(fragment==null){
        fragment = new BoundDialog();
        Bundle bundle = new Bundle();
        bundle.putString("login",login);
        bundle.putString("id", id);
        fragment.setArguments(bundle);
        return fragment;
        }else{
            return fragment;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_bound,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        //显示userid
        user_id=getResources().getIdentifier("tv_id","id",getActivity().getPackageName());
        TextView tv_user_id=view.findViewById(user_id);
        savedInstanceState=getArguments();
        if(!TextUtils.isEmpty(savedInstanceState.getString("id","null")))
        tv_user_id.setText(savedInstanceState.getString("id","null"));
        //取消对话框
        cancle=getResources().getIdentifier("ib_cancle","id",getActivity().getPackageName());
        ImageButton ib_cancle=view.findViewById(cancle);
        ib_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        //绑定邮箱
        bound_mail=getResources().getIdentifier("b_connect_admin","id",getActivity().getPackageName());
        Button b_connect_admin=view.findViewById(bound_mail);
        id=savedInstanceState.getString("id","");
        login=savedInstanceState.getString("login","");
        b_connect_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BoundEmailDialog.newInstance(id,login)
                        .show(getActivity().getSupportFragmentManager(),"EMAILBOUND");
                getDialog().dismiss();
            }
        });
        //切换账号
        change_admin=getResources().getIdentifier("b_change_admin","id",getActivity().getPackageName());
        Button b_change_admin=view.findViewById(change_admin);
        b_change_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpUtil.putString(getContext(),"account",null);
                SpUtil.putString(getActivity(),"user_id","");
                MainDialog.newInstance(getActivity(), "").show(getActivity().getSupportFragmentManager(), "MAIN");
                getDialog().dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
