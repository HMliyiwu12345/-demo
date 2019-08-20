package com.zerogame.fragmentdialog.bean;

public class GooglePay {

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
        private String pay_order_number;

        private String share_object;

        public void setPay_order_number(String pay_order_number){
            this.pay_order_number = pay_order_number;
        }
        public String getPay_order_number(){
            return this.pay_order_number;
        }
        public void setShare_object(String share_object){
            this.share_object = share_object;
        }
        public String getShare_object(){
            return this.share_object;
        }

    }
}
