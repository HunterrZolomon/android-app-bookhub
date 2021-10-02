package com.android.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bookhub.R
import com.android.bookhub.adapter.DashboardRecyclerAdapter
import com.android.bookhub.model.Book
import com.android.bookhub.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    //lateinit var btnCheckInternet:Button

    lateinit var  recyclerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar

    val bookInfoList = arrayListOf<Book>()

    val ratingComparator = Comparator<Book>{book1,book2->
        val book1Rating = book1.bookRating.toString()
        val book2Rating = book2.bookRating.toString()
        book1Rating.compareTo(book2Rating,true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setHasOptionsMenu(true)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        /*btnCheckInternet =view.findViewById(R.id.btnCheckInternet)
        btnCheckInternet.setOnClickListener{
            if(ConnectionManager().checkConnectivity(activity as Context)){
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("Ok"){text,listener ->}
                dialog.setNegativeButton("Cancel"){text,listener->}
                dialog.create()
                dialog.show()
            }else{
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Ok"){text,listener ->}
                dialog.setNegativeButton("Cancel"){text,listener->}
                dialog.create()
                dialog.show()
            }

        }*/

        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                //Here we will handle the response
                //println("Response is $it")

                try{
                    progressLayout.visibility= View.GONE
                    val success = it.getBoolean("success")
                    if(success){

                        val data = it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val bookJSONObject:JSONObject = data.getJSONObject(i)
                            val bookObject = Book (
                                bookJSONObject.getString("book_id"),
                                bookJSONObject.getString("name"),
                                bookJSONObject.getString("author"),
                                bookJSONObject.getString("price"),
                                bookJSONObject.getString("rating"),
                                bookJSONObject.getString("image")
                            )
                            bookInfoList.add(bookObject)
                            recyclerAdapter = DashboardRecyclerAdapter(activity as Context,bookInfoList)

                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager
                            //recyclerDashboard.addItemDecoration(DividerItemDecoration(recyclerDashboard.context,(layoutManager as LinearLayoutManager).orientation))
                        }

                    }else {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occured!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e:JSONException){
                    Toast.makeText(activity as Context, "Unexpected JSON Error!!",Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener {
                //Here we will handle the errors
                //println("Error is $it")
                if(activity!=null) {
                    Toast.makeText(activity as Context, "Volley Error Occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9cce751950871b"
                    return headers
                }

            }

            queue.add(jsonObjectRequest)
        } else  {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){text,listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }



        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if(id==R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}