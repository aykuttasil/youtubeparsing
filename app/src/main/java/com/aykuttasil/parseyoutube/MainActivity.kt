package com.aykuttasil.parseyoutube

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aykuttasil.parseyoutube.databinding.ItemVideoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

typealias VideoItemClickListener = (MainActivity.VideoItem) -> Unit

class MainActivity : AppCompatActivity() {

    private var listAdapter: VideoItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setRecyclerView()
        setSearchView()
    }

    private fun setSearchView() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    parseVideosByQuery(it)
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
    }

    private fun setRecyclerView() {
        listAdapter = VideoItemAdapter {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(it.videoUrl)
            )
            try {
                startActivity(webIntent)
            } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
            }
        }
        val listView = findViewById<RecyclerView>(R.id.listVideoItem)
        listView.layoutManager = GridLayoutManager(this, 2)
        listView.adapter = listAdapter
    }

    private fun parseVideosByQuery(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val itemList = mutableListOf<VideoItem>()

            // TODO :/ I didn't parsing youtube content
            val doc =
                Jsoup.connect("https://www.youtube.com/results").data("search_query", query).get()

            val elements = doc.allElements

            /*
            for (item in doc.allElements) {
                val videoItem =
                    VideoItem(
                        "",
                        "",
                        ""
                    )
                itemList.add(videoItem)
            }
            */

            for (item in 1..10) {
                val videoItem =
                    VideoItem(
                        "https://www.youtube.com/watch?v=RacxNskxySo",
                        "https://i.ytimg.com/vi/k3rRUt5oxSc/maxresdefault.jpg",
                        "Africa Twin $item"
                    )
                itemList.add(videoItem)
            }

            withContext(Dispatchers.Main) { listAdapter?.submitList(itemList) }
        }

    }

    data class VideoItem(
        var videoUrl: String?,
        var thumbnailUrl: String?,
        var title: String?
    )

    class VideoItemAdapter(val itemClickListener: VideoItemClickListener) :
        ListAdapter<VideoItem, VideoItemAdapter.VideoItemViewHolder>(DIFF_CALLBACK) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemViewHolder {
            val binding =
                ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VideoItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: VideoItemViewHolder, position: Int) {
            holder.bind(currentList[position])
        }

        inner class VideoItemViewHolder(private val binding: ItemVideoBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: VideoItem) {
                binding.txtTitle.text = item.title
                binding.imgThumbnail.load(item.thumbnailUrl)

                binding.root.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }

        object DIFF_CALLBACK : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return false
            }

        }

    }

}