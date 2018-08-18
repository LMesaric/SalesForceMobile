package hr.atoscvc.salesforcemobile


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class CompaniesFragment : Fragment() {

    companion object RequestCodesCompany {
        const val requestCodeRefresh = 1
        const val requestCodeChooseCompany = 2
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isForSelect: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private var fabAddCompanies: FloatingActionButton? = null

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_companies, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        isForSelect = arguments?.getBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT)) ?: false

        fabAddCompanies = activity?.findViewById(R.id.fabAdd)
        fabAddCompanies?.visibility = View.VISIBLE
        fabAddCompanies?.setOnClickListener {
            val intent = Intent(activity, CompanyEditorActivity::class.java).apply {
                putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            }
            activity?.startActivityForResult(intent, requestCodeRefresh)
        }

        val companyList = ArrayList<Company>()

        recyclerView = view.findViewById(R.id.recyclerViewCompanies)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = CompanyAdapter(companyList, activity as Activity, isForSelect)
        recyclerView.adapter = adapter

        val query: Query? = mAuth.uid?.let { db.collection("Users").document(it).collection("Companies").orderBy("name") }
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
