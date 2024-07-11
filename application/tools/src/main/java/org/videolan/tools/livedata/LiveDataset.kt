package org.videolan.tools.livedata

import androidx.lifecycle.MutableLiveData


class LiveDataset<T> : MutableLiveData<MutableList<T>>() {

    private val emptyList = mutableListOf<T>()

    private var internalList = emptyList

    fun isEmpty() = internalList.isEmpty()

    override fun setValue(value: MutableList<T>?) {
        internalList = value ?: emptyList
        super.setValue(value)
    }

    override fun getValue() = internalList

    fun get(position: Int) = internalList[position]

    fun getList() = internalList.toList()

    val size = internalList.size

    fun clear() {
        value = internalList.apply { clear() }
    }

    fun add(item: T) {
        value = internalList.apply { add(item) }
    }

    fun add(item: T, comparator: Comparator<T>) {
        var position = 0
        for (media in internalList) {
            if (comparator.compare(item, media) > 0) position++
            else break
        }
        value = internalList.apply { this.add(position, item) }
    }

    fun add(position: Int, item: T) {
        value = internalList.apply { add(position, item) }
    }

    fun add(items: List<T>) {
        value = internalList.apply { addAll(items.filter { !this.contains(it) }) }
    }

    fun remove(item: T) {
        value = internalList.apply { remove(item) }
    }

    fun replace(item: T) {
        value = internalList.apply {
            val index = indexOf(item)
            removeAt(index)
            add(index, item)
        }
    }

    fun remove(position: Int) {
        value = internalList.apply { removeAt(position) }
    }

    fun move(item: T, newIndex: Int) {
        val from = internalList.indexOf(item)
        if (from > 0) move(from, newIndex)
    }

    fun move(from: Int, to: Int) {
        value = internalList.apply {
            val item = this[from]
            removeAt(from)
            if (from > to)
                add(to, item)
            else
                add(to - 1, item)
        }
    }
}