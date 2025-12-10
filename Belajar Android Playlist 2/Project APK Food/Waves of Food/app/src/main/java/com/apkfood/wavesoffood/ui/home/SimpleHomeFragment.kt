package com.apkfood.wavesoffood.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apkfood.wavesoffood.R

/**
 * Simple Home Fragment - Backup version
 * Fragment sederhana untuk testing jika versi lengkap bermasalah
 */
class SimpleHomeFragment : Fragment() {
    
    private var username: String = "Guest"
    private var isLoggedIn: Boolean = false
    
    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_IS_LOGGED_IN = "isLoggedIn"
        
        fun newInstance(username: String, isLoggedIn: Boolean): SimpleHomeFragment {
            val fragment = SimpleHomeFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            args.putBoolean(ARG_IS_LOGGED_IN, isLoggedIn)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME, "Guest")
            isLoggedIn = it.getBoolean(ARG_IS_LOGGED_IN, false)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupSimpleUI(view)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupSimpleUI(view: View) {
        val welcomeText = view.findViewById<TextView>(R.id.tv_welcome)
        val userText = view.findViewById<TextView>(R.id.tv_user)
        
        welcomeText?.text = "Welcome to Waves of Food!"
        userText?.text = if (isLoggedIn && username != "Guest") {
            "Hello, $username!"
        } else {
            "Hello, Guest User!"
        }
    }
}
