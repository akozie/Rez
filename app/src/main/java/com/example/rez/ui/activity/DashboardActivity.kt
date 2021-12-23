package com.example.rez.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.rez.R
import com.example.rez.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView

class DashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var _binding: ActivityDashboardBinding
    private val binding get() = _binding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    // private lateinit var listener: NavController.OnDestinationChangedListener
    // private lateinit var editProfile: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbarUserName: SearchView
    private lateinit var toolbarFragmentName: TextView
    private lateinit var drawerCloseIcon: ImageView
    //private lateinit var profileImage: ImageView
    private lateinit var profilePicture: CircleImageView
    private lateinit var navigationView: NavigationView
    //private lateinit var profileName: TextView
    private lateinit var navView: NavigationView



//    @Inject
//    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    //    ( application as NexPortApp).localComponent?.inject(this)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val intent = getIntent()
//        if (intent.extras != null){
//
//            sharedPreferences.edit().putInt("USER_ID", intent.getIntExtra("USER_ID",0))
//        }
//        val email = intent.getStringExtra("USER_EMAIL")
//        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
//        Log.i("USER_EMAIL", email!!)

//        val bundle = Bundle();
//        bundle.putString("USER_EMAIL_ID", email)
//        val fragobj = PasswordResetConfirmationFragment()
//        fragobj.arguments = bundle

        setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)

        /*Set Status bar Color*/
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window?.statusBarColor = resources?.getColor(R.color.purple)!!
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        val navViewHeader = navView.getHeaderView(0)
       // editProfile = navViewHeader.findViewById(R.id.nav_drawer_edit_profile_text_view)
        drawerCloseIcon = navViewHeader.findViewById(R.id.nav_drawer_close_icon_image_view)
        //profileImage = navViewHeader.findViewById(R.id.nav_drawer_profile_avatar_image_view)


        /*Initialize Toolbar Views*/
        toolbarUserName = binding.appBarDashboard.dashboardActivityToolbarSearchView
        toolbarFragmentName = binding.appBarDashboard.dashboardActivityToolbarFragmentNameTextView
        bottomNavigationView = binding.appBarDashboard.contentDashboard.dashboardActivityBottomNavigationView
        profilePicture = binding.appBarDashboard.dashboardActivityToolbarProfileImageView
        navigationView = binding.navView

        //profileName = navViewHeader.findViewById(R.id.nav_drawer_user_full_name_text_view)
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

        /*Edit User Detail onClick*/
//        editProfile.setOnClickListener {
//            drawerLayout.closeDrawer(GravityCompat.START)
//            navController.navigate(R.id.editProfileFragment)
//        }

        /*Close Drawer Icon*/
        drawerCloseIcon.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.bookingHistory -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.bookingHistory)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.help2 -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.help2)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.securityAndPrivacy -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.securityAndPrivacy)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.settings2 -> {
                    findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                        R.id.settings2)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    // Using a dialog to ask the user for confirmation before logging out
                    val confirmationDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    confirmationDialog.setMessage(R.string.logout_confirmation_dialog_message)
                    confirmationDialog.setPositiveButton("YES") { _: DialogInterface, _: Int ->
                        // First clear the Shared preference, so that the authentication token stored in it will be deleted
                        //sharedPreferences.edit().clear().apply()
                        // After clearing the shared preference, navigate the user back to the landing screen which is the MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        // Use Toast to notify the user that they are being logged out
                        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                        // Finish the current activity so that the user can not use the back button to come back to this current screen
                        finish()
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
    }

    /*CLose Nav Drawer if open, on back press*/
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return  navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /*Set Up Navigation Change Listener*/
    private fun onDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbarFragmentName.text = destination.label ?: getString(R.string.app_name)
            when (destination.id) {
                R.id.home2 -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarUserName.visibility = View.VISIBLE
                    toolbarFragmentName.visibility = View.GONE
                }
                R.id.favorites -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarUserName.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    profilePicture.visibility = View.INVISIBLE
                }
                R.id.reservation -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarUserName.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    profilePicture.visibility = View.INVISIBLE
                }
                R.id.myProfile -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarUserName.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                    profilePicture.visibility = View.INVISIBLE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    toolbarUserName.visibility = View.INVISIBLE
                    toolbarFragmentName.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)


}
