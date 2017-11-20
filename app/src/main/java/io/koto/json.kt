package io.koto

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

fun obj(s: String): JSONObject = JSONObject(s)
fun array(s: String): JSONArray = JSONArray(s)
inline fun obj(block: JSONObject.() -> Unit) = JSONObject().apply(block)
inline fun array(block: JSONArray.() -> Unit) = JSONArray().apply(block)
inline fun obj(vararg pairs: Pair<String, Any>) = obj { pairs.forEach { put(it.first, it.second) } }
inline fun array(vararg values: Any) = array { values.forEach { put(it) } }

fun JSONObject.obj(s: String) = getJSONObject(s)
fun JSONObject.int(s: String) = getInt(s)
fun JSONObject.bool(s: String) = getBoolean(s)
fun JSONObject.float(s: String) = getDouble(s)
fun JSONObject.array(s: String) = getJSONArray(s)
fun JSONObject.str(s: String) = getString(s)
fun JSONObject.long(s: String) = getLong(s)
inline fun <reified T, R> JSONObject.one(s: String, predicate: T.() -> R?): R = array(s).one(predicate) ?: throw JSONException("not that object")
inline fun <reified T> JSONObject.one(s: String): T = array(s).one() ?: throw JSONException("not that object")
inline fun <reified R> JSONObject.oneobj(s: String, predicate: JSONObject.() -> R?): R = one(s, predicate) ?: throw JSONException("not that object")
inline fun JSONObject.oneobj(s: String): JSONObject = one(s) ?: throw JSONException("not that object")
inline fun <reified T> JSONObject.array(s: String, action: (T) -> Unit) = array(s).forEach(action)
inline fun JSONObject.objs(s: String, action: JSONObject.() -> Unit) = array(s, action)

inline fun <reified T> JSONArray.forEach(action: T.() -> Unit): Unit {
    for (i in 0..(length() - 1)) {
        val o = get(i)
        if (o is T) o.action()
    }
}

inline fun <reified T, R> JSONArray.one(predicate: T.() -> R?): R? {
    forEach<T> {
        val r = predicate()
        if (r != null) return r
    }
    return null
}

inline fun <reified T> JSONArray.one(): T? {
    forEach<T> {
        return this
    }
    return null
}

private fun split(path: String): List<String> {
    val matcher = Pattern.compile("\\.|@").matcher(path)
    val result = mutableListOf<String>()
    while (matcher.find()) {
        val buf = StringBuffer()
        matcher.appendReplacement(buf, "")
        result.add(buf.toString())
        result.add(matcher.group())
    }
    val buf = StringBuffer()
    matcher.appendTail(buf)
    result.add(buf.toString())
    return result
}

private fun getPath(names: List<String>, o: Any): Any {
    var obj: Any = o
    for (i in 0 until names.size step 2) {
        val name = names[i]
        obj = when (obj) {
            is JSONObject -> {
                if (name.matches("\\w+".toRegex())) {
                    obj.get(name)
                } else JSONException("Wrong Name Format!")
            }
            is JSONArray -> {
                if (name.matches("\\d+".toRegex())) {
                    obj.get(name.toInt())
                } else JSONException("Wrong Index Format!")
            }
            else -> throw JSONException("Wrong Path Format!")
        }
    }
    return obj
}

private fun getOrNewPath(names: List<String>, o: Any): Any {
    var obj: Any = o
    for (i in 0 until names.size step 2) {
        val name = names[i]
        val type = names[i + 1]
        obj = when (obj) {
            is JSONObject -> {
                if (name.matches("\\w+".toRegex())) {
                    val t = obj.opt(name)
                    when (type) {
                        "." -> {
                            if (t == null || t !is JSONObject) {
                                obj.put(name, JSONObject())
                            }
                        }
                        "@" -> {
                            if (t == null || t !is JSONArray) {
                                obj.put(name, JSONArray())
                            }
                        }
                        else -> throw JSONException("Wrong Path Format!")
                    }
                    obj.get(name)
                } else JSONException("Wrong Name Format!")
            }
            is JSONArray -> {
                if (name.matches("\\d+".toRegex())) {
                    val index = name.toInt()
                    val t = obj.opt(index)
                    when (type) {
                        "." -> {
                            if (t == null || t !is JSONObject) {
                                obj.put(index, JSONObject())
                            }
                        }
                        "@" -> {
                            if (t == null || t !is JSONArray) {
                                obj.put(index, JSONArray())
                            }
                        }
                        else -> throw JSONException("Wrong Path Format!")
                    }
                    obj.get(index)
                } else JSONException("Wrong Index Format!")
            }
            else -> throw JSONException("Wrong Path Format!")
        }
    }
    return obj
}

fun JSONObject.getPath(path: String) = getPath(split(path), this)
fun JSONArray.getPath(path: String) = getPath(split(path), this)

fun JSONObject.putPath(path: String, o: Any) {
    val names = split(path)
    val json = getOrNewPath(names.dropLast(1), this)
    val name = names.last()
    when (json) {
        is JSONObject -> {
            if (name.matches("\\w+".toRegex())) {
                json.put(name, o)
            } else JSONException("Wrong Name Format!")
        }
        is JSONArray -> {
            if (name.matches("\\d+".toRegex())) {
                json.put(name.toInt(), o)
            } else JSONException("Wrong Index Format!")
        }
    }
}

fun JSONArray.putPath(path: String, o: Any) {
    val names = split(path)
    val json = getOrNewPath(names.dropLast(1), this)
    val name = names.last()
    when (json) {
        is JSONObject -> {
            if (name.matches("\\w+".toRegex())) {
                json.put(name, o)
            } else JSONException("Wrong Name Format!")
        }
        is JSONArray -> {
            if (name.matches("\\d+".toRegex())) {
                json.put(name.toInt(), o)
            } else JSONException("Wrong Index Format!")
        }
    }
}

