package hr.atoscvc.salesforcemobile

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_company_list.*

class CompanyListActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)

        mAuth = FirebaseAuth.getInstance()

        recyclerViewCompanies.setHasFixedSize(true)
        recyclerViewCompanies.layoutManager = LinearLayoutManager(this)

        val companyList = ArrayList<Company>()

        companyList.add(Company(
                "asdbnasd",
                0,
                "976754689",
                "Atos CVC",
                "atos.net",
                0,
                "Very good company",
                "012475479",
                0,
                2,
                150000
        ))
        companyList.add(Company(
                "knksgnf",
                1,
                "45678934",
                "Siemens",
                "siemens.hr",
                1,
                "Very great company",
                "01484567",
                0,
                4,
                100000
        ))

        val adapter = CompanyAdapter(companyList, applicationContext)
        recyclerViewCompanies.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawable(BitmapDrawable(
                applicationContext.resources,
                (application as MyApp).getInstance(applicationContext.resources)
        ))
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            sendToLogin()
        }
    }

    private fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }
}
