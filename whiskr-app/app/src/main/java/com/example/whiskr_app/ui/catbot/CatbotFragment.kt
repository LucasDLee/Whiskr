package com.example.whiskr_app.ui.catbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.whiskr_app.databinding.FragmentCatbotAllChatsBinding

class CatbotFragment : Fragment() {

    private var _binding: FragmentCatbotAllChatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatbotAllChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textSlideshow
//        slideshowViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}