package com.example.fragmentdialog.fragmentdialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fragmentdialog.R;
import com.example.fragmentdialog.bean.CustomData;
import com.example.fragmentdialog.bean.EmailAdmin;
import com.example.fragmentdialog.util.SpUtil;
import com.example.fragmentdialog.util.WYLog;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;

import java.util.HashMap;

public class WaitDialog extends BaseDialog {
    private int tv_main,user_id;
    private TextView tv_user_id;
    private Thread mThread;


    public static WaitDialog newInstance() {
        WaitDialog fragment = new WaitDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_wait, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final  View view, @Nullable Bundle savedInstanceState) {
        tv_main=getResources().getIdentifier("tv_main", "id", getActivity().getPackageName());
        view.findViewById(tv_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpUtil.putString(getContext(),"account",null);
                mThread.interrupt();
                MainDialog.newInstance(getActivity(),"").show(getActivity().getSupportFragmentManager(),"MAIN");
                SpUtil.putString(getActivity(),"user_id","");
                getDialog().dismiss();

            }
        });

        //user_id
        user_id=getResources().getIdentifier("tv_user_id", "id", getActivity().getPackageName());
        tv_user_id=view.findViewById(user_id);
        tv_user_id.setText(SpUtil.getString(getActivity(),"user_id",""));
        mThread = new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     Thread.sleep(2000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 } finally {
                     view.findViewById(tv_main).setOnClickListener(null);
                     if(!TextUtils.isEmpty(SpUtil.getString(getActivity(), "account", null))){
                     if (SpUtil.getString(getActivity(), "account", null).equals("customLogin")) {
                         customLogin();
                     } else {
                         String account = SpUtil.getString(getActivity(), "account", null);
                         String password = SpUtil.getString(getActivity(), "password", null);
                         mailLogin(account, password);
                     }
                     }
                 }
             }
         });
       mThread.start();


        super.onViewCreated(view, savedInstanceState);
    }


    //邮箱登录
    private void mailLogin(final String account, final String password) {

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
                        SpUtil.putString(getActivity(),"user_id",emailAdmin.getDatas().getUser_id());
                        ToastUtil.showToast(getActivity(), "邮箱登录成功");
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
//                WYLog.e(e.getMessage());
                ToastUtil.showToast(getActivity(), "网络好像出了点问题");

                MainDialog.newInstance(getActivity(), "").show(getActivity().getSupportFragmentManager(), "MAIN");
                getDialog().dismiss();

            }
        });
    }

    //游客登录
    private void customLogin() {
        new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/guest_access",
                null, null, null), new HttpUtil.Enqueue() {
            @Override
            public void success(String s) {
                if (!TextUtils.isEmpty(s)) {
                    Gson gson = new Gson();
                    CustomData customData = gson.fromJson(s, CustomData.class);
                    if (customData.getStatus() == 0 ) {
                        WYLog.d(customData.getMsg());
                        SpUtil.putString(getActivity(),"user_id",customData.getDatas().getUser_id());
                        ToastUtil.showToast(getActivity(), "登陆成功");
                        BoundDialog boundDialog = BoundDialog.newInstance(customData.getDatas().getUser_id(),customData.getDatas().getLogin_token());
                        boundDialog.show(getActivity().getSupportFragmentManager(),"BOUND");
                        getDialog().dismiss();
                    } else {
                        ToastUtil.showToast(getActivity(), "登录失败");

                        MainDialog.newInstance(getActivity(), "").show(getActivity().getSupportFragmentManager(), "MAIN");
                        getDialog().dismiss();
                    }
                }
            }

            @Override
            public void fail(Exception e) {
//                WYLog.e(e.getMessage());

                ToastUtil.showToast(getActivity(), "网络好像出了点问题");

                MainDialog.newInstance(getActivity(), "").show(getActivity().getSupportFragmentManager(), "MAIN");
                getDialog().dismiss();

            }
        });
    }


}
