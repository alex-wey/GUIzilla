package guizilla.sol.client

import java.net.ServerSocket
import java.net.UnknownHostException
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import guizilla.src.Page
import scala.collection.mutable.HashMap
import java.net.URLDecoder
import guizilla.sol.pages.Application
import guizilla.sol.pages.FirstLast
import guizilla.sol.pages.Horoscope
import guizilla.sol.pages.Search
import java.lang.NoSuchMethodException

/**
 * Class that represents a server.
 */
class Server {

  // assigns the port
  private val Port = 8080

  // session ID map
  private val session: HashMap[Int, Page] = new HashMap()

  /**
    * listener
    * Method that listens for incoming connections to a socket on port 8080
    */
  @throws(classOf[IOException])
  private def listener() {
    val server_socket = new ServerSocket(Port)
    while (true) {
      val socket = server_socket.accept
      try {
        val iStream = socket.getInputStream
        val oStream = socket.getOutputStream
        val reader = new BufferedReader(new InputStreamReader(iStream))
        val writer = new BufferedWriter(new OutputStreamWriter(oStream))
        var request: String = ""
        var client_output = reader.readLine()
        while (client_output.trim() != "") {
          request = request + client_output
          client_output = reader.readLine
        }
        request = request + "\r\n" + client_output
        client_output = reader.readLine
        request = request + "\r\n" + client_output
        val relevant: Option[List[String]] = checkRequest(request)
        var response = ""
        try {
          val out = dispatcher(relevant)
          response = "HTTP/1.0 200 OK\r\nServer: Guiserver/1.0\r\n" +
                     "Connection: Close\r\nContent-Type: text/html \r\n\r\n" + out
        } catch {
          case nsm: NoSuchMethodException =>
            val out = "<html><body><p>Server error: " +
              "Invalid class name in URL - NoSuchMethodException</p></body></html>"
            response = "HTTP/1.0 404 Not Found\r\nServer: Guiserver/1.0\r\n" +
              "Connection: Close\r\nContent-Type: text/html \r\n\r\n" + out
          case cnf: ClassNotFoundException =>
            val out = "<html><body><p>Server error: " +
              "Invalid class name in URL - ClassNotFoundException</p></body></html>"
            response = "HTTP/1.0 400 Bad Request\r\nServer: Guiserver/1.0\r\n" +
              "Connection: Close\r\nContent-Type: text/html \r\n\r\n" + out
          case e : Exception =>
            val out = "<html><body><p>Server error: " +
              "Failed to process page</p></body></html>"
            response = "HTTP/1.0 500 Internal Server Error\r\nServer: Guiserver/1.0\r\n" +
              "Connection: Close\r\nContent-Type: text/html \r\n\r\n" + out
        }
        writer.write(response)
        writer.flush()
        socket.shutdownOutput()
      } finally {
        socket.close()
      }
    }
  }

  /**
   * dispatcher
   * Method that takes in the relevant elements of a request, invokes a method
   * (either one from the path or a default), and produces a page to be sent
   * back to the client
   *
   * @return String of HTML that represents the page to be sent back to the client
   */
  private def dispatcher(relevant: Option[List[String]]): String = {
    relevant match {
      case Some(details) =>
        var inputs: Map[String, String] = Map[String, String]()
        val className: String = details(1)
        val method: String = details(2)
        if (details(0).equals("POST")) {
          val data: String = details(3)
          val dataSplit = data.split('&')
          for (i <- 0 to dataSplit.length - 1) {
            val inputSplit = dataSplit(i).split('=')
            val keyDec = URLDecoder.decode(inputSplit(0), "UTF-8")
            var valDec = ""
            try {
              valDec = URLDecoder.decode(inputSplit(1), "UTF-8")
            } catch {
              case iob: IndexOutOfBoundsException =>
            }
            inputs = inputs + (keyDec -> valDec)
          }
        }
        getID(className) match {
          case Some(id) =>
            try {
              val id1 = id.toInt
              if (session.contains(id1)) {
                val page: Page = session.get(id1).get
                val copy: Page = page.clone()
                val id2: Int = generateID()
                session += (id2 -> copy)
                val classMethod = copy.getClass().getMethod(
                  method, classOf[Map[String, String]], classOf[String])
                val output = classMethod.invoke(
                  copy, inputs, id2.toString).asInstanceOf[String]
                output
              } else {
                "<html><body><p>Invalid ID in URL</p></body></html>"
              }
            } catch {
              case nfe: NumberFormatException =>
                "<html><body><p>Unknown host</p></body></html>"
            }
          case None =>
            val id: Int = generateID()
            val getClass = Class.forName("guizilla.sol.pages." + className)
            val classInst = getClass.newInstance().asInstanceOf[Page]
            session += id -> classInst
            val classMethod = classInst.getClass().getMethod(method, classOf[Map[String, String]], classOf[String])
            val output = classMethod.invoke(classInst, inputs, id.toString()).asInstanceOf[String]
            output
        }
      case None => "<html><body><p>Server error: Invalid Request</p></body></html>"
    }
  }

  /**
    * generateID
    * Method that generates a uniqe ID
    *
    * @return Int that represents a unique ID
    */
  private def generateID(): Int = {
    val r = scala.util.Random
    var id: Int = r.nextInt(1000000000)
    if (session.contains(id)) {
      id = r.nextInt(1000000000)
    }
    id
  }

  /**
    * getID
    * Method that checks if a session ID exists
    *
    * @return Some(ID) if the session ID exists, None otherwise
    */
  private def getID(host: String): Option[String] = {
    val split = host.split(":")
    if (split(0).equals("id")) Some(split(1)) else None
  }

  /**
    * checkRequest
    * Method that checks whether the request is a GET, POST, or and invalid one
    *
    * @param request - the request from the client
    * @return Some(List[String]) that represents the relevant elements of a GET
    *         or POST request, None otherwise
    */
  private def checkRequest(request: String): Option[List[String]] = {
    try {
      var toReturn: List[String] = List()
      val split = request.split("\r\n")
      val firstLineSplit = split(0).split("\\s+")
      val func = firstLineSplit(0)
      val path = firstLineSplit(1)
      val pathSplit = path.split('/')
      val hostIDName = pathSplit(1)
      var pathName = "defaultHandler"
      try {
        pathName = pathSplit(2)
      } catch {
        case iob : IndexOutOfBoundsException =>
      }
      toReturn = pathName :: toReturn
      toReturn = hostIDName :: toReturn
      toReturn = func :: toReturn
      if (func.equals("POST")) {
        val formData = split(split.length - 1)
        toReturn = toReturn ++ List(formData)
        Some(toReturn)
      } else {
        Some(toReturn)
      }
    } catch {
      case e: Exception => None
    }
  }
}

/**
  * Object of the Server class that calls methods that run the server.
  */
object Server extends App {
  val listener = new Server
  listener.listener
}