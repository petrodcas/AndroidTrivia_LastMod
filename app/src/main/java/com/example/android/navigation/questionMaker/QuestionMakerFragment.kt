package com.example.android.navigation.questionMaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.navigation.R
import com.example.android.navigation.databinding.FragmentQuestionMakerBinding

class QuestionMakerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentQuestionMakerBinding>(inflater, R.layout.fragment_question_adder, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }
}