package com.example.app.dao

import android.util.Log
import com.example.app.models.CartProduct
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Payments {

    internal fun buy(): Boolean {
        try {
            val jwt = Database.getUser()?.jwt ?: ""
            val url = URL("${Config.API_URL}/payments/buy")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")
            con.setRequestProperty("Authorization", "Bearer $jwt")

            var payload = "["
            Database.getCartProducts().forEach {
                payload += ",{\"productId\": \"${it.productId}\", \"quantity\": ${it.quantity}}"
            }
            payload += "]"
            payload = payload.replaceFirst(",", "")

            con.outputStream.use { os ->
                val input: ByteArray = payload.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            con.connect()

            if (con.responseCode != 200)
                throw Exception("Response code is ${con.responseCode} and should be 200")

            Database.realm.writeBlocking {
                val oldCartProducts: RealmResults<CartProduct> = this.query<CartProduct>().find()
                this.delete(oldCartProducts)
            }

            Database.init()

            return true
        } catch (e: Exception) {
            Log.e("PAYMENTS", e.toString())
            e.printStackTrace()
            return false
        }
    }

    internal fun payStripe(orderId: Int): Boolean {
        try {
            val jwt = Database.getUser()?.jwt ?: ""
            val url = URL("${Config.API_URL}/payments/stripe")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")
            con.setRequestProperty("Authorization", "Bearer $jwt")

            val payload = "{\"orderId\": $orderId}"
            con.outputStream.use { os ->
                val input: ByteArray = payload.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            con.connect()

            if (con.responseCode != 200)
                throw Exception("Response code is ${con.responseCode} and should be 200")

            Database.init()

            return true
        } catch (e: Exception) {
            Log.e("PAYMENTS", e.toString())
            e.printStackTrace()
            return false
        }
    }

    internal fun payPayU(orderId: Int): String {
        try {
            val jwt = Database.getUser()?.jwt ?: ""
            val url = URL("${Config.API_URL}/payments/payu")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")
            con.setRequestProperty("Authorization", "Bearer $jwt")

            val payload = "{\"orderId\": $orderId}"
            con.outputStream.use { os ->
                val input: ByteArray = payload.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            con.connect()

            if (con.responseCode != 200)
                throw Exception("Response code is ${con.responseCode} and should be 200")

            BufferedReader(InputStreamReader(con.inputStream, "utf-8")).use { br ->
                val response = StringBuilder()
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null)
                    response.append(responseLine!!.trim { it <= ' ' })
                Database.init()
                return response.toString()
            }
        } catch (e: Exception) {
            Log.e("PAYMENTS", e.toString())
            e.printStackTrace()
            return ""
        }
    }

    internal fun finalizePayment(orderId: Int): Boolean {
        try {
            val jwt = Database.getUser()?.jwt ?: ""
            val url = URL("${Config.API_URL}/payments/finalize")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")
            con.setRequestProperty("Authorization", "Bearer $jwt")

            val payload = "{\"orderId\": $orderId}"
            con.outputStream.use { os ->
                val input: ByteArray = payload.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            con.connect()

            if (con.responseCode != 200)
                throw Exception("Response code is ${con.responseCode} and should be 200")

            Database.init()
            return true
        } catch (e: Exception) {
            Log.e("PAYMENTS", e.toString())
            e.printStackTrace()
            return false
        }
    }
}
