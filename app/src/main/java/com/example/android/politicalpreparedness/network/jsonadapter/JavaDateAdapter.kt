package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class JavaDateAdapter {
    @FromJson
    fun divisionFromJson (electionDate: String) = SimpleDateFormat("yyyy-MM-YY").parse(electionDate)

    @ToJson
    fun divisionToJson (electionDate: Date) = SimpleDateFormat("yyyy-MM-YY").format(electionDate)
}