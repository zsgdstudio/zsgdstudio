package builder.model

abstract class TextMarker : TextElement() {}

object BOLD_START : TextMarker() //** __
object BOLD_FINISH: TextMarker()
object ITALIC_START : TextMarker() // * _
object ITALIC_FINISH : TextMarker()
object STRIKE_START: TextMarker() // ~~
object STRIKE_FINISH: TextMarker()
object CODE_START : TextMarker() // `
object CODE_FINISH : TextMarker()
object BR : TextMarker() // two+ spaces at the end of line