package com.zerogame.fragmentdialog.fragmentdialog;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class FindPswDialog extends BaseDialog implements View.OnClickListener {
    private int ic_back, et_account, b_sure, tv_send, et_password;
    private EditText et_mail, et_psw;
    private Button b_sure1;

    public static FindPswDialog newInstance() {
        FindPswDialog fragment = new FindPswDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_findpsw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        //取消对话框
        ic_back = getResources().getIdentifier("ib_back", "id", getActivity().getPackageName());
        ImageButton ib_back = view.findViewById(ic_back);
        ib_back.setOnClickListener(this);
        //发送验证码
        tv_send = getResources().getIdentifier("tv_go_login", "id", getActivity().getPackageName());
        view.findViewById(tv_send).setOnClickListener(this);
        //绑定邮箱控件和验证码输入框控件
        et_account = getResources().getIdentifier("et_mail", "id", getActivity().getPackageName());
        et_mail = view.findViewById(et_account);
        et_password = getResources().getIdentifier("et_password", "id", getActivity().getPackageName());
        et_psw = view.findViewById(et_password);
        et_psw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_psw.getText().length() != 0 && !TextUtils.isEmpty(et_mail.getText())) {
                    b_sure1.setBackgroundColor(Color.parseColor("#1E90FF"));
                    b_sure1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new NewPswDialog(et_mail.getText().toString(),
                                    et_psw.getText().toString()).show(getActivity().getSupportFragmentManager(), "NEW");
                            getDialog().cancel();


                        }
                    });

                } else {
                    b_sure1.setOnClickListener(null);
                    b_sure1.setBackgroundColor(Color.parseColor("#FFD5D6D6"));

                }
            }
        });

        //确定按钮
        b_sure = getResources().getIdentifier("b_sure", "id", getActivity().getPackageName());
        b_sure1 = view.findViewById(b_sure);


        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == ic_back) {
            getDialog().cancel();
            MainDialog.newInstance(getActivity(), "").show(getActivity().getSupportFragmentManager(), "MAIN");
        }
        if (view.getId() == tv_send) {
            String email = et_mail.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                if (email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                    HashMap<String, String> maps = new HashMap<String, String>();
                    maps.put("email", email);
                    maps.put("type", "1");
                    new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/send_email",
                            null, null, maps), new HttpUtil.Enqueue() {
                        @Override
                        public void success(String s) {
                            if (!TextUtils.isEmpty(s)) {
                                Gson gson = new Gson();
                                Root sendCode = gson.fromJson(s, Root.class);
                                if (sendCode.getStatus() == 0) {
                                    ToastUtil.showToast(getActivity(), "验证码发送成功");
                                } else if (sendCode.getStatus() == 1 && sendCode.getMsg().equals("LANG_EMAIL_NOTBOUND")) {
                                    ToastUtil.showToast(getActivity(), "这个邮箱还没有注册");
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
                ToastUtil.showToast(getActivity(), "邮箱不能为空");
            }
        }

    }
}
