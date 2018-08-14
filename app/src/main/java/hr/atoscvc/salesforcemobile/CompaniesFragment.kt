package hr.atoscvc.salesforcemobile


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class CompaniesFragment : Fragment() {

    companion object RequestCodesCompany {
        const val requestCodeRefresh = 1
        const val requestCodeChooseCompany = 2
    }

    private lateinit var recyclerView: RecyclerView
    private var fabAddCompanies: FloatingActionButton? = null

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_companies, container, false)

        fabAddCompanies = activity?.findViewById(R.id.fabAdd)
        fabAddCompanies?.visibility = View.VISIBLE
        fabAddCompanies?.setOnClickListener{
            val intent = Intent(activity, CompanyEditorActivity::class.java).apply {
                putExtra(getString(R.string.EXTRA_IS_EDITOR_FOR_NEW_ITEM), true)
            }
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.recyclerViewCompanies)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

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

        val adapter = CompanyAdapter(companyList, activity as Activity, false)
        recyclerView.adapter = adapter

        return view
    }

}
