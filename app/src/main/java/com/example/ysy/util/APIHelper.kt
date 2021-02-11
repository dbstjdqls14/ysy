/**
 *
 *  REST API HELPER
 *  제작자 : 김승용
 *  마지막 업데이트 : 2021-02-09 18:14
 *
 * */

package com.example.ysy.util

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CountDownLatch

class APIHelper {
    lateinit var client: OkHttpClient

    companion object {
        val instance = APIHelper()
    }
    // GET, GET Req
    fun API_CALL_GET(url: String, conPool: ConnectionPool): JSONObject {
        var results = JSONObject()
        // 카운트 1
        var cntDownLatch = CountDownLatch(1)

        try {
            client = OkHttpClient.Builder().connectionPool(conPool).build()
            val request: Request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Error GET! ", e.printStackTrace().toString())
                    // 카운트 1 감소
                    cntDownLatch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    val data: String = response.body!!.string()
                    results = JSONObject(data)
                    // 카운트 1 감소
                    cntDownLatch.countDown()
                }
            })
        } catch (e: Exception) {
            println("Exception : ${e to String}")
            cntDownLatch.countDown()
        }

        // 카운트가 0 될 때까지 기다림
        cntDownLatch.await()
        return results
    }

    // POST, POST Req
    fun API_CALL_POST(url: String, data: HashMap<String, String>, conPool: ConnectionPool): JSONObject {
        var results = JSONObject()
        // 카운트 1
        var cntDownLatch = CountDownLatch(1)

        try {
            client = OkHttpClient.Builder().connectionPool(conPool).build()

            val requestBody: RequestBody = FormBody.Builder()
                    .add("userId", data["userId"].toString())
                    .add("snsId", data["snsId"].toString())
                    .add("name", data["name"].toString())
                    .add("email", data["email"].toString())
                    .add("phone", data["phone"].toString())
                    .add("birthday", data["birthday"].toString()).build()
            val request: Request = Request.Builder().url(url).post(requestBody).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Error POST! ", e.printStackTrace().toString())
                    // 카운트 1 감소
                    cntDownLatch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    val data: String = response.body!!.string()
                    results = JSONObject(data)
                    // 카운트 1 감소
                    cntDownLatch.countDown()
                }
            })
        } catch (e: Exception) {
            println("Exception : ${e to String}")
            cntDownLatch.countDown()
        }

        // 카운트가 0 될 때까지 기다림
        cntDownLatch.await()
        return results
    }
}