package guizilla.src.parser

import guizilla.sol.client.HTMLElement
import guizilla.sol.client.Form
import guizilla.sol.client.Paragraph

// An abstract class corresponding to <htmlstuff> in the grammar
abstract class HTMLStuff {

  /**
    * Converts the parse tree into a list of HTML Element
    *
    * @return A list of HTML Elements
    */
  def toHTML: List[HTMLElement]

}

// The "<form ...>" case of <htmlstuff>
class HTMLForm(action: String, formStuff: FormStuff, htmlStuff: HTMLStuff) extends HTMLStuff {

  /**
    * Converts this form into a list of HTML Element
    *
    * @throws InvalidURLException
    * @return A list of HTML Elements
    */
  override def toHTML: List[HTMLElement] = {
    val form = new Form(action, List())
    form.setElements(formStuff.toHTML(form))
    form :: htmlStuff.toHTML
  }

}

// The "<p>" case of <htmlstuff>
class HTMLParagraph(paragraphStuff: ParagraphStuff, htmlStuff: HTMLStuff) extends HTMLStuff {

  override def toHTML: List[HTMLElement] =
    new Paragraph(paragraphStuff.toHTML) :: htmlStuff.toHTML

}

// The "</body>" case of HTMLStuff
class EndOfPage extends HTMLStuff {

  override def toHTML: List[HTMLElement] = List()

}
