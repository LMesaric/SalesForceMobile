package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_companies.*

class CompaniesFragment : Fragment(), SearchView.OnQueryTextListener {

    //FILIP - indexers Status_lastName i Status_firstName za citanje iz baze
    //LUKA - poruka da je popis Contacts/Companies empty kad nema nista - TextView u centru ekrana
    companion object RequestCodesCompany {
        const val requestCodeRefresh = 1
        const val requestItemRefresh = 2
        var companyList = ArrayList<Company>()
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var mainActivity: MainActivity? = null

    private var adapter: CompanyAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_companies, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCompanyList()
    }

    private fun setUpCompanyList() {
        try {
            mainActivity = activity as MainActivity
        } catch (e: ClassCastException) {

        }

        activity?.fabAdd?.show()
        activity?.fabAdd?.setOnClickListener {
            val intent = Intent(activity, CompanyEditorActivity::class.java).apply {
                putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            }
            activity?.startActivityForResult(intent, requestCodeRefresh)
        }

        recyclerViewCompanies.setHasFixedSize(true)
        recyclerViewCompanies.layoutManager = LinearLayoutManager(activity)

        companyList = ArrayList()

        var listenerCompanies: CompanyAdapter.RecyclerViewCompaniesOnClickListener? = null
        try {
            listenerCompanies = activity as MainActivity
        } catch (e: ClassCastException) {
            listenerCompanies = activity as ContactEditorActivity
        } finally {
            adapter = activity?.applicationContext?.let {
                CompanyAdapter(
                        companyList,
                        activity as Activity,
                        arguments
                                ?.getBoolean(getString(R.string.EXTRA_COMPANY_IS_LIST_FOR_SELECT))
                                ?: false,
                        listenerCompanies!!
                )
            }
        }

        recyclerViewCompanies.adapter = adapter

        val query: Query? = mAuth.uid?.let {
            db.collection(getString(R.string.databaseCollectionUsers))
                    .document(it)
                    .collection(getString(R.string.databaseCollectionCompanies))
                    .orderBy(getString(R.string.databaseDocumentName))
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
                        adapter?.notifyDataSetChanged()
                    }
                }
                mainActivity?.searchView?.setQuery(mainActivity?.searchView?.query, true)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            setUpCompanyList()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        filter(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        filter(query)
        return true
    }

    private fun filter(query: String?) {
        val filteredList = ArrayList<Company>()
        for (company: Company in companyList) {
            if (SearchFilter.satisfiesQuery(query, company, activity?.applicationContext!!)) {
                filteredList.add(company)
            }
        }
        adapter?.filter(filteredList)
    }
}
