package com.vshum.turbogum.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vshum.turbogum.R
import com.vshum.turbogum.databinding.FragmentHelpScreenBinding

class HelpScreenFragment : Fragment() {

    private lateinit var binding: FragmentHelpScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("vvshumilov@mail.ru"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "О приложении Вкладыши Turbo")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Добрый день, хотел бы получить пароль к приложению, что для этого нужно?"
            )
            startActivity(Intent.createChooser(intent, "Отправить сообщение"))
        }
    }

}