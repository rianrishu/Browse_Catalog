package com.rishu.browsecatalog.ui.catalogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rishu.browsecatalog.domain.Catalogue
import com.rishu.browsecatalog.domain.Product
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogueState(
    val products: List<Product> = emptyList(),
    val loading: Boolean = true,
    val loadFailed: Boolean = false,
    val delivery: Delivery = Delivery.Idle
)

enum class Delivery { Idle, Sending, Failed, Sent }

class CatalogueViewModel(private val catalogue: Catalogue) : ViewModel() {
    private val mutableState = MutableStateFlow(CatalogueState())
    val state = mutableState.asStateFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        mutableState.update { it.copy(loading = true, loadFailed = false) }
        try {
            val products = catalogue.products()
            mutableState.update { it.copy(products = products, loading = false) }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            mutableState.update { it.copy(loading = false, loadFailed = true) }
        }
    }

    fun resetEnquiry() = mutableState.update { it.copy(delivery = Delivery.Idle) }

    fun enquire(product: Product, name: String, email: String) {
        if (state.value.delivery == Delivery.Sending || state.value.delivery == Delivery.Sent) return
        mutableState.update { it.copy(delivery = Delivery.Sending) }
        viewModelScope.launch {
            val result = try {
                catalogue.enquire(product, name, email)
                Delivery.Sent
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Delivery.Failed
            }
            mutableState.update { it.copy(delivery = result) }
        }
    }
}
