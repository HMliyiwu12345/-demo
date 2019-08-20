package com.zerogame.fragmentdialog.fragmentdialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.zerogame.fragmentdialog.bean.Root;
import com.example.httpclient.conf.Constant;
import com.example.httpclient.util.HttpUtil;
import com.example.httpclient.util.ToastUtil;
import com.google.gson.Gson;
import com.zerogame.sdktest.R;

import java.util.HashMap;

public class AlterDialog extends BaseDialog implements View.OnClickListener {

    private int back,sure,newPsw,oldPsw;
    private EditText et_new_psw;
    private EditText et_old_psw;
    private String userId;
    private String login;

    public static AlterDialog getInstance(String email,String code){
        AlterDialog alterDialog=new AlterDialog();
        Bundle bundle=new Bundle();
        bundle.putString("email",email);
        bundle.putString("code",code);
        alterDialog.setArguments(bundle);
        return alterDialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.layout_alter_psw,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        savedInstanceState=getArguments();
        userId = savedInstanceState.getString("email");
        login = savedInstanceState.getString("code");
        back=getResources().getIdentifier("ib_back","id",getActivity().getPackageName());

        sure=getResources().getIdentifier("b_sure","id",getActivity().getPackageName());

        newPsw=getResources().getIdentifier("et_mail","id",getActivity().getPackageName());

        oldPsw=getResources().getIdentifier("et_again_password","id",getActivity().getPackageName());
        view.findViewById(sure).setOnClickListener(this);
        view.findViewById(back).setOnClickListener(this);
        et_new_psw = view.findViewById(newPsw);
        et_old_psw = view.findViewById(oldPsw);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==back){
            EmailDialog.getInstance("","","").show(getActivity().getSupportFragmentManager(),"EMAIL");
            getDialog().dismiss();
        }
        if(view.getId()==sure){
            String account=et_new_psw.getText().toString();
            String password=et_old_psw.getText().toString();
            if(!TextUtils.isEmpty(account)&&!TextUtils.isEmpty(password)){
                if(account.equals(password)){
                    HashMap<String,String> maps=new HashMap<String,String>();
                    maps.put("old_password",account);
                    maps.put("new_password",password);

                    new HttpUtil().getHttp(Constant.getURL(getActivity(), "User/change_password", userId, login, maps), new HttpUtil.Enqueue() {
                        @Override
                        public void success(String s) {
                            Gson gson=new Gson();
                            Root root = gson.fromJson(s, Root.class);
                            if(root.getStatus()==0){
                                ToastUtil.showToast(getActivity(),"密码修改成功");
                                EmailDialog.getInstance("","","").show(getActivity().getSupportFragmentManager(),"EMAIL");
                                getDialog().dismiss();
                            }else{
                                ToastUtil.showToast(getActivity(),"密码修改错误");
                            }
                        }

                        @Override
                        public void fail(Exception e) {

                        }
                    });
                }else{
                    ToastUtil.showToast(getActivity(),"两次输入密码不一致");
                }


            }else {
                ToastUtil.showToast(getActivity(),"两次密码不能为空");
            }



        }

    }
}
