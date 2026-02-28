package com.example.assignment02

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.assignment02.databinding.FragmentBroadcastOptionBinding

class BroadcastOptionFragment : Fragment() {

    private var _binding: FragmentBroadcastOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBroadcastOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = arrayOf("Custom broadcast receiver", "System battery notification receiver")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
        binding.spinnerOptions.adapter = adapter

        binding.btnProceed.setOnClickListener {
            val selectedOptionPosition = binding.spinnerOptions.selectedItemPosition
            if (selectedOptionPosition == 0) {
                findNavController().navigate(R.id.action_nav_broadcast_to_broadcastInputFragment)
            } else if (selectedOptionPosition == 1) {
                findNavController().navigate(R.id.action_nav_broadcast_to_broadcastBatteryFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
