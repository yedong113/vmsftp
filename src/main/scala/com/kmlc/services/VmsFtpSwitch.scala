package com.kmlc.services

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class AppConfig {

}

object VmsFtpSwitch extends App{
   SpringApplication.run(classOf[AppConfig])
}
