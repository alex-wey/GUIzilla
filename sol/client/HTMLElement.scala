package guizilla.sol.client

import javafx.scene.layout.VBox

/**
  * An element of a HTML Page.
  */
abstract class HTMLElement {

  /**
    * render
    * Method that renders element to console
    * 
    * @param box - the VBox containing corresponding children
    * @param browser - the GUIBrowser
    */
  def render(box: VBox, browser: GUIBrowser)
}