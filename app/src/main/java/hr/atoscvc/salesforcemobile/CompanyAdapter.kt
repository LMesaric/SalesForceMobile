package hr.atoscvc.salesforcemobile

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.list_layout_companies.view.*

class CompanyAdapter(
        private val companyList: ArrayList<Company>,
        private val context: Activity,
        private val isForSelect: Boolean,
        private val listenerCompanies: RecyclerViewCompaniesOnClickListener
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    interface RecyclerViewCompaniesOnClickListener {
        //FILIP - sortirati listu (Companies & Contacts) i onda pozvati notifyDataSetChanged(), pa se ne treba loadati ponovno iz baze
        fun recyclerViewCompaniesOnClick(
                circleImageView: CircleImageView,
                company: Company,
                hideEditButtons: Boolean
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.list_layout_companies,
                parent,
                false
        )
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company: Company = companyList[position]

        holder.tvCardCompanyName.text = company.name
        holder.tvCardCompanyCvsSegment.text = context.resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]
        holder.tvCardCompanyStatus.text = context.resources.getStringArray(R.array.status_array)[company.status]

        holder.constraintLayoutCompanyMain.setOnClickListener {
            listenerCompanies.recyclerViewCompaniesOnClick(
                    holder.ivCompanyAvatar,
                    companyList[position],
                    isForSelect
            )
        }

        if (isForSelect) {
            holder.btnCardCompanySelectCompany.visibility = View.VISIBLE
            holder.btnCardCompanySelectCompany.isEnabled = true

            holder.btnCardCompanySelectCompany.setOnClickListener {
                ContactEditFragment.chosenCompany = company
                context.onBackPressed()
            }
        } else {
            holder.btnCardCompanySelectCompany.visibility = View.GONE
            holder.btnCardCompanySelectCompany.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardCompanyName: TextView = itemView.tvCardCompanyName
        val tvCardCompanyCvsSegment: TextView = itemView.tvCardCompanyCvsSegment
        val tvCardCompanyStatus: TextView = itemView.tvCardCompanyStatus

        val ivCompanyAvatar: CircleImageView = itemView.ivCompanyAvatar
        val btnCardCompanySelectCompany: Button = itemView.btnCardCompanySelectCompany
        val constraintLayoutCompanyMain: ConstraintLayout = itemView.constraintLayoutCompanyMain
    }
}
