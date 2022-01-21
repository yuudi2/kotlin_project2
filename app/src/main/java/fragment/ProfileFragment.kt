package fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.kotlin_project2.MainActivity
import com.example.kotlin_project2.Main_activity
import com.example.kotlin_project2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var auth : FirebaseAuth? = null
    companion object{
        fun newInstance() : ProfileFragment{
            return ProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = Firebase.auth

        val logout = view.findViewById<Button>(R.id.logout)

        logout.setOnClickListener{
            // 로그인 화면으로
            val intent = Intent(view.context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth?.signOut()
        }

        return view
    }

}