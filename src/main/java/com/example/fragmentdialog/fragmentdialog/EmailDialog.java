package com.example.fragmentdialog.fragmentdialog;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fragmentdialog.R;

public class EmailDialog extends BaseDialog implements View.OnClickListener {
    private int accout,id,cancle,alterPsw;
    private String userId;
    private String login;
    private String account;
    private static EmailDialog emailDialog;

    public static EmailDialog getInstance(String id,String login,String account){
        if(emailDialog==null){
            Bundle bundle= new Bundle();
            bundle.putString("id",id);
            bundle.putString("login",login);
            bundle.putString("account",account);
            emailDialog = new EmailDialog();
            emailDialog.setArguments(bundle);
        }

        return emailDialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_email_login,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        savedInstanceState=getArguments();
        userId = savedInstanceState.getString("id","111");
        login = savedInstanceState.getString("login","login");
        account = savedInstanceState.getString("account","account");
        accout=getResources().getIdentifier("tv_test","id",getActivity().getPackageName());
        TextView tv_account=view.findViewById(accout);
        tv_account.setText(account);
        id=getResources().getIdentifier("tv_id","id",getActivity().getPackageName());
        TextView tv_id=view.findViewById(id);
        tv_id.setText(userId);

        tv_account.setText(account);
        cancle=getResources().getIdentifier("ib_cancle","id",getActivity().getPackageName());
        view.findViewById(cancle).setOnClickListener(this);
        alterPsw=getResources().getIdentifier("b_change_psw","id",getActivity().getPackageName());
        view.findViewById(alterPsw).setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==cancle){
            getDialog().dismiss();
        }
        if(view.getId()==alterPsw){
            alterPsw();
        }
    }

    private void alterPsw() {
        AlterDialog.getInstance(userId,login).show(getActivity().getSupportFragmentManager(),"ALTER");
        getDialog().dismiss();
    }
}
