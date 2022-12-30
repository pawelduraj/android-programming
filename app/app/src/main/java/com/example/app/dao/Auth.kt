package com.example.app.dao

import android.util.Log
import com.example.app.models.User
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

internal object Auth {
    internal fun signUp(name: String, email: String, password: String): Boolean {
        try {
            val url = URL("${Config.API_URL}/auth/register")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")

            val payload =
                "{\"name\": \"$name\", \"email\": \"$email\", \"password\": \"$password\"}"
            con.outputStream.use { os ->
                val input: ByteArray = payload.toByteArray(charset("utf-8"))
                os.write(input, 0, input.size)
            }

            con.connect()

            if (con.responseCode != 201)
                throw Exception("Response code is ${con.responseCode} and should be 201")

            return true
        } catch (e: Exception) {
            Log.e("AUTH", e.toString())
            e.printStackTrace()
            return false
        }
    }

    internal fun signIn(email: String, password: String): Boolean {
        try {
            val url = URL("${Config.API_URL}/auth/login")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")

            val payload = "{\"email\": \"$email\", \"password\": \"$password\"}"
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

                if (!updateUserFromJWT(response.toString()))
                    throw Exception("Cannot update user from JWT")
            }

            Database.init()
            return true
        } catch (e: Exception) {
            Log.e("AUTH", e.toString())
            e.printStackTrace()
            return false
        }
    }

    internal fun signOut() {
        Database.realm.writeBlocking {
            val oldUsers: RealmResults<User> = this.query<User>().find()
            this.delete(oldUsers)
        }
        Database.sync()
        Database.init()
    }

    internal fun signInWithGithub(token: String): Boolean {
        try {
            val url = URL("${Config.API_URL}/auth/github")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")

            val payload = "{\"token\": \"$token\"}"
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

                if (!updateUserFromJWT(response.toString()))
                    throw Exception("Cannot update user from JWT")
            }

            Database.init()
            return true
        } catch (e: Exception) {
            Log.e("AUTH", e.toString())
            e.printStackTrace()
            return false
        }
    }

    internal fun signInWithGoogle(token: String): Boolean {
        try {
            val url = URL("${Config.API_URL}/auth/google")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 3000
            con.doOutput = true
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Accept", "application/json")

            val payload = "{\"token\": \"$token\"}"
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

                if (!updateUserFromJWT(response.toString()))
                    throw Exception("Cannot update user from JWT")
            }

            Database.init()
            return true
        } catch (e: Exception) {
            Log.e("AUTH", e.toString())
            e.printStackTrace()
            return false
        }
    }

    private class Token {
        val userId: Int = -1
        val name: String = ""
        val email: String = ""
        val admin: Boolean = false
        val exp: Int = -1
    }

    private fun updateUserFromJWT(jwt: String): Boolean {
        try {
            val mapper = jacksonObjectMapper()
            val payload = jwt.split(".")[1]
            val json = String(android.util.Base64.decode(payload, android.util.Base64.DEFAULT))
            val token = mapper.readValue(json, Token::class.java)
            Database.realm.writeBlocking {
                val oldUsers: RealmResults<User> = this.query<User>().find()
                this.delete(oldUsers)
                val newUser = User(token.userId, token.name, token.email, token.admin, jwt)
                this.copyToRealm(newUser)
            }
            Database.sync()
            return true
        } catch (e: Exception) {
            Log.e("AUTH", e.toString())
            e.printStackTrace()
            return false
        }
    }
}
