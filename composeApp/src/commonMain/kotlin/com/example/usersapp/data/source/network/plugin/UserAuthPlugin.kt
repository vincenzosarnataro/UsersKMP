package com.example.usersapp.data.source.network.plugin

import io.ktor.client.plugins.api.createClientPlugin

val userAuthPlugin = createClientPlugin("UserAuthPlugin", ::UserAuthPluginConfig) {
    onRequest { request, _ ->
        val apiKey = this@createClientPlugin.pluginConfig.apiKey
        request.headers.append("x-api-key", apiKey)
    }
}

class UserAuthPluginConfig {
    var apiKey: String = "reqres-free-v1"
}