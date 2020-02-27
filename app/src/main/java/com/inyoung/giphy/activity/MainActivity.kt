package com.inyoung.giphy.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.inyoung.giphy.R
import com.inyoung.giphy.fragment.FavoritesFragment
import com.inyoung.giphy.fragment.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val FRAGMENT_SEARCH = 0
        private const val FRAGMENT_FAVORITES = 1

        private val fragments = listOf(
            SearchFragment(),
            FavoritesFragment()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentTransaction(FRAGMENT_SEARCH)
        button_like_tab.setOnClickListener(this)
        button_search_tab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_search_tab -> {
                fragmentTransaction(FRAGMENT_SEARCH)
                button_search_tab.setBackgroundColor(resources.getColor(R.color.white_22))
                button_like_tab.setBackgroundColor(resources.getColor(R.color.transparent))
            }
            R.id.button_like_tab -> {
                fragmentTransaction(FRAGMENT_FAVORITES)
                button_like_tab.setBackgroundColor(resources.getColor(R.color.white_22))
                button_search_tab.setBackgroundColor(resources.getColor(R.color.transparent))
            }
        }
    }

    private fun fragmentTransaction(id: Int) {
        supportFragmentManager.beginTransaction().also {
            it.replace(R.id.fragment_container, fragments[id])
                .commitAllowingStateLoss()
        }
    }
}