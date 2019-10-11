package guizilla.src.parser

import guizilla.src._

/**
  * A class that parsers an HTML file
  *
  * @param tokenizer - an HTMLTokenizer ready to begin tokenizing an HTML file
  */
class HTMLParser(tokenizer: HTMLTokenizer) {

  /**
    * @return the current token
    */
  private def current: Option[Token] = tokenizer.current

  /**
    * Calls advance() on the tokenizer
    *
    * @throws LexicalException
    * @throws IOException
    */
  private def advance() = tokenizer.advance()

  /**
    * Parses the HTML page
    *
    * @throws ParseException If a problem occurs while parsing the file
    * @throws LexicalException If a problem occurs while tokenizing the file
    * @throws IOException If a problem occurs while reading from file
    * @return A HTMLPage
    */
  def parse(): HTMLPage = {
    //Prime the tokenizer
    advance()

    //Parse the page
    parseHTMLPage()
  }

  /**
    * Parses the HTML page
    *
    * @throws ParseException If a problem occurs while parsing the file
    * @throws LexicalException If a problem occurs while tokenizing the file
    * @throws IOException If a problem occurs while reading from file
    * @return A HTMLPage
    */
  private def parseHTMLPage(): HTMLPage = current match {
    case Some(OpenHTML) =>
      advance()
      current match {
        case Some(OpenBody) =>
          advance()
          new AHTMLPage(parseHTMLStuff())
        case _ => throw new ParseException("open body", current)
      }
    case _ => throw new ParseException("open HTML", current)
  }

  /**
    * Parses HTMLStuff
    *
    * @throws ParseException If a problem occurs while parsing the file
    * @throws LexicalException If a problem occurs while tokenizing the file
    * @throws IOException If a problem occurs while reading from file
    * @return A HTMLStuff
    */
  private def parseHTMLStuff(): HTMLStuff = current match {
    case Some(OpenForm(action)) =>
      advance()
      new HTMLForm(action, parseFormStuff(), parseHTMLStuff())
    case Some(OpenParagraph) =>
      advance()
      new HTMLParagraph(parseParagraphStuff(), parseHTMLStuff())
    case Some(CloseBody) =>
      advance()
      current match {
        case Some(CloseHTML) =>
          advance()
          current match {
            case Some(Eof) => new EndOfPage
            case _         => throw new ParseException("EOF", current)
          }
        case _ => throw new ParseException("close HTML", current)
      }
    case _ =>
      throw new ParseException("open form, open paragraph, or close body", current)
  }

  /**
    * Parses FormStuff
    *
    * @throws ParseException If a problem occurs while parsing the file
    * @throws LexicalException If a problem occurs while tokenizing the file
    * @throws IOException If a problem occurs while reading from file
    * @return A FormStuff
    */
  private def parseFormStuff(): FormStuff = current match {
    case Some(OpenParagraph) =>
      advance()
      new FormParagraph(parseParagraphStuff(), parseFormStuff())
    case Some(Input(name)) =>
      advance()
      new FormText(name, parseFormStuff())
    case Some(Submit) =>
      advance()
      new FormSubmit(parseFormStuff())
    case Some(CloseForm) =>
      advance()
      new EndOfForm
    case _ =>
      throw new ParseException("open paragraph, input, submit, or close form", current)
  }

  /**
    * Parses ParagraphStuff
    *
    * @throws ParseException If a problem occurs while parsing the file
    * @throws LexicalException If a problem occurs while tokenizing the file
    * @throws IOException If a problem occurs while reading from file
    * @return A ParagraphStuff
    */
  private def parseParagraphStuff(): ParagraphStuff = current match {
    case Some(Text(text)) =>
      advance()
      new ParagraphText(text, parseParagraphStuff())
    case Some(OpenLink(href)) =>
      advance()
      current match {
        case Some(Text(text)) =>
          advance()
          current match {
            case Some(CloseLink) =>
              advance()
              new ParagraphLink(href, text, parseParagraphStuff())
            case _ => throw new ParseException("close link", current)
          }
        case _ => throw new ParseException("text", current)
      }
    case Some(CloseParagraph) =>
      advance()
      new EndOfParagraph
    case _ => throw new ParseException("text, open link, or close paragraph", current)
  }

}
