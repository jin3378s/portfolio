package portfolio

interface JsonProcessor {
    fun toJson(json: Any): String
    fun <T> fromJson(json: String, type: Class<T>): T
}
