package com.moapdev.proverapp.add

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.moapdev.proverapp.Constants
import com.moapdev.proverapp.entities.EventPost
import com.moapdev.proverapp.home.InterfaceHA
import com.moapdev.proverapp.databinding.FragmentDialogAddBinding
import com.moapdev.proverapp.entities.Producto

class AddDialogFragment:DialogFragment(),DialogInterface.OnShowListener {

    private lateinit var mBinding: FragmentDialogAddBinding
    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private var mProductoSelected: Producto?=null
    private var mPhotoSelectedUri: Uri?=null

    private val resultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode==Activity.RESULT_OK){
            mPhotoSelectedUri= it.data?.data

            // mBinding?.imgProducto.setImageURI(mPhotoSelectedUri)     //Se puede cargar assi o por Glide
            Glide.with(this)
                .load(mPhotoSelectedUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //guarda toda la imagen
                .centerCrop()
                .into(mBinding.imgProducto)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { activity->                 //la actividad es distinta de null
            mBinding= FragmentDialogAddBinding.inflate(LayoutInflater.from(context))
            mBinding?.let {                         //mBinding es distinto de null
                var title: String
                if(mProductoSelected==null)
                    title = "Agregar Producto"
                else
                    title = "Modificar Producto"

                val builder=AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setPositiveButton("Agregar",null)
                    .setNegativeButton("Cancelar",null)
                    .setView(it.root)

                val dialog= builder.create()
                dialog.setOnShowListener(this)

                return dialog
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onShow(p0: DialogInterface?) {

        inicializarProducto()
        configBuscarImagen()

        val dialog= dialog as? AlertDialog
        dialog?.let{            //si dialog no es null
            positiveButton=it.getButton(Dialog.BUTTON_POSITIVE)
            negativeButton=it.getButton(Dialog.BUTTON_NEGATIVE)

            positiveButton?.setOnClickListener{
                mBinding?.let {         //al hacer asi se corrobora de que no este null
                    enableUI(false)     //deshabilita los botones para q no se haga otro ingreso
                    uploadImage(mProductoSelected?.id){ eventPost ->
                        if(eventPost.isSuccess) {   //si la imagen se subio correctamente
                            //producto nuevo
                            if (mProductoSelected == null) {
                                val producto = Producto(
                                    name = it.etName.text.toString().trim(),
                                    descripcion = it.etDescripcion.text.toString().trim(),
                                    precio = it.etPrecio.text.toString().toDouble(),
                                    cantidad = it.etCantidad.text.toString().toInt(),
                                    imgUrl= eventPost.photoUrl
                                )
                                save(producto, eventPost.documentId!!)
                            }
                            //producto actualizado
                            else {
                                mProductoSelected?.apply {
                                    name = it.etName.text.toString().trim()
                                    descripcion = it.etDescripcion.text.toString().trim()
                                    cantidad = it.etCantidad.text.toString().toInt()
                                    precio = it.etPrecio.text.toString().toDouble()
                                    imgUrl = eventPost.photoUrl
                                }
                                update()
                            }
                        }
                    }
                }
            }
            negativeButton?.setOnClickListener{
                dismiss()       //sale del dialog
            }
        }
    }
    private fun inicializarProducto(){
        mProductoSelected= (activity as? InterfaceHA)?.getProductSelected()  //hace un casteo y despues obtiene el producto
        mProductoSelected?.let { producto->     //si el producto es != de null
            mBinding?.let {
                it.etName.setText(producto.name)
                it.etDescripcion.setText(producto.descripcion)
                it.etCantidad.setText(producto.cantidad.toString())
                it.etPrecio.setText(producto.precio.toString())

                Glide.with(this)
                    .load(producto.imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //guarda toda la imagen
                    .centerCrop()
                    .into(it.imgProducto)
            }
        }
    }

    private fun uploadImage(productId: String?, callback: (EventPost)-> Unit){
        val eventPost= EventPost()

        //si se recibe un productId!=null se asigna ese, sino se busca el id correcpondiente
        eventPost.documentId= productId ?: FirebaseFirestore.getInstance().collection(Constants.FR_PRODUCTS).document().id

        val storageRef= FirebaseStorage.getInstance().reference.child(Constants.PATH_IMAGES)

        mPhotoSelectedUri?.let { uri->
            mBinding?.let { binding->
                binding.progresBar.visibility= View.VISIBLE
                val photoRef= storageRef.child(eventPost.documentId!!)
                photoRef.putFile(uri)
                    .addOnProgressListener {
                        val progress= (100*it.bytesTransferred / it.totalByteCount).toInt()
                        binding.progresBar.progress=progress
                    }
                    .addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener { downLoadUrl->
                            eventPost.isSuccess=true
                            eventPost.photoUrl= downLoadUrl.toString()
                            callback(eventPost)
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(context,"Error al subir la imagen.",Toast.LENGTH_SHORT).show()
                        enableUI(true)

                        eventPost.isSuccess=false
                        callback(eventPost)
                    }
            }
        }
    }
    private fun save(producto: Producto, documentId: String){
        val db= FirebaseFirestore.getInstance()
        db.collection(Constants.FR_PRODUCTS)
            .document(documentId)   //primero se pasa el id
            .set(producto)          //despues se guarda el producto
            //.add(producto)        //antes de guardar la imagen se guardaba directamente
            .addOnSuccessListener { Toast.makeText(context,"Producto Añadido", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context,"Error al insertar producto",Toast.LENGTH_SHORT).show() }
            .addOnCompleteListener { dismiss() }
    }
    private fun update(){
        val db= FirebaseFirestore.getInstance()
        mProductoSelected!!.id?.let { id->
            db.collection(Constants.FR_PRODUCTS)
                .document(id)
                .set(mProductoSelected!!)
                .addOnSuccessListener { Toast.makeText(context,"Producto actualizado", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(context,"Error al actualizar el producto",Toast.LENGTH_SHORT).show() }
                .addOnCompleteListener { dismiss() }
        }
    }

    private fun configBuscarImagen(){
        mBinding?.let {
            it.btnBuscar.setOnClickListener {
                //abrir galeria
                val intent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(intent)
            }
        }
    }
    private fun enableUI(enable:Boolean){
        positiveButton?.isEnabled=enable
        negativeButton?.isEnabled=enable
        mBinding?.let {
            with(it){
                etName.isEnabled=enable
                etDescripcion.isEnabled=enable
                etCantidad.isEnabled=enable
                etPrecio.isEnabled=enable
            }
        }

    }
}