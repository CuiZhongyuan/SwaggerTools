package org.example

import java.io.{File, PrintWriter}

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import okhttp3.{OkHttpClient, Request}
import org.yaml.snakeyaml.constructor.Tuple

import scala.collection.mutable

/**
 * swagger用例模板生成
 *
 *  参数看 AppTest
 */
object ympt {
  val filePath = System.getProperty("user.dir") + File.separator + "features/"
  println(s"文件储存目录 ： $filePath")

  def main(args: Array[String]): Unit = {
    val client = new OkHttpClient()
    val moudle = "hr"
    val url = s"http://47.97.154.153:8201/${moudle}/v2/api-docs"
    //    val formBody = new FormBody.Builder().add("params", "{  \"moduId\" : \"MODU202002290000000016\",  \"withRelate\" : true,  \"pageSize\" : -1 }").add("TOKEN", token).build()
    val request = new Request.Builder().get().url(url).build()
    val response = client.newCall(request).execute
    val json = JSON.parseObject(response.body.string)
    val dataJson = JSON.parseObject(json.get("paths").toString)
    val m = "\""
    var method = "get"
    dataJson.forEach((key, value) => {
      println("==============================文件开始=================================")

      println(s"api 请求路径${key}")
      val dataJsonValue = JSON.parseObject(value.toString)
      /**
       * 场景名称
       */
      var get = JSON.parseObject(dataJsonValue.getOrDefault("get","").toString)
      var post = JSON.parseObject(dataJsonValue.getOrDefault("post","").toString)
      var delete = JSON.parseObject(dataJsonValue.getOrDefault("delete","").toString)
      var put = JSON.parseObject(dataJsonValue.getOrDefault("put","").toString)
      var outFiles = out(get,post,delete,put)
      dataJsonValue.forEach((k, v) => {
        k match {
          case "get" => {
            println("get 方法")
            method = "get"
            val getObj = JSON.parseObject(v.toString)
            outFiles.println(s"Feature: ${ getObj.get("tags")}  \n")
            outFiles.println(s" Scenario Outline: ${getObj.get("summary").toString}")
            outFiles.println(s"   Given API ${m}/${moudle}${key}${m}")
            outFiles.println("   And HeadersX")
            outFiles.println("   | Authorization | ${Authorization} |")
            outFiles.println("   And Param ")
            getApiParse("get",getObj, json, getObj , outFiles )

          }
          case "post" => {
            println("post 方法")
            method = "post"
            val postObj = JSON.parseObject(v.toString)
            outFiles.println(s"Feature: ${postObj.get("tags").toString}  \n")
            outFiles.println(s" Scenario Outline: ${postObj.get("summary").toString} ")
            outFiles.println(s"   Given API ${m}/${moudle}${key}${m}")
            outFiles.println("   And HeadersX")
            outFiles.println("   | Authorization | ${Authorization} |")
            outFiles.println("   And Param")
            getApiParse("post",postObj, json, postObj ,outFiles)
          }
          case "delete" => {
            println("delete 方法")
            method = "delete"
            val deleteJson = JSON.parseObject(v.toString)
            outFiles.println(s"Feature: ${deleteJson.get("tags").toString} \n")
            outFiles.println(s" Scenario Outline: ${deleteJson.get("summary").toString}")
            outFiles.println(s"   Given API ${m}/${moudle}${key}${m}")
            outFiles.println("   And HeadersX")
            outFiles.println("   | Authorization | ${Authorization} |")
            outFiles.println("   And Param")
            getApiParse("delete",deleteJson, json, deleteJson ,outFiles)
          }
          case "put" => {
            println("put 方法")
            method = "put"
            val deleteJson = JSON.parseObject(v.toString)
            outFiles.println(s"Feature: ${deleteJson.get("tags").toString} \n")
            outFiles.println(s" Scenario Outline: ${deleteJson.get("summary").toString}")
            outFiles.println(s"   Given API ${m}/${moudle}${key}${m}")
            outFiles.println("   And HeadersX")
            outFiles.println("   | Authorization | ${Authorization} |")
            outFiles.println("   And Param")
            getApiParse("put",deleteJson, json, deleteJson ,outFiles)
          }
          case _ =>
        }
      })
      // 文件结束
      outFiles.println("   \"\"\" ")
      method match {
        case "get" =>  outFiles.println(s"   When GET");
        case "post" =>  outFiles.println(s"   When POST");
        case "delete" => outFiles.println("   When DELETE");
        case "put" => outFiles.println("   When PUT");
        case _ =>
      }
      outFiles.println("   Then STATUS \"200\"")
      outFiles.println("   Then JSONPATH_ASSERT \"<jsonPath>\" equals \"<value>\"")
      outFiles.println("   Examples:")
      outFiles.println("   | jsonPath | value  |")
      outFiles.println("   | code   | 0 |")
      outFiles.close()
    })
  }

  def getApiParse(method:String ,obj: JSONObject, allJson: JSONObject, testjson: JSONObject ,outFiles :PrintWriter): Unit = {
    method match {
      case "get" =>  outFiles.println("   \"\"\" ")
      case "post" =>  outFiles.println("   \"\"\" ")
      case "delete" => outFiles.println("   \"\"\" ")
      case "put" => outFiles.println("   \"\"\" ")
      case _ =>
    }
    obj.forEach((k, v) => {
      k match {
        case "summary" => {
          println("api name :" + v)
        }
        case "description" => {
          println("api description :" + v)
        }
        case "parameters" => {
          getApiParesArry(JSON.parseArray(v.toString), allJson ,outFiles)
        }
        case "originalRef" => {
          val oo = JSON.parseObject(allJson.get("definitions").toString).get(v.toString)
          print(s"get : -----------------------  ${oo}")
          if(oo!=null){
            val ol = JSON.parseObject(oo.toString)
            getApiParse(method,JSON.parseObject(oo.toString), allJson, ol ,outFiles)
          }
        }
        case "properties" => {
          var it = JSON.parseObject(v.toString)
          it.forEach((itK, itV) => {
            var itemVl = JSON.parseObject(itV.toString)
            var description = itemVl.getOrDefault("description", "未说明")
            var t = itemVl.get("type")
            /**
             * 判断是否必填
             */
            var required = JSON.parseArray(testjson.getOrDefault("required", "[]").toString)
            if (required.contains(itK)) {
              println(s"${itK} : ${description} , type = ${t} , isRequired ")
              outFiles.println( s"     ${itK} : ${description} , type = ${t} , isRequired ")
            } else {
              println(s"${itK} : ${description} , type = ${t} ")
              outFiles.println(s"     ${itK} : ${description} , type = ${t} ")
            }
          })
        }
        case _ =>
      }
    })
  }

  def getApiParesArry(obj: JSONArray, allJson: JSONObject ,outFiles :PrintWriter) = {
    obj.forEach(item => {
      val dataObj = JSON.parseObject(item.toString)
      dataObj.forEach((key, value) => {
        key match {
          case "schema" =>  getApiParse("method",JSON.parseObject(value.toString), allJson, JSON.parseObject(value.toString),outFiles )
          case "name" => {
            //            print(s"name = ${value} \n")
            if (dataObj.get("required").toString == "true") {
              outFiles.println(s"     ${value} : description=${dataObj.get("description")} , default =${dataObj.get("default")} , type = ${dataObj.get("type")} , in =${value}   isRequired" )
            } else {
              outFiles.println(s"     ${value} : description=${dataObj.get("description")} ,  default =${dataObj.get("default")} , type = ${dataObj.get("type")} , in =${value} ")
            }
          }
//            case "originalRef" => {
//              val oo = JSON.parseObject(allJson.get("definitions").toString).get(value.toString)
//              print(s"get : -----------------------  ${oo}")
//              val ol = JSON.parseObject(oo.toString)
//              getApiParse(_,JSON.parseObject(oo.toString), allJson, ol ,outFiles)
//            }

//          case "default" =>{
//            if(!value.toString.isEmpty){
//              print(s" ,default = ${value} ," )
//            }
//          }
//          case "required" => {
//            if (value == "true") {
//              print(" isRequired ")
//            }
//          }
//          case "in" => {
//            if(!value.equals("String")){
//              print(s" , in =${value} ," )
//            }
//          }
//          case "type" => {
//            print( s" ,type = ${value}" )
//          }
          case _ =>
        }
      })
    })
  }

  def out(get: JSONObject,post:JSONObject,delete:JSONObject,put:JSONObject): PrintWriter ={
    if(get != null){
      new PrintWriter(filePath+ strReplace(get.get("summary").toString) + ".feature")
    }else if(post != null){
       new PrintWriter(filePath+ strReplace(post.get("summary").toString) + ".feature")
    }else if(delete != null){
       new PrintWriter(filePath+ strReplace(delete.get("summary").toString) + ".feature")
    }else {
      new PrintWriter(filePath+ strReplace(put.get("summary").toString) + ".feature")
    }
  }

  def strReplace(str:String): String ={
    val dot = List("/","\"","[","]","<",">","?")
    var strTo = str
    dot.foreach( f =>{
      if(str.contains(f)){
        strTo = strReplace(str.replace(f,"").trim)
      }
    })
    strTo
  }

}
