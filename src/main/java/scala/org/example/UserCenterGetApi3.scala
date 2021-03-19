package org.example

import java.io.{File, PrintWriter}

import com.alibaba.fastjson.JSON
import okhttp3.{FormBody, OkHttpClient, Request, RequestBody}


object UserCenterGetApi3 {

  val filePath = System.getProperty("user.dir") +File.separator + "features/"
  println(s"文件储存目录 ： $filePath")

  def main(args: Array[String]): Unit = {
    val client = new OkHttpClient()
    val token = "24DC91D1C2D04FC58EFB8EA720BB3F37"
    val url = "http://apimoncheck.niceloo.com:9090/apimon/api/fdm/api/list"
    val formBody = new FormBody.Builder().add("params", "{  \"moduId\" : \"MODU202002290000000016\",  \"withRelate\" : true,  \"pageSize\" : -1 }").add("TOKEN", token).build()
    val request = new Request.Builder().method("POST", formBody).url(url).build()
    val response = client.newCall(request).execute
    val json = JSON.parseObject(response.body.string)
    val dataJson = JSON.parseObject(json.get("data").toString).get("data")
    val dataArry = JSON.parseArray(dataJson.toString)
    val mao = "\""
    dataArry.forEach(item => {
      val itemObj = JSON.parseObject(item.toString)
      val itemKey = itemObj.keySet()
      if (itemObj.get("apiAvlstatus").toString.equals("Y")) {
        val outFiles = new PrintWriter(filePath+itemObj.get("apiProtocol") + ".feature")
        outFiles.println(s"Feature: ${itemObj.get("moduName")} (${itemObj.get("apiProtocol")})")
        itemKey.forEach(key => {
          val value = itemObj.get(key)
          println(s"$key -> $value")
          key match {
            case "apiName" => {
              outFiles.println(s"${itemObj.get("moduName")} API \n \n Scenario Outline: $value (${itemObj.get("apiProtocol").toString})")
              outFiles.println(s"\t Given API ${mao}/api/${itemObj.get("apiPath")}${mao}")
              outFiles.println("\t And CookieX")
              outFiles.println("\t | TOKEN | ${token} |")
            }
            case "params" => {
              if (!(value == "[]")) {
                outFiles.println("\t And Param")
                outFiles.println("\t \"\"\"")
                JSON.parseArray(value.toString).forEach(paramItem => {
                  val paramJson = JSON.parseObject(paramItem.toString)
                  val paramKey = paramJson.keySet()
                  val itr = paramKey.iterator()
                  while (itr.hasNext) {
                    val name = itr.next()
                    val vlu = paramJson.get(name).toString
                    name match {
                      case "paramCode" => {
                        outFiles.print(s"\t $vlu : ${paramJson.get("paramName").toString} ")
                        var memo = paramJson.get("paramMemo").toString;
                        if(!memo.isEmpty){
                          outFiles.print(s"\t 备注 : ${memo} ")
                        }
                        if(paramJson.get("paramIsrequire").toString.equals("Y")){
                          outFiles.print("\t Isrequire \n")
                        }else{
                          outFiles.print("\n")
                        }
                      }
                      case _ =>
                    }
                  }
                })
                outFiles.println("\t \"\"\"")
              }
            }
            case _ =>
          }
        })
        outFiles.println("\t When GET")
        outFiles.println("\t Then STATUS \"200\"")
        outFiles.println("\t Then JSONPATH_ASSERT \"<jsonPath>\" equals \"<value>\"")
        outFiles.println("\t Then JSONPATH_GET_MONGO")
        outFiles.println("\t | data.data[0].docId | docId |")
        outFiles.println("\t# Then DB_ASSERT_X")
        outFiles.println("\t# | DcProject.projectId | PROJECT20191126010000000213 |")
        outFiles.println("\t Examples:")
        outFiles.println("\t | jsonPath | value  |")
        outFiles.println("\t | result   | 000000 |")
        outFiles.close()
      }
    })
  }
}
