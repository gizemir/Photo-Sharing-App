package com.gizemir.photosharingapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.gizemir.photosharingapp.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSignup.setOnClickListener { signUp(it) }
        binding.buttonSignin.setOnClickListener { signIn(it) }

        //eğer uygulamada kayıtlı bir kullanıcı varsa tekrar giriş yapmasına gerek olmaması için direkt feed sayfasına yönlendirilir
        val currentUser = auth.currentUser
        if(currentUser != null){
            val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun signUp(view: View){
        val email = binding.editTextEmailText.text.toString()
        val password = binding.editTextPasswordText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            //interneti yormamak adına kutucukların boş olup olmadığını kontrol ettik
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //kullanıcı oluşturuldu
                    //diğer sayfaya gittik
                    val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }.addOnFailureListener { exception ->
                //kullanıcı oluşturulurken herhangi bir başarısızlık olursa(email yanlıs, parola az karakter)
                //sunucudan otomatik hata mesajı istedik
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signIn(view: View){
        val email = binding.editTextEmailText.text.toString()
        val password = binding.editTextPasswordText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(action)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}