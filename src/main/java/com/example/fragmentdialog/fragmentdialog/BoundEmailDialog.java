package com.example.fragmentdialog.fragmentdialog;

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

import com.example.fragmentdialog.R;
import com.example.fragmentdialog.bean.Root;
import com.example.fragmentdialog.util.WYLog;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;

import java.util.HashMap;

public class BoundEmailDialog extends BaseDialog {

    private int back,account,password,sure,sengCode;
    private EditText et_password;
    private EditText et_account;
    private String id,login;

    public static BoundEmailDialog newInstance(String id,String login) {
        BoundEmailDialog fragment = new BoundEmailDialog();
        Bundle bundle=new Bundle();
        bundle.putString("id",id);
        bundle.putString("login",login);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_bound_email,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        //取消对话框
        back=getResources().getIdentifier("ib_back","id",getActivity().getPackageName());
        ImageButton ib_back=view.findViewById(back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoundDialog.newInstance("123","").show(getActivity().getSupportFragmentManager(),"BOUND");
                getDialog().dismiss();
            }
        });
        //邮箱密码框控件
        account=getResources().getIdentifier("et_mail","id",getActivity().getPackageName());
        password=getResources().getIdentifier("et_psw","id",getActivity().getPackageName());
        sure=getResources().getIdentifier("b_sure","id",getActivity().getPackageName());
        et_account = view.findViewById(account);
        et_password = view.findViewById(password);
        sengCode=getResources().getIdentifier("tv_send_code","id",getActivity().getPackageName());
        savedInstanceState=getArguments();

            id=savedInstanceState.getString("id","");
            login=savedInstanceState.getString("login","");

        view.findViewById(sengCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                WYLog.d("id="+id+"login="+login);

                String account=et_account.getText().toString();
                if(!TextUtils.isEmpty(account)){
                    if(account.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
                        HashMap<String,String> maps=new HashMap<>();
                        maps.put("email",account);
                        maps.put("type","2");
                        new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/send_email", id, login, maps), new HttpUtil.Enqueue() {
                            @Override
                            public void success(String s) {
                                Gson gson=new Gson();
                                Root root = gson.fromJson(s, Root.class);
                                if(!TextUtils.isEmpty(s)){
                                    if(root.getStatus()==0){
                                        ToastUtil.showToast(getActivity(),"验证码发送成功");
                                    }else if(root.getStatus()==1&&root.getMsg().equals("LANG_EXIST_EMAIL")){
                                        ToastUtil.showToast(getActivity(),"该邮箱已经被绑定");
                                    }
                                }


                            }

                            @Override
                            public void fail(Exception e) {

                            }
                        });
                    }else{
                        ToastUtil.showToast(getActivity(),"邮箱格式不对");
                    }

                }else{
                    ToastUtil.showToast(getActivity(),"邮箱不能为空");
                }

            }
        });
        Button b_sure=view.findViewById(sure);
        b_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account=et_account.getText().toString();
                String password=et_password.getText().toString();
                if(!TextUtils.isEmpty(account)&&!TextUtils.isEmpty(password)){
                    if(account.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
                        HashMap<String,String> maps=new HashMap<String,String>();
                        maps.put("email",account);
                        maps.put("password",password);
                        new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/email_bind", id, login, maps), new HttpUtil.Enqueue() {
                            @Override
                            public void success(String s) {
                                Gson gson=new Gson();
                                if(!TextUtils.isEmpty(s)){
                                    Root root = gson.fromJson(s, Root.class);
                                    if(root.getStatus()==0){
                                        ToastUtil.showToast(getActivity(),"绑定成功");
                                        getDialog().dismiss();
                                    }else if(root.getStatus()==1){
                                        WYLog.d(s);
                                        ToastUtil.showToast(getActivity(),"绑定失败");
                                    }
                                }else{
                                    ToastUtil.showToast(getActivity(),"数据获取失败");
                                }


                            }

                            @Override
                            public void fail(Exception e) {

                            }
                        });
                    }else{
                        ToastUtil.showToast(getActivity(),"邮箱格式不对");
                    }
                }else{
                    ToastUtil.showToast(getActivity(),"邮箱或者验证码不能为空");
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
