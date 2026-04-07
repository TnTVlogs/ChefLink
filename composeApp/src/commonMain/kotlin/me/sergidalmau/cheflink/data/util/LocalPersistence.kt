package me.sergidalmau.cheflink.data.util

expect fun saveLocalData(key: String, value: String)
expect fun getLocalData(key: String): String?
