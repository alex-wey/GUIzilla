package guizilla.sol.client

import java.net.URLEncoder
import javafx.scene.layout.VBox

/**
  * A form element of an HTMLPage.
  * 
  * @param url - the URL to send the form data
  * @param elements - the HTML elements contained in the form
  */
case class Form(val url: String, private var elements: List[HTMLElement]) extends HTMLElement {

  /**
    * render
    * Method that renders element to console
    * 
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  override def render(box: VBox, browser: GUIBrowser): Unit = {
    for (el <- elements) {
      el.render(box, browser)
    }
  }

  /**
    * setElements
    * Method that adds all of the action events of the form to the form object
    * 
    * @param els - a list of all elements in a form
    */
  def setElements(els: List[HTMLElement]): Unit = {
    for (element <- els) {
      elements = element :: elements
    }
    elements = elements.reverse
  }

  /**
    * getElements
    * Method that retrieves the elements from the form
    * 
    * @return - the list of HTML elements
    */
  def getElements(): List[HTMLElement] = elements

  /**
    * getEncodedElements
    * Method that retrieves all elements in encoded form
    * 
    * @return String of encoded elements
    */
  def getEncodedElements(): String = {
    var encoded: String = ""
    for (element <- elements) {
      element match {
        case input: TextInput =>
          if (encoded != "") {
            encoded = encoded + "&"
          }
          if (input.getValue != None) {
            encoded = encoded + input.name + "=" + URLEncoder.encode(input.getValue.get, "UTF-8")
          } else {
            encoded = encoded + input.name + "="
          }
        case _ =>
      }
    }
    encoded
  }
}