package com.example.services

import com.example.DatabaseFactory.dbQuery
import com.example.models.OrderDetails
import com.example.models.Orders
import com.example.models.Product
import com.example.models.Products
import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

private fun resultRowToProduct(row: ResultRow) = Product(
    productId = row[Products.productId],
    name = row[Products.name],
    categoryId = row[Products.categoryId],
    price = row[Products.price],
    inStock = row[Products.inStock],
    description = row[Products.description]
)

@Serializable
private class BuyBody(val productId: Int, val quantity: Int)

@Serializable
private class PayBody(val orderId: Int)

@Suppress("LocalVariableName", "DuplicatedCode")
fun Application.paymentsRoutes() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val STRIPE_KEY = config.propertyOrNull("stripe.key")?.getString() ?: "null"
    val PAYU_CLIENT_ID = config.propertyOrNull("payu.client.id")?.getString() ?: "null"
    val PAYU_CLIENT_SECRET = config.propertyOrNull("payu.client.secret")?.getString() ?: "null"

    routing {
        authenticate("auth-user") {
            post("/payments/buy") {
                val body = call.receive<Array<BuyBody>>()
                val token = call.authentication.principal<JWTPrincipal>()
                val tokenUserId = token?.payload?.getClaim("userId")?.asInt()

                try {
                    if (body.isEmpty())
                        throw Exception("No products in body")

                    val products = dbQuery { Products.selectAll().map { resultRowToProduct(it) } }
                    val orderDate = java.time.LocalDateTime.now().toString().split("T")[0] + " " +
                            java.time.LocalDateTime.now().toString().split("T")[1].split(".")[0]
                    var orderPrice = 0
                    for (product in body) {
                        val productInDb = products.find { it.productId == product.productId }
                            ?: throw Exception("Product with id ${product.productId} not found")
                        if (productInDb.inStock < product.quantity)
                            throw Exception("Product with id ${product.productId} is out of stock")
                        orderPrice += productInDb.price * product.quantity
                    }

                    dbQuery {
                        transaction {
                            val order = Orders.insert {
                                it[userId] = tokenUserId!!
                                it[date] = orderDate
                                it[price] = orderPrice
                                it[paid] = false
                                it[payment] = "null"
                            } get Orders.orderId

                            for (product in body) {
                                OrderDetails.insert {
                                    it[orderId] = order
                                    it[productId] = product.productId
                                    it[quantity] = product.quantity
                                    it[price] = products.find { it1 -> it1.productId == product.productId }!!.price
                                }
                            }

                            for (product in body) {
                                Products.update({ Products.productId eq product.productId }) {
                                    it[inStock] =
                                        products.find { it1 -> it1.productId == product.productId }!!.inStock - product.quantity
                                }
                            }
                        }
                    }

                    call.respondText("OK", status = HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            post("/payments/stripe") {
                val body = call.receive<PayBody>()
                val bodyOrderId = body.orderId
                val token = call.authentication.principal<JWTPrincipal>()
                val tokenUserId = token?.payload?.getClaim("userId")?.asInt()

                var orderPrice = 0
                dbQuery {
                    val order = Orders.select { Orders.orderId eq bodyOrderId }.firstOrNull()
                        ?: throw Exception("Order with id $bodyOrderId not found")
                    if (order[Orders.userId] != tokenUserId)
                        throw Exception("Order with id $bodyOrderId not found")
                    orderPrice = order[Orders.price]
                }

                Stripe.apiKey = STRIPE_KEY
                val params: PaymentIntentCreateParams = PaymentIntentCreateParams.builder()
                    .setAmount(orderPrice.toLong())
                    .setCurrency("pln")
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                            .builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build()
                val paymentIntent: PaymentIntent = PaymentIntent.create(params)

                dbQuery {
                    Orders.update({ Orders.orderId eq bodyOrderId }) { it[payment] = paymentIntent.clientSecret }
                }

                call.respondText(paymentIntent.clientSecret, status = HttpStatusCode.OK)
            }

            post("/payments/payu") {
                val body = call.receive<PayBody>()
                val bodyOrderId = body.orderId
                val token = call.authentication.principal<JWTPrincipal>()
                val tokenUserId = token?.payload?.getClaim("userId")?.asInt()

                var orderPrice = 0
                dbQuery {
                    val order = Orders.select { Orders.orderId eq bodyOrderId }.firstOrNull()
                        ?: throw Exception("Order with id $bodyOrderId not found")
                    if (order[Orders.userId] != tokenUserId)
                        throw Exception("Order with id $bodyOrderId not found")
                    orderPrice = order[Orders.price]
                }

                val client = HttpClient()
                val tokenResponse: HttpResponse =
                    client.post("https://secure.snd.payu.com/pl/standard/user/oauth/authorize") {
                        setBody("grant_type=client_credentials&client_id=${PAYU_CLIENT_ID}&client_secret=${PAYU_CLIENT_SECRET}")
                        contentType(ContentType.Application.FormUrlEncoded)
                    }
                val tokenData = tokenResponse.body<String>()
                val accessToken =
                    Json.parseToJsonElement(tokenData).jsonObject["access_token"]?.jsonPrimitive?.content.toString()

                val orderResponse: HttpResponse = client.post("https://secure.snd.payu.com/api/v2_1/orders") {
                    setBody(
                        """{
                            "notifyUrl": "https://example.com/notify",
                            "customerIp": "127.0.0.1",
                            "merchantPosId": "459695",
                            "description": "Cube Shop Order",
                            "currencyCode": "PLN",
                            "totalAmount": "$orderPrice"
                        }""".trimIndent()
                    )
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $accessToken")
                }

                val orderData = orderResponse.body<String>()
                val order = Json.parseToJsonElement(orderData).jsonObject["orderId"]?.jsonPrimitive?.content.toString()
                val ur = Json.parseToJsonElement(orderData).jsonObject["redirectUri"]?.jsonPrimitive?.content.toString()

                dbQuery {
                    Orders.update({ Orders.orderId eq bodyOrderId }) { it[payment] = order }
                }

                call.respondText(ur, status = HttpStatusCode.OK)
            }

            post("/payments/finalize") {
                val body = call.receive<PayBody>()
                val bodyOrderId = body.orderId
                val token = call.authentication.principal<JWTPrincipal>()
                val tokenUserId = token?.payload?.getClaim("userId")?.asInt()

                try {
                    dbQuery {
                        val order = Orders.select { Orders.orderId eq bodyOrderId }.firstOrNull()
                            ?: throw Exception("Order with id $bodyOrderId not found")

                        if (order[Orders.userId] != tokenUserId)
                            throw Exception("Order with id $bodyOrderId not found")

                        val payment = order[Orders.payment]

                        // Stripe
                        if (payment.startsWith("pi")) {
                            Stripe.apiKey = STRIPE_KEY
                            val paymentIntent = PaymentIntent.retrieve(payment.split("_secret_")[0])
                            if (paymentIntent.amount != paymentIntent.amountReceived)
                                throw Exception("Stripe payment is not completed")
                        }

                        // PayU
                        else {
                            val client = HttpClient()
                            val tokenResponse: HttpResponse =
                                client.post("https://secure.snd.payu.com/pl/standard/user/oauth/authorize") {
                                    setBody("grant_type=client_credentials&client_id=${PAYU_CLIENT_ID}&client_secret=${PAYU_CLIENT_SECRET}")
                                    contentType(ContentType.Application.FormUrlEncoded)
                                }
                            val tokenData = tokenResponse.body<String>()
                            val accessToken =
                                Json.parseToJsonElement(tokenData).jsonObject["access_token"]?.jsonPrimitive?.content.toString()

                            val orderResponse: HttpResponse =
                                client.get("https://secure.snd.payu.com/api/v2_1/orders/${payment}") {
                                    header("Authorization", "Bearer $accessToken")
                                }
                            if (orderResponse.body<String>().split("\"status\":\"")[1].split("\"")[0] != "COMPLETED")
                                throw Exception("PayU payment is not completed")
                        }

                        Orders.update({ Orders.orderId eq bodyOrderId }) { it[paid] = true }
                        call.respondText("OK", status = HttpStatusCode.OK)
                    }
                } catch (e: Exception) {
                    call.respondText("Something went wrong", status = HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
