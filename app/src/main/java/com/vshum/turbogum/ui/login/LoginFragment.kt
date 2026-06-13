package com.vshum.turbogum.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.vshum.turbogum.App
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentLoginBinding
import com.vshum.turbogum.navigator.AppNavigator
import com.vshum.turbogum.navigator.Screen
import com.vshum.turbogum.ui.login.impl.LoginContract
import com.vshum.turbogum.ui.login.impl.LoginPresenterImpl

/** Login screen: email/password sign-in & registration, plus Google sign-in. */
class LoginFragment : Fragment(), LoginContract.View {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var appNavigator: AppNavigator
    private lateinit var presenter: LoginContract.Presenter

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            presenter.onGoogleAccount(account)
        } catch (e: ApiException) {
            onAuthError(e.localizedMessage ?: e.toString())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val app = context.applicationContext as App
        appNavigator = app.servicesLocator.providerNavigator(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireContext().applicationContext as App
        presenter = LoginPresenterImpl(
            this,
            app.servicesLocator.providerAuthRepository(),
            app.servicesLocator.providerUserRepository()
        )

        binding.btnLogin.setOnClickListener {
            presenter.signInEmail(
                binding.inputEmail.text.toString().trim(),
                binding.inputPassword.text.toString()
            )
        }

        binding.btnRegister.setOnClickListener {
            presenter.registerEmail(
                binding.inputEmail.text.toString().trim(),
                binding.inputPassword.text.toString()
            )
        }

        binding.btnGoogle.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(requireActivity(), options)
            googleSignInLauncher.launch(client.signInIntent)
        }
    }

    override fun setLoading(loading: Boolean) {
        binding.loadingProgress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
        binding.btnRegister.isEnabled = !loading
        binding.btnGoogle.isEnabled = !loading
    }

    override fun onAuthSuccess(needsNickname: Boolean) {
        binding.errorText.visibility = View.GONE
        appNavigator.navigateTo(
            if (needsNickname) Screen.NICKNAME_SCREEN else Screen.WRAPPERS_LIST_SCREEN
        )
    }

    override fun onAuthError(message: String) {
        val text = if (message == LoginPresenterImpl.EMPTY_FIELDS_ERROR) {
            getString(R.string.login_error_empty_fields)
        } else {
            message
        }
        binding.errorText.text = text
        binding.errorText.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
