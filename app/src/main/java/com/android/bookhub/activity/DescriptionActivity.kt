package com.android.bookhub.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.bookhub.R
import com.android.bookhub.database.BookDatabase
import com.android.bookhub.database.BookEntity
import com.android.bookhub.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName:TextView
    lateinit var txtBookAuthor:TextView
    lateinit var txtBookPrice:TextView
    lateinit var txtBookRating:TextView
    lateinit var imgBookImg:ImageView
    lateinit var txtBookDesc:TextView
    lateinit var btnAddToFav:Button
    lateinit var progressBar:ProgressBar
    lateinit var progressLayout:RelativeLayout

    lateinit var toolbar: Toolbar

    var bookID:String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookNameDescription)
        txtBookAuthor = findViewById(R.id.txtBookAuthorDescription)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRatingDescription)
        imgBookImg = findViewById(R.id.imgBookImageDescription)
        txtBookDesc = findViewById(R.id.txtBookDescription)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"


        if(intent!=null){
            bookID = intent.getStringExtra("book_id")
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Intent error occurred!!",Toast.LENGTH_SHORT).show()
        }

        if(bookID=="100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Book ID not fetched from intent!!",Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id",bookID)


        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){
            val jsonRequest = @SuppressLint("SetTextI18n")
            object:JsonObjectRequest(Method.POST,url,jsonParams, Response.Listener {

                try{
                    val success = it.getBoolean("success")
                    if(success){
                        val bookJsonObject = it.getJSONObject("book_data")
                        progressLayout.visibility = View.GONE

                        val bookImageUrl = bookJsonObject.getString("image")

                        Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImg)
                        txtBookName.text = bookJsonObject.getString("name")
                        txtBookAuthor.text = bookJsonObject.getString("author")
                        txtBookPrice.text = bookJsonObject.getString("price")
                        txtBookRating.text = bookJsonObject.getString("rating")
                        txtBookDesc.text = bookJsonObject.getString("description")

                        val bookEntity = BookEntity(
                            bookID?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDesc.text.toString(),
                            bookImageUrl
                        )

                        val checkFav = DBAsyncTask(applicationContext,bookEntity,1).execute()
                        val isFav = checkFav.get()

                        if(isFav){
                            btnAddToFav.text = "Remove from Favourites"
                            val favColor = ContextCompat.getColor(applicationContext,R.color.blue)
                            btnAddToFav.setBackgroundColor(favColor)
                        }else{
                            btnAddToFav.text = "Add to Favourites"
                            val noFavColor = ContextCompat.getColor(applicationContext,R.color.black)
                            btnAddToFav.setBackgroundColor(noFavColor)
                        }

                        btnAddToFav.setOnClickListener{
                            if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){

                                val async = DBAsyncTask(applicationContext,bookEntity,2).execute()
                                val result = async.get()
                                if(result){
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Book added to Favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    btnAddToFav.text = "Remove from Favourites"
                                    val favColor = ContextCompat.getColor(applicationContext,R.color.blue)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Cannot add to Favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                val async = DBAsyncTask(applicationContext,bookEntity,3).execute()
                                val result = async.get()

                                if(result){
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Book removed from Favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    btnAddToFav.text = "Add to Favourites"
                                    val noFavColor = ContextCompat.getColor(applicationContext,R.color.black)
                                    btnAddToFav.setBackgroundColor(noFavColor)
                                }else{
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Cannot be removed from Favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }


                    }else{
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch(e:Exception){
                    Toast.makeText(
                        this@DescriptionActivity,
                        "JSON Error Occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            },Response.ErrorListener {
                Toast.makeText(
                    this@DescriptionActivity,
                    "Volley Error Occurred!!",
                    Toast.LENGTH_SHORT
                ).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "9cce751950871b"
                    return headers
                }
            }

            queue.add(jsonRequest)
        } else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){text,listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){

        //Mode 1 = Check DB if book is already in Favourites or not
        //Mode 2 = Save the book into DB as favourite
        //Mode 3 = Remove the favourite book

        val db = Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode){
                1->{
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null
                }
                2->{
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3->{
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }



            return false
        }

    }
}