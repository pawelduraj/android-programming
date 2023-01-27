package com.example.app.ui.account

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.app.MapActivity
import com.example.app.R
import com.example.app.dao.Config
import com.example.app.dao.Database
import com.example.app.databinding.FragmentAccountBinding
import com.example.app.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private var createNewAccount = false
    private lateinit var githubDialog: Dialog

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            startActivity(Intent(context, MapActivity::class.java))
        }

        root.findViewById<Button>(R.id.buttonChange).setOnClickListener {
            createNewAccount = !createNewAccount
            setupInterface(root, null)
        }

        root.findViewById<Button>(R.id.buttonSignInWithGithub).setOnClickListener {
            val webView = WebView(requireContext())
            webView.webViewClient = GithubWebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(
                "https://github.com/login/oauth/authorize?client_id=${
                    Config.GITHUB_CLIENT_ID
                }&scope=read:user,user:email&state=${
                    TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                }"
            )
            githubDialog = Dialog(requireContext())
            githubDialog.setContentView(webView)
            githubDialog.show()
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Config.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(requireActivity(), options)

        root.findViewById<SignInButton>(R.id.buttonSignInWithGoogle).setOnClickListener {
            client.signOut()
            startActivityForResult(client.signInIntent, 9001)
        }

        root.findViewById<Button>(R.id.buttonSignOut).setOnClickListener {
            client.signOut()
            Database.signOut()
            setupInterface(root, null)
        }

        setupInterface(root, Database.getUser())

        return root
    }

    private fun setupInterface(root: View, user: User?) {
        if (user == null) {
            root.findViewById<ConstraintLayout>(R.id.layoutAuth).visibility = View.VISIBLE
            root.findViewById<ConstraintLayout>(R.id.layoutAccount).visibility = View.GONE

            val nameEditText = root.findViewById<EditText>(R.id.editTextName)
            val emailEditText = root.findViewById<EditText>(R.id.editTextEmail)
            val passwordEditText = root.findViewById<EditText>(R.id.editTextPassword)
            val changeButton = root.findViewById<Button>(R.id.buttonChange)
            val authButton = root.findViewById<Button>(R.id.buttonAuth)

            if (createNewAccount) {
                nameEditText.visibility = View.VISIBLE
                changeButton.text = getString(R.string.auth_sign_in)
                authButton.text = getString(R.string.auth_sign_up)
                authButton.setOnClickListener {
                    val name = nameEditText.text.toString()
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    if (Database.signUp(name, email, password)) {
                        Toast.makeText(
                            context, getString(R.string.auth_sign_up_success), Toast.LENGTH_SHORT
                        ).show()
                        createNewAccount = false
                    } else Toast.makeText(
                        context, getString(R.string.auth_sign_up_fail), Toast.LENGTH_SHORT
                    ).show()
                    setupInterface(root, Database.getUser())
                }
            } else {
                nameEditText.visibility = View.INVISIBLE
                changeButton.text = getString(R.string.auth_sign_up)
                authButton.text = getString(R.string.auth_sign_in)
                authButton.setOnClickListener {
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    if (Database.signIn(email, password)) Toast.makeText(
                        context, getString(R.string.auth_sign_in_success), Toast.LENGTH_SHORT
                    ).show()
                    else Toast.makeText(
                        context, getString(R.string.auth_sign_in_fail), Toast.LENGTH_SHORT
                    ).show()
                    setupInterface(root, Database.getUser())
                }
            }
        } else {
            root.findViewById<ConstraintLayout>(R.id.layoutAuth).visibility = View.GONE
            root.findViewById<ConstraintLayout>(R.id.layoutAccount).visibility = View.VISIBLE

            val userIdTextView = root.findViewById<TextView>(R.id.textViewUserId)
            val nameTextView = root.findViewById<TextView>(R.id.textViewName)
            val emailTextView = root.findViewById<TextView>(R.id.textViewEmail)
            val adminTextView = root.findViewById<TextView>(R.id.textViewAdmin)

            userIdTextView.text = "${getString(R.string.auth_user_id)}: ${user.userId}"
            nameTextView.text = "${getString(R.string.auth_name)}: ${user.name}"
            emailTextView.text = "${getString(R.string.auth_email)}: ${user.email}"
            adminTextView.text = "${getString(R.string.auth_admin)}: ${user.admin}"
        }
    }

    inner class GithubWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            handleUrl(request!!.url.toString())
            githubDialog.dismiss()
            return true
        }

        private fun handleUrl(url: String) {
            try {
                val uri = Uri.parse(url)
                val githubCode = uri.getQueryParameter("code") ?: ""
                if (!Database.signInWithGithub(githubCode))
                    throw Exception("Sign in with Github failed")
                Toast.makeText(
                    context, getString(R.string.auth_sign_in_success), Toast.LENGTH_SHORT
                ).show()
                setupInterface(requireView(), Database.getUser())
            } catch (e: Exception) {
                Log.e("ACCOUNT FRAGMENT", e.toString())
                e.printStackTrace()
                Toast.makeText(
                    context, getString(R.string.auth_sign_in_fail), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if (!Database.signInWithGoogle(account.idToken ?: ""))
                throw Exception("Cannot sign in with Google")
            setupInterface(requireView(), Database.getUser())
            Toast.makeText(context, getString(R.string.auth_sign_in_success), Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.e("ACCOUNT FRAGMENT", e.toString())
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.auth_sign_in_fail), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
