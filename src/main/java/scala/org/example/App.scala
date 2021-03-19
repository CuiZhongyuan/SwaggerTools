package org.example

import java.io.PrintWriter

import org.jsoup.Jsoup

import scala.collection.mutable

object App {
  val  redMap = new mutable.HashMap[String,Int]()
  val  blueMap = new mutable.HashMap[String,Int]()
  def getPhaseBallElements(): mutable.LinkedHashSet[PhaseBall] ={
    // 开始期次
    val star = "03001"
    // 结束期次
    val end = "20080"
    val url =  s"https://datachart.500.com/ssq/history/newinc/history.php?start=$star&end=$end"
    val els  =Jsoup.connect(url).get().getElementsByClass("t_tr1")
    val iterator = els.iterator()
    val list:mutable.LinkedHashSet[PhaseBall] = mutable.LinkedHashSet()
    // 解析所有期次的双色球，生成 PhaseBall，并添加到list中
    while( iterator.hasNext ){
      val element = iterator.next()
      val phaseBallTest =  element.children()
      val phaseBallList =  phaseBallTest.get(1).text::
        phaseBallTest.get(2).text::
        phaseBallTest.get(3).text::
        phaseBallTest.get(4).text::
        phaseBallTest.get(5).text::
        phaseBallTest.get(6).text::
        Nil
      val phase = PhaseBall.apply(phaseBallTest.get(0).text,phaseBallList,phaseBallTest.get(7).text)
      list.addOne(phase)
    }
    list
  }
  /**
   * 解析list ，分析每个球出现的次数
   * @param list  [PhaseBall]
   */
  def paramPbElList(list: mutable.LinkedHashSet[PhaseBall]) ={
    var ii = ""
    var redCount,blueCount = 0
    for ( i <- 1 to 33 ){
      redCount = 0
      blueCount = 0
      if(i <10 ) {
        ii =  "0"+i.toString
      }else {
        ii = i.toString
      }
//     统计出现次数
    list.foreach(f =>{
      if(f.redBall.contains(ii)){
        redCount+=1
        redMap.addOne(ii,redCount)
      }
      if(f.blueBall == ii){
        blueCount+=1
        blueMap.addOne(ii,redCount)
      }
    })
  }
//  println(s"RedMap : ${redMap.toString()}")
//  println(s"BlueMap : ${blueMap.toString()}")
  }
  /**
   * 计算概率
   * @param m   Map[ 球号 ： 出现次数 ]
   * @param s   总次数
   * @param name red & blue
   */
  def turnOut(m:mutable.HashMap[String,Int],s:Double,name:String): Unit ={
    val url = System.getProperty("user.dir")
    println(s"文件储存目录 ： $url")
    val outFiles = new PrintWriter(s"$url\\$name.txt")
    val listMap =  mutable.HashMap[String,Double]()
    m.foreach(e => {
      val(k,v) = e
      val x = v/s
      listMap.addOne(k,x)
    })
    listMap.toList.sortBy(_._2).foreach(e => {
      val(k,v) = e
      outFiles.println(s"$name :$k ，出现的概率为 ： $v")
    })
    outFiles.println(s"统计批次共 : $s 个批次")
    outFiles.close()
  }
}
