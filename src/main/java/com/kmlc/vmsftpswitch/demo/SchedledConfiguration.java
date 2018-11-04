package com.kmlc.vmsftpswitch.demo;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;



@Component
@Configurable
@EnableScheduling
public class SchedledConfiguration {

    //每1分钟执行一次
    @Scheduled(cron = "0 */1 *  * * * ")
    public void reportCurrentByCron(){
        System.out.println ("Scheduling Tasks Examples By Cron: The time is now " + dateFormat ().format (new Date()));
    }

    private SimpleDateFormat dateFormat(){
        return new SimpleDateFormat("HH:mm:ss");
    }


}
