package guizilla.src

/**
  * An abstract class and all its possible cases
  * that represent tokens in an HTML file
  */
abstract class Token

case object OpenHTML extends Token
case object CloseHTML extends Token

case object OpenParagraph extends Token
case object CloseParagraph extends Token

case object OpenBody extends Token
case object CloseBody extends Token

case class OpenLink(link: String) extends Token
case object CloseLink extends Token

case class Text(text: String) extends Token

case class Input(name: String) extends Token

case object Submit extends Token

case class OpenForm(link: String) extends Token
case object CloseForm extends Token

case object Eof extends Token
