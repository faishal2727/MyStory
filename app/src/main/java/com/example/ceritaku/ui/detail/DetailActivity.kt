package com.example.ceritaku.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.ceritaku.databinding.ActivityDetailBinding
import com.example.ceritaku.model.Story

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDetail()
        setActionBar()
    }

    private fun setActionBar(){
        supportActionBar?.title = "Detail Story"
    }

    private fun getDetail() {
        val getData = intent.getParcelableExtra<Story>(EXTRA_ID) as Story
        binding?.apply {
            tvUsernameDetail.text = getData.name
            tvCreatedDetail.text = getData.createdAt
            tvDescDetail.text = getData.description
            tvDetailLat.text = getData.lat.toString()
            tvDetailLon.text = getData.lon.toString()
            Glide.with(this@DetailActivity)
                .load(getData.photoUrl)
                .into(ivDetail)
        }
    }

    companion object {
        const val EXTRA_ID = "extra id"
    }
}