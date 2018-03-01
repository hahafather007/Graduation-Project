package com.hello.model.data

import com.google.gson.annotations.SerializedName

/*
参数	类型	是否必须	取值范围	说明
reqType	int	N	-	输入类型:0-文本(默认)、1-图片、2-音频
perception	-	Y	-	输入信息
userInfo	-	Y	-	用户参数

perception
参数	类型	是否必须	取值范围	说明
inputText	-	N	-	文本信息
inputImage	-	N	-	图片信息
inputMedia	-	N	-	音频信息
selfInfo	-	N	-	客户端属性

注意：输入参数必须包含inputText或inputImage或inputMedia！

inputText
参数	类型	是否必须	取值范围	说明
text	String	Y	1-128字符	直接输入文本

inputImage
参数	类型	是否必须	取值范围	说明
url	String	Y		图片地址

inputMedia
参数	类型	是否必须	取值范围	说明
url	String	Y		音频地址

selfInfo
参数	类型	是否必须	取值范围	说明
location	-	N	-	地理位置信息

location
参数	类型	是否必须	取值范围	说明
city	String	Y	-	所在城市
province	String	N	-	省份
street	String	N	-	街道

userInfo
参数	类型	是否必须	取值范围	说明
apiKey	String	Y	32位	机器人标识
userId	String	Y	长度小于等于32位	用户唯一标识
groupId	String	N	长度小于等于64位	群聊唯一标识
userIdName	String	N	长度小于等于64位	群内用户昵称*/

data class TuLingSendData(@SerializedName("reqType") val type: Int,
                          @SerializedName("perception") val info: SendInfo,
                          val userInfo: UserInfo) {
    data class SendInfo(@SerializedName("inputText") val text: Text?,
                        @SerializedName("inputImage") val image: Image?,
                        val selfInfo: SelfInfo?) {
        data class Text(val text: String)
        data class Image(val url: String)
        data class SelfInfo(val location: Location) {
            data class Location(val city: String,
                                val province: String?,
                                val street: String?)
        }
    }

    data class UserInfo(@SerializedName("apiKey") val key: String,
                        @SerializedName("userId") val id: String)
}
