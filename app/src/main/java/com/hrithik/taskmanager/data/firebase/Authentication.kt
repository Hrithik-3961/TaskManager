package com.hrithik.taskmanager.data.firebase

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hrithik.taskmanager.R
import com.hrithik.taskmanager.data.room.TaskDao
import com.hrithik.taskmanager.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Authentication : Fragment(R.layout.fragment_sign_in) {

    @Inject
    lateinit var taskDao: TaskDao

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()
    lateinit var collection: CollectionReference

    companion object {
        const val RC_SIGN_IN = 100
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val action =
                AuthenticationDirections.actionAuthenticationToHomeFragment()
            findNavController().navigate(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val binding = FragmentSignInBinding.bind(view)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.signInBtn.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        requireActivity().setResult(RC_SIGN_IN, signInIntent)
        resultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val dialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.progress_dialog, null)
        dialog.setContentView(view)
        dialog.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        collection = db.collection(user.uid)
                        dialog.dismiss()
                        val action =
                            AuthenticationDirections.actionAuthenticationToHomeFragment()
                        findNavController().navigate(action)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Sign In failed due to ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }

            }
    }
}