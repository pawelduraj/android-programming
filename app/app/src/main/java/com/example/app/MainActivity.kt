package com.example.app

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.app.dao.Config
import com.example.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val standardChannelId = "com.example.app.standard"
    private val highChannelId = "com.example.app.high"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_categories,
                R.id.navigation_products,
                R.id.navigation_cart,
                R.id.navigation_account,
                R.id.navigation_orders
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        createNotificationChannels()
        sharedPreferences = getSharedPreferences("com.example.app", Context.MODE_PRIVATE)
        val notificationNew = sharedPreferences.getLong("notificationNewLong", 0)
        val notificationPromo = sharedPreferences.getLong("notificationPromoLong", 0)
        val currentTimeInMillis = System.currentTimeMillis()
        if (currentTimeInMillis - notificationNew > 15 * 1000)
            displayNewProductNotification("New product: Gan 13 M MagLev")
        if (currentTimeInMillis - notificationPromo > 15 * 1000)
            displayPromoNotification("Promo: 20% off on all Gan cubes")

        answerNotificationIfRequired()
    }

    private fun createNotificationChannels() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val standardChannel = NotificationChannel(
            standardChannelId,
            "Standard",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(true)
        }
        val highChannel = NotificationChannel(
            highChannelId,
            "High",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(standardChannel)
        notificationManager.createNotificationChannel(highChannel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun displayNewProductNotification(title: String) {
        val replyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val replyPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val remoteInput: RemoteInput = RemoteInput.Builder("KEY_TEXT_REPLY").run {
            setLabel(getString(R.string.notification_contact_us))
            build()
        }
        val action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(
                R.drawable.ic_send_24dp,
                getString(R.string.notification_contact_us),
                replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .build()
        val contentIntent: PendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_products)
            .createPendingIntent()
        val builder = NotificationCompat.Builder(this, standardChannelId)
            .setSmallIcon(R.drawable.ic_new_releases_24dp)
            .setContentTitle(title)
            .setContentText("Check out list of all our products.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(action)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(0, builder.build())
        }
        val editor = sharedPreferences.edit()
        editor.putLong("notificationNewLong", System.currentTimeMillis())
        editor.apply()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun displayPromoNotification(title: String) {
        val replyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val replyPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val remoteInput: RemoteInput = RemoteInput.Builder("KEY_TEXT_REPLY").run {
            setLabel(getString(R.string.notification_contact_us))
            build()
        }
        val action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(
                R.drawable.ic_send_24dp,
                getString(R.string.notification_contact_us),
                replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .build()
        val contentIntent: PendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_products)
            .createPendingIntent()
        val builder = NotificationCompat.Builder(this, highChannelId)
            .setSmallIcon(R.drawable.ic_campaign_24dp)
            .setContentTitle(title)
            .setContentText("Check out list of all our products.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(action)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
        val editor = sharedPreferences.edit()
        editor.putLong("notificationPromoLong", System.currentTimeMillis())
        editor.apply()
    }

    private fun createResponseNotification(title: String, description: String): Notification {
        return NotificationCompat.Builder(this, standardChannelId)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_send_24dp)
            .build()
    }

    private fun answerNotificationIfRequired() {
        val text = RemoteInput.getResultsFromIntent(this.intent)?.getCharSequence("KEY_TEXT_REPLY")
            ?: return

        val url = URL("${Config.API_URL}/contact")
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.connectTimeout = 3000
        con.doOutput = true
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Accept", "application/json")

        val payload = "{\"message\": $text}"
        con.outputStream.use { os ->
            val input: ByteArray = payload.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }

        con.connect()

        val notification = if (con.responseCode != 200)
            createResponseNotification("Error", "Something went wrong, please try again later.")
        else createResponseNotification("Success", "Your message has been sent.")

        NotificationManagerCompat.from(this).cancel(0)
        NotificationManagerCompat.from(this).cancel(1)
        NotificationManagerCompat.from(this).apply {
            notify(2, notification)
        }
    }
}
