package com.hello.model.data

import com.google.gson.annotations.JsonAdapter
import com.hello.model.service.adapter.LocalDateAdapter
import org.joda.time.LocalDate

//版本更新信息
data class UpdateInfoData(val code: Int,
                          val info: String,
                          @JsonAdapter(LocalDateAdapter::class)
                          val time: LocalDate)