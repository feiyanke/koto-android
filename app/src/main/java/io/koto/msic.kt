package io.koto

inline fun <T> List<T>.orNull(): List<T>? = if (isEmpty()) null else this
inline fun Boolean.be() : Boolean? = if (this) this else null
infix inline fun <T> Boolean.be(v:T) : T? = if (this) v else null
inline fun <R> Boolean.be(expression: () -> R) : R? = if (this) expression() else null

inline fun <reified R> Iterable<*>.find(predicate: (R) -> Boolean): List<R> {
    val list : MutableList<R> = mutableListOf()
    for (i in this) {
        if ((i is R) && predicate(i)) list.add(i)
    }
    return list
}

inline fun <reified R> tryOr(default:R, expression:()->R) : R {
    return try {expression()}catch (e:Throwable) {e.printStackTrace();default}
}

inline fun <reified R> tryOrNull(expression:()->R) : R?{
    return try {expression()}catch (e:Throwable) {e.printStackTrace();null}
}
