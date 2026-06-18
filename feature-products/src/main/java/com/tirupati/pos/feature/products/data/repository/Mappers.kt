package com.tirupati.pos.feature.products.data.repository

import com.tirupati.pos.feature.products.data.local.LocalCompany
import com.tirupati.pos.feature.products.data.local.LocalProduct
import com.tirupati.pos.feature.products.domain.model.Company
import com.tirupati.pos.feature.products.domain.model.Product

fun LocalCompany.toDomain(): Company {
    return Company(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Company.toLocal(): LocalCompany {
    return LocalCompany(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun LocalProduct.toDomain(): Product {
    return Product(
        id = id,
        companyId = companyId,
        itemCode = itemCode,
        itemName = itemName,
        unit = unit,
        purchaseRate = purchaseRate,
        sellingRate = sellingRate,
        stockQuantity = stockQuantity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Product.toLocal(): LocalProduct {
    return LocalProduct(
        id = id,
        companyId = companyId,
        itemCode = itemCode,
        itemName = itemName,
        unit = unit,
        purchaseRate = purchaseRate,
        sellingRate = sellingRate,
        stockQuantity = stockQuantity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
