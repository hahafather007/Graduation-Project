package com.hello.model.service

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.hello.model.data.HelpFunData
import io.reactivex.Single
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

class HelpService @Inject constructor() {
    @Inject
    lateinit var context: Context

    //从本地获取json文件
    fun getFunctions(): Single<List<HelpFunData>> {
        val builder = StringBuilder()

        try {
            val manager = context.assets

            val reader = BufferedReader(InputStreamReader(manager.open("help_function.json")))

            while (true) {
                val line = reader.readLine() ?: break

                builder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val gson = Gson()
        val jsonArray = JsonParser().parse(builder.toString()).asJsonArray

        val functions: List<HelpFunData> =
                (0 until jsonArray.size()).map {
                    gson.fromJson(jsonArray[it], HelpFunData::class.java)
                }
        return Single.just(functions)
    }
}