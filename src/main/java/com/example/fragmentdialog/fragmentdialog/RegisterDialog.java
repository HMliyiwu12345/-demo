package com.example.fragmentdialog.fragmentdialog;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fragmentdialog.R;
import com.example.fragmentdialog.bean.EmailAdmin;
import com.example.fragmentdialog.util.WYLog;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;

import java.util.HashMap;

public class RegisterDialog extends BaseDialog  implements View.OnClickListener {
    private int account,password,new_password,check,register,back;
    private EditText et_account,et_psw,et_new_psw;
    private CheckBox ck_check;
    private Button  b_register;


    public static RegisterDialog newInstance() {
        RegisterDialog fragment = new RegisterDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.layout_register,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        account=getResources().getIdentifier("et_mail", "id", getActivity().getPackageName());
        password=getResources().getIdentifier("et_again_password", "id", getActivity().getPackageName());
        new_password=getResources().getIdentifier("et_password", "id", getActivity().getPackageName());
        check=getResources().getIdentifier("cb_sure", "id", getActivity().getPackageName());
        register=getResources().getIdentifier("b_sure", "id", getActivity().getPackageName());
        back=getResources().getIdentifier("ib_back", "id", getActivity().getPackageName());


        et_account=view.findViewById(account);
        et_psw=view.findViewById(password);
        et_new_psw=view.findViewById(new_password);
        ck_check=view.findViewById(check);
        b_register=view.findViewById(register);
        b_register.setOnClickListener(this);
        view.findViewById(back).setOnClickListener(this);
        ck_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    b_register.setBackgroundColor(Color.parseColor("#1E90FF"));
                    b_register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            register();
                        }
                    });
                }else{
                    b_register.setBackgroundColor(Color.parseColor("#FFD5D6D6"));
                    b_register.setOnClickListener(null);
                }
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void register() {
        String account =et_account.getText().toString();
        String password=et_psw.getText().toString();
        String again_password=et_new_psw.getText().toString();
        if(!TextUtils.isEmpty(account)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(again_password)){
            if(account.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
                if(password.equals(again_password)){
                    HashMap<String,String> maps=new HashMap<String,String>();
                    new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/email_register2", null, null, maps), new HttpUtil.Enqueue() {
                        @Override
                        public void success(String s) {
                            if(!TextUtils.isEmpty(s)){
                                Gson gson=new Gson();
                                EmailAdmin emailAdmin = gson.fromJson(s, EmailAdmin.class);
                                if(emailAdmin.getStatus()==0){
                                    ToastUtil.showToast(getActivity(),"注册成功");
                                    LoginDialog.newInstance().show(getActivity().getSupportFragmentManager(),"LOGIN");
                                }else if(emailAdmin.getStatus()==1&&emailAdmin.getMsg().equals("LANG_INVALID_EMAIL")){
                                    ToastUtil.showToast(getActivity(),"账号已经存在");
                                }
                            }
                        }

                        @Override
                        public void fail(Exception e) {
                            WYLog.e(e.getMessage());
                        }
                    });
                }else{
                    ToastUtil.showToast(getActivity(),"两者密码不一致");
                }
            }else{
                ToastUtil.showToast(getActivity(),"邮箱格式不对");
            }
        }else{
            ToastUtil.showToast(getActivity(),"账号密码不能为空");
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==register){
            register();
        }
        if(view.getId()==back){
            getDialog().cancel();
            LoginDialog.newInstance().show(getActivity().getSupportFragmentManager(),"LOGIN");
        }
    }
}
