package com.example.rez.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.rez.R
import com.example.rez.RezApp
import com.example.rez.api.NameInterface
import com.example.rez.api.Resource
import com.example.rez.databinding.ActivityDashboardBinding
import com.example.rez.repository.AuthRepository
import com.example.rez.ui.RezViewModel
import com.example.rez.ui.RezViewModelProviderFactory
import com.example.rez.ui.fragment.dashboard.NotificationFragment
import com.example.rez.util.visible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject


class DashboardActivity : AppCompatActivity(), NameInterface {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var _binding: ActivityDashboardBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var searchview: TextView
    private lateinit var hiText: TextView
    private lateinit var toolbarFragmentName: TextView
    private lateinit var drawerCloseIcon: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var notification: CircleImageView

    private lateinit var navView: NavigationView
    lateinit var rezViewModel: RezViewModel


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as RezApp).localComponent?.inject(this)

        //    ( application as NexPortApp).localComponent?.inject(this)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rezRepository = AuthRepository()
        val viewModelProviderFactory = RezViewModelProviderFactory(application, rezRepository)
        rezViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(RezViewModel::class.java)

        setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)

        /*Set Status bar Color*/
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window?.statusBarColor = resources?.getColor(R.color.blue)!!
//            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }


        drawerLayout = binding.drawerLayout
        navView = binding.navView
        val navViewHeader = navView.getHeaderView(0)
        drawerCloseIcon = navViewHeader.findViewById(R.id.nav_drawer_close_icon_image_view)


        /*Initialize Toolbar Views*/
        searchview = binding.appBarDashboard.dashboardActivityToolbarSearchView
        hiText = binding.appBarDashboard.hiText
        searchview.text = sharedPreferences.getString("name", "name")
        toolbarFragmentName = binding.appBarDashboard.dashboardActivityToolbarFragmentNameTextView
        bottomNavigationView =
            binding.appBarDashboard.contentDashboard.dashboardActivityBottomNavigationView
        notification = binding.appBarDashboard.dashboardActivityNotificationIcon
        navigationView = binding.navView



        navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)

        /*Set Up Navigation Change Listener*/
        onDestinationChangedListener()


        notification.setOnClickListener {
            navController.navigate(R.id.notificationFragment)
        }

        /*Close Drawer Icon*/
        drawerCloseIcon.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.bookingHistory -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.bookingHistory
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.pendingReviewsFragment -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.pendingReviewsFragment
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.notificationFragment -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.notificationFragment
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.help2 -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.help2
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.securityAndPrivacy -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.securityAndPrivacy
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.settings2 -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.settings2
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    // Using a dialog to ask the user for confirmation before logging out
                    val confirmationDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    confirmationDialog.setMessage(R.string.logout_confirmation_dialog_message)
                    confirmationDialog.setPositiveButton("YES") { _: DialogInterface, _: Int ->
                        // First clear the Shared preference, so that the authentication token stored in it will be deleted

                        rezViewModel.logout(
                            "Bearer ${
                                sharedPreferences.getString(
                                    "token",
                                    "token"
                                )
                            }"
                        )
                        rezViewModel.logoutResponse.observe(this) {
                            when (it) {
                                is Resource.Success -> {
                                    //will delete later, but will toast for development
                                    if (it.value.status) {
                                        sharedPreferences.edit().clear().apply()
                                        Toast.makeText(
                                            this,
                                            "${it.value.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        // After clearing the shared preference, navigate the user back to the landing screen which is the MainActivity
                                        startActivity(intent)
                                        // Use Toast to notify the user that they are being logged out
                                        // Finish the current activity so that the user can not use the back button to come back to this current screen
                                        finish()
                                    }
                                }
                                is Resource.Failure -> {
                                    Toast.makeText(
                                        this,
                                        "Please check your internet connection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {}
                            }
                        }
                    }
                    confirmationDialog.setNegativeButton(
                        "NO"
                    ) { _: DialogInterface, _: Int ->
                    }
                    confirmationDialog.create().show()
                    return@setNavigationItemSelectedListener true
                }
                else -> return@setNavigationItemSelectedListener true
            }
        }

        rezViewModel.countNotification(
            "Bearer ${
                sharedPreferences.getString(
                    "token",
                    "token"
                )
            }"
        )
        rezViewModel.notificationCountResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    //will delete later, but will toast for development
                    if (it.value.status) {
                        if(it.value.data.count > 0){
                            binding.appBarDashboard.notificationCount.visible(true)
                            binding.appBarDashboard.notificationCount.text = it.value.data.count.toString()
                        }
                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        this,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    override fun onDataPass(data: String) {
        sharedPreferences.edit().putString("name", data).apply()
        searchview.text = data
        Log.d("LOGNAME", "hello " + data)
    }

    /*CLose Nav Drawer if open, on back press*/
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//
//    }

    /*Set Up Navigation Change Listener*/
    private fun onDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbarFragmentName.text = destination.label ?: getString(R.string.app_name)
            toolbarFragmentName.setTextColor(Color.WHITE)
            when (destination.id) {
                R.id.home2 -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    //toolbarFragmentName.text = sharedPreferences.getString("username", "username")
                    searchview.visibility = View.VISIBLE
                    hiText.visibility = View.VISIBLE
                    toolbarFragmentName.visibility = View.GONE
                    notification.visibility = View.VISIBLE
                }
                R.id.favoritesCover -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.favorites -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.reservation -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.myProfile -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.topRecommended -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.nearRestaurant -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.suggestionForYou -> {
                    hiText.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.GONE
                }
                R.id.topFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.nearRestFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.suggestFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.tableDetails -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.proceedToPayment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.favoriteDetailsFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.searchFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.search -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.openingHoursFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.bookingDetailsFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.settings2 -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.notificationFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.pendingReviewsFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.help2 -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.securityAndPrivacy -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.changePassword -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.QRCodeFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.successFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.errorFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                R.id.complaintsFragment -> {
                    hiText.visibility = View.GONE
                    toolbarFragmentName.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                    searchview.visibility = View.INVISIBLE
                    notification.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    searchview.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    notification.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val orderFragment = intent.getStringExtra("PUSHNOTIFICATION");
            if (orderFragment != null) {
                navController.navigate(R.id.notificationFragment)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        onDestinationChangedListener()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)


    override fun onDestroy() {
        super.onDestroy()
        finishAffinity()
    }
}
