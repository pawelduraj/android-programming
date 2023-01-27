package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        title = "Map"
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val sydney = LatLng(-33.856624008034046, 151.21533961356047)
        val paris = LatLng(48.858490077684586, 2.294470569508355)
        val istanbul = LatLng(41.00868233044558, 28.980164698801946)
        val dubai = LatLng(25.197362019104713, 55.27436566900655)
        val london = LatLng(51.50084939688686, -0.12459321507162664)
        val vienna = LatLng(48.19143481542646, 16.380623508012796)
        val nice = LatLng(43.69487190330069, 7.280528232819627)
        val milano = LatLng(45.464441967395295, 9.192037439567162)
        val rioDeJaneiro = LatLng(-22.95092298020202, -43.21073571686171)
        val lasVegas = LatLng(37.268839360697434, -115.79661264126061)

        googleMap.addMarker(MarkerOptions().position(sydney).title("Store in Sydney"))
        googleMap.addMarker(MarkerOptions().position(paris).title("Store in Paris"))
        googleMap.addMarker(MarkerOptions().position(istanbul).title("Store in Istanbul"))
        googleMap.addMarker(MarkerOptions().position(dubai).title("Store in Dubai"))
        googleMap.addMarker(MarkerOptions().position(london).title("Store in London"))
        googleMap.addMarker(MarkerOptions().position(vienna).title("Store in Vienna"))
        googleMap.addMarker(MarkerOptions().position(nice).title("Store in Nice"))
        googleMap.addMarker(MarkerOptions().position(milano).title("Store in Milano"))
        googleMap.addMarker(MarkerOptions().position(rioDeJaneiro).title("Store in Rio de Janeiro"))
        googleMap.addMarker(MarkerOptions().position(lasVegas).title("Store in Las Vegas"))

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nice, 10f))
    }
}
