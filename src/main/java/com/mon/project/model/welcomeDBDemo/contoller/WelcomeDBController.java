package com.mon.project.model.welcomeDBDemo.contoller;

import com.mon.project.model.welcomeDBDemo.dao.WelcomeDBDao;
import com.mon.project.model.welcomeDBDemo.vo.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcomeDB")
public class WelcomeDBController {
    @Autowired
    WelcomeDBDao welcomeDBDao;

    @RequestMapping(value = "/getWelcomeDB",method = RequestMethod.GET)
    public int getWelcomeDB(){
        return welcomeDBDao.findWelcomeDB();
    }

    @RequestMapping(value = "/findOperator",method = RequestMethod.GET)
    public Operator findOperator(){
        return welcomeDBDao.findOperator();
    }



}
