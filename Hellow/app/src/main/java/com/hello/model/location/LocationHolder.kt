package com.hello.model.location

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
import com.hello.model.data.LocationData
import com.hello.utils.Log
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class LocationHolder @Inject constructor() {
    val loaction: Subject<LocationData> = PublishSubject.create()

    private lateinit var client: AMapLocationClient

    @Inject
    lateinit var context: Context

    @Inject
    fun init() {
        client = AMapLocationClient(context)
        val option = AMapLocationClientOption()

        client.setLocationListener {
            if (it != null) {
                //0表示定位成功
                if (it.errorCode == 0) {
                    val data = LocationData(it.address, it.country, it.city, it.street,
                            it.longitude, it.latitude)



                    loaction.onNext(data)

                    Log.i("定位信息：$it")
                } else {
                    Log.e("定位出错：${it.adCode}-->\n${it.errorInfo}")
                }
            }
        }
        //设置高精度模式
        option.locationMode = Hight_Accuracy
        option.isOnceLocation = true

        client.setLocationOption(option)
    }

    //开始定位
    fun startLocation() {
        client.startLocation()
    }
}