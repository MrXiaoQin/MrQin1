package com.sc.entity;

/**
 * Created by Mr Qin on 2019/4/24.
 */
public class User extends BaseDTO{
    private Integer user_id;
    private String user_name;
    private String password;

    public User(){
        super();
    }
    public User(Integer user_id,String user_name, String password) {
        super();
        this.user_id=user_id;
        this.user_name = user_name;
        this.password = password;
    }


    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
