package builder.util

class Flow<T>(private var values: MutableList<T>) {
    private var index: Int = 0


    fun hasNext(): Boolean {
        return index < values.size
    }

    fun next(): T {
        return values[index++]
    }

    fun peek(offset: Int = 1): T? {
        return values.getOrNull(index + offset - 1)
    }

    fun testPeek(offset: Int = 1, predicate: (T?) -> Boolean): Boolean {
        return predicate(peek(offset))
    }

    fun isPeek(offset: Int = 1, value: T): Boolean {
        return testPeek(offset) {it == value}
    }

    fun checkNext(value: T): Flow<T> {
        check(next() == value) {"next is not $value"}
        return this
    }



    fun hasPrev(): Boolean {
        return index > 0
    }

    fun prev(): T {
        return values[--index]
    }

    fun peekBack(offset: Int = 1): T? {
        return values.getOrNull(index - offset)
    }

    fun testPeekBack(offset: Int = 1, predicate: (T?) -> Boolean): Boolean {
        return predicate(peekBack(offset))
    }

    fun isPeekBack(offset: Int = 1, value: T): Boolean {
        return testPeekBack(offset) {it == value}
    }



    fun add(value: T) {
        values.add(value)
    }

    override fun toString(): String {
        return values.toString()
    }
}


fun <T> emptyFlow(): Flow<T> {
    return Flow<T>(ArrayList())
}