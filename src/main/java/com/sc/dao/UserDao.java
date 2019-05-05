package com.sc.dao;

import com.sc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Created by Mr Qin on 2019/4/8.
 */
@Component
public class UserDao {
    @Autowired
    private BaseDao baseDao;
    //登录方法
    public User Login(String user_name, String password) {

        String sql = "select * from User where user_name=? and password=?";
        List<User> list = baseDao.query(sql, User.class, new Object[]{user_name, password});
        return list.isEmpty() ? null : list.get(0);
    }
    public User getUserById(Integer user_id){
        String sql ="select * from User where user_id";
        return  baseDao.queryById(sql,User.class,user_id);//query(sql,User.class,user_id);
    }
    public User getUserById(String  user_name){
        String sql ="select * from User where user_name";
        return  baseDao.queryById(sql,User.class,user_name);//query(sql,User.class,user_id);
    }

    //注册方法
    public int Regist(User user) {
        String sql = "insert into User(user_name,password) values ( :user_name,:password) ";
        return baseDao.insert(sql, user);
    }

    //修改密码
    public int updatePws(User user) {
        String sql = "update User set password=:password, user_name =:user_name";
        return baseDao.update(sql, user);
    }

    //删除
    public int deleteUser(Integer id) {
        return baseDao.deleteById("User", id);
    }
    //所有
    public List showAll(){
        String sql="select * from User";
        return baseDao.query(sql,User.class, new Object[]{});
        // return baseDao.query(sql,User.class，new Object[]{});
    }
}
