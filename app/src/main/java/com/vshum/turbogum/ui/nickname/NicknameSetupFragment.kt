package com.vshum.turbogum.ui.nickname

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentNicknameSetupBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.nickname.impl.NicknameContract
import com.vshum.turbogum.ui.nickname.impl.NicknamePresenterImpl

/** Shown right after the first sign-in until the user picks a nickname. */
class NicknameSetupFragment : Fragment(), NicknameContract.View {

    private var _binding: FragmentNicknameSetupBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var presenter: NicknameContract.Presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator = app.servicesLocator.providerNavigator(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNicknameSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireContext().applicationContext as App
        presenter = NicknamePresenterImpl(
            this,
            app.servicesLocator.providerAuthRepository(),
            app.servicesLocator.providerUserRepository()
        )

        binding.inputNickname.setText(presenter.prefilledNickname())

        binding.btnContinue.setOnClickListener {
            presenter.saveNickname(binding.inputNickname.text.toString().trim())
        }
    }

    override fun setLoading(loading: Boolean) {
        binding.loadingProgress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnContinue.isEnabled = !loading
    }

    override fun onSaved() {
        appNavigator.navigateTo(Screen.WRAPPERS_LIST_SCREEN)
    }

    override fun onError(message: String) {
        val text = when (message) {
            NicknamePresenterImpl.EMPTY_NICKNAME_ERROR -> getString(R.string.nickname_error_empty)
            else -> message
        }
        binding.errorText.text = text
        binding.errorText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
