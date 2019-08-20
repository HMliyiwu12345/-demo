package com.zerogame.fragmentdialog.bean;

public class EmailLogin {

    private int status;

    private String msg;

    private Datas datas;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setDatas(Datas datas){
        this.datas = datas;
    }
    public Datas getDatas(){
        return this.datas;
    }


    public class Datas {
        private int user_id;

        private String sys_id;

        private String sub_user_id;

        public void setUser_id(int user_id){
            this.user_id = user_id;
        }
        public int getUser_id(){
            return this.user_id;
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

    }
}
