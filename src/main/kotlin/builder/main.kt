package builder

import builder.model.Context
import builder.translator.translateToHtml

fun main(args: Array<String>) {
    check(args.size == 3) {"3 agrs required: SRC, TRG directories and ENTRY_POINT file"}
    val context = Context(args[0], args[1], args[2])
    printWarnings(context)
    translateToHtml(context)
}

fun printWarnings(context: Context) {
    val maxLength = context.warnings.map { it.atLine.length }.maxOrNull() ?: return
    context.warnings.forEach {
        val res = StringBuilder("[parser warning] ")
        res.append(it.atLine)
        var i = it.atLine.length
        while (i < maxLength) {
            res.append(" ")
            i++
        }
        res.append(" :: ").append(it.message)
        println(res.toString())
    }
}