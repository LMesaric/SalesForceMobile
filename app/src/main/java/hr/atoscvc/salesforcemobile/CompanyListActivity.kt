package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CompanyListActivity : AppCompatActivity() {

    //TODO Implementirati Search funkcionalnost

    companion object RequestCodesCompany {
        const val requestCodeRefresh = 1
        const val requestCodeChooseCompany = 2
    }

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        //recyclerViewCompanies.setHasFixedSize(true)
       // recyclerViewCompanies.layoutManager = LinearLayoutManager(this)

        val companyList = ArrayList<Company>()      //LUKA Prebaciti ovo (i contactList) u posebne Singleton objekte?

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
                "150000"
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
                "100000"
        ))

        val adapter = CompanyAdapter(
                companyList,
                this,               // Do NOT use 'applicationContext' instead of 'this'
                intent.getBooleanExtra(
                        getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT),
                        false
                )
        )
        //recyclerViewCompanies.adapter = adapter
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
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onUserInteraction() {
        (application as MyApp).onUserInteracted()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestCodeRefresh) {
            if (resultCode == RESULT_OK) {
                //recyclerViewCompanies.adapter?.notifyDataSetChanged()
            }
        }
    }
}
