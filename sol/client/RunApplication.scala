package guizilla.sol.client

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.layout.GridPane
import guizilla.src.HTMLTokenizer 
import guizilla.src.parser.HTMLParser
import java.io.BufferedReader
import java.io.FileReader
import javafx.scene.layout.VBox

/**
  * Class that handles the JavaFX application
  */
class RunApplication extends Application {
  
  override def start(stage: Stage) {
    // Loads fxml_main.fxml
    var loader = new FXMLLoader(getClass().getResource("fxml_main.fxml"))

    // Loads parent root
    var root: Parent = loader.load().asInstanceOf[GridPane]

    // Creates controller object from GUIBrowser
    var controller: GUIBrowser = loader.getController()

    // Sets the stage for GUIBrowser 
    controller.setStage(stage)

    // Customizes and shows stage
    val scene = new Scene(root, 1000, 700)
    stage.setTitle("Guizilla")
    stage.setScene(scene)

    // Renders home page
    val page: List[HTMLElement] = List(Paragraph(List(
        PageText("Welcome to Guizilla!"),
        PageText("See TA servers here:"),
        Link("http://stilgar/Index", PageText("Stilgar")),
        Link("http://thufir/Index",PageText("Thufir")),
        PageText("See pages on our server:"),
        Link("http://localhost/Search/search",PageText("Search")),
        Link("http://localhost/Horoscope/search",PageText("Horoscope")),
        Link("http://localhost/Application/apply",PageText("Application")),
        Link("http://localhost/FirstLast/names",PageText("Names")),
        Link("http://localhost/BadPage/malformed",PageText("Bad Page")))))
    controller.cache = page::controller.cache
    controller.render(page)
    stage.show()
  }
}

/**
  * Object of the RunApplication class that calls methods that run the browser.
  */
object RunApplication extends App {
  javafx.application.Application.launch(classOf[RunApplication])
}