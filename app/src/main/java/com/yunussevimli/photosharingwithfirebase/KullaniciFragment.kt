package com.yunussevimli.photosharingwithfirebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.yunussevimli.photosharingwithfirebase.databinding.FragmentKullaniciBinding

class KullaniciFragment : Fragment() {
    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kayitButton.setOnClickListener { kayitOl(it) }
        binding.girisButton.setOnClickListener { girisYap(it) }
    }

    fun kayitOl(view: View){
        val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
        Navigation.findNavController(view).navigate(action)
    }

    fun girisYap(view: View){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}