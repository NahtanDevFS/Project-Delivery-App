package com.appcliente

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.appcliente.alarms.AlarmNotification
import com.appcliente.alarms.AlarmNotification.Companion.NOTIFICATION_ID
import com.appcliente.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    companion object{
        const val MY_CHANNEL_ID = "myChannel"
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var NavController = findNavController(R.id.fragmentContainerView)
        var bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomnav.setupWithNavController(NavController)

        createChannel()

        binding.notificationButton.setOnClickListener {
            val bottomSheetDialog = NotificationBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }

        if (isNotificationServiceEnabled()) {
            // Notification service is enabled
            // Perform your actions here
            scheduleNotification()
        } else {
            // Notification service is not enabled
            // Prompt the user to enable it or handle accordingly
            Toast.makeText(this, "Debes habilitar las notificaciones de la aplicación", Toast.LENGTH_LONG).show()
            redirectToAppSettings()
            finish()
        }

        //createSimpleNotification()
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        return notificationManagerCompat.areNotificationsEnabled()
    }

    private fun redirectToAppSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(settingsIntent)
                Toast.makeText(this, "Por favor, habilita las notificaciones", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 10000, pendingIntent)
    }

    fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "NOTIFICATION"
            }

            val notificationManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}