package guizilla.src.parser

import guizilla.sol.client.HTMLElement
import guizilla.sol.client.Form
import guizilla.sol.client.Paragraph
import guizilla.sol.client.TextInput
import guizilla.sol.client.SubmitInput

// An abstract class corresponding to <formstuff> in the grammar
abstract class FormStuff {

  /**
    * Converts the parse tree into a list of HTML Element
    *
    * @return A list of HTML Elements
    */
  def toHTML(form: Form): List[HTMLElement]

}

// The "<p>" case of <formstuff>
class FormParagraph(paragraphStuff: ParagraphStuff, formStuff: FormStuff) extends FormStuff {

  override def toHTML(form: Form): List[HTMLElement] =
    new Paragraph(paragraphStuff.toHTML) :: formStuff.toHTML(form)

}

// The text input case of <formstuff>
class FormText(name: String, formStuff: FormStuff) extends FormStuff {

  override def toHTML(form: Form): List[HTMLElement] =
    new TextInput(name, None) :: formStuff.toHTML(form)

}

// The submit input case of <formstuff>
class FormSubmit(formStuff: FormStuff) extends FormStuff {

  override def toHTML(form: Form): List[HTMLElement] =
    new SubmitInput(form) :: formStuff.toHTML(form)

}

// The "</form>" case of <formstuff>
class EndOfForm extends FormStuff {

  override def toHTML(form: Form): List[HTMLElement] = List()

}
