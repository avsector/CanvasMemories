package com.samsaz.canvasmemories.util

import androidx.collection.SparseArrayCompat


/**
 * Copyright 2018
 * Created and maintained by Hamid Moazzami
 */

class SparseArrayIterator<T>(private val array: SparseArrayCompat<T>) : Iterator<T> {
    private var index: Int = 0

    override fun hasNext(): Boolean {
        return array.size() > index
    }

    override fun next(): T {
        return array.valueAt(index++)
    }
}

fun <T> SparseArrayCompat<T>.iterator(): SparseArrayIterator<T> {
    return SparseArrayIterator(this)
}
