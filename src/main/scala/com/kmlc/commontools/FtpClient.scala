package com.kmlc.commontools
import java.io.{FileInputStream, FileOutputStream}
import org.apache.commons.net.ftp.{FTP, FTPClient}

/**
  *
  * @param host
  * @param port
  * @param user
  * @param pass
  */
class FtpClient(host:String,port:Int,user:String,pass:String) {
   val ftpClient = new FTPClient

   /**
     * 登陆FTP服务器
     *
     * @return
     */
   def login: Boolean = {
      try {
         ftpClient.connect(host, port)
         ftpClient.login(user, pass)
         ftpClient.enterLocalPassiveMode()
         ftpClient.setBufferSize(10240)
         ftpClient.setControlEncoding("utf8")
         ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
         true
      }
      catch {
         case ex: Exception => {
            println("Ftp Client Login Error: " + ex.getMessage)
            false
         }
      }
   }

   /**
     * 上传文件到ftp服务器
     *
     * @param directory   上传至ftp的路径名不包括ftp地址
     * @param srcFileName 要上传的文件全路径名
     * @param destName    上传至ftp后存储的文件名
     * @return
     */
   def upload(directory: String, srcFileName: String, destName: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         val fis = new FileInputStream(srcFileName)
         val result = ftpClient.storeFile(destName, fis)
         result
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }

   /*
  String directory, String destFileName, String downloadName
  * */

   /**
     * 下载文件
     *
     * @param directory    要下载的文件所在ftp的路径名不包含ftp地址
     * @param destFileName 要下载的文件名
     * @param downloadName 下载后所存储的文件名全路径
     * @return
     */
   def download(directory: String, destFileName: String, downloadName: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         val out=new FileOutputStream(downloadName)
         ftpClient.retrieveFile(destFileName, out)
         out.close()
         true
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }

   /**
     * 重命名文件
     *
     * @param directory   要重命名的文件所在ftp的路径名不包含ftp地址
     * @param oldFileName 要重命名的文件名
     * @param newFileName 重命名后的文件名
     * @return
     */
   def rename(directory: String, oldFileName: String, newFileName: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         ftpClient.rename(oldFileName, newFileName) //重命名远程文件
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }

   /**
     * 删除文件
     *
     * @param directory 要删除的文件所在ftp的路径名不包含ftp地址
     * @param fileName  要删除的文件名
     * @return
     */
   def remove(directory: String, fileName: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         ftpClient.deleteFile(fileName) //删除远程文件
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }

   /**
     * 创建文件夹
     *
     * @param directory    要创建的目录所在ftp的路径名不包含ftp地址
     * @param newDirectory 要创建的目录名称
     * @return
     */
   def makeDirecotory(directory: String, newDirectory: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         ftpClient.makeDirectory(newDirectory) //创建新目录
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }


   /**
     * 重命名FTP指定目录下的目录
     *
     * @param directory    要重命名的目录所在ftp的路径名不包含ftp地址
     * @param oldDirectory 要重命名的旧目录名
     * @param newDirectory 重命名后的新目录
     * @return
     */
   def renameDirecotory(directory: String, oldDirectory: String, newDirectory: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         ftpClient.rename(oldDirectory, newDirectory) //重命名目录
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }


   def currentWorkingDirectory :String = {
      try{
         val workingpath = ftpClient.printWorkingDirectory()
         workingpath
      }
      catch {
         case ex:Exception =>{
            //ex.printStackTrace()
            throw ex
         }
      }
   }



   def changeWorkingDirectory(directory: String) = {
      try{
         ftpClient.changeWorkingDirectory(directory)
      }
      catch {
         case ex:Exception =>{
            throw ex
         }
      }
   }

   def list(directory: String,filename:String):List[String] ={
      try {
         ftpClient.changeWorkingDirectory(directory)
         val result = ftpClient.listNames(filename)
         result.toList
      }
      catch {
         case ex: Exception =>
         {
            //ex.printStackTrace()
            throw ex
         }
      }
   }



   /**
     * 列出FTP uwuqi指定目录下的所有文件,包括文件夹
     *
     * @param directory
     * @return
     */
   def list(directory: String): List[String] = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         val result = ftpClient.listNames
         result.toList
      }
      catch {
         case ex: Exception =>
         {
            throw ex
         }
      }
   }

   /**
     * 删除FTP服务商的目录
     *
     * @param directory    要重命名的目录所在ftp的路径名不包含ftp地址
     * @param deldirectory 要删除的目录名
     * @return
     */
   def removeDirecotory(directory: String, deldirectory: String): Boolean = {
      try {
         ftpClient.changeWorkingDirectory(directory)
         val result = ftpClient.removeDirectory(deldirectory) //删除目录
         result
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }


   def closeClient: Boolean = {
      try {
         ftpClient.disconnect()
         true
      }
      catch {
         case ex: Exception => {
            throw ex
         }
      }
   }

}


/*
object FtpClient extends App {
  val host="192.168.128.135"
  val user="uftp"
  val pass="uftp"
  val srcFile="e:\\te_illegalvehicle.txt"
  val destName = "te_illegalvehicle.txt"
  val ftpClient = new FTPClient
  ftpClient.connect(host,21)

  val fis = new FileInputStream(srcFile)
  ftpClient.login(user,pass)
  ftpClient.enterLocalPassiveMode()
  ftpClient.setBufferSize(10240)
  ftpClient.setControlEncoding("utf8")
  ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
  val result = ftpClient.storeFile(destName,fis)

  val ftpClient = new FtpClient("192.168.128.135",21,"uftp","uftp")
  ftpClient.login
  val listFile = ftpClient.list(".")
  listFile.foreach(println)
}
*/


object FtpClient {
   def apply(host: String, port: Int, user: String, pass: String) = {
      new FtpClient(host, port, user, pass)
   }
}
