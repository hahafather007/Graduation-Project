package com.hello.model.service.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.hello.common.Constants.DATA_FORMAT
import org.joda.time.LocalDate

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter?, value: LocalDate?) {
        out?.value(value?.toString(DATA_FORMAT))
    }

    override fun read(`in`: JsonReader?): LocalDate {
        return LocalDate.parse(`in`?.nextString())
    }
}