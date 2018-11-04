package com.kmlc.services

import java.text.SimpleDateFormat
import java.util.Date

import org.springframework.beans.factory.annotation.{Configurable, Value}
import org.springframework.scheduling.annotation.{EnableScheduling, Scheduled}
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration


@Component
@Configurable
@Configuration
@EnableScheduling
class SchedledConfiguration {

   private val logger = LoggerFactory.getLogger(classOf[SchedledConfiguration])

   @Value("${kmlc.ftpinfo.host}")
   private val host:String = null
   @Value("${kmlc.ftpinfo.port}")
   private val port:Int = 0
   @Value("${kmlc.ftpinfo.user}")
   private val user:String = null
   @Value("${kmlc.ftpinfo.pass}")
   private val pass:String = null
   @Value("${kmlc.ftpinfo.path1}")
   private val ftppath:String = null
   @Value("${kmlc.localfilepath.path1}")
   private val localpath1:String = null
   @Value("${kmlc.readfile}")
   private val readfilepath:String = null
   @Value("${kmlc.ftpinfo.prefix}")
   private val listprefixs:String= null




   @Scheduled(initialDelay = 3*1000, fixedRate = 1000)
   def reportCurrentByCron = {
      val listprefix:List[String] = listprefixs.split(",").toList
      val downloadFile:DownloadFile = new DownloadFile(host,port,user,pass,localpath1,ftppath,readfilepath,listprefix)
      ReadFileHis.init(readfilepath)
      downloadFile.getftpfiles
   }


   def dateFormat:SimpleDateFormat = {
      new SimpleDateFormat("HH:mm:ss")
   }

}
