package com.kmlc.services

import java.io.{File, FileWriter}
import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import com.kmlc.commontools.{FtpClient, VmsDayFiles}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Configurable, Value}
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.{Component, Service}

import scala.io.Source
import org.quartz._
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.QuartzJobBean
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import scala.collection.mutable.ListBuffer


object ListFilesRead {
   val dayfiles = new VmsDayFiles
   var file: FileWriter = null

   def init(filePath: String) = {
      try {

         val bufferdsource = Source.fromFile(filePath)
         for (line <- bufferdsource.getLines) {
            dayfiles.putfilename(line)
         }
         bufferdsource.close()
         val f = new File(filePath)
         file = new FileWriter(f, true)
      }
      catch {
         case ex: Exception =>
            println(ex.getMessage)
      }
   }

   def fileisdownload(filename: String): Boolean = dayfiles.fileisdownload(filename)

   def put(filename: String) = {
      dayfiles.putfilename(filename)
      file.write(filename + "\n")
      file.flush()
   }

}


trait VmsFileSwitchService{
   def downloadvmsfile
}



@Component
@Configurable
@Configuration
@Service
class VmsFileSwitchServiceImpl extends VmsFileSwitchService with CommandLineRunner{

   private val logger = LoggerFactory.getLogger(classOf[VmsFileSwitchServiceImpl])
   @Value("${kmlc.ftpinfo.host}")
   private val host: String = null
   @Value("${kmlc.ftpinfo.port}")
   private val port: Int = 0
   @Value("${kmlc.ftpinfo.user}")
   private val user: String = null
   @Value("${kmlc.ftpinfo.pass}")
   private val pass: String = null
   @Value("${kmlc.ftpinfo.path1}")
   private val ftppath: String = null
   @Value("${kmlc.localfilepath.path1}")
   private val localpath1: String = null
   @Value("${kmlc.readfile}")
   private val readfilepath: String = null
   @Value("${kmlc.ftpinfo.prefix}")
   private val listprefixs: String = null

   var ftpClient: FtpClient = null
   var loginStatus = false
   var listprefix: List[String] = null


   /**
     * 当容器加载完毕后启动 run
     *
     * @param args
     */
   override def run(args: String*): Unit = {
      ListFilesRead.init(readfilepath)
      ftpClient = new FtpClient(host, port, user, pass)
      loginStatus = ftpClient.login
      listprefix = listprefixs.split(",").toList
   }


   private def listftpfile(ftppath: String): List[String] = {
      val x = new ListBuffer[String]()
      for (item <- listprefix) {
         val listfile = ftpClient.list(ftppath, item)
         for (filename <- listfile) {
            if (!ListFilesRead.fileisdownload(filename)) x += filename
         }
      }
      x.toList
   }

   private def login: Boolean = {
      if (!loginStatus) {
         ftpClient = new FtpClient(host, port, user, pass)
         loginStatus=ftpClient.login
         logger.info("loginstatus="+loginStatus.toString)
      }
      loginStatus
   }
   private def logout:Boolean = {
      ftpClient.closeClient
      loginStatus=false
      true
   }


   def getUUID32:String = UUID.randomUUID().toString().replace("-","").toLowerCase()

   private
   def getftpfile(ftppath: String, localpath: String): Boolean = {
      try {
         val listFile = listftpfile(ftppath)
         for (filename <- listFile) {
            val destfilename = localpath + "\\" + filename
            val swapfile = localpath + "\\"+getUUID32 + ".swf"
            ftpClient.download(ftppath, filename, swapfile)
            new File(swapfile).renameTo(new File(destfilename))
            logger.info("下载文件:" + ftppath + "\\" + filename + " 到 " + destfilename)
            ListFilesRead.put(filename)
         }
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
      true
   }

   private def downloadvmsfileImpl ={
      val rootDirectory = ftpClient.currentWorkingDirectory
      getftpfile(ftppath,localpath1)
      ftpClient.changeWorkingDirectory(rootDirectory)
   }

   override def downloadvmsfile: Unit = {
      try{
         logger.info("Weather Data Sync Job logStatus="+loginStatus.toString)
         if(loginStatus) {
            downloadvmsfileImpl
         }else{
            login
         }
         if(loginStatus) downloadvmsfileImpl
      }
      catch {
         case ex:Exception =>{
            loginStatus=false
            logger.info(ex.getMessage)
            //ex.printStackTrace()
         }
      }
   }
}

@Component
@Configurable
@Configuration
class SchedledVmsDownload extends QuartzJobBean {

   @Autowired
   val vmsFileSwitchService:VmsFileSwitchService = null



   override def executeInternal(jobExecutionContext: JobExecutionContext): Unit = {
      vmsFileSwitchService.downloadvmsfile
   }
}


@Component
@Configurable
@Configuration
class QuartzConfiguration {
   // JobDetail

   @Value("${kmlc.intervalInSeconds}")
   val TIME: Int = 10

   @Bean
   def weatherDataSyncJobDetail: JobDetail = {
      JobBuilder.newJob(classOf[SchedledVmsDownload]).withIdentity("weatherDataSyncJob").storeDurably().build()
   }

   // Trigger
   @Bean
   def weatherDataSyncTrigger: Trigger = {
      val date = new Date()
      date.setTime(date.getTime()+5000)
      val schedBuilder = SimpleScheduleBuilder.simpleSchedule.withIntervalInSeconds(TIME).repeatForever
      TriggerBuilder.newTrigger.forJob(weatherDataSyncJobDetail).withIdentity("weatherDataSyncTrigger").withSchedule(schedBuilder).startAt(date).build
   }
}




