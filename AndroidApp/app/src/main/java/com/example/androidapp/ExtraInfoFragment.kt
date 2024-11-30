package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidapp.models.UserInfo

class ExtraInfoFragment : Fragment() {

    private var partialUserInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            partialUserInfo = it.getParcelable("partialUserInfo")
        }
    }

    private fun createAccount(
        partialUserInfo: UserInfo,
        email: String,
        password: String,
        weight: Float,
        height: Int
    ): Boolean {
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.extra_info_fragment, container, false)
        val emailEntry = view.findViewById<TextView>(R.id.emailInputView)
        val passwordEntry = view.findViewById<TextView>(R.id.passwordInputView)
        val repeatPasswordEntry = view.findViewById<TextView>(R.id.repeatPasswordInputView)
        val userWeightEntry = view.findViewById<TextView>(R.id.weightInputView)
        val userHeightEntry = view.findViewById<TextView>(R.id.heightInputView)
        val confirmAccountButton = view.findViewById<Button>(R.id.confirmAccountButton)

        confirmAccountButton.setOnClickListener {
            val email = emailEntry.text.toString()
            val password = passwordEntry.text.toString()
            val repeatPassword = repeatPasswordEntry.text.toString()
            val userWeightString = userWeightEntry.text.toString()
            val userHeightString = userHeightEntry.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty() && userWeightString.isNotEmpty() && userHeightString.isNotEmpty()) {
                val userWeight = userWeightString.toFloat()
                val userHeight = userHeightString.toInt()
                if (userWeight > 0 && userHeight > 0) {
                    if (password == repeatPassword) {
                        if (password.length > 7) {
                            if (email.contains("@")) {
                                if (createAccount(
                                        partialUserInfo!!,
                                        email,
                                        password,
                                        userWeight,
                                        userHeight
                                    )
                                ) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Contul a fost creat cu succes.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(requireContext(), LoginActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            } else Toast.makeText(
                                requireContext(),
                                "Email-ul introdus nu este corect.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else Toast.makeText(
                            requireContext(),
                            "Parola trebuie sa contina minim 8 caractere.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Parola nu a fost rescrisa la fel.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Greutatea si inaltimea trebuie sa fie valori pozitive.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Toate campurile trebuie completate.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(userInfo: UserInfo) = ExtraInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("partialUserInfo", userInfo)
            }
        }
    }
}