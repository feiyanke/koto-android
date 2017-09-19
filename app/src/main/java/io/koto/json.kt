package io.koto

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Administrator on 2017/8/8.
 */
fun obj(s:String): JSONObject = JSONObject(s)
fun array(s:String): JSONArray = JSONArray(s)
inline fun obj(block: JSONObject.()->Unit) = JSONObject().apply(block)
inline fun array(block: JSONArray.() -> Unit) = JSONArray().apply(block)
inline fun obj(vararg pairs : Pair<String, Any>) = obj { pairs.forEach { put(it.first, it.second) } }
inline fun array(vararg values : Any) = array { values.forEach { put(it) } }

fun JSONObject.obj(s:String) = getJSONObject(s)
fun JSONObject.int(s:String) = getInt(s)
fun JSONObject.bool(s:String) = getBoolean(s)
fun JSONObject.float(s:String) = getDouble(s)
fun JSONObject.array(s:String) = getJSONArray(s)
fun JSONObject.str(s:String) = getString(s)
fun JSONObject.long(s:String) = getLong(s)
inline fun <reified T, R> JSONObject.one(s:String, predicate: T.() -> R?) : R = array(s).one(predicate)?:throw JSONException("not that object")
inline fun <reified T> JSONObject.one(s:String) : T = array(s).one()?:throw JSONException("not that object")
inline fun <reified R> JSONObject.oneobj(s:String, predicate: JSONObject.() -> R?) : R = one(s, predicate)?:throw JSONException("not that object")
inline fun JSONObject.oneobj(s:String) : JSONObject = one(s)?:throw JSONException("not that object")
inline fun <reified T> JSONObject.array(s: String, action: (T) -> Unit) = array(s).forEach(action)
inline fun JSONObject.objs(s: String, action: JSONObject.() -> Unit) = array(s, action)

inline fun <reified T> JSONArray.forEach(action: T.() -> Unit): Unit {
    for (i in 0..(length()-1)) {
        val o = get(i)
        if (o is T) o.action()
    }
}

inline fun <reified T, R> JSONArray.one(predicate: T.() -> R?) : R? {
    forEach<T> {
        val r = predicate()
        if (r!=null) return r
    }
    return null
}

inline fun <reified T> JSONArray.one() : T? {
    forEach<T> {
        return this
    }
    return null
}


