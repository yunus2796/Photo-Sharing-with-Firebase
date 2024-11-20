package com.yunussevimli.photosharingwithfirebase.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yunussevimli.photosharingwithfirebase.databinding.FragmentKullaniciBinding

class KullaniciFragment : Fragment() {
    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth //FirebaseAuth sınıfından bir nesne oluşturduk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kayitButton.setOnClickListener { kayitOl(it) }
        binding.girisButton.setOnClickListener { girisYap(it) }

        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null){ // kullanici zaten giriş yapmışsa feed'e gönderiyoruz.
            val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun kayitOl(view: View){
        val email = binding.emailText.text.toString() //Kullanıcının girdiği email ve şifreyi alıyoruz
        val password = binding.passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){ 
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task-> //FirebaseAuth sınıfının createUserWithEmailAndPassword metodu ile kullanıcı oluşturuyoruz
                if(task.isSuccessful){ //Kullanıcı oluşturulduysa
                    val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment() //Kullanıcı oluşturulduktan sonra FeedFragment'e yönlendiriyoruz
                    Navigation.findNavController(view).navigate(action)
                }
            }.addOnFailureListener { expection -> //Kullanıcı oluşturulamazsa
                Toast.makeText(requireContext(),expection.localizedMessage,Toast.LENGTH_LONG).show() //Hata mesajı gösteriyoruz
            }
        }


    }

    fun girisYap(view: View){
        val email = binding.emailText.text.toString() //Kullanıcının girdiği email ve şifreyi alıyoruz
        val password = binding.passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val action = KullaniciFragmentDirections.actionKullaniciFragmentToFeedFragment()
                Navigation.findNavController(view).navigate(action)
            }.addOnFailureListener { expection -> //Kullanıcı giriş yapamazsa
                Toast.makeText(requireContext(),expection.localizedMessage,Toast.LENGTH_LONG).show() //Hata mesajı gösteriyoruz

            }
        }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}