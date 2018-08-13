package hr.atoscvc.salesforcemobile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class CompaniesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_companies, container, false)

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

        val adapter = activity?.baseContext?.let { CompanyAdapter(companyList, it, false) }
        recyclerView.adapter = adapter

        return view
    }


}
