package guizilla.src.parser

import guizilla.sol.client.HTMLElement
import guizilla.sol.client.PageText
import guizilla.sol.client.Link

// An abstract class corresponding to <paragraphstuff> in the grammar
abstract class ParagraphStuff {

  /**
    * Converts the parse tree into a list of HTML Element
    *
    * @return A list of HTML Elements
    */
  def toHTML: List[HTMLElement]

}

// The text case of <paragraphstuff>
class ParagraphText(text: String, stuff: ParagraphStuff) extends ParagraphStuff {

  override def toHTML: List[HTMLElement] =
    new PageText(text) :: stuff.toHTML

}

// The <link> case of <paragraphstuff>
class ParagraphLink(href: String, text: String, stuff: ParagraphStuff) extends ParagraphStuff {

  override def toHTML: List[HTMLElement] =
    new Link(href, new PageText(text)) :: stuff.toHTML

}

// The "</p>" case of <paragraphstuff>
class EndOfParagraph extends ParagraphStuff {

  override def toHTML: List[HTMLElement] = List()

}
