package com.zerogame.fragmentdialog.bean;

public class CustomData {
    private int status;
    private String msg;
    private Datas datas;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setDatas(Datas datas) {
        this.datas = datas;
    }

    public Datas getDatas() {
        return datas;
    }

    public class Datas {
        private int is_new;

        private String user_id;

        private String login_token;

        private String sys_id;

        private String sub_user_id;

        private String sub_login_token;

        public void setIs_new(int is_new){
            this.is_new = is_new;
        }
        public int getIs_new(){
            return this.is_new;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setLogin_token(String login_token){
            this.login_token = login_token;
        }
        public String getLogin_token(){
            return this.login_token;
        }
        public void setSys_id(String sys_id){
            this.sys_id = sys_id;
        }
        public String getSys_id(){
            return this.sys_id;
        }
        public void setSub_user_id(String sub_user_id){
            this.sub_user_id = sub_user_id;
        }
        public String getSub_user_id(){
            return this.sub_user_id;
        }
        public void setSub_login_token(String sub_login_token){
            this.sub_login_token = sub_login_token;
        }
        public String getSub_login_token(){
            return this.sub_login_token;
        }

    }
}
