package guizilla.sol.client

import javafx.event.ActionEvent
import guizilla.src.LexicalException
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.stage.Stage
import guizilla.src.HTMLTokenizer
import guizilla.src.parser.HTMLParser
import java.io.BufferedReader
import java.net.ConnectException
import guizilla.src.Page
import java.net.UnknownHostException
import java.io.IOException
import guizilla.src.parser.ParseException

/**
  * Class responsible for handling browser navigation.
  */
class GUIBrowser {

  @FXML protected var gPane: GridPane = null
  @FXML protected var urlBar: TextField = null
  @FXML var box: VBox = null

  // stage initialized to null
  private var stage: Stage = null
  // URL initialized to null
  private var urlText: String = null
  // current host initialized to null
  var currentHost: String = null
  // currentPage initialized to null
  var currentPage: List[HTMLElement] = null
  // cache of all pages visited
  var cache: List[List[HTMLElement]] = List()
  // cache of all path names
  var pathCache: List[String] = List()
  // cache of all host names, initialized with currentHost so that indices are set correctly
  var hostCache: List[String] = List(null)

  /**
    * Handles the pressing of the submit button on the main GUI page.
    */
  @FXML def handleQuitButtonAction(event: ActionEvent) {
    stage.close()
  }

  /**
    * Handles the pressing of the back button on the main GUI page.
    */
  @FXML def handleBackButtonAction(event: ActionEvent) {
    if (cache.size > 1) {
      cache = cache.drop(1)
    }
    val prevPage = cache(0)
    render(prevPage)

    try {
      hostCache = hostCache.drop(1)
      pathCache = pathCache.drop(1)
      urlText = pathCache(0)
      currentHost = hostCache(0)
      if (isNull(urlText)) {
        urlBar.setText("")
      } else if (isRelative(urlText)) {
        urlBar.setText("http://" + currentHost + urlText)
      } else {
        urlBar.setText(urlText)
      }
    } catch {
      case iob: IndexOutOfBoundsException =>
        currentHost = null
        
        urlText = null
        urlBar.setText(urlText)
    }
  }

  /**
    * isNull
    * Method that checks if there exists a path
    *
    * @param path - the path
    * @return Boolean true if path doesn't exist, false otherwise
    */
  private def isNull(path: String): Boolean = path.equals("")

  /**
    * isRelative
    * Method that checks if path is relative
    *
    * @param path - the path
    * return Boolean false if relative path can be parsed, true otherwise
    */
  private def isRelative(path: String): Boolean = {
    try {
      path.split("http://")(1)
      false
    } catch {
      case iob: IndexOutOfBoundsException => true
    }
  }

  /**
    * Handles submitting URL button action.
    */
  @FXML def handleSubmitButtonAction(event: ActionEvent) {
    try {
      urlText = urlBar.getText
      try {
        val hostName = getHost(urlText)
        val pathName = getPath(urlText)
        val elements = getPage("GET", hostName, pathName)
        currentHost = hostName
        currentPage = elements
        cache = elements :: cache
        pathCache = urlText :: pathCache
        hostCache = currentHost :: hostCache
        render(elements)
      } catch {
        case iob: IndexOutOfBoundsException =>
          try {
            urlText.split('/')(1)
            val elements = getPage("GET", currentHost, getPath(urlText))
            currentPage = elements
            cache = elements :: cache
            pathCache = urlText :: pathCache
            hostCache = currentHost :: hostCache
            render(elements)
          } catch {
            case iob : IndexOutOfBoundsException => 
              urlBar.setText("")   
          }
        case uhe: UnknownHostException =>
          val unkownHost: List[HTMLElement] = List(new PageText("Unknown host"))
          cache = unkownHost :: cache
          hostCache = currentHost :: hostCache
          pathCache = "" :: pathCache
          urlBar.setText("")
          render(unkownHost)
      }
    } catch {
      case nul: NullPointerException      =>
      case iob: IndexOutOfBoundsException =>
    }
  }

  /**
    * setStage
    * Method that sets the stage field of the controller to the given stage.
    *
    * @param stage - the stage
    */
  def setStage(stage: Stage) {
    this.stage = stage
  }

  /**
    * render
    * Method that renders element to console
    *
    * @param page - a list of HTML elements that represents a page
    */
  def render(page: List[HTMLElement]) {
    box.getChildren().clear()
    val actions =
      for (el <- page) {
        el.render(box, this)
      }
  }

  /**
    * getPage
    * Method that retrieves the page from the server after given a request
    *
    * @param func - distinguishes GET or POST request
    * @param host - the host name
    * @param path - the page
    * @return List[HTMLElement] that represents the page being requested
    */
  def getPage(func: String, host: String, path: String): List[HTMLElement] = {
    try {
      try {
        val client = new Client(host)
        val reader = client.run(func + " " + path + " HTTP/1.0\r\n " +
          "Connection: close\r\n User-Agent: Guizilla/1.0\r\n \r\n", path)
        val elements = getHTMLElementList(reader)
        updateURL(host, path)
        elements
      } catch {
        case con: ConnectException =>
          val client = new Client(currentHost)
          val reader = client.run(func + " " + path + " HTTP/1.0\r\n " +
            "Connection: close\r\n User-Agent: Guizilla/1.0\r\n \r\n", path)
          val elements = getHTMLElementList(reader)
          updateURL(currentHost, path)
          elements
      }
    } catch {
      case io: IOException     => List(PageText("Error Communicating With Server"))
      case mal: ParseException => List(PageText("Malformed HTML"))
    }
  }

  /**
    * getHost
    * Method that retrieves the host name
    *
    * @param url - String form of the URL
    * @return String corresponding to the host name
    */
  def getHost(url: String): String = {
    val httpRegex = "http://"
    val getSecond = "/"
    val hostName = url.split(httpRegex)(1).split(getSecond)(0)
    hostName
  }

  /**
    * getPath
    * Method that retrieves the path
    *
    * @param url - String form of the URL
    * @return String corresponding to the path
    */
  def getPath(url: String): String = {
    val httpRegex = "http://"
    val getSecond = "/"
    try {
      val httpRemoved = url.split(httpRegex)(1)
      val pathName = httpRemoved.dropWhile { x => x != '/' }
      pathName
    } catch {
      case iob: IndexOutOfBoundsException => url
    }
  }

  /**
    * getHTMLElementList
    * Method that parses the input from the server into a list of HTMLElements
    *
    * @param inputFromServer- BufferedReader containing HTML from server
    * @returns hierarchical list of the HTMLElements (see the documentation
    *          and view the sol code for the specific composition of each
    *          HTMLElement within the list)
    */
  def getHTMLElementList(inputFromServer: BufferedReader): List[HTMLElement] = {
    try {
      println("Parsing page...")
      val parser = new HTMLParser(new HTMLTokenizer(inputFromServer))
      return parser.parse().toHTML
    } catch {
      case l: LexicalException => List(PageText("Malformed HTML: bad token"))
    }
  }

  /**
    * updateURL
    * Method that updates URL bar text field on GUI
    *
    * @param host - String indicating hostname
    * @param path - String indicating path
    */
  def updateURL(host: String, path: String): Unit = {
    urlText = "http://" + host + path
    urlBar.setText(urlText)
  }
}
