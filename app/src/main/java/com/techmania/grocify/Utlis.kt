package com.techmania.grocify

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.techmania.grocify.databinding.ProgressDialogBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utlis {

    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String){
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.dismiss()
    }

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    private  var firebaseAuthInstance: FirebaseAuth? = null
    fun getAuthInstance(): FirebaseAuth{
        if(firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

//    fun getCurrentUserId(): String?{
//        return FirebaseAuth.getInstance().currentUser?.uid
//    }


    fun getCurrentUserId() : String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
//fun getCurrentUserId() : String? {
//    val currentUser = FirebaseAuth.getInstance().currentUser
//    return currentUser?.uid
//}

    fun getRandomId(): String{
        return (1..25).map{ (('A'..'Z') + ('a'..'z')+ ('0'..'9')).random()}.joinToString("")
    }

    fun getCurrentDate(): String? {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }
}