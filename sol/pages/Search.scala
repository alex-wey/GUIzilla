package guizilla.sol.pages

import guizilla.src.Page
import java.net.Socket
import java.io.InputStream
import java.io.OutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.IOException
import scala.collection.mutable.HashMap

/**
  * Class that represents the Search page.
  */
class Search extends Page {

  // pages map that stores results returned by Query server
  val pages: HashMap[Int, String] = new HashMap[Int, String]()

  override def defaultHandler(inputs: Map[String, String], sessionID: String): String = {
    search(inputs, sessionID)
  }

  /**
    * runSearch
    * Method that sends and receives data once connected to the Query server
    * 
    * @param query - a free text query
    * @return String that represents up to the top 10 results
    */
  private def runSearch(query: String): String = {
    try {
      val socket = new Socket("eckert", 8081)
      val iStream = socket.getInputStream
      val oStream = socket.getOutputStream
      val reader = new BufferedReader(new InputStreamReader(iStream))
      val writer = new BufferedWriter(new OutputStreamWriter(oStream))
      writer.write(s"REQUEST\t$query\n")
      writer.flush()
      socket.shutdownOutput()
      val response = reader.readLine()
      socket.close()
      response
    } catch {
      case io: IOException =>
        "<html><body><p>Error communication with Query server</p></body></html>"
    }
  }

  /**
    * findPage
    * Method that sends and receives data once connected to the Page server
    * 
    * @param page - the title of a requested page in the wiki
    * @return String that represents the text of the article itself
    */
  private def findPage(page: String): String = {
    try {
      val socket = new Socket("eckert", 8082)
      val iStream = socket.getInputStream
      val oStream = socket.getOutputStream
      val reader = new BufferedReader(new InputStreamReader(iStream))
      val writer = new BufferedWriter(new OutputStreamWriter(oStream))
      writer.write(page + '\n')
      writer.flush()
      socket.shutdownOutput()
      var response = ""
      var server_output = reader.readLine()
      while (server_output != null) {
        response = response + '\n' + server_output 
        server_output = reader.readLine()
      }
      socket.close()
      s"<html><body><p>$response</p></body></html>"
    } catch {
      case io: IOException =>
        "<html><body><p>Error communication with Page server</p></body></html>"
    }
  }

  /**
    * search
    * Method that prompts the Search home page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML to be rendered on home page
    */
  def search(inputs: Map[String, String], sessionID: String): String = {
    "<html><body><p>Welcome to Search!</p>" +
      "<form method=\"post\" action=\"/id:" + sessionID + "/select\">" +
      "<p>Please enter a free text query:</p>" +
      "<input type=\"text\" name=\"query\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"
  }

  /**
    * select
    * Method that selects a requested page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from a page 
    */
  def select(inputs: Map[String, String], sessionID: String): String = {
    val response = runSearch(inputs.get("query").get)
    val open = "<html><body><p>Select a page: "
    var links: List[String] = List()
    val split = response.split("""\t""")
    try {
      for (i <- 1 to 10) {
        val linkSplit = split(i).split(" ")
        var toReturn : String = ""
        for(i<- 1 to linkSplit.length - 1) {
          toReturn = toReturn + " " + linkSplit(i)
        }
        toReturn = toReturn.drop(1)
        pages += (linkSplit(0).toInt -> toReturn)
        links = toReturn :: links
      }
    } catch {
      case iob: IndexOutOfBoundsException =>
    }
    val linkReverse = links.reverse
    var linksPrint: String = "\n"
    var x: Integer = 1
    for (link <- linkReverse) {
      val html = s"""<a href="/id:$sessionID/findPage$x">$link</a>""" + '\n'
      linksPrint = linksPrint + html
      x += 1
    }
    open + linksPrint + "</p></body></html>"
  }

  /**
    * findPage1
    * Method that retrieves the text from the first page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the first page 
    */
  def findPage1(inputs: Map[String, String], sessionID: String): String = findPageHelper(1)
  
  /**
    * findPage2
    * Method that retrieves the text from the second page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the second page 
    */
  def findPage2(inputs: Map[String, String], sessionID: String): String = findPageHelper(2)
  
  /**
    * findPage3
    * Method that retrieves the text from the third page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the third page 
    */
  def findPage3(inputs: Map[String, String], sessionID: String): String = findPageHelper(3)
  
  /**
    * findPage4
    * Method that retrieves the text from the fourth page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the fourth page 
    */
  def findPage4(inputs: Map[String, String], sessionID: String): String = findPageHelper(4)
  
  /**
    * findPage5
    * Method that retrieves the text from the fifth page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the fifth page 
    */
  def findPage5(inputs: Map[String, String], sessionID: String): String = findPageHelper(5)
  
  /**
    * findPage6
    * Method that retrieves the text from the sixth page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the sixth page 
    */
  def findPage6(inputs: Map[String, String], sessionID: String): String = findPageHelper(6)
  
  /**
    * findPage7
    * Method that retrieves the text from the seventh page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the seventh page 
    */
  def findPage7(inputs: Map[String, String], sessionID: String): String = findPageHelper(7)
  
  /**
    * findPage8
    * Method that retrieves the text from the eighth page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the eighth page 
    */
  def findPage8(inputs: Map[String, String], sessionID: String): String = findPageHelper(8)
  
  /**
    * findPage9
    * Method that retrieves the text from the ninth page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the ninth page 
    */
  def findPage9(inputs: Map[String, String], sessionID: String): String = findPageHelper(9)
  
  /**
    * findPage10
    * Method that retrieves the text from the tenth page
    * 
    * @param inputs - inputs map containing form field names to form field responses
    * @param sessionID - the session ID
    * @return String of HTML that represents the text from the tenth page 
    */
  def findPage10(inputs: Map[String, String], sessionID: String): String = findPageHelper(10)

  /**
    * findPageHelper
    * Method that retrieves the text from a requested page
    * 
    * @param i - integer that corresponds to a page
    * @return String of HTML that represents the text from the requested page 
    */
  private def findPageHelper(i: Int): String = {
    val page = pages.get(i)
    page match {
      case Some(title) => findPage(title)
      case None        => ""
    }
  }
}