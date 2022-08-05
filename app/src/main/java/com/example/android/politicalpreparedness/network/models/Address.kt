package com.example.android.politicalpreparedness.network.models


data class Address (
        var line1: String? = null,
        var line2: String? = null,
        var city: String,
        var state: String,
        var zip: String
) {
    fun toFormattedString(): String {
        var output = line1.plus("\n")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }
    fun toQueryString():String{
        var output = ""
        if (!line1.isNullOrEmpty()) output = output.plus(line1).plus(",")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus(",")
        if (!city.isNullOrEmpty()) output = output.plus(city).plus(",")
        if (!state.isNullOrEmpty()) output = output.plus(state).plus(",")
        if (!zip.isNullOrEmpty()) output = output.plus(zip).plus(",")
        output = output.dropLast(1)
        return output
    }

}