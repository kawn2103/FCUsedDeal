package kst.app.fcuseddeal.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kst.app.fcuseddeal.R
import kst.app.fcuseddeal.databinding.FragmentMyPageBinding

class MyPageFragment: Fragment(R.layout.fragment_my_page) {

    private var binding : FragmentMyPageBinding? = null

    private val auth : FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMyPageBinding = FragmentMyPageBinding.bind(view)
        binding = fragmentMyPageBinding

        fragmentMyPageBinding.signOutBt.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEt.text.toString()
                val password = binding.passwordEt.text.toString()

                if (auth.currentUser == null ){
                    // 로그인
                    auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(requireActivity()){ task ->
                            if (task.isSuccessful){
                                successSignIn()
                            } else {
                                Toast.makeText(context,"로그인에 실패 함",Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    auth.signOut()

                    binding.emailEt.text.clear()
                    binding.emailEt.isEnabled = true
                    binding.passwordEt.text.clear()
                    binding.passwordEt.isEnabled = true

                    binding.signOutBt.text = "로그인"
                    binding.signOutBt.isEnabled = true
                    binding.signUpBt.isEnabled = false
                }
            }
        }

        fragmentMyPageBinding.signUpBt.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEt.text.toString()
                val password = binding.passwordEt.text.toString()

                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(requireActivity()){ task ->
                        if (task.isSuccessful){
                            Toast.makeText(context,"회원가입 성공",Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context,"회원가입 실패",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        fragmentMyPageBinding.emailEt.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEt.text.isNotEmpty() && binding.passwordEt.text.isNotEmpty()
                binding.signOutBt.isEnabled = enable
                binding.signUpBt.isEnabled = enable
            }
        }

        fragmentMyPageBinding.passwordEt.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEt.text.isNotEmpty() && binding.passwordEt.text.isNotEmpty()
                binding.signOutBt.isEnabled = enable
                binding.signUpBt.isEnabled = enable
            }
        }

    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null){
            binding?.let { binding ->
                binding.emailEt.text.clear()
                binding.emailEt.isEnabled = true
                binding.passwordEt.text.clear()
                binding.passwordEt.isEnabled = true

                binding.signOutBt.text = "로그인"
                binding.signOutBt.isEnabled = true
                binding.signUpBt.isEnabled = false
            }
        } else {
            binding?.let { binding ->
                binding.emailEt.setText(auth.currentUser!!.email)
                binding.emailEt.isEnabled = false
                binding.passwordEt.setText("**********")
                binding.passwordEt.isEnabled = false

                binding.signOutBt.text = "로그아웃"
                binding.signOutBt.isEnabled = true
                binding.signUpBt.isEnabled = false
            }
        }
    }

    private fun successSignIn(){
        if (auth.currentUser == null){
            Toast.makeText(context,"로그인 실패",Toast.LENGTH_SHORT).show()
            return
        }

        binding?.emailEt?.isEnabled = false
        binding?.passwordEt?.isEnabled = false
        binding?.signUpBt?.isEnabled = false
        binding?.signOutBt?.text = "로그아웃"

    }
}