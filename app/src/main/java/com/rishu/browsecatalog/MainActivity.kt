package com.rishu.browsecatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rishu.browsecatalog.data.createCatalogue
import com.rishu.browsecatalog.ui.catalogue.CatalogueScreen
import com.rishu.browsecatalog.ui.catalogue.CatalogueViewModel
import com.rishu.browsecatalog.ui.theme.BrowseCatalogTheme

class MainActivity : ComponentActivity() {
    private val model by viewModels<CatalogueViewModel> {
        viewModelFactory { initializer { CatalogueViewModel(createCatalogue()) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { BrowseCatalogTheme { CatalogueScreen(model) } }
    }
}
