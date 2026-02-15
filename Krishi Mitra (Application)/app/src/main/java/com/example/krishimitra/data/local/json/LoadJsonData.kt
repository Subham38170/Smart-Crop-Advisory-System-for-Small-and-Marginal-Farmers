package com.example.krishimitra.data.local.json


import android.content.Context
import com.example.krishimitra.R
import com.example.krishimitra.domain.StateModel
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

fun loadStatesAndDistricts(context: Context): List<StateModel> {
    val inputStream = context.resources.openRawResource(R.raw.states_districts)
    val jsonString = inputStream.bufferedReader().use { it.readText() }

    return Gson().fromJson(jsonString, Array<StateModel>::class.java).toList()
}


fun getMandiCropImageUrl(context: Context, cropName: String): String? {
    val inputStream = context.resources.openRawResource(R.raw.mandi_crop_images)
    val bufferReader = BufferedReader(InputStreamReader(inputStream))

    val jsonString = bufferReader.use { it.readText() }

    val jsonObject = JSONObject(jsonString)

    return if (jsonObject.has(cropName)) jsonObject.getString(cropName) else null

}

fun getMandiModalTrendPrice(
    context: Context,
    state: String,
    district: String,
    market: String,
    commodity: String,
    rawResId: Int = R.raw.mandi_trend
): Int? {
    val inputStream = context.resources.openRawResource(rawResId)
    val jsonString = inputStream.bufferedReader().use { it.readText() }
    val jsonArray = JSONArray(jsonString)

    val searchKey = "${state.lowercase()}|${district.lowercase()}|${market.lowercase()}|${commodity.lowercase()}"

    for (i in 0 until jsonArray.length()) {
        val item = jsonArray.getJSONObject(i)
        val key = "${item.getString("State").lowercase()}|${item.getString("District").lowercase()}|${item.getString("Market").lowercase()}|${item.getString("Commodity").lowercase()}"
        if (key == searchKey) {
            return item.getInt("Modal_x0020_Price")
        }
    }

    // return null if not found
    return null
}