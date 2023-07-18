package com.example.chimpunkofflinenav

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chimpunkofflinenav.databinding.ActivityMainBinding
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {
    companion object{
        val CHENNAI = LatLong(12.902661, 80.227296)
        val GOA = LatLong(15.568365, 73.752938)
    }
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidGraphicFactory.createInstance(application)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val contract = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            result?.data?.data?.let{uri->
                openmap(uri)
            }
        }
        b.openMap.setOnClickListener{
            contract.launch(
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply{
                 type = "*/*"
                 addCategory(Intent.CATEGORY_OPENABLE)
                }

            )

        }
    }
    fun openmap(uri: Uri){
        b.mapView.mapScaleBar.isVisible = true
        b.mapView.setBuiltInZoomControls(true)
        val cache = AndroidUtil.createTileCache(
            this,
            "mycache",
            b.mapView.model.displayModel.tileSize,
            1f,
            b.mapView.model.frameBufferModel.overdrawFactor
        )

        val stream = contentResolver.openInputStream(uri) as FileInputStream

        val mapStore = MapFile(stream)

        val renderLayer = TileRendererLayer(
            cache,
            mapStore,
            b.mapView.model.mapViewPosition,
            AndroidGraphicFactory.INSTANCE
        )

        renderLayer.setXmlRenderTheme(

            InternalRenderTheme.DEFAULT
        )

        b.mapView.layerManager.layers.add(renderLayer)

        b.mapView.setCenter(CHENNAI)
        b.mapView.setZoomLevel(18)


    }
}