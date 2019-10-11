package guizilla.src

abstract class Page extends Cloneable {

  override def clone = super.clone.asInstanceOf[Page]

  def defaultHandler(inputs: Map[String, String], sessionId: String): String

}
	