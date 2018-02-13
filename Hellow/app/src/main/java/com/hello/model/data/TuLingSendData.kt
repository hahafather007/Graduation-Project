package com.hello.model.data

import com.hello.common.Constants

data class TuLingSendData(var key: String = Constants.TULING_KEY,
                          var info: String,
                          var local: String?,
                          var userId: String = "1")