package com.kmlc.services

import java.io.{File, FileWriter}

import com.kmlc.commontools.{FtpClient, VmsDayFiles}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.io.Source

object ReadFileHis{
   var filePath=""
   val dayfiles = new VmsDayFiles
   var file:FileWriter=null

   def init(filePath:String) = {
      if(file==null){
         try{

            val bufferdsource = Source.fromFile(filePath)
            for(line <- bufferdsource.getLines){
               dayfiles.putfilename(line)
            }
            bufferdsource.close()
            val f = new File(filePath)
            file = new FileWriter(f,true)
         }
         catch {
            case ex:Exception =>
               println(ex.getMessage)
         }
      }
   }

   def fileisdownload(filename:String):Boolean = dayfiles.fileisdownload(filename)

   def put(filename:String) = {
      dayfiles.putfilename(filename)
      file.write(filename+"\n")
      file.flush()
   }
}

class DownloadFile(host:String,port:Int,user:String,pass:String,localpath1:String,ftppath:String,readfilepath:String,listprefix:List[String]) {
   private val log = LoggerFactory.getLogger(classOf[DownloadFile])

   val dayfiles = new VmsDayFiles

   val ftpClient = FtpClient(host, port, user, pass)
   val file = new FileWriter("d:\\aaa.txt",true)

   override def toString: String = {
      val sb = new StringBuilder
      sb.append("\r\nhost=").append(host).append("\r\n")
      sb.append("user=").append(user).append("\r\n")
      sb.append("pass=").append(pass).append("\r\n")
      sb.append("port=").append(port.toString()).append("\r\n")
      sb.append("localpath1=").append(localpath1).append("\r\n")
      sb.append("readfilepath=").append(readfilepath).append("\r\n")
      sb.toString()
   }



   def listftpfile (ftppath: String):List[String]= {
      val  x = new ListBuffer[String]()
      for(item <- listprefix){
         val listfile=ftpClient.list(ftppath,item)
         for (filename <- listfile){
            if(!ReadFileHis.fileisdownload(filename)) x+=filename
         }
      }
      x.toList
   }

   def getftpfiles = {
      ftpClient.login
      val rootDirectory = ftpClient.currentWorkingDirectory
      getftpfile(ftppath,localpath1)
      ftpClient.changeWorkingDirectory(rootDirectory)
      ftpClient.closeClient
   }

   def getftpfile(ftppath: String, localpath: String) = {
      val listFile = listftpfile(ftppath)
      for(filename <- listFile){
         val destfilename=localpath+"\\"+filename
         ftpClient.download(ftppath,filename,destfilename)
         log.info("下载文件:"+ftppath+"\\"+filename+" 到 " + destfilename)

         ReadFileHis.put(filename)
      }
   }

}
