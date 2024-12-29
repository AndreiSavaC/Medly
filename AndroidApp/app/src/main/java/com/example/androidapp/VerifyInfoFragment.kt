package com.example.androidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.androidapp.models.UserInfo

class VerifyInfoFragment : Fragment() {

    private var partialUserInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION") partialUserInfo = it.getParcelable("partialUserInfo")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.verify_info_fragment, container, false)
        val firstNameTextView = view.findViewById<TextView>(R.id.weightInputView)
        val lastNameTextView = view.findViewById<TextView>(R.id.emailInputView)
        val genderTextView = view.findViewById<TextView>(R.id.heightInputView)
        val birthDateTextView = view.findViewById<TextView>(R.id.birthDateTextView)
        val confirmButton = view.findViewById<Button>(R.id.confirmAccountButton)

        partialUserInfo?.let {
            firstNameTextView.text = it.firstName
            lastNameTextView.text = it.lastName
            genderTextView.text = it.gender
            birthDateTextView.text = it.birthDate.toString()
        }

        confirmButton.isEnabled = false
        confirmButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), android.R.color.darker_gray
            )
        )

        Handler(Looper.getMainLooper()).postDelayed({
            confirmButton.isEnabled = true
            confirmButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.mainRed
                )
            )
        }, 1500)

        confirmButton.setOnClickListener {
            val fragment = ExtraInfoFragment.newInstance(partialUserInfo!!)
            parentFragmentManager.beginTransaction().replace(R.id.framgment_container, fragment)
                .addToBackStack(null).commit()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(userInfo: UserInfo) = VerifyInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("partialUserInfo", userInfo)
            }
        }
    }
}