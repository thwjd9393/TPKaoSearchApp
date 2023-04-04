package com.bsj0420.tpkaosearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bsj0420.tpkaosearchapp.databinding.FragmentPlaceMapBinding

class PlaceMapFragment : Fragment() {

    //private val binding : FragmentPlaceListBinding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }
    lateinit var binding : FragmentPlaceMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceMapBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}