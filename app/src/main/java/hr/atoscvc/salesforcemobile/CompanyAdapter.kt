package hr.atoscvc.salesforcemobile

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.list_layout_companies.view.*

class CompanyAdapter(private val companyList: ArrayList<Company>, val context: Context) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    private var currentPosition = -1    // If -1 is replaced with 0 then the first card will automatically be expanded

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.list_layout_companies,
                parent,
                false
        )
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companyList[position]
        //LUKA - outOfBounds za Company i Contact
        holder.tvCardCompaniesName.text = company.name
        holder.tvCardCompaniesCvsSegment.text = context.resources.getStringArray(R.array.companyCVS_array)[company.cvsSegment]
        holder.tvCardCompaniesStatus.text = context.resources.getStringArray(R.array.status_array)[company.status]
        holder.tvCardCompaniesOIB.text = company.OIB
        holder.tvCardCompaniesWebPage.text = company.webPage
        holder.tvCardCompaniesPhone.text = company.phone
        holder.tvCardCompaniesIncome.text = company.income.toString()
        holder.tvCardCompaniesCommunicationType.text = context.resources.getStringArray(R.array.companyCommunicationType_array)[company.communicationType]
        holder.tvCardCompaniesEmployees.text = context.resources.getStringArray(R.array.companyEmployees_array)[company.employees]
        holder.tvCardCompaniesDetails.text = company.details

        if (currentPosition == position) {
            if (holder.constraintLayoutCompaniesExpandable.visibility == View.GONE) {
//                val slideDown = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_expand)
                holder.constraintLayoutCompaniesExpandable.visibility = View.VISIBLE
//                holder.constraintLayoutCompaniesExpandable.startAnimation(slideDown)
            } else {
//                val slideUp = AnimationUtils.loadAnimation(context, R.anim.animation_cardview_collapse)
                holder.constraintLayoutCompaniesExpandable.visibility = View.GONE
//                holder.constraintLayoutCompaniesExpandable.startAnimation(slideUp)
            }
        } else {    // Remove this else statement and the other cards will not automatically collapse
            holder.constraintLayoutCompaniesExpandable.visibility = View.GONE
        }

        holder.constraintLayoutCompaniesMain.setOnClickListener {
            currentPosition = holder.adapterPosition
            notifyDataSetChanged()
        }

        holder.btnCardCompaniesAddContact.setOnClickListener {
            val intent = Intent(context, ContactEditorActivity::class.java).apply {
                putExtra(context.resources.getString(R.string.EXTRA_CONTACT_COMPANY), company.name)
            }
            context.startActivity(intent)   //LUKA for result? - OVDJE SE RUSI
        }

        holder.btnCardCompaniesEditCompany.setOnClickListener {
            //LUKA potrpati sve u intent (true/false !) - napraviti posebnu metodu
        }

    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCardCompaniesName: TextView = itemView.tvCardCompaniesName
        val tvCardCompaniesCvsSegment: TextView = itemView.tvCardCompaniesCvsSegment

        val tvCardCompaniesStatus: TextView = itemView.tvCardCompaniesStatus
        val tvCardCompaniesOIB: TextView = itemView.tvCardCompaniesOIB
        val tvCardCompaniesWebPage: TextView = itemView.tvCardCompaniesWebPage
        val tvCardCompaniesPhone: TextView = itemView.tvCardCompaniesPhone
        val tvCardCompaniesIncome: TextView = itemView.tvCardCompaniesIncome
        val tvCardCompaniesCommunicationType: TextView = itemView.tvCardCompaniesCommunicationType
        val tvCardCompaniesEmployees: TextView = itemView.tvCardCompaniesEmployees
        val tvCardCompaniesDetails: TextView = itemView.tvCardCompaniesDetails

        val constraintLayoutCompaniesMain: ConstraintLayout = itemView.constraintLayoutCompaniesMain
        val constraintLayoutCompaniesExpandable: ConstraintLayout = itemView.constraintLayoutCompaniesExpandable

        val btnCardCompaniesAddContact: Button = itemView.btnCardCompaniesAddContact
        val btnCardCompaniesEditCompany: Button = itemView.btnCardCompaniesEditCompany
    }
}