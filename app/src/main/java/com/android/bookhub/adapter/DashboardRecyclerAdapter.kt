package com.android.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Movie
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.bookhub.R
import com.android.bookhub.activity.DescriptionActivity
import com.android.bookhub.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context,val itemList : ArrayList<Book>):RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtBookName:TextView = view.findViewById(R.id.txtBookName)
        val txtBookAuthor:TextView = view.findViewById(R.id.txtBookAuthor)
        val txtBookRating:TextView = view.findViewById(R.id.txtBookRating)
        val txtBookPrice:TextView = view.findViewById(R.id.txtBookPrice)
        val imgBookImg:ImageView = view.findViewById(R.id.imgBookImage)
        val llContent : LinearLayout = view.findViewById(R.id.recyclerDashboard_linear_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)

        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
        //holder.imgBookImg.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImg)

        holder.llContent.setOnClickListener{
            //Toast.makeText(context,"Clicked on ${holder.txtBookName.text}",Toast.LENGTH_SHORT).show()

            val intent = Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookID)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}