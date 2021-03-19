package org.example

import java.io.PrintWriter

import com.alibaba.fastjson.JSON
import okhttp3.{FormBody, OkHttpClient, Request, RequestBody}

abstract class api
case class apiItme(var moduleName:String,var apiName:String,var apiPath:String)

object UserCenterGetApi {

  val url = System.getProperty("user.dir")
  println(s"文件储存目录 ： $url")
  val outFiles = new PrintWriter(s"$url\\newApiTest.txt")
  def main(args: Array[String]): Unit = {
    val client = new OkHttpClient()
    val token = "5B7448F2FCF24CB791C0FDBBF1FB20BC"
    val url = "http://192.168.10.54:9080/apimon/api/fdm/api/list"
    val formBody = new FormBody.Builder().add("params", "{  \"moduId\" : \"MODU201901210000000001\",  \"withRelate\" : true,  \"pageSize\" : -1 }").add("TOKEN", token).build()
    val request = new Request.Builder().method("POST", formBody).url(url).build()
    val response = client.newCall(request).execute
    val json = JSON.parseObject(response.body.string)
    val dataJson = JSON.parseObject(json.get("data").toString).get("data")
    val dataArry = JSON.parseArray(dataJson.toString)

    val mao = "\""
    dataArry.forEach(item => {
      val itemObj = JSON.parseObject(item.toString)
      val itemKey = itemObj.keySet()
      val ite = itemKey.iterator()
      while (ite.hasNext) {
        val instance = ite.next()
        val value = itemObj.get(instance).toString
        println(s"$instance = $value")
        if(itemObj.get("apiAvlstatus").toString.equals("Y")){
          instance match {
            //          case "moduName" => outFiles.println(s"Feature: 2002-$value")
            case  "apiName" =>outFiles.println(s"@get \n Scenario Outline: $value (${itemObj.get("apiProtocol").toString})")
            case "params" => {
              if (!(value=="[]")){
                outFiles.println(s"  Given JSON参数")
                JSON.parseArray(value).forEach(paramItem =>{
                  val paramJson = JSON.parseObject(paramItem.toString)
                  val paramKey =  paramJson.keySet()
                  val itr = paramKey.iterator()
                  while (itr.hasNext){
                    val name = itr.next()
                    val vlu = paramJson.get(name).toString
                    name match{
                      case "paramCode" => outFiles.println(s"  |$vlu |${paramJson.get("paramName").toString} | ")
                      case _ =>
                    }
                  }
                })
              }
            }
            case "apiPath" =>
              outFiles.println(s"  When I send a GET request to $mao$value$mao ")
              outFiles.println(s"  Then the response status should be ${mao}200$mao")
              outFiles.println(s"  Then the JSON response $mao<jsonPath>$mao equals $mao<value>$mao")
              outFiles.println(s"  Examples: \n | jsonPath     | value             | \n | result       | 000000             |")
              outFiles.println("\n")
            case _ =>
          }
        }
      }
    })
  }
}
