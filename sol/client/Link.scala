package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.Hyperlink
import javafx.event.EventHandler
import javafx.event.ActionEvent
import java.lang.IndexOutOfBoundsException

/**
  * A link element of an HTML page.
  *
  * @param href - the URL of the link
  * @param text - the text to be rendered
  */
case class Link(href: String, text: PageText) extends HTMLElement {

  /**
    * render
    * Method that renders element to console
    *
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  override def render(box: VBox, browser: GUIBrowser): Unit = {
    val link = new Hyperlink(text.text)
    val event = new EventHandler[ActionEvent]() {
      override def handle(a: ActionEvent) {
        try {
          val hostName = browser.getHost(href)
          val pathName = browser.getPath(href)
          val elements = browser.getPage("GET", hostName, pathName)
          browser.currentPage = elements
          browser.cache = elements :: browser.cache
          browser.currentHost = hostName
          browser.pathCache = href :: browser.pathCache
          browser.hostCache = browser.currentHost :: browser.hostCache
          browser.render(elements)
        } catch {
          case iob: IndexOutOfBoundsException =>
            val elements = browser.getPage("GET", browser.currentHost, href)
            browser.currentPage = elements
            browser.cache = elements :: browser.cache
            browser.pathCache = ("http://" + browser.currentHost + href) :: browser.pathCache
            browser.hostCache = browser.currentHost :: browser.hostCache
            browser.render(elements)
        }
      }
    }
    link.setOnAction(event)
    box.getChildren().add(link)
  }
}
