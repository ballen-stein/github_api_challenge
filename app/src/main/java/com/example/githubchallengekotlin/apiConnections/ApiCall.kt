package com.example.githubchallengekotlin.apiConnections

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class ApiCall(apiUrl : String) {
    private var url : String ?= apiUrl
    private val client : OkHttpClient = OkHttpClient()

    fun getResponse(): Response {
        val request : Request = Request.Builder()
            .url(url.toString())
            .build()
        return client.newCall(request).execute()
    }
}