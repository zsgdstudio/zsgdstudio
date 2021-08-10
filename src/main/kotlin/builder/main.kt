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
    context.warnings.forEach {
        println("[parser warning]\n    ${it.fileName}\n    ${it.line}\n    ${it.message}")
    }
}