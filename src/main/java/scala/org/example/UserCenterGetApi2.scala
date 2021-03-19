package org.example

import java.io.PrintWriter

import com.alibaba.fastjson.{JSON, JSONObject}
import okhttp3.{FormBody, OkHttpClient, Request, RequestBody}


object UserCenterGetApi2 {

  val url = System.getProperty("user.dir")
  println(s"文件储存目录 ： $url")
  val outFiles = new PrintWriter(s"$url\\newApiTest.txt")
  def main(args: Array[String]): Unit = {
    val client = new OkHttpClient()
    val token = "0FE0E3F86F4E46CCAB531A6AA0DB099E"
    val url = "http://apimoncheck.niceloo.com/apimon/api/fdm/api/list"
    var moduId = "MODU201901210000000001"
    var params = "{  \"moduId\" : \""+moduId+ "\",  \"withRelate\" : true,  \"pageSize\" : -1 }"
    val formBody = new FormBody.Builder().add("params", params).add("TOKEN", token).build()
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
            case "apiName" =>
              outFiles.println(s"场景大纲: $value (${itemObj.get("apiProtocol").toString})")
              outFiles.println(s"  当    发送 GET 请求 $mao/api/${itemObj.get("apiPath").toString}$mao ")
            case "params" => {
              if (!(value=="[]")){
                outFiles.println(s"  假如 Param INFO")
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
              outFiles.println(s"  那么  状态码 必须为 ${mao}200$mao")
              outFiles.println(s"  那么  JSON 断言 $mao<jsonPath>$mao equals $mao<value>$mao")
              outFiles.println(s"  例子: \n | jsonPath     | value             | \n | result       | 000000             |")
              outFiles.println("\n")
            }

            case _ =>
          }
        }
      }
    })

    outFiles.close()
  }


}
