package com.mvp.payment.application.repository

import com.mvp.payment.domain.model.payment.listener.Product
import com.mvp.payment.infrastruture.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductRepositoryTest {

    private val productRepository: ProductRepository = mockk()

    @Test
    fun `test findByExternalId`() {
        val externalId = "some-external-id"
        val product = Product(externalId = externalId)
        val productList = listOf(product)

        // Mocking the behavior
        every { productRepository.findByExternalId(externalId) } returns productList

        // Call the method
        val result = productRepository.findByExternalId(externalId)

        // Verify the result
        assertEquals(productList, result)

        // Verify interactions (optional)
        verify(exactly = 1) { productRepository.findByExternalId(externalId) }
    }
}
