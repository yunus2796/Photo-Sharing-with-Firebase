package com.yunussevimli.photosharingwithfirebase

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.yunussevimli.photosharingwithfirebase.databinding.FragmentYuklemeBinding
import java.util.UUID

class YuklemeFragment : Fragment() {
    private var _binding: FragmentYuklemeBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher : ActivityResultLauncher<String> // izin isteme işlemi için ActivityResultLauncher nesnesi oluşturulur
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent> // galeriye erişim işlemi için ActivityResultLauncher nesnesi oluşturulur
    private var secilenGorsel : Uri? = null // seçilen görselin uri'si tutulur
    private var secilenBitmap : Bitmap? = null // seçilen görselin bitmap'i tutulur

    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        auth = Firebase.auth
        storage = Firebase.storage
    }

    private fun registerLauncher() { // ActivityResultLauncher nesneleri oluşturulur
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //izin verildi
                    //galeriye erişim işlemleri yapılabilir
                    val galeriIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ) // galeriye erişim için intent oluşturulur
                    activityResultLauncher.launch(galeriIntent) // galeriye erişim işlemi başlatılır
                } else {
                    //izin verilmedi
                    Toast.makeText(
                        requireContext(),
                        "Galeriye erişmek için izin vermelisiniz.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result -> // galeriye erişim işlemi sonucu alınır
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                //galeriye erişim başarılı
                val intentFromResult = result.data
                if(intentFromResult != null){
                    secilenGorsel = intentFromResult.data
                    try {
                        if(Build.VERSION.SDK_INT >= 28){ // Android 9 ve üzeri için galeriye erişim işlemleri
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        } else { // Android 8 ve altı için galeriye erişim işlemleri
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    } catch (e:Exception){
                        println(e.localizedMessage)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYuklemeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.yukleButton.setOnClickListener { yukleTiklandi(it) }
        binding.imageView.setOnClickListener { gorselSec(it) }
    }

    fun yukleTiklandi(view: View){
        val uuid = UUID.randomUUID() // rastgele bir uuid oluşturulur.
        val gorselAdi = "${uuid}.jpg" // rastgele oluşturulan uuid'nin sonuna .jpg uzantısı eklenir
        val reference = storage.reference
        val gorselReference = reference.child("images").child(gorselAdi) // storage'da images klasörü altında uuid.jpg adında bir dosya oluşturulur. ismi rastgele oluşturmazsak her seferinde aynı isimde dosya oluşturulur ve eski dosya üzerine yazılır.
        if(secilenGorsel != null) {
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener { uploadTask ->
                //url'yi alma işlemi yapacağız.
                gorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println(downloadUrl)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun gorselSec(view: View){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){ // Android 10 ve üzeri için galeriye erişim izni kontrolü
            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş, izin istememiz gerekiyor.
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_MEDIA_IMAGES)){
                    //izin daha önce reddedilmiş, kullanıcıya neden izin istediğimizi snackbar ile göstermeliyiz.
                    Snackbar.make(view,"Galeriye erişmek için izin vermelisiniz.",Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin Ver",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                }
                else{
                    //izin daha önce reddedilmemiş, izin istememiz gerekiyor.
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                //izin verilmiş, galeriye erişebiliriz.
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // galeriye erişim için intent oluşturulur
                activityResultLauncher.launch(galeriIntent) // galeriye erişim işlemi başlatılır
            }
        } else { // Android 9 ve altı için galeriye erişim izni kontrolü
            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmemiş, izin istememiz gerekiyor.
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //izin daha önce reddedilmiş, kullanıcıya neden izin istediğimizi snackbar ile göstermeliyiz.
                    Snackbar.make(view,"Galeriye erişmek için izin vermelisiniz.",Snackbar.LENGTH_INDEFINITE).setAction(
                        "İzin Ver",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    ).show()
                }
                else{
                    //izin daha önce reddedilmemiş, izin istememiz gerekiyor.
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                //izin verilmiş, galeriye erişebiliriz.
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // galeriye erişim için intent oluşturulur
                activityResultLauncher.launch(galeriIntent) // galeriye erişim işlemi başlatılır
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}