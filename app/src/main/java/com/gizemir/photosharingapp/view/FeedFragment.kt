package com.gizemir.photosharingapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gizemir.photosharingapp.R
import com.gizemir.photosharingapp.adapter.PostAdapter
import com.gizemir.photosharingapp.databinding.FragmentFeedBinding
import com.gizemir.photosharingapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var popup: PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    var postList: ArrayList<Post> = arrayListOf()
    private var adapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { floatingButtonClick(it) }
        popup = PopupMenu(requireContext(), binding.floatingActionButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)

        firestoreReadData()

        adapter = PostAdapter(postList)
        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFeed.adapter = adapter
    }
    private fun firestoreReadData(){
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            //Firestore'da oluşturduğumuz "Posts" isimli collectiondaki verileri anlık olarak çekiyoruz
            if(error != null){
                //eğer çekemezsek hata mesajı
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        //eğer verileri çekersek
                        postList.clear()
                        //firestore'daki "post" collectionındaki documentsleri getirdik(document: "posts" altındaki kaydedilen veriler bütünü)
                        val documents = value.documents
                        for(document in documents){
                            val comment = document.get("comment").toString()
                            val email = document.get("email").toString()
                            val downloadUrl = document.get("downloadUrl").toString()

                            val post = Post(comment, email, downloadUrl)
                            postList.add(post)
                        }
                        //yeni veriler geldiğinde adapteri baştan oluştur
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    fun floatingButtonClick(view: View){
        popup.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        //buttondaki popup elemanlarına tıklandığında ne olacak
        if(item?.itemId == R.id.uploadItem){
            //resim yükleme işlemi
            val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }else if(item?.itemId == R.id.signoutItem){
            //çıkış yapma işlemi
            auth.signOut()
            //çıkış yapıldı, kullanıcı giriş sayfasına geri dönüldü
            val action = FeedFragmentDirections.actionFeedFragmentToUserFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }


}