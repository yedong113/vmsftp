package com.kmlc.commontools

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import scala.collection.mutable

class VmsDayFiles {
   private val dayfiles = new mutable.HashMap[String,mutable.HashMap[String,String]]()

   def getNowDate:String={
      val now:Date = new Date()
      val  dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
      val hehe = dateFormat.format( now )
      hehe
   }

   def getYesterday:String= {
      val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
      val cal: Calendar = Calendar.getInstance()
      cal.add(Calendar.DATE, -1)
      val yesterday = dateFormat.format(cal.getTime())
      yesterday
   }

   def fileisdownload(fileName:String):Boolean = {
      val today=getNowDate
      val yesterday=getYesterday
      dayfiles.remove(yesterday)
      if(dayfiles.contains(today)){
         val mapfiles = dayfiles.getOrElse(today,null)
         if(mapfiles.contains(fileName)) return true else return false
      }
      false
   }


   def putfilename(fileName:String) = {
      val today=getNowDate
      if(!dayfiles.contains(today)) dayfiles.put(today,new mutable.HashMap[String,String]())
      dayfiles.getOrElse(today,null).put(fileName,fileName.substring(7,7+8))
   }


}