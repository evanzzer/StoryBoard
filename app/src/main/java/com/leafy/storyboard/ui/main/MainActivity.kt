package com.leafy.storyboard.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leafy.storyboard.R
import com.leafy.storyboard.databinding.ActivityMainBinding
import com.leafy.storyboard.ui.login.LoginActivity
import com.leafy.storyboard.ui.main.insert.NewStoryActivity
import com.leafy.storyboard.ui.main.maps.MapsActivity
import com.leafy.storyboard.utils.PagingDataAdapter.withLoadStateAdapters
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val sp by lazy {
        getSharedPreferences(resources.getString(R.string.appDirectory), Context.MODE_PRIVATE)
    }
    private val adapter = StoryAdapter()
    private val addStoryIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == NewStoryActivity.RESULT_CODE) {
            this.adapter.refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter.withLoadStateAdapters(
                header = LoadingAdapter {
                    this@MainActivity.adapter.refresh()
                },
                footer = LoadingAdapter {
                    this@MainActivity.adapter.retry()
                }
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < 0 && !binding.fabPost.isShown)
                        binding.fabPost.show()
                    else if (dy > 0 && binding.fabPost.isShown)
                        binding.fabPost.hide()
                }
            })
        }

        adapter.addLoadStateListener { loadState ->
            val isEmpty = loadState.source.refresh is LoadState.NotLoading
                && loadState.append.endOfPaginationReached
                && adapter.itemCount < 1

            binding.rvStory.isVisible = !isEmpty
            binding.layoutEmpty.root.isVisible = isEmpty
        }

        val token = sp.getString(resources.getString(R.string.tokenKey), "") ?: ""
        viewModel.getStoryList(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        binding.fabPost.setOnClickListener {
            addStoryIntentLauncher.launch(Intent(this, NewStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuLogout -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.alertTitle)
                    .setMessage(R.string.logoutMsg)
                    .setPositiveButton(R.string.alertYes) { _, _ ->
                        sp.edit {
                            remove(resources.getString(R.string.tokenKey))
                        }
                        startActivity(
                            Intent(this, LoginActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                        finish()
                    }
                    .setNegativeButton(R.string.alertNo) { _, _ -> }
                    .show()
                true
            }
            R.id.menuMap -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}