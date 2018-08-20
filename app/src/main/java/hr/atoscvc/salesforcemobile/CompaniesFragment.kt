package hr.atoscvc.salesforcemobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_companies.view.*

class CompaniesFragment : Fragment() {
    //TODO Implementirati Search funkcionalnost
    companion object RequestCodesCompany {
        const val requestCodeRefresh = 1
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_companies, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        activity?.fabAdd?.visibility = View.VISIBLE
        activity?.fabAdd?.setOnClickListener {
            val intent = Intent(activity, CompanyEditorActivity::class.java).apply {
                putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            }
            activity?.startActivityForResult(intent, requestCodeRefresh)
        }

        val companyList = ArrayList<Company>()      //LUKA - TWO SINGLETONS

        view.recyclerViewCompanies.setHasFixedSize(true)
        view.recyclerViewCompanies.layoutManager = LinearLayoutManager(activity)

        val adapter = CompanyAdapter(
                companyList,
                activity as Activity,
                arguments
                        ?.getBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT))
                        ?: false
        )
        view.recyclerViewCompanies.adapter = adapter

        val query: Query? = mAuth.uid?.let {
            db.collection("Users")
                    .document(it)
                    .collection("Companies")
                    .orderBy("name")
        }
        query?.addSnapshotListener { p0, p1 ->
            if (p1 != null) {
                Log.d("ERRORS", p1.message)
            }
            if (p0 != null) {
                for (doc in p0.documentChanges) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        val newCompany = doc.document.toObject<Company>(Company::class.java)
                        companyList.add(newCompany)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        return view
    }

}
