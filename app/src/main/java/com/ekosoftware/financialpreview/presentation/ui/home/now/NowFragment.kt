package com.ekosoftware.financialpreview.presentation.ui.home.now

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ekosoftware.financialpreview.core.Resource
import com.ekosoftware.financialpreview.databinding.FragmentNowBinding
import com.ekosoftware.financialpreview.databinding.FragmentPageDefaultBinding

class NowFragment : Fragment() {
    private var _binding: FragmentPageDefaultBinding? = null
    private val binding get() = _binding!!

    private val nowViewModel by activityViewModels<NowViewModel>()

    companion object {
        private const val TAG = "NowFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nowViewModel.accounts.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> Log.d(TAG, "Now -> Cargando cuentas")
                is Resource.Success -> result.data.forEach { Log.d(TAG, "Cuenta $it") }
                is Resource.Failure -> Log.d(TAG, "Now -> Cuentas falló porque: ${result.exception}")
            }
        })
        nowViewModel.movements.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> Log.d(TAG, "Now -> Cargando movimientos")
                is Resource.Success -> result.data.forEach { Log.d(TAG, "Movimiento: $it") }
                is Resource.Failure -> Log.d(TAG, "Now -> Movimientos falló porque: ${result.exception}")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}