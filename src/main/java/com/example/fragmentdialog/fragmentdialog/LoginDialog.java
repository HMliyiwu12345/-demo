package com.example.fragmentdialog.fragmentdialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.fragmentdialog.bean.EmailAdmin;
import com.example.fragmentdialog.util.SpUtil;
import com.example.fragmentdialog.util.WYLog;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;

import java.util.HashMap;

public class LoginDialog extends BaseDialog implements View.OnClickListener {

    private int ic_back, ic_sure, ic_account, ic_password,tv_findpsw,tv_register;
    private EditText et_account, et_password;


    public static LoginDialog newInstance() {
        LoginDialog fragment = new LoginDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.layout_mail_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //取消对话框
        ic_back = getResources().getIdentifier("ib_back", "id", getActivity().getPackageName());
        ImageButton ib_back = view.findViewById(ic_back);
        ib_back.setOnClickListener(this);
        //登录邮箱
        ic_sure = getResources().getIdentifier("b_sure", "id", getActivity().getPackageName());
        Button ib_sure = view.findViewById(ic_sure);
        ib_sure.setOnClickListener(this);
        //获取账号密码控件
        ic_account = getResources().getIdentifier("et_mail", "id", getActivity().getPackageName());
        ic_password = getResources().getIdentifier("et_password", "id", getActivity().getPackageName());
        et_account = view.findViewById(ic_account);
        et_password = view.findViewById(ic_password);
        //找回密码
        tv_findpsw=getResources().getIdentifier("tv_findpsw", "id", getActivity().getPackageName());
        view.findViewById(tv_findpsw).setOnClickListener(this);

        //注册邮箱
        tv_register=getResources().getIdentifier("tv_go_login", "id", getActivity().getPackageName());
        view.findViewById(tv_register).setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == ic_back) {
            getDialog().cancel();
            MainDialog.newInstance(getActivity(),"").show(getActivity().getSupportFragmentManager(),"MAIN");
        }
        if (view.getId() == ic_sure) {
            String account = et_account.getText().toString().trim();
            String password = et_password.getText().toString().trim();
            loginEmail(account,password);
        }
        if(view.getId()==tv_findpsw){
            findPassword();
        }if(view.getId()==tv_register){
            register();
        }
    }

    /**
     * 邮箱注册账号
     */
    private void register() {
        getDialog().cancel();
        RegisterDialog.newInstance().show(getActivity().getSupportFragmentManager(),"REGISTER");
    }

    /**
     * 找回密码
     */
    private void findPassword() {
        getDialog().cancel();
        FindPswDialog findPswDialog=FindPswDialog.newInstance();
        findPswDialog.show(getActivity().getSupportFragmentManager(),"FIND");

    }

    /*
    邮箱登录
     */
    public void loginEmail(final String account, final String password) {

        if (!TextUtils.isEmpty(account) || !TextUtils.isEmpty(password)) {
            if (account.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {


                HashMap<String, String> maps = new HashMap<String, String>();
                maps.put("account", account);
                maps.put("password", password);
                new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/user_login", null, null, maps), new HttpUtil.Enqueue() {
                    @Override
                    public void success(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
                            EmailAdmin emailAdmin = gson.fromJson(s, EmailAdmin.class);
                            if (emailAdmin.getStatus() == 0) {
                                SpUtil.putString(getActivity(),"account",account);
                                SpUtil.putString(getActivity(),"password",password);
                                ToastUtil.showToast(getActivity(), "邮箱登录成功");
                                SpUtil.putString(getActivity(),"user_id",emailAdmin.getDatas().getUser_id());
                                String account=emailAdmin.getDatas().getAccount();
                                String id=emailAdmin.getDatas().getUser_id();
                                String login=emailAdmin.getDatas().getLogin_token();
                                EmailDialog.getInstance(id,login,account).show(getActivity().getSupportFragmentManager(),"EMAIL");
                                getDialog().cancel();
                            } else if (emailAdmin.getStatus() == 1 && emailAdmin.getMsg().equals("LANG_WRONG_PWD")) {
                                ToastUtil.showToast(getActivity(), "密码错误");
                            } else if (emailAdmin.getStatus() == 1 && emailAdmin.getMsg().equals("LANG_NO_SUCH_ACCOUNT")) {
                                ToastUtil.showToast(getActivity(), "没有这个账号");
                            }
                        }
                    }

                    @Override
                    public void fail(Exception e) {
                        WYLog.e( e.getMessage());
                    }
                });
            } else {
                ToastUtil.showToast(getActivity(), "邮箱格式不对");
            }
        } else {
            ToastUtil.showToast(getActivity(), "账号或者密码不能为空");
        }

    }


}
