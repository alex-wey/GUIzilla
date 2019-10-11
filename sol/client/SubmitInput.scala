package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.Button
import javafx.event.EventHandler
import javafx.event.ActionEvent

/**
  * A submit input element of an HTML page, handling the submit button for a form.
  *
  * @param form - the form that contains this submit button
  */
case class SubmitInput(form: Form) extends HTMLElement {

  /**
    * render
    * Method that renders element to console
    *
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  override def render(box: VBox, browser: GUIBrowser): Unit = {
    val button = new Button()
    button.setText("Submit")
    val event = new EventHandler[ActionEvent]() {
      override def handle(a: ActionEvent) {
        for (el <- form.getElements()) {
          el match {
            case textIn: TextInput =>
              val text = textIn.text.getText
              textIn.addValue(text)
            case _ =>
          }
        }
        val encoded = form.getEncodedElements()
        val client = new Client(browser.currentHost)
        val length = encoded.length().toString()
        val headers = " HTTP/1.0\r\nConnection: close\r\nUser-Agent: Sparkzilla/1.0\r\n " +
          "Content-Type: application/x-www-form-urlencoded\r\nContent-Length: "
        val reader = client.run("POST " + form.url + headers + length + "\r\n\r\n" +
          encoded, form.url)
        val elements = browser.getHTMLElementList(reader)
        browser.currentPage = elements
        browser.cache = elements :: browser.cache
        browser.pathCache = form.url :: browser.pathCache
        browser.hostCache = browser.currentHost :: browser.hostCache
        browser.updateURL(browser.currentHost, form.url)
        browser.render(elements)
      }
    }
    button.setOnAction(event)
    box.getChildren().add(button)
  }
}