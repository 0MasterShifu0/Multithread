package com.mon.project.model.welcomeDBDemo.dao;

import com.mon.project.model.welcomeDBDemo.vo.Operator;
import org.springframework.stereotype.Repository;

@Repository
public interface WelcomeDBDao {

    public int findWelcomeDB();

    public Operator findOperator();
}
