package com.example.teste

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform