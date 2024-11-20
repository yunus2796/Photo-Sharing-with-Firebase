package com.yunussevimli.photosharingwithfirebase.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yunussevimli.photosharingwithfirebase.R
import com.yunussevimli.photosharingwithfirebase.adaptor.PostAdaptor
import com.yunussevimli.photosharingwithfirebase.databinding.FragmentFeedBinding
import com.yunussevimli.photosharingwithfirebase.model.Post

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var popup : PopupMenu
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    val postList : ArrayList<Post> = arrayListOf()
    private var adapter : PostAdaptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // PopupMenu'yu burada başlatın
        popup = PopupMenu(requireContext(), binding.floatingActionButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)

        binding.floatingActionButton.setOnClickListener {
            floatingButtonTiklandi(it)
        }

        fireStoreVerileriAl()

        adapter = PostAdaptor(postList)
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter
    }

    private fun fireStoreVerileriAl(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener{value, error ->
            if(error != null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if(value != null) {
                    if(!value.isEmpty) {
                        postList.clear()
                        val documents = value.documents
                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val email = document.get("email") as String
                            val downloadURL = document.get("downloadURL") as String

                            val post = Post(email,comment,downloadURL)
                            postList.add(post)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun floatingButtonTiklandi(view: View){
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.yuklemeItem){
            val action = FeedFragmentDirections.actionFeedFragmentToYuklemeFragment()
            Navigation.findNavController(requireView()).navigate(action)
        } else if (item?.itemId == R.id.cikisItem){
            auth.signOut()
            val action = FeedFragmentDirections.actionFeedFragmentToKullaniciFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }

}