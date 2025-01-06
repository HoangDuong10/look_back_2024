package com.example.loadimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.loadimage.databinding.FragmentDialogLookBackBinding


class LookBackFragment : DialogFragment() {
    lateinit var binding : FragmentDialogLookBackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogLookBackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewBgLookBack.setOnClickListener {
            findNavController().navigate(R.id.lookback_fragment12)
        }
    }
}
