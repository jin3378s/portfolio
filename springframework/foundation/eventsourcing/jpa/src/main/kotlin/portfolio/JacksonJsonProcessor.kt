package portfolio

import com.fasterxml.jackson.databind.ObjectMapper

class JacksonJsonProcessor(
    private val mapper: ObjectMapper,
) : JsonProcessor {
    override fun toJson(json: Any): String {
        return mapper.writeValueAsString(json)
    }

    override fun <T> fromJson(json: String, type: Class<T>): T {
        return mapper.readValue(json, type)
    }
}
