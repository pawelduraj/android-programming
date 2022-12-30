package com.example.app.dao

import android.util.Log
import com.example.app.models.Category
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

internal object Admin {
    internal fun addNewProduct(
        name: String, category: Category, price: Int, inStock: Int, description: String
    ): Boolean {
        try {
            val jwt = Database.getUser()?.jwt ?: ""
            val url = URL("${Config.API_URL}/products")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")
            con.setRequestProperty("Authorization", "Bearer $jwt")

            val payload =
                "{\"name\": \"$name\", \"categoryId\": \"${category.categoryId}\", \"price\": $price, \"inStock\": $inStock, \"description\": \"$description\"}"
            con.outputStream.use { os ->
                val input: ByteArray = payload.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            con.connect()

            if (con.responseCode !in 200..299)
                throw Exception("Response code is ${con.responseCode} and should be 2xx")

            BufferedReader(InputStreamReader(con.inputStream, "utf-8")).use { br ->
                val response = StringBuilder()
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null)
                    response.append(responseLine!!.trim { it <= ' ' })
            }

            Data.init()
            return true
        } catch (e: Exception) {
            Log.e("ADMIN", e.toString())
            e.printStackTrace()
            return false
        }
    }
}
