package com.sc.controller;


import com.sc.dao.UserDao;
import com.sc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr Qin on 2019/4/8.
 */
@Controller
/*@RequestMapping(value = "user")*/
public class UserController {
    @Autowired
    public UserDao userDao;

    @RequestMapping(value = "index")
    public String index(){
        return "/login";
    }
    @RequestMapping(value = "upd")
    public String upd(){return "/updatepwd";}
    @RequestMapping(value = "login")
    public String Login(@RequestParam String user_name, @RequestParam String password){
        User user=userDao.Login(user_name,password);
        if(user.getCount()==1){
            return "login";
        }else{
            return "Left";
        }

    }
  /* public String Login(Model model){
       model.
       return "login";
    }*/
    @RequestMapping(value = "regist")
    public String  Regist(@RequestParam String user_name,@RequestParam String password){
       /* User user=new User(user_id, user_name, password);*/
        User user = new User();
        user.setUser_name(user_name);
        user.setPassword(password);
        userDao.Regist(user);
        return "/login";
    }
    @RequestMapping(value = "delete")
    public void Deleteuser(@RequestParam Integer user_id){

        userDao.deleteUser(user_id);
    }
    @RequestMapping(value = "update")
    public void Updateuser(@RequestParam String user_name,@RequestParam String password){
        User user=new User();
        user.setUser_name(user_name);
        user.setPassword(password);
        userDao.updatePws(user);
    }
    @RequestMapping(value = "showAll")
    public String UserList(Model model){
        List<User> list = new ArrayList();
        list=userDao.showAll();
        //model.addAllAttributes("list",list);
        for (User user:list) {
            System.out.println(user);
        }
        model.addAttribute("list",list);
        System.out.println(model);
        return "/showuserlist";
    }
    @RequestMapping(value = "findById")
    public String findById(HttpServletResponse response, Integer user_id){
        User user= userDao.getUserById(user_id);
        System.out.println("0000");
        return "index";
    }
}
