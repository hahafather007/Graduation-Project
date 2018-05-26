package com.hello.model.data

import com.google.gson.annotations.SerializedName

//AIUI上传动态实体的data
data class AIUIUploadData(val param: Param,
                          val data: String) {
    data class Param(
            @SerializedName("id_name")
            val name: String,// 维度
            @SerializedName("id_value")
            val value: String,// 维度具体值，当维度取uid或appid时，该值可取空，AIUI会自动补全
            @SerializedName("res_name")
            val resName: String)// 资源名称（XXX.user_applist）
}

data class AIUIUploadPhonePeopleData(val name: String,
                                     @SerializedName("phoneNumber")
                                     val num: String)