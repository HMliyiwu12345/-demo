package com.zerogame.fragmentdialog.fragmentdialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.zerogame.fragmentdialog.bean.Root;
import com.zerogame.fragmentdialog.util.WYLog;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;
import com.zerogame.sdktest.R;

import java.util.HashMap;

public class NewPswDialog extends BaseDialog {
    private String email,password;
    private int psw,new_psw,sure,cancle;
    private EditText et_psw,et_new_psw;
    private Button b_sure;
    private ImageButton ib_cancle;



    public NewPswDialog(String email,String password){
        this.email=email;
        this.password=password;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.layout_new_psw,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //取消对话框
        cancle=getResources().getIdentifier("ic_back", "id", getActivity().getPackageName());
        ib_cancle=view.findViewById(cancle);
        ib_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
                LoginDialog.newInstance().show(getActivity().getSupportFragmentManager(),"LOGIN");
            }
        });

        psw=getResources().getIdentifier("et_mail", "id", getActivity().getPackageName());
        et_psw=view.findViewById(psw);
        new_psw=getResources().getIdentifier("et_password", "id", getActivity().getPackageName());
        et_new_psw=view.findViewById(new_psw);
        sure=getResources().getIdentifier("b_sure", "id", getActivity().getPackageName());
        b_sure=view.findViewById(sure);
        b_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwor=et_psw.getText().toString().trim();
                String new_password=et_new_psw.getText().toString().trim();
                if(!TextUtils.isEmpty(passwor)&&!TextUtils.isEmpty(new_password)){
                    if(passwor.equals(new_password)){
                        HashMap<String,String> maps=new HashMap<String,String>();
                        maps.put("email",email);
                        maps.put("code",password);
                        maps.put("password",passwor);
                        new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/forget_pwd_email", null, null, maps), new HttpUtil.Enqueue() {
                            @Override
                            public void success(String s) {
                                if(!TextUtils.isEmpty(s)){
                                   Gson gson=new Gson();
                                   Root newPsw=gson.fromJson(s, Root.class);
                                   if(newPsw.getStatus()==0){
                                       ToastUtil.showToast(getActivity(),"修改成功");
                                       getDialog().cancel();
                                       LoginDialog.newInstance().show(getActivity().getSupportFragmentManager(),"LOGIN");
                                   }
                                }
                            }

                            @Override
                            public void fail(Exception e) {
                                WYLog.e(e.getMessage());
                            }
                        });
                    }else{
                        ToastUtil.showToast(getActivity(),"两次输入的密码不一致");
                    }
                }else{
                    ToastUtil.showToast(getActivity(),"新密码不能为空");
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


}
