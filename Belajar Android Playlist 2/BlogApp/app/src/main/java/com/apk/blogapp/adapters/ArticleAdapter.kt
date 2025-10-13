package com.apk.blogapp.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apk.blogapp.R
import com.apk.blogapp.models.Article

class ArticleAdapter(
    private var articles: MutableList<Article> = mutableListOf(),
    private val onItemClick: (Article) -> Unit = {},
    private val onAuthorClick: (Article) -> Unit = {},
    private val onLikeClick: (Article) -> Unit = {},
    private val onShareClick: (Article) -> Unit = {},
    private val onBookmarkClick: (Article, android.widget.ImageButton) -> Unit = { _, _ -> },
    private val onBindBookmarkState: (Article, android.widget.ImageButton) -> Unit = { _, btn -> btn.setImageResource(R.drawable.ic_favorite_outline) }
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvArticleTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvArticleDescription)
        private val tvAuthorName: TextView = itemView.findViewById(R.id.tvAuthorName)
        private val tvPublishDate: TextView = itemView.findViewById(R.id.tvPublishDate)
        private val tvLikesCount: TextView = itemView.findViewById(R.id.tvLikesCount)
        private val ivArticleImage: ImageView = itemView.findViewById(R.id.ivArticleImage)
        private val ivAuthorAvatar: ImageView = itemView.findViewById(R.id.ivAuthorAvatar)
        private val btnLike: ImageButton = itemView.findViewById(R.id.btnLike)
        private val btnBookmark: ImageButton = itemView.findViewById(R.id.btnBookmark)

        fun bind(article: Article) {
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvAuthorName.text = article.authorName
            tvPublishDate.text = article.getFormattedDate()
            tvLikesCount.text = article.likesCount.toString()
            
            // Load article image from URL
            if (article.imageUrl.isNotEmpty()) {
                loadImageFromUrl(ivArticleImage, article.imageUrl)
            } else {
                ivArticleImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Set author initials if no avatar
            if (article.authorAvatar.isEmpty()) {
                // Set initials background and text
                ivAuthorAvatar.setBackgroundResource(R.drawable.circle_background)
                // For now, just set a placeholder
            }

            // Click listeners
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = android.content.Intent(context, com.apk.blogapp.ArticleDetailActivity::class.java)
                intent.putExtra("articleId", article.id)
                context.startActivity(intent)
            }
            // Set bookmark state (owner can override to query Firestore or set filled for favorites list)
            onBindBookmarkState(article, btnBookmark)
            ivAuthorAvatar.setOnClickListener { onAuthorClick(article) }
            tvAuthorName.setOnClickListener { onAuthorClick(article) }
            btnLike.setOnClickListener { onLikeClick(article) }
            btnBookmark.setOnClickListener { 
                onBookmarkClick(article, btnBookmark)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
        try {
            android.util.Log.d("ArticleAdapter", "onBindViewHolder position=$position title=${articles[position].title}")
            val itemView = holder.itemView
            android.util.Log.d("ArticleAdapter", "itemView visibility=${itemView.visibility} alpha=${itemView.alpha} height=${itemView.height} attached=${itemView.isAttachedToWindow}")
            // Temporary visual aid: tint the item background slightly so it's obvious in the UI while debugging
            try {
                itemView.setBackgroundColor(android.graphics.Color.parseColor("#30FFEB3B")) // semi-transparent yellow
            } catch (e: Exception) {
                // ignore
            }
        } catch (e: Exception) {
            android.util.Log.e("ArticleAdapter", "onBindViewHolder log failed: ${e.message}")
        }
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        android.util.Log.d("ArticleAdapter", "updateArticles - count=${articles.size}")
        notifyDataSetChanged()
    }

    fun addArticle(article: Article) {
        articles.add(0, article) // Add to top
        notifyItemInserted(0)
    }

    fun updateArticle(article: Article) {
        val index = articles.indexOfFirst { it.id == article.id }
        if (index != -1) {
            articles[index] = article
            notifyItemChanged(index)
        }
    }

    fun removeArticle(articleId: String) {
        val index = articles.indexOfFirst { it.id == articleId }
        if (index != -1) {
            articles.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    
    private fun loadImageFromUrl(imageView: ImageView, url: String) {
        try {
            // Check if it's a base64 image (with or without data URI prefix)
            if (url.startsWith("data:image/")) {
                val base64String = url.substring(url.indexOf(",") + 1)
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else if (url.isNotEmpty() && !url.startsWith("http")) {
                // Assume it's a base64 string without data URI prefix
                try {
                    val decodedBytes = Base64.decode(url, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    } else {
                        imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    }
                } catch (e: Exception) {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                // For regular URLs, use placeholder for now
                // TODO: Implement with Glide or Picasso for better image loading
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } catch (e: Exception) {
            // Fallback to placeholder if any error occurs
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}