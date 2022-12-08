package com.example.ceritaku.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ceritaku.R
import com.example.ceritaku.adapter.StoryAdapter
import com.example.ceritaku.databinding.ActivityMainBinding
import com.example.ceritaku.factory.ViewModelFactory
import com.example.ceritaku.model.LoginResultModel
import com.example.ceritaku.model.Story
import com.example.ceritaku.preference.PreferenceLogin
import com.example.ceritaku.ui.detail.DetailActivity
import com.example.ceritaku.ui.detail.DetailActivity.Companion.EXTRA_ID
import com.example.ceritaku.ui.login.LoginActivity
import com.example.ceritaku.ui.maps.MapsActivity
import com.example.ceritaku.ui.upload.UploadActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var preferenceLogin: PreferenceLogin
    private lateinit var loginResultModel: LoginResultModel
    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceLogin = PreferenceLogin(binding.root.context)
        loginResultModel = preferenceLogin.getUser()

        setViewModel()
        setRecyler(binding.root.context)
        getStories()
        onClick()
        toUploadActivity()
    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun setRecyler(context: Context) {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            storyAdapter = StoryAdapter()
            adapter = storyAdapter
        }
    }

    private fun getStories() {
        binding.rvStory.adapter = storyAdapter
        homeViewModel.getAllStory.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun onClick() {
        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                startActivity(Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(EXTRA_ID, data)
                })
            }
        })
    }

    private fun toUploadActivity() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert_title))
        builder.setMessage(getString(R.string.alert_message))
        builder.setNegativeButton(getString(R.string.alert_negativ)) { _, _ ->

        }
        builder.setPositiveButton(getString(R.string.alert_positive)) { _, _ ->
            startActivity(Intent(this@MainActivity, LoginActivity::class.java).also {
                preferenceLogin.deleteUser()
                Toast.makeText(this, getString(R.string.sampai_jumpa), Toast.LENGTH_SHORT)
                    .show()
            })
        }
        val alert = builder.create()
        when (item.itemId) {
            R.id.logout ->
                alert.show()
            R.id.maps ->
                startActivity(Intent(this, MapsActivity::class.java))
        }
        return true
    }
}