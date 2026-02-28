package com.example.assignment02

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment02.databinding.FragmentBroadcastCustomReceiverBinding

class BroadcastCustomReceiverFragment : Fragment() {

    private var _binding: FragmentBroadcastCustomReceiverBinding? = null
    private val binding get() = _binding!!

    private val customAction = "com.example.assignment02.ACTION_CUSTOM_BROADCAST"
    private var messageToBroadcast: String? = null

    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == customAction) {
                val receivedMessage = intent.getStringExtra("EXTRA_MESSAGE")
                binding.tvReceiverStatus.text = "Received Broadcast Message:\n$receivedMessage"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBroadcastCustomReceiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageToBroadcast = arguments?.getString("message")

        binding.btnSendBroadcast.setOnClickListener {
            val intent = Intent(customAction).apply {
                putExtra("EXTRA_MESSAGE", messageToBroadcast)
                setPackage(requireContext().packageName)
            }
            requireContext().sendBroadcast(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(customAction)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(customReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireContext().registerReceiver(customReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(customReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
