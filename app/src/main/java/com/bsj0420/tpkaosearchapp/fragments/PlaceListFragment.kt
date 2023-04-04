package com.bsj0420.tpkaosearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bsj0420.tpkaosearchapp.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {

    //private val binding : FragmentPlaceListBinding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }
    lateinit var binding : FragmentPlaceListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}