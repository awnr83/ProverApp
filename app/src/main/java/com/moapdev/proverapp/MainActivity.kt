package com.moapdev.proverapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moapdev.proverapp.databinding.ActivityMainBinding
import com.moapdev.proverapp.orden.OrdenActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val response = IdpResponse.fromResultIntent(it.data)

        if (it.resultCode == RESULT_OK){
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null){
                mBinding.progresBar.visibility= View.GONE
                Snackbar.make(mBinding.root, "Bienvenido", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            if (response == null){
                Snackbar.make(mBinding.root,"Hasta pronto", Snackbar.LENGTH_SHORT).show()
                finish()
            } else {
                response.error?.let {
                    if (it.errorCode == ErrorCodes.NO_NETWORK){
                        Snackbar.make(mBinding.root, "Sin red", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(mBinding.root,"Código de error: ${it.errorCode}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
        //HomeViewModel->  firestore.collection("productos").addSnapshotListener
        //Hay que liberar eliminar el Oyente para que no haya mas de uno, esto consumiria mas datos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_main)

        configAuth()

        val navCont= this.findNavController(R.id.myHostNavFragment)
        NavigationUI.setupActionBarWithNavController(this, navCont)
        mAppBarConfiguration= AppBarConfiguration(navCont.graph)
    }

    //menu y button up
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mnuCerrar -> {
                AuthUI.getInstance().signOut(this)
                    .addOnSuccessListener {
                        Snackbar.make(mBinding.root,"Hasta luego", Snackbar.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            mBinding.progresBar.visibility= View.VISIBLE
                        }
                    }
            }
            R.id.mnuHistorial->{
                startActivity(Intent(this, OrdenActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navCont=this.findNavController(R.id.myHostNavFragment)
        return NavigationUI.navigateUp(navCont,mAppBarConfiguration)
    }

    private fun configAuth(){
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null){
                mBinding.progresBar.visibility= View.GONE
//                supportActionBar?.title = "Historia"  //titulo
            } else {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build())

                resultLauncher.launch(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)       //con esto no ofrece las cuentas ya usadas cuando se cierra sesion
                        .build())
            }
        }
    }
}