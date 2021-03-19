package org.example





import java.io.{File, PrintWriter}

import org.example.ympt.filePath
import org.junit.jupiter.api._

import scala.io.Source

class AppTest  {
  @Test
  def testGetPhaseBall(): Unit = {
    val testList = App.getPhaseBallElements()
    App.paramPbElList(testList)
    App.turnOut(App.redMap, testList.size, "RedBall")
    App.turnOut(App.blueMap, testList.size, "BlueBall")
  }


  /**
   *
   * 一下为 学习代码
   */
  @Test
  def testBigInt(): Unit = {
    println(BigInt.apply(2).pow(1024))
  }

  @Test
  def testString(): Unit = {
    var h = "HELLO"
    println(h.head)
    println(h.last)
    println(10.max(9))
    var s4 = "12.5"
    println(s4.toDouble.toInt)
  }

  @Test
  def testA(): Unit = {
    val n1 = 4
    val n2 = 5
    val n6 = 7
    println(n1 min n2 min n6)
  }

  @Test
  def testB(): Unit = {
    val n = 400
    if ((n % 4 == 0 && n % 100 != 0) || n % 400 == 0) {
      println(true)
    } else println(false)
  }


  @Test
  def testC(): Unit = {
    //   猴子吃桃算法 。 10天剩1个 ，前9天 每天吃一半 +1个
    def taoZ(i: Int): Int = {
      if (i == 10) {
        1
      } else {
        (taoZ(i + 1) + 1) * 2
      }
    }

    println(taoZ(1))
  }

  @Test
  def testGold() {
    def test(count: Int) {
      for (i <- 1 to count; if (i == 1 || i % 2 == 1)) {
        p(i, count)
        println
      }
    }

    def p(c: Int, k: Int) {
      val ii = (k - c) / 2
      for (i <- 1 to ii) {
        print(" ")
      }
      for (i <- 1 to c) {
        print("*")
      }
    }

    test(90)
  }

  @Test
  def testMath() {
    for (i <- 1 to 9) {
      for (j <- 1 to i) {
        print(s" $j * $i = ${i * j}\t")
      }
      println
    }
  }

  @Test
  def testIm(): Unit = {

    implicit def f(f: Any): String = {
      f.toString
    }

    val d: String = 123
    val d1: String = 123.33D
    val d2: String = 123.33F
    val d3: String = 'd'
    val nin = new Array[Int](3)
    println(nin.hashCode())
    nin(0) = 456
    for (i <- nin) {
      printf(i)
    }
    println(nin.hashCode())
  }

  @Test
  def testDim(): Unit = {
    var arr = Array.ofDim[Int](3, 4)

    for (item <- arr) {
      for (i <- item) {
        print(i + "\t")
      }
      print("\n")
    }
  }


  @Test
  def testJavaList(): Unit = {
    val arr = List("1", "123", "123")
    print("1" :: "22" :: arr :: Nil)
  }

  @Test
  def testMap(): Unit = {
    val m = Map("test" -> 123, "aaa" -> 333)
    println(m.getOrElse("test1", "hahahah"))
    println(m.get("test").get)
    println(m - "test")
    println(m - "aaa")
    println("================================")
    m.foreach(e => {
      val (k, v) = e
      println(s" $k = $v")
    })
    println("================================")
    for ((k, v) <- m) {
      println(s" $k = $v")
    }
  }

  @Test
  def testList() {
    val list = List(1, 3, 4, 5, 6, 67, 4)

    def setNewInt(f: Int => Int, i: Int): Int = {
      f(i)
    }

    def newInt(i: Int): Int = {
      i + i
    }

    val pr = print _

  }

  /**
   * 化简   支持一个二元函数
   */
  @Test
  def testListReduce() {
    val list = List(1, 3, 4, 1, 5, 5, 6, 67, 4)
    //    def isBig(x: Int, y: Int): Int = {
    //      if (x < y) x else y
    //    }
    println(list.reduceLeft(_ + _))
    println(list.foldLeft(100)(_ - _))

    /**
     * 一下方法已废弃
     */
    println((100 /: list) (_ - _))
    println((100 /: list) (_ + _))
    println((list :\ 100) (_ + _))
    println((list :\ 100) (_ - _))
  }

  /**
   * 折叠  / 与扫描的却别，扫描会将所有中间结果置于一个结合中
   */

  import scala.collection.mutable

  @Test
  def testListScan() {
    val list = "AAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBCCCCCCCCCCCCCCCCCCC"

    def reMap(m: mutable.Map[Char, Int], s: Char) = {
      m.addOne(s -> (m.getOrElse(s, 0) + 1))
    }

    val map = mutable.Map[Char, Int]();
    list.foldLeft(map)(reMap)
    map.foreach(item => {
      val (k, v) = item
      println(s" in map , $k count  =  $v")
    })
  }

  @Test
  def testView(): Unit = {
    def isVis(i: Int): Boolean = {
      i.toString.equals(i.toString.reverse)
    }
    import scala.collection.parallel.CollectionConverters._
    //    val view = (1 to 1000000).view.filter(isVis)
    //    (view).foreach(print)
    var test = (1 to 1000000).par.map {
      case _ => Thread.currentThread().getName
    }.distinct
    print(test)
  }

  case class t(var name: String, var age: Int)

  @Test
  def testMath1(): Unit = {
    val n = t("a", 1)
    var n2 = n.copy()

    n match {
      case t(na, a) => println(s" name = $na & age = $a")
    }
    println(n.hashCode)
    println(n2.hashCode())
  }

  abstract class Item

  case class Book(name: String, price: Double) extends Item

  case class Bundle(des: String, dis: Double, item: Item*) extends Item

  @Test
  def testMatch(): Unit = {
    val sale = Bundle("书籍", 0.7, Book("一只特立独行的猪", 45.5), // 86  + 83.8
      Book("我所理解的生活", 40.5),
      Bundle("管理", 0.6, Book("第五项修炼", 50.9), Book("一分钟管理人", 32.9)))

    var list = mutable.ListBuffer[Double]()

    def paramSale1(s: Item): Double = {
      s match {
        case Book(n, p) =>
          println(s"$n = ￥$p")
          p
        case Bundle(dec, des, res@_*) => {
          var d = res.map(paramSale1).sum
          var ds = des * d
          println(s"$des  = $d 折后 = ${ds}")
          list.addOne((ds))
          ds
        }

      }
    }

    println(paramSale1(sale))
    println(list)
  }

  @Test
  def testPanhanshu(): Unit = {
    var list = mutable.ListBuffer(1, 23, 4, 5, 6, 6, "you name")
    var testFunction = new PartialFunction[Any, Int] {
      override def isDefinedAt(x: Any): Boolean = x.isInstanceOf[Int]

      override def apply(v1: Any): Int = v1.asInstanceOf[Int] + 1
    }
    //   println(list.collect(testFunction))
    /**
     * 简化1
     *
     * @return
     */
    def f1: PartialFunction[Any, Int] = {
      case i: Int => i + 1
    }

    println("简化1")
    println(list.collect(f1))

    /**
     * 简化2
     *
     * @return
     */
    println("简化2")
    println(list.collect({
      case i: Int => i + 2 + 2
      case s: String => s + " is pig"
    }))

  }


  @Test
  def testHigherFunction(): Unit = {
    def her(f: Double => Double)(n: (Double, Int) => Double)(a: Double, b: Int): Int = {
      f(n(a, b)).asInstanceOf[Int]
    }

    def f(n: Double): Double = {
      n * 2
    }

    def n(a: Double, b: Int): Double = {
      a * b
    }

    println(her(f)(n)(3, 2))
  }

  @Test
  def testClosureDemo(): Unit = {
    /**
     * 第一种方式 返回函数
     *
     * @param suffix
     * @return
     */
    def setSuffix1(suffix: String) = {
      (fileName: String) => {
        if (fileName.endsWith(suffix)) fileName else fileName + suffix
      }
    }

    val suffix = ".jpg"
    //    val testFile = setSuffix(suffix)
    /**
     * 第二种方式 柯力化
     *
     * @param suffix
     * @param fileName
     * @return
     */
    def setSuffix(suffix: String)(fileName: String): String = {
      if (fileName.endsWith(suffix) || fileName.endsWith(suffix.toUpperCase)) fileName else fileName + suffix
    }

    val testSuffix = setSuffix(".jpg")(_)
    println(testSuffix("DOG"))
    println(testSuffix("PIG.JPG"))
  }


  /**
   * 抽象控制
   */
  @Test
  def abstractControl(): Unit = {

    /**
     * 抽象控制 + 柯力化 实现 循环
     * 抽象控制， 提供一个没有参数，没有返回值的 函数为参数
     *
     * @param is
     * @param block
     */
    def until(is: => Boolean)(block: => Unit): Unit = {
      if (is) {
        block
        until(is)(block)
      }
    }

    var x = 0
    until(x < 1000000)({
      x += 1
    })

  }

  case class pp(name: String, age: Int)

  @Test
  def testJson(): Unit = {
    var tet = Map("name" -> pp("test", 123))
    tet + ("tet" -> pp("xiaohong", 123))
    println(tet + ("tet" -> pp("xiaohong", 123)) + ("name" -> pp("test", 1234)))
  }


  //  @Test
  //  def tttttt() {
  //    val sex = List("男","女","未知");
  //    val op = List("填","不填");
  //    val local = List("公司","代理商","家","学校");
  //    val all =  List(sex,op,local);
  //
  //    all.flatMap(list=>{
  //      print(list)
  //      list :: list
  //    })
  //  }

  @Test
  def redText(): Unit = {
    var text = Source.fromFile("E:\\develop\\pairs\\1.txt").getLines().toList;
    val filePath = System.getProperty("user.dir") + File.separator + "features/"

    var head =  text.head.split("\t");
    var m = "\""
    /**
     * 阶段修改 module
     */
    var module = " Scenario Outline: 采购单录入\n   Given API \"/jxc/jxc/purchase/order/save\"\n   And HeadersX\n   | Authorization | ${Authorization} |\n   And Param|\n    ${text}\n   When POST\n   Then STATUS \"200\"\n   Then JSONPATH_ASSERT \"<jsonPath>\" equals \"<value>\"\n   Examples:\n   | jsonPath | value  |\n   | code   | 0 |"
    val out = new PrintWriter(filePath+"text.feature")
    text.foreach{ ch=>{
      var arr = ch.split("\t")
      var str = ""
      str =  str + "   \"\"\" \n"
      str =  str +"   { \n"
      for(i <- 1 to arr.length-2){
        str = str + s"    ${m}${head(i)}${m} : ${m}${arr(i)}${m}"
        if(i != arr.length-2){
          str =  str +","
          str =  str + "\n"
        }else{
          str =  str + "\n"
        }
      }
      str =  str +"   } \n"
      str =  str +"    \"\"\" "
      out.println(module.replace("${text}",str))
      out.println("")
    }
    }
    out.close()
  }
}
