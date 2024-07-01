package com.video.offline.videoplayer.gui.preferences.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import com.video.offline.videoplayer.R
import com.video.offline.videoplayer.databinding.FragmentThemeBinding
import com.video.offline.videoplayer.gui.BaseFragment


class ThemeFragment : BaseFragment() {
    private var _binding: FragmentThemeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ThemeAdapter

    override fun getTitle(): String = getString(R.string.theme)

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentThemeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        // Initialize listener
        val itemClickListener = ItemClickListener { s -> // Notify adapter
            binding.recyclerView.post(adapter::notifyDataSetChanged)
            // Display toast
            Toast.makeText(
                requireContext(), "Selected : $s", Toast.LENGTH_SHORT
            ).show()
        }

        // Set layout manager
        binding.recyclerView.setLayoutManager(LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false))
        val arrayList: ArrayList<String> =
            resources.getStringArray(R.array.daynight_mode_entries).toList() as ArrayList<String>
        // Initialize adapter
        adapter = ThemeAdapter(arrayList, itemClickListener, requireContext())
        // set adapter
        binding.recyclerView.setAdapter(adapter)
    }

}