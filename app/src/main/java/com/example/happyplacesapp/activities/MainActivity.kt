package com.example.happyplacesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.utils.Constants
import com.example.happyplacesapp.utils.HappyPlaceApp
import com.example.happyplacesapp.utils.SwipeToEditCallback
import com.example.happyplacesapp.adapters.HappyPlaceAdapter
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.example.happyplacesapp.happy_place_database.HappyPlaceDAO
import com.example.happyplacesapp.happy_place_database.HappyPlaceEntity
import com.karumi.dexter.Dexter
import com.example.happyplacesapp.utils.SwipeToDeleteCallback
import kotlinx.coroutines.launch
import android.Manifest
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private var happyPlaceAdapter : HappyPlaceAdapter? = null

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val happyPlaceDao = (application as HappyPlaceApp).db.getHappyPlaceDao()

        binding?.fabAdd?.setOnClickListener {
            val addIntent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivity(addIntent)
        }

        displayAllHappyPlaces(happyPlaceDao)
        getLocationPermission()
    }

    private fun getLocationPermission() {
        Dexter.withContext(this@MainActivity).withPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    return
                } else if (report.isAnyPermissionPermanentlyDenied) {
                    showRationalDialogOnPermission()
                } else {
                    Toast.makeText(this@MainActivity, "we need access to your location to have the best app experience", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissionRequests: MutableList<PermissionRequest>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

        })
    }

    private fun showRationalDialogOnPermission() {

    }

    private fun displayAllHappyPlaces(happyPlaceDAO: HappyPlaceDAO) {

        lifecycleScope.launch {
            happyPlaceDAO.getAllHappyPlaces().collect { happyPlacesList ->
                setHappyPlacesRecyclerView(ArrayList(happyPlacesList))
            }
        }
    }

    private fun setHappyPlacesRecyclerView(happyPlaces: ArrayList<HappyPlaceEntity>) {
        if (happyPlaces.isEmpty()) {
            binding?.tvEmptyList?.visibility = View.VISIBLE
            binding?.rvHappyPlace?.visibility = View.GONE
            return
        }
        happyPlaceAdapter = HappyPlaceAdapter(this@MainActivity, happyPlaces)
        binding?.rvHappyPlace?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.rvHappyPlace?.adapter = happyPlaceAdapter

        binding?.tvEmptyList?.visibility = View.GONE
        binding?.rvHappyPlace?.visibility = View.VISIBLE

        happyPlaceAdapter!!.setOnClickListener(object : HappyPlaceAdapter.OnClickListener {
            override fun onClick(position: Int, entity: HappyPlaceEntity) {
                val detailsIntent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                detailsIntent.putExtra(Constants.RV_HAPPY_PLACE_ITEM, entity)
                startActivity(detailsIntent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvHappyPlace?.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, Constants.RV_ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlace)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val adapter = binding?.rvHappyPlace?.adapter as HappyPlaceAdapter
                val happyPlaceToDelete = adapter.notifyDeleteItem(viewHolder.adapterPosition)
                deleteHappyPlace(happyPlaceToDelete)
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlace)
    }
    fun deleteHappyPlace(happyPlace: HappyPlaceEntity) {
        val happyPlaceDao = (application as HappyPlaceApp).db.getHappyPlaceDao()

        lifecycleScope.launch {
            happyPlaceDao.deleteHappyPlace(happyPlace)
            Toast.makeText(applicationContext, "happy place deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}